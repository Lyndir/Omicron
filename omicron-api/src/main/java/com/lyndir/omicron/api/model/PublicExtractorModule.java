package com.lyndir.omicron.api.model;

public class PublicExtractorModule extends PublicModule implements IExtractorModule {

    private final IExtractorModule core;

    protected PublicExtractorModule(final IExtractorModule core) {
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
    public int getSpeed()
            throws Security.NotAuthenticatedException, Security.NotObservableException {
        assertObservable();

        return core.getSpeed();
    }

    @Override
    public PublicModuleType<? extends IExtractorModule> getType() {
        return PublicModuleType.EXTRACTOR;
    }
}
