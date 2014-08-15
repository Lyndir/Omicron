package com.lyndir.omicron.api;

public interface IExtractorModule extends IModule {

    @Override
    default PublicModuleType<? extends IExtractorModule> getType() {
        return PublicModuleType.EXTRACTOR;
    }

    /**
     * @return The type of resources this extractor is able to mine.
     */
    ResourceType getResourceType();

    /**
     * @return The speed at which the extractor is able to mine resources in a single turn.
     */
    int getSpeed();

    @Override
    IExtractorModuleController getController();
}
