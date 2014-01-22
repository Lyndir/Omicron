package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.util.Maybool;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class PublicPlayer extends MetaObject implements IPlayer {

    private final IPlayer core;

    PublicPlayer(final IPlayer core) {
        this.core = core;
    }

    @Override
    public int hashCode() {
        return core.hashCode();
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj instanceof PublicPlayer)
            return core.equals( ((PublicPlayer) obj).core );

        return core.equals( obj );
    }

    @Override
    @Nonnull
    public IPlayerController getController() {
        return core.getController();
    }

    @Authenticated
    @Override
    public Maybool canObserve(@Nonnull final ITile location) {
        return core.canObserve( location );
    }

    @Authenticated
    @Nonnull
    @Override
    public Iterable<? extends ITile> listObservableTiles() {
        return core.listObservableTiles();
    }

    @Nonnull
    @Override
    public Optional<? extends IPlayer> getOwner() {
        return core.getOwner();
    }

    @Override
    public int getPlayerID() {
        return core.getPlayerID();
    }

    @Override
    public boolean hasKey(final PlayerKey playerKey) {
        return core.hasKey( playerKey );
    }

    @Override
    public String getName() {
        return core.getName();
    }

    @Override
    public Color getPrimaryColor() {
        return core.getPrimaryColor();
    }

    @Override
    public Color getSecondaryColor() {
        return core.getSecondaryColor();
    }

    @Override
    public int getScore() {
        return core.getScore();
    }
}
