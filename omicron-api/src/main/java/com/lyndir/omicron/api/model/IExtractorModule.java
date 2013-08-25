package com.lyndir.omicron.api.model;

public interface IExtractorModule extends IModule {

    @Override
    PublicModuleType<? extends IExtractorModule> getType();

    ResourceType getResourceType()
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    int getSpeed()
            throws Security.NotAuthenticatedException, Security.NotObservableException;
}
