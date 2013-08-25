package com.lyndir.omicron.api.model;

import static com.lyndir.omicron.api.model.Security.*;

import com.google.common.collect.ImmutableSet;
import com.lyndir.omicron.api.model.error.OmicronException;


public interface IWeaponModule extends IModule {

    @Override
    PublicModuleType<? extends IWeaponModule> getType();

    int getFirePower()
            throws NotAuthenticatedException, NotObservableException;

    int getVariance()
            throws NotAuthenticatedException, NotObservableException;

    int getRange()
            throws NotAuthenticatedException, NotObservableException;

    int getRepeat()
            throws NotAuthenticatedException, NotObservableException;

    int getAmmunitionLoad()
            throws NotAuthenticatedException, NotObservableException;

    ImmutableSet<LevelType> getSupportedLayers()
            throws NotAuthenticatedException, NotObservableException;

    int getRepeated()
            throws NotAuthenticatedException, NotObservableException;

    int getAmmunition()
            throws NotAuthenticatedException, NotObservableException;

    boolean fireAt(final ITile target)
            throws NotAuthenticatedException, NotOwnedException, NotObservableException, OutOfRangeException, OutOfRepeatsException,
                   OutOfAmmunitionException;

    class OutOfRangeException extends OmicronException {

        OutOfRangeException() {
            super( "The target is out of range for this weapon." );
        }
    }


    class OutOfRepeatsException extends OmicronException {

        OutOfRepeatsException() {
            super( "The weapon cannot repeat anymore." );
        }
    }


    class OutOfAmmunitionException extends OmicronException {

        OutOfAmmunitionException() {
            super( "The weapon is out of ammunition." );
        }
    }
}
