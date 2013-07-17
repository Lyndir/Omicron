package com.lyndir.omicron.api.controller;

import com.google.common.base.Predicate;
import com.lyndir.omicron.api.model.*;
import com.lyndir.omicron.api.util.PathUtils;
import javax.annotation.Nullable;


public class ContainerModule extends Module {

    private final ResourceType resourceType;
    private final int          capacity;
    private       int          stock;

    public ContainerModule(final ResourceType resourceType, final int capacity) {

        this.resourceType = resourceType;
        this.capacity = capacity;
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
    public void onNewTurn() {

    }
}
