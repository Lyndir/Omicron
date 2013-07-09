package com.lyndir.omnicron.api.controller;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.lyndir.omnicron.api.model.*;
import com.lyndir.omnicron.api.view.PlayerGameInfo;
import java.util.Collection;
import java.util.List;


public class GameController {

    private final Game game;

    public GameController(final Game game) {

        this.game = game;

        newTurn();
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

    public void setReady(final Player currentPlayer) {

        game.getReadyPlayers().add( currentPlayer );

        if (game.getReadyPlayers().containsAll( game.getPlayers() )) {
            newTurn();
            game.getReadyPlayers().clear();
        }
    }

    private void newTurn() {

        for (final Player player : game.getPlayers())
            player.getController().newTurn();
    }

    public ImmutableList<Level> listLevels() {

        return game.listLevels();
    }
}
