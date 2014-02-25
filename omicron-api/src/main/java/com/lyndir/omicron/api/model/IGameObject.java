package com.lyndir.omicron.api.model;

import static com.lyndir.omicron.api.model.Security.*;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableCollection;
import com.lyndir.lhunath.opal.system.util.PredicateNN;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public interface IGameObject extends GameObserver, GameObservable {

    @Nonnull
    IGameObjectController<? extends IGameObject> getController();

    boolean isOwnedByCurrentPlayer()
            throws NotAuthenticatedException;

    int getObjectID();

    IGame getGame();

    IUnitType getType();

    /**
     * Get this object's module of the given type at the given index.
     *
     * @param moduleType The type of module to get.
     * @param index      The index of the module.
     * @param <M>        The type of the module.
     *
     * @return The module of the given type at the given index.
     */
    <M extends IModule> Optional<M> getModule(PublicModuleType<M> moduleType, int index)
            throws NotAuthenticatedException, NotObservableException;

    /**
     * Get this object's module of the given type at the given index.
     *
     * @param moduleType The type of module to get.
     * @param predicate  Return the first module of the given type for which the predicate holds true.
     * @param <M>        The type of the module.
     *
     * @return The module of the given type at the given index.
     */
    <M extends IModule> Optional<M> getModule(PublicModuleType<M> moduleType, PredicateNN<M> predicate)
            throws NotAuthenticatedException, NotObservableException;

    /**
     * Get this object's modules of the given type.
     *
     * @param moduleType The type of module to get.
     * @param <M>        The type of the module.
     *
     * @return A list of modules of the given type or an empty list if there are none.
     */
    @SuppressWarnings("unchecked")
    <M extends IModule> List<M> getModules(PublicModuleType<M> moduleType)
            throws NotAuthenticatedException, NotObservableException;

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
    <M extends IModule> M onModuleElse(PublicModuleType<M> moduleType, int index, @Nullable Object elseValue)
            throws NotAuthenticatedException, NotObservableException;

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
    <M extends IModule> M onModuleElse(PublicModuleType<M> moduleType, PredicateNN<M> predicate, @Nullable Object elseValue)
            throws NotAuthenticatedException, NotObservableException;

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
    <M extends IModule> M onModule(PublicModuleType<M> moduleType, int index)
            throws NotAuthenticatedException, NotObservableException;

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
    <M extends IModule> M onModule(PublicModuleType<M> moduleType, PredicateNN<M> predicate)
            throws NotAuthenticatedException, NotObservableException;

    ImmutableCollection<? extends IModule> listModules()
            throws NotAuthenticatedException, NotObservableException;
}
