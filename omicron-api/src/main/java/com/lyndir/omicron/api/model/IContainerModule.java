package com.lyndir.omicron.api.model;

public interface IContainerModule extends IModule {

    @Override
    PublicModuleType<? extends IContainerModule> getType();

    ResourceType getResourceType()
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    int getCapacity()
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    int getStock()
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    int getAvailable()
            throws Security.NotAuthenticatedException, Security.NotObservableException;
}
