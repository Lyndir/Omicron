package com.lyndir.omicron.api.model;

import com.google.common.collect.*;
import com.lyndir.omicron.api.GameListener;
import com.lyndir.omicron.api.view.PlayerGameInfo;


public interface IGameController {

    IGame getGame();

    void addGameListener(GameListener gameListener)
            throws Security.NotAuthenticatedException;

    /**
     * Retrieve information on a given player.
     *
     * @param player The player whose information is being requested.
     *
     * @return Information visible to the current player about the given player.
     */
    PlayerGameInfo getPlayerGameInfo(IPlayer player)
            throws Security.NotAuthenticatedException;

    ImmutableCollection<PlayerGameInfo> listPlayerGameInfo()
            throws Security.NotAuthenticatedException;

    Iterable<? extends IPlayer> listPlayers();

    /**
     * Indicate that the current player is ready with his turn.
     *
     * @return true if this action has caused a new turn to begin.
     */
    boolean setReady()
            throws Security.NotAuthenticatedException;

    ImmutableList<? extends ILevel> listLevels();

    ImmutableSet<? extends IPlayer> listReadyPlayers();
}
