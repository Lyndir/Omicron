package com.lyndir.omicron.api.model;

import javax.annotation.Nonnull;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public interface IPlayer extends GameObserver {

    @Nonnull
    IPlayerController getController();

    int getPlayerID();

    boolean hasKey(PlayerKey playerKey);

    String getName();

    Color getPrimaryColor();

    Color getSecondaryColor();

    int getScore();
}
