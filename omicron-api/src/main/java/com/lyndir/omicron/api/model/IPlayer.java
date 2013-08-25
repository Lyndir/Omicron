package com.lyndir.omicron.api.model;

import com.lyndir.omicron.api.util.Maybool;
import javax.annotation.Nonnull;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public interface IPlayer extends GameObserver {

    @Nonnull
    IPlayerController getController();

    @Override
    Maybool canObserve(@Nonnull final ITile location)
            throws Security.NotAuthenticatedException;

    int getPlayerID();

    boolean hasKey(final PlayerKey playerKey);

    String getName();

    Color getPrimaryColor();

    Color getSecondaryColor();

    int getScore();
}
