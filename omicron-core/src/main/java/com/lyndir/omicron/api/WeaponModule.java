package com.lyndir.omicron.api;

import static com.lyndir.omicron.api.error.ExceptionUtils.assertState;

import com.google.common.collect.ImmutableSet;
import com.lyndir.omicron.api.error.*;
import com.lyndir.omicron.api.util.Maybe;
import java.util.Random;
import java.util.Set;


public class WeaponModule extends Module implements IWeaponModule, IWeaponModuleController {

    private static final Random RANDOM = new Random();
    private final int                     firePower;
    private final int                     variance;
    private final int                     range;
    private final int                     repeat;
    private final int                     ammunitionLoad;
    private final ImmutableSet<LevelType> supportedLayers;
    private       int                     repeated;
    private       int                     ammunition;

    protected WeaponModule(final ImmutableResourceCost resourceCost, final int firePower, final int variance, final int range,
                           final int repeat, final int ammunitionLoad, final Set<LevelType> supportedLayers) {
        super( resourceCost );

        this.firePower = firePower;
        this.variance = variance;
        this.range = range;
        this.repeat = repeat;
        this.ammunitionLoad = ammunitionLoad;
        this.supportedLayers = ImmutableSet.copyOf( supportedLayers );

        ammunition = ammunitionLoad;
    }

    static Builder0 createWithStandardResourceCost() {
        return createWithExtraResourceCost( ResourceCost.immutable() );
    }

    static Builder0 createWithExtraResourceCost(final ImmutableResourceCost resourceCost) {
        return new Builder0( ModuleType.WEAPON.getStandardCost().add( resourceCost ) );
    }

    @Override
    public int getFirePower()
            throws NotAuthenticatedException, NotObservableException {
        return firePower;
    }

    @Override
    public int getVariance()
            throws NotAuthenticatedException, NotObservableException {
        return variance;
    }

    @Override
    public int getRange()
            throws NotAuthenticatedException, NotObservableException {
        return range;
    }

    @Override
    public int getRepeat()
            throws NotAuthenticatedException, NotObservableException {
        return repeat;
    }

    @Override
    public int getAmmunitionLoad()
            throws NotAuthenticatedException, NotObservableException {
        return ammunitionLoad;
    }

    @Override
    public int getRepeated() {
        return repeated;
    }

    @Override
    public int getAmmunition() {
        return ammunition;
    }

    @Override
    public ImmutableSet<LevelType> getSupportedLayers()
            throws NotAuthenticatedException, NotObservableException {
        assertObservable();

        return ImmutableSet.copyOf( supportedLayers );
    }

    @Override
    public boolean fireAt(final ITile target)
            throws NotAuthenticatedException, NotOwnedException, NotObservableException, IWeaponModuleController.OutOfRangeException,
                   IWeaponModuleController.OutOfRepeatsException, IWeaponModuleController.OutOfAmmunitionException {
        assertOwned();
        Security.assertObservable( target );
        assertState( getGameObject().getLocation().get().getPosition().distanceTo( target.getPosition() ) <= range,
                     IWeaponModuleController.OutOfRangeException.class );
        assertState( repeated < repeat, IWeaponModuleController.OutOfRepeatsException.class );
        assertState( ammunition > 0, IWeaponModuleController.OutOfAmmunitionException.class );

        ChangeInt.From repeatedChange = ChangeInt.from( repeated );
        ChangeInt.From ammunitionChange = ChangeInt.from( ammunition );

        ++repeated;
        --ammunition;

        getGameObject().getGame()
                       .getController()
                       .fireIfObservable( getGameObject() )
                       .onWeaponFired( this, target, repeatedChange.to( repeated ), ammunitionChange.to( ammunition ) );

        Maybe<? extends IGameObject> targetGameObject = target.getContents();
        if (targetGameObject.isPresent())
            targetGameObject.get().onModule( ModuleType.BASE, 0, module -> {
                module.addDamage( firePower + RANDOM.nextInt( variance ) );
                return Void.TYPE;
            } );

        return true;
    }

    @Override
    protected void onReset() {
        repeated = 0;
    }

    @Override
    protected void onNewTurn() {
    }

    @Override
    public IWeaponModuleController getController() {
        return this;
    }

    @Override
    public IWeaponModule getModule() {
        return this;
    }

    @SuppressWarnings({ "ParameterHidesMemberVariable", "InnerClassFieldHidesOuterClassField" })
    static class Builder0 {

        private final ImmutableResourceCost resourceCost;

        private Builder0(final ImmutableResourceCost resourceCost) {
            this.resourceCost = resourceCost;
        }

        Builder1 firePower(final int firePower) {
            return new Builder1( firePower );
        }

        class Builder1 {

            private final int firePower;

            private Builder1(final int firePower) {
                this.firePower = firePower;
            }

            Builder2 armor(final int variance) {
                return new Builder2( variance );
            }

            class Builder2 {

                private final int variance;

                private Builder2(final int variance) {
                    this.variance = variance;
                }

                Builder3 range(final int range) {
                    return new Builder3( range );
                }

                class Builder3 {

                    private final int range;

                    private Builder3(final int range) {
                        this.range = range;
                    }

                    Builder4 repeat(final int repeat) {
                        return new Builder4( repeat );
                    }

                    class Builder4 {

                        private final int repeat;

                        private Builder4(final int repeat) {
                            this.repeat = repeat;
                        }

                        Builder5 ammunitionLoad(final int ammunitionLoad) {
                            return new Builder5( ammunitionLoad );
                        }

                        class Builder5 {

                            private final int ammunitionLoad;

                            private Builder5(final int ammunitionLoad) {
                                this.ammunitionLoad = ammunitionLoad;
                            }

                            WeaponModule supportedLayers(final Set<LevelType> supportedLayers) {
                                return new WeaponModule( resourceCost, firePower, variance, range, repeat, ammunitionLoad,
                                                         supportedLayers );
                            }
                        }
                    }
                }
            }
        }
    }
}
