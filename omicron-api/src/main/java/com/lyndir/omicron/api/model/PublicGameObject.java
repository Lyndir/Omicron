package com.lyndir.omicron.api.model;

import static com.lyndir.omicron.api.model.Security.assertObservable;

import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.Authenticated;
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
public class PublicGameObject extends MetaObject implements IGameObject {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger logger = Logger.get( PublicGameObject.class );

    private final IGameObject core;

    PublicGameObject(final IGameObject core) {
        this.core = core;
    }

    @Override
    public int hashCode() {
        return core.hashCode();
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj instanceof PublicGameObject)
            return core.equals( ((PublicGameObject) obj).core );

        return core.equals( obj );
    }

    @Override
    @Nonnull
    public IGameObjectController<? extends IGameObject> getController() {
        return core.getController();
    }

    @Override
    @Authenticated
    public boolean isOwnedByCurrentPlayer()
            throws Security.NotAuthenticatedException {
        return core.isOwnedByCurrentPlayer();
    }

    @Override
    @Authenticated
    public Maybe<? extends IPlayer> checkOwner() {
        return core.checkOwner();
    }

    @Override
    @Authenticated
    public Maybool canObserve(@Nonnull final GameObservable observable)
            throws Security.NotAuthenticatedException {
        return core.canObserve( observable );
    }

    @Authenticated
    @Nonnull
    @Override
    public Iterable<? extends ITile> iterateObservableTiles() {
        return core.iterateObservableTiles();
    }

    @Override
    public int getObjectID() {
        return core.getObjectID();
    }

    @Override
    public IGame getGame() {
        return core.getGame();
    }

    @Override
    @Authenticated
    public Maybe<? extends ITile> checkLocation()
            throws Security.NotAuthenticatedException {
        return core.checkLocation();
    }

    @Override
    public IUnitType getType() {
        return core.getType();
    }

    @Override
    public <M extends IModule> Optional<M> getModule(final PublicModuleType<M> moduleType, final int index)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable( this );

        return core.getModule( moduleType, index );
    }

    @Override
    public <M extends IModule> Optional<M> getModule(final PublicModuleType<M> moduleType, final PredicateNN<M> predicate)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable( this );

        return core.getModule( moduleType, predicate );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <M extends IModule> List<M> getModules(final PublicModuleType<M> moduleType)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable( this );

        // Checked by Module's constructor.
        return core.getModules( moduleType );
    }

    @Override
    public <M extends IModule> M onModuleElse(final PublicModuleType<M> moduleType, final int index, @Nullable final Object elseValue)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable( this );

        return core.onModuleElse( moduleType, index, elseValue );
    }

    @Override
    public <M extends IModule> M onModuleElse(final PublicModuleType<M> moduleType, final PredicateNN<M> predicate,
                                              @Nullable final Object elseValue)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable( this );

        return core.onModuleElse( moduleType, predicate, elseValue );
    }

    @Override
    public <M extends IModule> M onModule(final PublicModuleType<M> moduleType, final int index)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable( this );

        return core.onModule( moduleType, index );
    }

    @Override
    public <M extends IModule> M onModule(final PublicModuleType<M> moduleType, final PredicateNN<M> predicate)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable( this );

        return core.onModule( moduleType, predicate );
    }

    @Override
    public ImmutableCollection<? extends IModule> listModules()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable( this );

        return core.listModules();
    }
}
