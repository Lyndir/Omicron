package com.lyndir.omicron.api.controller;

import com.lyndir.omicron.api.model.*;


public class ContainerModule extends Module {

    private final ResourceType resourceType;
    private final int          capacity;
    private       int          stock;

    protected ContainerModule(final ResourceCost resourceCost, final ResourceType resourceType, final int capacity) {
        super( resourceCost );
        this.resourceType = resourceType;
        this.capacity = capacity;
    }

    public static Builder0 createWithStandardResourceCost() {
        return createWithExtraResourceCost( new ResourceCost() );
    }

    public static Builder0 createWithExtraResourceCost(final ResourceCost resourceCost) {
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
    public int addStock(final int amount) {

        int newStock = Math.min( stock + amount, capacity );
        int stocked = newStock - stock;
        stock = newStock;

        return stocked;
    }

    @Override
    public void onReset() {
    }

    @Override
    public void onNewTurn() {
    }

    @Override
    public ModuleType<?> getType() {
        return ModuleType.CONTAINER;
    }

    @SuppressWarnings({ "ParameterHidesMemberVariable", "InnerClassFieldHidesOuterClassField" })
    public static class Builder0 {

        private final ResourceCost resourceCost;

        private Builder0(final ResourceCost resourceCost) {

            this.resourceCost = resourceCost;
        }

        public Builder1 resourceType(final ResourceType resourceType) {
            return new Builder1( resourceType );
        }

        public class Builder1 {

            private final ResourceType resourceType;

            private Builder1(final ResourceType resourceType) {
                this.resourceType = resourceType;
            }

            public ContainerModule capacity(final int capacity) {
                return new ContainerModule( resourceCost, resourceType, capacity );
            }
        }
    }
}
