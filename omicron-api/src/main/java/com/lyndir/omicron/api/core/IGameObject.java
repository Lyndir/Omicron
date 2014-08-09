package com.lyndir.omicron.api.core;

import static com.lyndir.omicron.api.core.Security.*;

import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.error.AlreadyCheckedException;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.lhunath.opal.system.util.PredicateNN;
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
public interface IGameObject extends GameObserver, GameObservable {

    /**
     * @return The unique identifier of this unit in the game.
     */
    long getObjectID();

    /**
     * @return The game that hosts this object.
     */
    IGame getGame();

    /**
     * @return The type that defines the behavior of this unit.
     */
    IUnitType getType();

    /**
     * @return The modules that implement the behavior of this unit mapped by module type.
     */
    ImmutableMultimap<PublicModuleType<?>, ? extends IModule> getModulesByType();

    /**
     * Get this object's module of the given type at the given index.
     *
     * @param moduleType The type of module to get.
     * @param index      The index of the module.
     * @param <M>        The type of the module.
     *
     * @return The module of the given type at the given index.
     */
    default <M extends IModule> Optional<M> getModule(final PublicModuleType<M> moduleType, final int index) {
        return Optional.fromNullable( Iterables.get( getModules( moduleType ), index, null ) );
    }

    /**
     * Get this object's module of the given type at the given index.
     *
     * @param moduleType The type of module to get.
     * @param predicate  Return the first module of the given type for which the predicate holds true.
     * @param <M>        The type of the module.
     *
     * @return The module of the given type at the given index.
     */
    default <M extends IModule> Optional<M> getModule(final PublicModuleType<M> moduleType, final PredicateNN<M> predicate) {
        assertObservable( this );

        return FluentIterable.from( getModules( moduleType ) ).firstMatch( predicate );
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
    <M extends IModule> List<M> getModules(PublicModuleType<M> moduleType);

    /**
     * Run a method on a module but return {@code elseValue} if this object doesn't have such a module.
     *
     * @param moduleType The type of the module to run a method on.
     * @param index      The index of the module to run a method on.
     * @param elseValue  The value to return if this object doesn't have a module of the given type at the given index.
     * @param <M>        The type of the module to run a method on.
     *
     * @return A proxy object that you can run your method on.
     */
    default <M extends IModule> M onModuleElse(final PublicModuleType<M> moduleType, final int index, @Nullable final Object elseValue)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable( this );

        return ObjectUtils.ifNotNullElse( moduleType.getModuleType(), getModule( moduleType, index ).orNull(), elseValue );
    }

    /**
     * Run a method on a module but return {@code elseValue} if this object doesn't have such a module.
     *
     * @param moduleType The type of the module to run a method on.
     * @param predicate  Run the method on the first module for which this predicate holds true.
     * @param elseValue  The value to return if this object doesn't have a module of the given type.
     * @param <M>        The type of the module to run a method on.
     *
     * @return A proxy object that you can run your method on.
     */
    default <M extends IModule> M onModuleElse(final PublicModuleType<M> moduleType, final PredicateNN<M> predicate,
                                               @Nullable final Object elseValue)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable( this );

        return ObjectUtils.ifNotNullElse( moduleType.getModuleType(), getModule( moduleType, predicate ).orNull(), elseValue );
    }

    /**
     * Run a method on a module or do nothing if this object doesn't have such a module
     * (in this case, if the method has a return value, it will return {@code null}).
     *
     * @param moduleType The type of the module to run a method on.
     * @param index      The index of the module to run a method on.
     * @param <M>        The type of the module to run a method on.
     *
     * @return A proxy object that you can run your method on.
     */
    default <M extends IModule> M onModule(final PublicModuleType<M> moduleType, final int index)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable( this );

        return onModuleElse( moduleType, index, null );
    }

    /**
     * Run a method on a module or do nothing if this object doesn't have such a module
     * (in this case, if the method has a return value, it will return {@code null}).
     *
     * @param moduleType The type of the module to run a method on.
     * @param predicate  Run the method on the first module for which this predicate holds true.
     * @param <M>        The type of the module to run a method on.
     *
     * @return A proxy object that you can run your method on.
     */
    default <M extends IModule> M onModule(final PublicModuleType<M> moduleType, final PredicateNN<M> predicate)
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable( this );

        return onModuleElse( moduleType, predicate, null );
    }

    /**
     * @return The modules that implement the behavior of this unit.
     */
    ImmutableCollection<? extends IModule> listModules();

    @Nonnull
    IGameObjectController<? extends IGameObject> getController();

    @Override
    default Maybool canObserve(@Nonnull final GameObservable observable) {
        return onModuleElse( PublicModuleType.BASE, 0, Maybool.NO ).getController().canObserve( observable );
    }

    @Nonnull
    @Override
    default Iterable<? extends ITile> iterateObservableTiles() {
        return onModuleElse( PublicModuleType.BASE, 0, ImmutableList.of() ).getController().iterateObservableTiles();
    }

    /**
     * @return {@code true} if this object is owned by the currently authenticated player.
     */
    default boolean isOwnedByCurrentPlayer() {
        Maybe<? extends IPlayer> owner = getOwner();
        switch (owner.presence()) {
            case ABSENT:
                return false;
            case UNKNOWN:
                return false;
            case PRESENT:
                return owner.get().isCurrentPlayer();
        }

        throw new AlreadyCheckedException();
    }
}
