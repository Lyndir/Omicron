package com.lyndir.omicron.api.model;

import com.google.common.collect.ImmutableSet;


public interface IBaseModule extends IModule, GameObserver {

    @Override
    PublicModuleType<? extends IBaseModule> getType();

    int getMaxHealth()
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    int getRemainingHealth()
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    int getArmor()
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    int getViewRange()
            throws Security.NotAuthenticatedException, Security.NotObservableException;

    ImmutableSet<LevelType> getSupportedLayers()
            throws Security.NotAuthenticatedException, Security.NotObservableException;
}
