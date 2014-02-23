package com.lyndir.omicron.api.model;

import static com.lyndir.omicron.api.model.CoreUtils.*;
import static com.lyndir.omicron.api.model.Security.*;

import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.Change;
import com.lyndir.omicron.api.util.Maybe;
import com.lyndir.omicron.api.util.Maybool;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class GameObject extends MetaObject implements IGameObject {

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    final Logger logger = Logger.get( getClass() );

    private final GameObjectController<? extends GameObject>   controller;
    private final UnitType                                     unitType;
    private final Game                                         game;
    private final int                                          objectID;
    private final ImmutableListMultimap<ModuleType<?>, Module> modules;
    private       Player                                       owner;
    private       Tile                                         location;

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
            owner.addObjects( this );
        location.setContents( this );
    }

    @Override
    public int hashCode() {
        return objectID;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof IGameObject && objectID == ((IGameObject) obj).getObjectID();
    }

    @Override
    @Nonnull
    public GameObjectController<? extends GameObject> getController() {
        return controller;
    }

    @Nonnull
    @Override
    public Optional<Player> getOwner() {
        return Optional.fromNullable( owner );
    }

    @Override
    @Authenticated
    public boolean isOwnedByCurrentPlayer()
            throws NotAuthenticatedException {
        return isAuthenticated() && ObjectUtils.isEqual( owner, currentPlayer() );
    }

    void setOwner(@Nullable final Player owner) {
        Change.From<IPlayer> ownerChange = Change.<IPlayer>from( this.owner );

        this.owner = owner;

        getGame().getController().fireIfObservable( getLocation() ) //
                .onUnitCaptured( this, ownerChange.to( this.owner ) );
    }

    @Authenticated
    @Override
    @SuppressWarnings("ParameterHidesMemberVariable")
    public Maybool canObserve(@Nonnull final ITile location)
            throws NotAuthenticatedException {
        return getController().canObserve( location );
    }

    @Authenticated
    @Nonnull
    @Override
    public Iterable<Tile> listObservableTiles()
            throws NotAuthenticatedException, NotObservableException {
        return getController().listObservableTiles();
    }

    @Override
    public int getObjectID() {
        return objectID;
    }

    @Override
    public Game getGame() {
        return game;
    }

    Tile getLocation() {
        return location;
    }

    @Override
    public Maybe<Tile> checkLocation()
            throws NotAuthenticatedException {
        if (!isGod() && !currentPlayer().canObserve( getLocation() ).isTrue())
            return Maybe.unknown();

        return Maybe.fromNullable( getLocation() );
    }

    void setLocation(@Nonnull final Tile location) {
        Change.From<ITile> locationChange = Change.<ITile>from( this.location );
        this.location = location;

        getGame().getController().fireIfObservable( location ) //
                .onUnitMoved( this, locationChange.to( this.location ) );
    }

    @Override
    public UnitType getType() {
        return unitType;
    }

    @Override
    public <M extends IModule> Optional<M> getModule(final PublicModuleType<M> moduleType, final int index)
            throws NotAuthenticatedException, NotObservableException {
        assertObservable( getLocation() );

        return Optional.fromNullable( Iterables.get( getModules( moduleType ), index, null ) );
    }

    @Override
    public <M extends IModule> Optional<M> getModule(final PublicModuleType<M> moduleType, final PredicateNN<M> predicate)
            throws NotAuthenticatedException, NotObservableException {
        assertObservable( getLocation() );

        return FluentIterable.from( getModules( moduleType ) ).firstMatch( predicate );
    }

    @Override
    public <M extends IModule> List<M> getModules(final PublicModuleType<M> moduleType)
            throws NotAuthenticatedException, NotObservableException {
        assertObservable( getLocation() );

        // Checked by Module's constructor.
        //noinspection unchecked
        return (List<M>) modules.get( coreMT( moduleType ) );
    }

    @Override
    public <M extends IModule> M onModuleElse(final PublicModuleType<M> moduleType, final int index, @Nullable final Object elseValue)
            throws NotAuthenticatedException, NotObservableException {
        assertObservable( getLocation() );

        return ObjectUtils.ifNotNullElse( moduleType.getModuleType(), getModule( moduleType, index ).orNull(), elseValue );
    }

    @Override
    public <M extends IModule> M onModuleElse(final PublicModuleType<M> moduleType, final PredicateNN<M> predicate,
                                              @Nullable final Object elseValue)
            throws NotAuthenticatedException, NotObservableException {
        assertObservable( getLocation() );

        return ObjectUtils.ifNotNullElse( moduleType.getModuleType(), getModule( moduleType, predicate ).orNull(), elseValue );
    }

    @Override
    public <M extends IModule> M onModule(final PublicModuleType<M> moduleType, final int index)
            throws NotAuthenticatedException, NotObservableException {
        assertObservable( getLocation() );

        return onModuleElse( moduleType, index, null );
    }

    @Override
    public <M extends IModule> M onModule(final PublicModuleType<M> moduleType, final PredicateNN<M> predicate)
            throws NotAuthenticatedException, NotObservableException {
        assertObservable( getLocation() );

        return onModuleElse( moduleType, predicate, null );
    }

    @Override
    public ImmutableCollection<Module> listModules()
            throws NotAuthenticatedException, NotObservableException {
        assertObservable( getLocation() );

        return ImmutableList.copyOf( modules.values() );
    }
}
