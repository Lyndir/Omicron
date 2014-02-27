package com.lyndir.omicron.api.model;

import com.google.common.collect.ImmutableSet;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.util.Maybool;
import javax.annotation.Nonnull;


public class PublicBaseModule extends PublicModule implements IBaseModule {

    private final IBaseModule core;

    PublicBaseModule(final IBaseModule core) {
        super( core );

        this.core = core;
    }

    @Override
    @Authenticated
    public Maybool canObserve(@Nonnull final GameObservable observable)
            throws Security.NotAuthenticatedException {
        return core.canObserve( observable );
    }

    @Nonnull
    @Override
    @Authenticated
    public Iterable<? extends ITile> iterateObservableTiles() {
        return core.iterateObservableTiles();
    }

    @Override
    public int getMaxHealth()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return core.getMaxHealth();
    }

    @Override
    public int getRemainingHealth()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return core.getRemainingHealth();
    }

    @Override
    public int getArmor()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return core.getArmor();
    }

    @Override
    public int getViewRange()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return core.getViewRange();
    }

    @Override
    public ImmutableSet<LevelType> getSupportedLayers()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return core.getSupportedLayers();
    }

    @Override
    public PublicModuleType<? extends IBaseModule> getType() {
        return PublicModuleType.BASE;
    }
}
