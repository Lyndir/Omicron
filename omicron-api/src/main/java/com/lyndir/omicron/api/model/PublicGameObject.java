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

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    final Logger logger = Logger.get( getClass() );

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

    @Nonnull
    @Override
    public Optional<? extends IPlayer> getOwner() {
        return core.getOwner();
    }

    @Override
    @Authenticated
    public boolean isOwnedByCurrentPlayer()
            throws Security.NotAuthenticatedException {
        return core.isOwnedByCurrentPlayer();
    }

    @Authenticated
    @Override
    @SuppressWarnings("ParameterHidesMemberVariable")
    public Maybool canObserve(@Nonnull final ITile location) {
        return core.canObserve( location );
    }

    @Authenticated
    @Nonnull
    @Override
    public Iterable<? extends ITile> listObservableTiles() {
        return core.listObservableTiles();
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

    /**
     * Get this object's module of the given type at the given index.
     *
     * @param moduleType The type of module to get.
     * @param index      The index of the module.
     * @param <M>        The type of the module.
     *
     * @return The module of the given type at the given index.
     */
    @Override
    public <M extends IModule> Optional<M> getModule(final PublicModuleType<M> moduleType, final int index)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable( this );

        return core.getModule( moduleType, index );
    }

    /**
     * Get this object's modules of the given type.
     *
     * @param moduleType The type of module to get.
     * @param <M>        The type of the module.
     *
     * @return A list of modules of the given type or an empty list if there are none.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <M extends IModule> List<M> getModules(final PublicModuleType<M> moduleType)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable( this );

        // Checked by Module's constructor.
        return core.getModules( moduleType );
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
    @Override
    public <M extends IModule> M onModuleElse(final PublicModuleType<M> moduleType, final int index, @Nullable final Object elseValue)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable( this );

        return core.onModuleElse( moduleType, index, elseValue );
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
    @Override
    public <M extends IModule> M onModule(final PublicModuleType<M> moduleType, final int index)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable( this );

        return core.onModule( moduleType, index );
    }

    @Override
    public ImmutableCollection<? extends IModule> listModules()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable( this );

        return core.listModules();
    }
}
