package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.Change;
import com.lyndir.omicron.api.util.Maybool;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class GameObject extends MetaObject implements GameObserver {

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    final Logger logger = Logger.get( getClass() );

    private final GameObjectController<?>             controller;
    private final UnitType                            unitType;
    private final Game                                game;
    private final int                                 objectID;
    private final ListMultimap<ModuleType<?>, Module> modules;
    private       Player                              owner;
    private       Tile                                location;

    GameObject(@Nonnull final UnitType unitType, @Nonnull final Game game, @Nonnull final Player owner, @Nonnull final Tile location) {
        this( unitType, game, owner, owner.nextObjectID(), location );
    }

    GameObject(@Nonnull final UnitType unitType, @Nonnull final Game game, @Nullable final Player owner, final int objectID,
               @Nonnull final Tile location) {
        this.unitType = unitType;
        this.game = game;
        this.owner = owner;
        this.objectID = objectID;
        this.location = location;

        ImmutableListMultimap.Builder<ModuleType<?>, Module> modulesBuilder = ImmutableListMultimap.builder();
        for (final Module module : unitType.createModules()) {
            modulesBuilder.put( module.getType(), module );
            module.setGameObject( this );
        }
        modules = modulesBuilder.build();
        controller = new GameObjectController<>( this );

        // Register ourselves into the game.
        if (owner != null)
            owner.addObject( this );
        location.setContents( this );
    }

    @Nonnull
    public GameObjectController<? extends GameObject> getController() {
        return controller;
    }

    @Nonnull
    @Override
    public Optional<Player> getOwner() {
        return Optional.fromNullable( owner );
    }

    @Authenticated
    public boolean isOwnedByCurrentPlayer() {
        return Security.isAuthenticated() && ObjectUtils.isEqual( owner, Security.currentPlayer() );
    }

    void setOwner(@Nullable final Player owner) {
        Change.From<Player> ownerChange = Change.from( this.owner );

        this.owner = owner;

        getGame().getController().fireIfObservable( getLocation() ) //
                .onUnitCaptured( this, ownerChange.to( this.owner ) );
    }

    @Authenticated
    @Override
    @SuppressWarnings("ParameterHidesMemberVariable")
    public Maybool canObserve(@Nonnull final Tile location) {
        return getController().canObserve( location );
    }

    @Authenticated
    @Nonnull
    @Override
    public Iterable<Tile> listObservableTiles() {
        return getController().listObservableTiles();
    }

    public int getObjectID() {
        return objectID;
    }

    public Game getGame() {
        return game;
    }

    public Tile getLocation() {
        return location;
    }

    void setLocation(@Nonnull final Tile location) {
        final Change.From<Tile> locationChange = Change.from( this.location );
        this.location = location;

        getGame().getController().fireIfObservable( location ) //
                .onUnitMoved( this, locationChange.to( this.location ) );
    }

    public UnitType getType() {
        return unitType;
    }

    /**
     * Get this object's module of the given type at the given index.
     *
     * @param moduleType The type of module to get.
     * @param index      The index of the module.
     * @param <M>        The type of the module.
     *
     * @return The module of the given type at the given index.
     */
    public <M extends Module> Optional<M> getModule(final ModuleType<M> moduleType, final int index) {
        return Optional.fromNullable( Iterables.get( getModules( moduleType ), index, null ) );
    }

    /**
     * Get this object's modules of the given type.
     *
     * @param moduleType The type of module to get.
     * @param <M>        The type of the module.
     *
     * @return A list of modules of the given type or an empty list if there are none.
     */
    @SuppressWarnings("unchecked")
    public <M extends Module> List<M> getModules(final ModuleType<M> moduleType) {
        // Checked by Module's constructor.
        return (List<M>) modules.get( moduleType );
    }

    /**
     * Run a method on a module but return {@code elseValue} if this object doesn't have such a module.
     *
     * @param moduleType The type of the module to run a method on.
     * @param elseValue  The value to return if this object doesn't have a module of the given type.
     * @param <M>        The type of the module to run a method on.
     *
     * @return A proxy object that you can run your method on.
     */
    public <M extends Module> M onModuleElse(final ModuleType<M> moduleType, final int index, @Nullable final Object elseValue) {
        return ObjectUtils.ifNotNullElse( moduleType.getModuleType(), getModule( moduleType, index ).orNull(), elseValue );
    }

    /**
     * Run a method on a module or do nothing if this object doesn't have such a module
     * (in this case, if the method has a return value, it will return {@code null}).
     *
     * @param moduleType The type of the module to run a method on.
     * @param <M>        The type of the module to run a method on.
     *
     * @return A proxy object that you can run your method on.
     */
    public <M extends Module> M onModule(final ModuleType<M> moduleType, final int index) {
        return onModuleElse( moduleType, index, null );
    }

    public ImmutableCollection<? extends Module> listModules() {
        return ImmutableList.copyOf( modules.values() );
    }
}
