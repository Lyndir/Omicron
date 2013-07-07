package com.lyndir.omnicron.api.controller;

import com.google.common.base.Preconditions;
import com.lyndir.omnicron.api.model.*;


public class BaseController<O extends BaseGameObject> extends ObjectController<O> {

    public BaseController(final O gameObject) {

        super( gameObject );
    }

    @Override
    public boolean canObserve(final Player currentPlayer, final Tile tile) {

        return getGameObject().getLocation().getPosition().distanceTo( tile.getPosition() ) <= getGameObject().getBaseModule()
                .getViewRange();
    }

    @Override
    public void move(final Player currentPlayer, final int du, final int dv) {

        Preconditions.checkState( getGameObject().getPlayer().equals( currentPlayer ),
                                  "Cannot move object that doesn't belong to the current player." );

        super.move( currentPlayer, du, dv );
    }
}
