package com.lyndir.omicron.api;

import static com.lyndir.omicron.api.Security.*;

import com.google.common.collect.*;
import java.util.*;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.util.Maybe;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class GameObject extends MetaObject implements IGameObject {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger logger = Logger.get( GameObject.class );

    private final GameObjectController<? extends GameObject>         controller;
    private final UnitType                                           unitType;
    private final Game                                               game;
    private final long                                               objectID;
    private final ImmutableListMultimap<PublicModuleType<?>, Module> modules;
    @Nullable
    private       Player                                             owner;
    private       Tile                                               location;

    GameObject(@Nonnull final UnitType unitType, @Nonnull final Game game, @Nonnull final Player owner, final Tile location) {
        this( unitType, game, owner, location, owner.nextObjectID() );
    }

    GameObject(@Nonnull final UnitType unitType, @Nonnull final Game game, @Nullable final Player owner, final Tile location,
               final long objectID) {
        this.unitType = unitType;
        this.game = game;
        this.owner = owner;
        this.location = location;
        this.objectID = objectID;

        ImmutableListMultimap.Builder<PublicModuleType<?>, Module> modulesBuilder = ImmutableListMultimap.builder();
        for (final Module module : unitType.createModules()) {
            modulesBuilder.put( module.getType(), module );
            module.setGameObject( this );
        }
        modules = modulesBuilder.build();
        controller = new GameObjectController<>( this );
    }

    /**
     * Register ourselves into the game.
     */
    void register() {
        location.setContents( this );
        if (owner != null)
            owner.addObjects( this );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( objectID );
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

    @Override
    @Nonnull
    public Optional<Player> getOwner() {
        return Optional.ofNullable( owner );
    }

    void setOwner(@Nullable final Player owner) {
        Change.From<IPlayer> ownerChange = Change.<IPlayer>from( this.owner );

        this.owner = owner;

        getGame().getController().fireIfObservable( this ) //
                .onUnitCaptured( this, ownerChange.to( this.owner ) );
    }

    @Override
    public long getObjectID() {
        return objectID;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public Maybe<Tile> getLocation() {
        if (!isGod() && !isOwnedByCurrentPlayer())
            if (!currentPlayer().canObserve( location ).isTrue())
                // Has a location but current player cannot observe it.
                return Maybe.unknown();

        // We're either god, can be observed by or are owned by the current player.
        return Maybe.of( location );
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
    public ImmutableMultimap<PublicModuleType<?>, ? extends IModule> getModulesByType() {
        assertObservable( this );
        return modules;
    }

    @Nullable
    static GameObject cast(@Nullable final IGameObject gameObject) {
        return (GameObject) gameObject;
    }
}
