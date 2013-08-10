package com.lyndir.omicron.api.model;

import com.google.common.base.Preconditions;


public class ContainerModule extends Module {

    private final ResourceType resourceType;
    private final int          capacity;
    private       int          stock;

    protected ContainerModule(final ImmutableResourceCost resourceCost, final ResourceType resourceType, final int capacity) {
        super( resourceCost );
        this.resourceType = resourceType;
        this.capacity = capacity;
    }

    static Builder0 createWithStandardResourceCost() {
        return createWithExtraResourceCost( ResourceCost.immutable() );
    }

    static Builder0 createWithExtraResourceCost(final ImmutableResourceCost resourceCost) {
        return new Builder0( ModuleType.CONTAINER.getStandardCost().add( resourceCost ) );
    }

    public ResourceType getResourceType() {

        return resourceType;
    }

    public int getCapacity() {

        return capacity;
    }

    public int getStock() {

        return stock;
    }

    public int getAvailable() {

        return capacity - stock;
    }

    /**
     * Add an amount of this container's resource type in resources to the container's stock.
     *
     * @param amount The amount of resources to add.
     *
     * @return The amount of resources that has been added to the stock.  It will be a value between 0 and the given amount, depending on
     *         how much available stock this container has left.
     */
    int addStock(final int amount) {
        Preconditions.checkArgument( amount >= 0, "Amount of stock to add must be positive." );

        int newStock = Math.min( stock + amount, capacity );
        int stocked = newStock - stock;
        stock = newStock;

        return stocked;
    }

    /**
     * Deplete an amount of this container's resource type in resources from the container's stock.
     *
     * @param amount The amount of resources to remove.
     *
     * @return The amount of resources that has been removed from the stock.  It will be a value between 0 and the given amount, depending
     *         on how much available stock this container had left.
     */
    int depleteStock(final int amount) {
        Preconditions.checkArgument( amount >= 0, "Amount of stock to deplete must be positive." );

        int newStock = Math.max( stock - amount, 0 );
        int depleted = stock - newStock;
        stock = newStock;

        return depleted;
    }

    @Override
    protected void onReset() {
    }

    @Override
    protected void onNewTurn() {
    }

    @Override
    public ModuleType<?> getType() {
        return ModuleType.CONTAINER;
    }

    @SuppressWarnings({ "ParameterHidesMemberVariable", "InnerClassFieldHidesOuterClassField" })
    static class Builder0 {

        private final ImmutableResourceCost resourceCost;

        private Builder0(final ImmutableResourceCost resourceCost) {

            this.resourceCost = resourceCost;
        }

        Builder1 resourceType(final ResourceType resourceType) {
            return new Builder1( resourceType );
        }

        class Builder1 {

            private final ResourceType resourceType;

            private Builder1(final ResourceType resourceType) {
                this.resourceType = resourceType;
            }

            ContainerModule capacity(final int capacity) {
                return new ContainerModule( resourceCost, resourceType, capacity );
            }
        }
    }
}
