package com.lyndir.omicron.api;

import com.google.common.collect.*;
import com.lyndir.omicron.api.view.PlayerGameInfo;


public interface IGameController {

    IGame getGame();

    void addGameListener(GameListener gameListener);

    /**
     * Retrieve information on a given player.
     *
     * @param player The player whose information is being requested.
     *
     * @return Information visible to the current player about the given player.
     */
    PlayerGameInfo getPlayerGameInfo(IPlayer player);

    ImmutableCollection<PlayerGameInfo> listPlayerGameInfo();

    /**
     * Indicate that the current player is ready with his turn.
     *
     * @return true if this action has caused a new turn to begin.
     */
    boolean setReady();
}
