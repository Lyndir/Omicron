package com.lyndir.omicron.api.model;

import static com.lyndir.omicron.api.model.Security.*;

import com.google.common.collect.ImmutableSet;


public class PublicWeaponModule extends PublicModule implements IWeaponModule {

    private final IWeaponModule core;

    protected PublicWeaponModule(final IWeaponModule core) {
        super( core );

        this.core = core;
    }

    @Override
    public int getFirePower()
            throws NotAuthenticatedException, NotObservableException {
        assertObservable();

        return core.getFirePower();
    }

    @Override
    public int getVariance()
            throws NotAuthenticatedException, NotObservableException {
        assertObservable();

        return core.getVariance();
    }

    @Override
    public int getRange()
            throws NotAuthenticatedException, NotObservableException {
        assertObservable();

        return core.getRange();
    }

    @Override
    public int getRepeat()
            throws NotAuthenticatedException, NotObservableException {
        assertObservable();

        return core.getRepeat();
    }

    @Override
    public int getAmmunitionLoad()
            throws NotAuthenticatedException, NotObservableException {
        assertObservable();

        return core.getAmmunitionLoad();
    }

    @Override
    public ImmutableSet<LevelType> getSupportedLayers()
            throws NotAuthenticatedException, NotObservableException {
        assertObservable();

        return core.getSupportedLayers();
    }

    @Override
    public int getRepeated()
            throws NotAuthenticatedException, NotObservableException {
        assertObservable();

        return core.getRepeated();
    }

    @Override
    public int getAmmunition()
            throws NotAuthenticatedException, NotObservableException {
        assertObservable();

        return core.getAmmunition();
    }

    @Override
    public boolean fireAt(final ITile target)
            throws NotObservableException, IWeaponModule.OutOfRepeatsException, IWeaponModule.OutOfAmmunitionException,
                   IWeaponModule.OutOfRangeException, NotOwnedException, NotAuthenticatedException {

        return core.fireAt( target );
    }

    @Override
    public PublicModuleType<IWeaponModule> getType() {
        return PublicModuleType.WEAPON;
    }
}
