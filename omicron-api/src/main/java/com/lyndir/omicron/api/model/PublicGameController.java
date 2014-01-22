package com.lyndir.omicron.api.model;

import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.GameListener;
import com.lyndir.omicron.api.view.PlayerGameInfo;
import javax.annotation.Nullable;


public class PublicGameController extends MetaObject implements IGameController {

    private final IGameController core;

    PublicGameController(final IGameController core) {
        this.core = core;
    }

    @Override
    public int hashCode() {
        return core.hashCode();
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj instanceof PublicGameController)
            return core.equals( ((PublicGameController) obj).core );

        return core.equals( obj );
    }

    @Override
    public IGame getGame() {
        return core.getGame();
    }

    @Override
    @Authenticated
    public void addGameListener(final GameListener gameListener) {
        core.addGameListener( gameListener );
    }

    /**
     * Retrieve information on a given player.
     *
     * @param player The player whose information is being requested.
     *
     * @return Information visible to the current player about the given player.
     */
    @Override
    @Authenticated
    public PlayerGameInfo getPlayerGameInfo(final IPlayer player) {
        return core.getPlayerGameInfo( player );
    }

    @Override
    @Authenticated
    public ImmutableCollection<PlayerGameInfo> listPlayerGameInfo() {
        return core.listPlayerGameInfo();
    }

    @Override
    public Iterable<? extends IPlayer> listPlayers() {
        return core.listPlayers();
    }

    /**
     * Indicate that the current player is ready with his turn.
     *
     * @return true if this action has caused a new turn to begin.
     */
    @Override
    @Authenticated
    public boolean setReady() {
        return core.setReady();
    }

    @Override
    public ImmutableList<? extends ILevel> listLevels() {
        return core.listLevels();
    }

    @Override
    public ImmutableSet<? extends IPlayer> listReadyPlayers() {
        return core.listReadyPlayers();
    }
}
