package com.lyndir.omicron.api;

public interface IContainerModule extends IModule {

    @Override
    default PublicModuleType<? extends IContainerModule> getType() {
        return PublicModuleType.CONTAINER;
    }

    /**
     * @return The type of resources this container is able to store.
     */
    ResourceType getResourceType();

    /**
     * @return The total amount of resources this container is able to hold.
     */
    int getCapacity();

    /**
     * @return The current stock of resources currently present in this container.
     */
    int getStock();

    default int getAvailable() {
        return Math.max( 0, getCapacity() - getStock() );
    }

    @Override
    IContainerModuleController getController();
}
