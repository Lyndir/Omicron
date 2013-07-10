package com.lyndir.omicron.api.controller;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.lyndir.omicron.api.model.*;
import com.lyndir.omicron.api.view.PlayerGameInfo;
import java.util.Collection;
import java.util.Set;


public class GameController {

    private final Game game;

    public GameController(final Game game) {

        this.game = game;
    }

    /**
     * Retrieve information on a given player.
     *
     * @param currentPlayer The player requesting the information.
     * @param player        The player whose information is being requested.
     *
     * @return Information visible to the current player about the given player.
     */
    public PlayerGameInfo getPlayerGameInfo(final Player currentPlayer, final Player player) {

        if (player.listObservableTiles( currentPlayer ).iterator().hasNext())
            return PlayerGameInfo.discovered( player, player.getScore() );

        return PlayerGameInfo.undiscovered( player );
    }

    public Collection<PlayerGameInfo> listPlayerGameInfo(final Player currentPlayer) {

        return Collections2.transform( game.getPlayers(), new Function<Player, PlayerGameInfo>() {
            @Override
            public PlayerGameInfo apply(final Player input) {

                return getPlayerGameInfo( currentPlayer, input );
            }
        } );
    }

    public Iterable<Player> listPlayers() {

        return game.getPlayers();
    }

    /**
     * Indicate that the current player is ready with his turn.
     *
     * @param currentPlayer The current player.
     *
     * @return true if this action has caused a new turn to begin.
     */
    public boolean setReady(final Player currentPlayer) {

        game.getReadyPlayers().add( currentPlayer );

        if (game.getReadyPlayers().containsAll( game.getPlayers() )) {
            game.getReadyPlayers().clear();
            newTurn();
            return true;
        }

        return false;
    }

    public void newTurn() {

        for (final Player player : game.getPlayers())
            player.getController().newTurn( this );
    }

    public ImmutableList<Level> listLevels() {

        return game.listLevels();
    }

    public Set<Player> listReadyPlayers() {

        return game.getReadyPlayers();
    }
}
