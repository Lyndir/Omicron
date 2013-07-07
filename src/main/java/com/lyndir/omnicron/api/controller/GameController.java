package com.lyndir.omnicron.api.controller;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.lyndir.omnicron.api.model.*;
import com.lyndir.omnicron.api.view.PlayerGameInfo;
import java.util.*;


public class GameController {

    private final Game game;

    public GameController(final Game game) {

        this.game = game;
    }

    public PlayerGameInfo getPlayerGameInfo(final GameObserver observer, final Player player) {

        if (hasDiscovered( observer, player ))
            return PlayerGameInfo.discovered( player, player.getScore() );

        return PlayerGameInfo.undiscovered( player );
    }

    private boolean hasDiscovered(final GameObserver observer, final GameObserver target) {

        return FluentIterable.from( getObservedTiles( observer ) ).anyMatch( new Predicate<Tile>() {
            @Override
            public boolean apply(final Tile input) {

                return input.contains( target );
            }
        } );
    }

    private Iterable<Tile> getObservedTiles(final GameObserver observer) {

        return FluentIterable.from( game.getGround().getTiles().values() ).filter( new Predicate<Tile>() {
            @Override
            public boolean apply(final Tile input) {

                return observer.getController().canObserve( observer.getPlayer(), input);
            }
        } );
    }

    public Collection<PlayerGameInfo> listPlayerGameInfo(final GameObserver observer) {

        return Collections2.transform( game.getPlayers(), new Function<Player, PlayerGameInfo>() {
            @Override
            public PlayerGameInfo apply(final Player input) {

                return getPlayerGameInfo( observer, input );
            }
        } );
    }

    public Iterable<Player> listPlayers() {

        return game.getPlayers();
    }
}
