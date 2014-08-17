package com.lyndir.omicron.api;

import static com.lyndir.omicron.api.Security.*;

import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.error.AlreadyCheckedException;
import com.lyndir.lhunath.opal.system.error.InternalInconsistencyException;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.omicron.api.error.NotAuthenticatedException;
import com.lyndir.omicron.api.util.Maybe;
import com.lyndir.omicron.api.util.Maybool;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;


// TODO: Should this module's logic be moved to GameObjectController?
// TODO: It uniquely describes a game object and can exist only once and lots of external code assumes there is one and only one...
public class BaseModule extends Module implements IBaseModule, IBaseModuleController {

    private final int                     maxHealth;
    private final int                     armor;
    private final int                     viewRange;
    private final ImmutableSet<LevelType> supportedLayers;
    private       int                     damage;

    protected BaseModule(final ImmutableResourceCost resourceCost, final int maxHealth, final int armor, final int viewRange,
                         final Set<LevelType> supportedLayers) {
        super( resourceCost );

        this.maxHealth = maxHealth;
        this.armor = armor;
        this.viewRange = viewRange;
        this.supportedLayers = ImmutableSet.copyOf( supportedLayers );
    }

    static Builder0 createWithStandardResourceCost() {
        return createWithExtraResourceCost( ResourceCost.immutable() );
    }

    static Builder0 createWithExtraResourceCost(final ImmutableResourceCost resourceCost) {
        return new Builder0( ModuleType.BASE.getStandardCost().add( resourceCost ) );
    }

    /**
     * @see GameObservable#getLocation()
     */
    @Override
    public Maybool canObserve(@Nonnull final GameObservable observable)
            throws NotAuthenticatedException {
        if (observable.equals( getGameObject() ))
            return Maybool.yes();

        Maybe<Tile> ourLocation = getGameObject().getLocation();
        switch (ourLocation.presence()) {
            case EMPTY:
                return Maybool.no();
            case UNKNOWN:
                return Maybool.unknown();
            case PRESENT:
                ITile observableLocation = null;
                if (isGod() || getGameObject().isOwnedByCurrentPlayer()) {
                    // FIXME: HACKS!  Get rid of the instanceof.
                    if (observable instanceof ITile)
                        observableLocation = (ITile) observable;
                    else if (observable instanceof IGameObject)
                        observableLocation = observable.getLocation().get();
                    else
                        throw new InternalInconsistencyException( "This hack failed.  We seem to have an unexpected kind of observable." );
                }
                else {
                    Maybe <? extends ITile> location = observable.getLocation();
                    switch (location.presence()) {
                        case EMPTY:
                            return Maybool.no();
                        case UNKNOWN:
                            return Maybool.unknown();
                        case PRESENT:
                            observableLocation = location.get();
                    }
                }

                return Maybool.from(ourLocation.get().getPosition().distanceTo( observableLocation.getPosition() ) <= viewRange);
        }

        throw new AlreadyCheckedException( "Switch statement should handle all cases." );
    }

    @Nonnull
    @NotNull
    @Override
    public Stream<? extends ITile> observableTiles() {
        Maybe<Tile> location = getGameObject().getLocation();
        if (location.presence() != Maybe.Presence.PRESENT)
            return ImmutableList.<Tile>of().stream();

        return location.get().getLevel().getTilesByPosition().values().stream().filter( tile -> canObserve( tile ).isTrue() );
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public int getArmor() {
        return armor;
    }

    @Override
    public int getViewRange() {
        return viewRange;
    }

    @Override
    public ImmutableSet<LevelType> getSupportedLayers() {
        return supportedLayers;
    }

    @Override
    protected void onReset() {
    }

    @Override
    protected void onNewTurn() {
    }

    void addDamage(final int incomingDamage) {
        ChangeInt.From damageChange = ChangeInt.from( damage );

        damage += Math.max( 0, incomingDamage - armor );

        if (getRemainingHealth() <= 0)
            getGameObject().getController().die();

        Security.currentGame().getController().fireIfObservable( getGameObject() ) //
                .onBaseDamaged( this, damageChange.to( damage ) );
    }

    @Override
    public IBaseModuleController getController() {
        return this;
    }

    @Override
    public IBaseModule getModule() {
        return this;
    }

    @SuppressWarnings({ "ParameterHidesMemberVariable", "InnerClassFieldHidesOuterClassField" })
    static class Builder0 {

        private final ImmutableResourceCost resourceCost;

        private Builder0(final ImmutableResourceCost resourceCost) {
            this.resourceCost = resourceCost;
        }

        Builder1 maxHealth(final int maxHealth) {
            return new Builder1( maxHealth );
        }

        class Builder1 {

            private final int maxHealth;

            private Builder1(final int maxHealth) {
                this.maxHealth = maxHealth;
            }

            Builder2 armor(final int armor) {
                return new Builder2( armor );
            }

            class Builder2 {

                private final int armor;

                private Builder2(final int armor) {
                    this.armor = armor;
                }

                Builder3 viewRange(final int viewRange) {
                    return new Builder3( viewRange );
                }

                class Builder3 {

                    private final int viewRange;

                    private Builder3(final int viewRange) {
                        this.viewRange = viewRange;
                    }

                    BaseModule supportedLayers(final LevelType... supportedLayers) {
                        return supportedLayers( ImmutableSet.copyOf( supportedLayers ) );
                    }

                    BaseModule supportedLayers(final ImmutableSet<LevelType> supportedLayers) {
                        return new BaseModule( resourceCost, maxHealth, armor, viewRange, supportedLayers );
                    }
                }
            }
        }
    }
}
