package com.lyndir.omnicron.api.controller;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.lyndir.lhunath.opal.system.util.ConversionUtils;
import com.lyndir.omnicron.api.model.*;
import org.jetbrains.annotations.Nullable;


public class PlayerController implements GameObserverController {

    private final Player player;

    public PlayerController(final Player player) {

        this.player = player;
    }

    @Override
    public boolean canObserve(final Player currentPlayer, final Tile tile) {

        return FluentIterable.from( player.getObjects() ).anyMatch( new Predicate<GameObject>() {
            @Override
            public boolean apply(final GameObject input) {

                return input.getController().canObserve( currentPlayer, tile );
            }
        } );
    }

    public Iterable<GameObject> iterateObservableObjects(final GameObserver observer) {

        return FluentIterable.from( player.getObjects() ).filter( new Predicate<GameObject>() {
            @Override
            public boolean apply(final GameObject input) {

                return observer.getController().canObserve( observer.getPlayer(), input.getLocation() );
            }
        } );
    }

    @Nullable
    public GameObject findObject(final GameObserver observer, final int objectId) {

        GameObject object = player.getObject( objectId );
        if (object != null && !observer.getController().canObserve( observer.getPlayer(), object.getLocation() ))
            return null;

        return object;
    }

    public int newObjectID() {

        return player.nextObjectID();
    }

    public void addObject(final BaseGameObject gameObject) {

        Preconditions.checkState( gameObject.getPlayer() == player, "Cannot add object to this player: belongs to another player." );
        player.addObject(gameObject);
    }
}
