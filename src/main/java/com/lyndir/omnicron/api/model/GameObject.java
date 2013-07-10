package com.lyndir.omnicron.api.model;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.omnicron.api.controller.GameObjectController;
import com.lyndir.omnicron.api.controller.Module;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public abstract class GameObject extends MetaObject implements GameObserver {

    private final String                              typeName;
    private final int                                 objectID;
    private final ImmutableClassToInstanceMap<Module> modules;
    private       Tile                                location;

    protected GameObject(final String typeName, final int objectID, final Tile location, final Module... modules) {

        this.typeName = typeName;
        this.objectID = objectID;
        this.location = location;

        ImmutableClassToInstanceMap.Builder<Module> modulesBuilder = ImmutableClassToInstanceMap.builder();
        for (final Module module : modules) {
            //noinspection unchecked
            modulesBuilder.put( (Class<Module>) module.getClass(), module );
            module.setGameObject( this );
        }
        this.modules = modulesBuilder.build();

        location.setContents( this );
    }

    @NotNull
    public abstract GameObjectController<? extends GameObject> getController();

    @Nullable
    @Override
    public Player getPlayer() {

        return null;
    }

    @Override
    @SuppressWarnings("ParameterHidesMemberVariable")
    public boolean canObserve(@NotNull final Player currentPlayer, @NotNull final Tile location) {

        return getController().canObserve( currentPlayer, location );
    }

    @NotNull
    @Override
    public Iterable<Tile> listObservableTiles(@NotNull final Player currentPlayer) {

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

    public String getTypeName() {

        return typeName;
    }

    public <M extends Module> Optional<M> getModule(final Class<M> moduleType) {

        return Optional.fromNullable( modules.getInstance( moduleType ) );
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
    public <M extends Module> M onModuleElse(final Class<M> moduleType, @Nullable final Object elseValue) {

        return ObjectUtils.ifNotNullElse( moduleType, getModule( moduleType ).orNull(), elseValue );
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
    public <M extends Module> M onModule(final Class<M> moduleType) {

        return onModuleElse( moduleType, null );
    }

    public ImmutableCollection<Module> listModules() {

        return ImmutableList.copyOf( modules.values() );
    }
}
