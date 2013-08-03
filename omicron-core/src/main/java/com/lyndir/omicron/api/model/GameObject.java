package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.omicron.api.controller.GameObjectController;
import com.lyndir.omicron.api.controller.Module;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public abstract class GameObject extends MetaObject implements GameObserver {

    private final UnitType                            unitType;
    private final int                                 objectID;
    private final ListMultimap<ModuleType<?>, Module> modules;
    private       Tile                                location;

    protected GameObject(final UnitType unitType, final int objectID, final Tile location) {
        this.unitType = unitType;
        this.objectID = objectID;
        this.location = location;

        ImmutableListMultimap.Builder<ModuleType<?>, Module> modulesBuilder = ImmutableListMultimap.builder();
        for (final Module module : unitType.createModules()) {
            modulesBuilder.put( module.getType(), module );
            module.setGameObject( this );
        }
        modules = modulesBuilder.build();

        location.setContents( this );
    }

    @Nonnull
    public abstract GameObjectController<? extends GameObject> getController();

    @Nullable
    @Override
    public Player getPlayer() {

        return null;
    }

    @Override
    @SuppressWarnings("ParameterHidesMemberVariable")
    public boolean canObserve(@Nonnull final Player currentPlayer, @Nonnull final Tile location) {

        return getController().canObserve( currentPlayer, location );
    }

    @Nonnull
    @Override
    public Iterable<Tile> listObservableTiles(@Nonnull final Player currentPlayer) {

        return getController().listObservableTiles( currentPlayer );
    }

    public int getObjectID() {

        return objectID;
    }

    public Tile getLocation() {

        return location;
    }

    public void setLocation(final Tile location) {

        this.location = location;
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

    public ImmutableCollection<Module> listModules() {

        return ImmutableList.copyOf( modules.values() );
    }
}
