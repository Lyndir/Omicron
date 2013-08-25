package com.lyndir.omicron.api.model;

public class PublicContainerModule extends PublicModule implements IContainerModule {

    private final IContainerModule core;

    protected PublicContainerModule(final IContainerModule core) {
        super( core );

        this.core = core;
    }

    @Override
    public ResourceType getResourceType()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return core.getResourceType();
    }

    @Override
    public int getCapacity()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return core.getCapacity();
    }

    @Override
    public int getStock()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return core.getStock();
    }

    @Override
    public int getAvailable()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return core.getAvailable();
    }

    @Override
    public PublicModuleType<? extends IContainerModule> getType() {
        return PublicModuleType.CONTAINER;
    }
}
