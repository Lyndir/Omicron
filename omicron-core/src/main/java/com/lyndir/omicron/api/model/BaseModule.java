package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.lyndir.lhunath.opal.system.util.PredicateNN;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.ChangeInt;
import javax.annotation.Nonnull;


// TODO: Should this module's logic be moved to GameObjectController?
// TODO: It uniquely describes a game object and can exist only once and lots of external code assumes there is one and only one...
public class BaseModule extends Module implements GameObserver {

    private final int                     maxHealth;
    private final int                     armor;
    private final int                     viewRange;
    private final ImmutableSet<LevelType> supportedLayers;
    private       int                     damage;

    protected BaseModule(final ImmutableResourceCost resourceCost, final int maxHealth, final int armor, final int viewRange,
                         final ImmutableSet<LevelType> supportedLayers) {
        super( resourceCost );

        this.maxHealth = maxHealth;
        this.armor = armor;
        this.viewRange = viewRange;
        this.supportedLayers = supportedLayers;
    }

    static Builder0 createWithStandardResourceCost() {
        return createWithExtraResourceCost( ResourceCost.immutable() );
    }

    static Builder0 createWithExtraResourceCost(final ImmutableResourceCost resourceCost) {
        return new Builder0( ModuleType.BASE.getStandardCost().add( resourceCost ) );
    }

    @Override
    @Authenticated
    public boolean canObserve(@Nonnull final Tile location) {
        boolean canView = getGameObject().getLocation().getPosition().distanceTo( location.getPosition() ) <= viewRange;
        if (!canView || getGameObject().isOwnedByCurrentPlayer())
            return canView;

        // Game object not owned by current player, check if player can see location.
        return Security.isAuthenticated() && Security.getCurrentPlayer().canObserve( location );
    }

    @Nonnull
    @Override
    @Authenticated
    public Iterable<Tile> listObservableTiles() {
        return FluentIterable.from( getGameObject().getLocation().getLevel().getTiles().values() ).filter( new Predicate<Tile>() {
            @Override
            public boolean apply(final Tile input) {
                return canObserve( input );
            }
        } );
    }

    @Nonnull
    @Override
    public Optional<Player> getOwner() {
        return getGameObject().getOwner();
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getRemainingHealth() {
        return Math.max( 0, maxHealth - damage );
    }

    public int getArmor() {
        return armor;
    }

    public int getViewRange() {
        return viewRange;
    }

    public ImmutableSet<LevelType> getSupportedLayers() {
        return supportedLayers;
    }

    @Override
    protected void onReset() {
    }

    @Override
    protected void onNewTurn() {
    }

    @Override
    public ModuleType<?> getType() {
        return ModuleType.BASE;
    }

    void addDamage(final int incomingDamage) {
        ChangeInt.From damageChange = ChangeInt.from( damage );

        damage += Math.max( 0, incomingDamage - armor );

        if (getRemainingHealth() <= 0)
            getGameObject().getController().die();

        getGameObject().getGame().getController().fireFor( new PredicateNN<Player>() {
            @Override
            public boolean apply(@Nonnull final Player input) {
                return input.canObserve( getGameObject().getLocation() );
            }
        } ).onBaseDamaged( this, damageChange.to( damage ) );
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
                        return supportedLayers( ImmutableSet.<LevelType>copyOf( supportedLayers ) );
                    }

                    BaseModule supportedLayers(final ImmutableSet<LevelType> supportedLayers) {
                        return new BaseModule( resourceCost, maxHealth, armor, viewRange, supportedLayers );
                    }
                }
            }
        }
    }
}
