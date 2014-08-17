package com.lyndir.omicron.api;

import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.error.NotAuthenticatedException;
import com.lyndir.omicron.api.error.NotObservableException;
import java.util.Optional;
import com.lyndir.omicron.api.util.Maybool;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
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
        return Optional.ofNullable( Iterables.get( getModules( moduleType ), index, null ) );
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
    default <M extends IModule> Optional<M> getModule(final PublicModuleType<M> moduleType, final Predicate<M> predicate) {
        return getModules( moduleType ).stream().filter( predicate ).findFirst();
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
    default <M extends IModule> List<M> getModules(final PublicModuleType<M> moduleType) {
        // Checked by Module's constructor.
        return (List<M>) getModulesByType().get( moduleType );
    }

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
    default <M extends IModule, T> T onModuleElse(final PublicModuleType<M> moduleType, final int index, final T elseValue, final NNFunctionNN<M, T> operation)
            throws NotAuthenticatedException, NotObservableException {
        Optional<M> module = getModule( moduleType, index );
        return module.isPresent()? operation.apply( module.get() ): elseValue;
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
    default <M extends IModule, T> T onModuleElse(final PublicModuleType<M> moduleType, final Predicate<M> predicate,
                                                final T elseValue, final NNFunctionNN<M, T> operation)
            throws NotAuthenticatedException, NotObservableException {
        Optional<M> module = getModule( moduleType, predicate );
        return module.isPresent()? operation.apply( module.get() ): elseValue;
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
    @Nullable
    default <M extends IModule, T> T onModule(final PublicModuleType<M> moduleType, final int index, final NFunctionNN<M, T> operation)
            throws NotAuthenticatedException, NotObservableException {
        Optional<M> module = getModule( moduleType, index );
        return module.isPresent()? operation.apply( module.get() ): null;
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
    @Nullable
    default <M extends IModule, T> T onModule(final PublicModuleType<M> moduleType, final Predicate<M> predicate, final NFunctionNN<M, T> operation)
            throws NotAuthenticatedException, NotObservableException {
        Optional<M> module = getModule( moduleType, predicate );
        return module.isPresent()? operation.apply( module.get() ): null;
    }

    /**
     * @return The modules that implement the behavior of this unit.
     */
    default ImmutableCollection<? extends IModule> getModules(){
        return getModulesByType().values();
    }

    @Nonnull
    IGameObjectController<? extends IGameObject> getController();

    @Override
    default Maybool canObserve(@Nonnull final GameObservable observable) {
        return onModuleElse( PublicModuleType.BASE, 0, Maybool.no(), module -> module.getController().canObserve( observable ) );
    }

    @Nonnull
    @Override
    default Stream<? extends ITile> observableTiles() {
        return onModuleElse( PublicModuleType.BASE, 0, Stream.empty(), module -> module.getController().observableTiles() );
    }

    /**
     * @return {@code true} if this object is owned by the currently authenticated player.
     */
    default boolean isOwnedByCurrentPlayer() {
        Optional<? extends IPlayer> owner = getOwner();
        return owner.isPresent() && owner.get().isCurrentPlayer();
    }
}
