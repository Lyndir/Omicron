package com.lyndir.omicron.webapp.data.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.lyndir.omicron.api.model.IGame;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;


/**
 * @author lhunath, 1/28/2014
 */
public class StateManager {

    private static final Random RANDOM = new SecureRandom();

    private static final Map<Long, IGame>               games        = Maps.newConcurrentMap();
    private static final Map<Long, IGame.IBuilder>      gameBuilders = Maps.newConcurrentMap();
    private static final Table<Map<Long, ?>, Long, URI> redirections = HashBasedTable.create();

    @Nonnull
    public IGame getGame(final long gameID) {
        return Preconditions.checkNotNull( findGame( gameID ), "Game not found: %s", gameID );
    }

    @Nullable
    public IGame findGame(final long gameID) {
        return get( games, gameID );
    }

    public long addGame(@Nonnull final IGame game) {
        return add( games, game );
    }

    @Nonnull
    public IGame.IBuilder getGameBuilder(final long gameBuilderID) {
        return Preconditions.checkNotNull( findGameBuilder( gameBuilderID ), "Game builder not found: %s", gameBuilderID );
    }

    @Nullable
    public IGame.IBuilder findGameBuilder(final long gameBuilderID) {
        return get( gameBuilders, gameBuilderID );
    }

    public long addGameBuilder(@Nonnull final IGame.IBuilder gameBuilder) {
        return add( gameBuilders, gameBuilder );
    }

    public void dropAndRedirectGameBuilder(final long gameBuilderID, final URI destination) {
        redirections.put( gameBuilders, gameBuilderID, destination );
        gameBuilders.remove( gameBuilderID );
    }

    private static <T> T get(final Map<Long, T> map, final long key) {
        URI redirection = redirections.get( map, key );
        if (redirection != null)
            throw new WebApplicationException( Response.temporaryRedirect( redirection ).build() );

        return map.get( key );
    }

    private static <T> long add(final Map<Long, T> map, final T value) {
        long id;
        synchronized (map) {
            do {
                id = RANDOM.nextInt( Integer.MAX_VALUE );
            }
            while (map.containsKey( id ));
            map.put( id, value );
        }

        return id;
    }
}
