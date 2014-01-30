package com.lyndir.omicron.webapp.resource;

import static com.lyndir.lhunath.opal.system.util.StringUtils.*;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;
import com.lyndir.lhunath.opal.system.util.URLUtils;
import com.lyndir.omicron.api.model.*;
import com.lyndir.omicron.api.view.PlayerGameInfo;
import com.lyndir.omicron.webapp.data.service.SessionManager;
import com.lyndir.omicron.webapp.data.service.StateManager;
import javax.annotation.Nullable;
import javax.ws.rs.*;
import javax.ws.rs.core.*;


/**
 * @author lhunath, 2013-10-15
 */
@Path("/game")
public class GameResource {

    private final SessionManager sessionManager;
    private final StateManager   stateManager;

    @Inject
    public GameResource(final SessionManager sessionManager, final StateManager stateManager) {
        this.sessionManager = sessionManager;
        this.stateManager = stateManager;
    }

    @GET
    @Path("{gameID}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
    public Response get(@PathParam("gameID") final long gameID) {
        IGame game = stateManager.getGame( gameID );
        if (game == null)
            return Response.serverError().entity( str( "No game for ID: {0}", gameID ) ).build();

        // Response.
        return Response.ok( new GetResponse( game ) ).build();
    }

    @POST
    public Response post(@QueryParam( "gameBuilderID" ) final long gameBuilderID, final UriInfo uriInfo) {
        IGame.IBuilder gameBuilder = stateManager.getGameBuilder(gameBuilderID);
        if (gameBuilder == null)
            return Response.serverError().entity( str( "No game builder for ID: {0}", gameBuilderID ) ).build();

        long gameID = stateManager.addGame( gameBuilder.build() );
        stateManager.dropAndRedirectGameBuilder( gameBuilderID, uriInfo.getAbsolutePathBuilder().path( "./%l" ).build( gameID ) );

        // Response.
        return Response.created( UriBuilder.fromPath( "./%l" ).build( gameID ) ).build();
    }

    public static class GetResponse {

        Turn                     turn;
        Size                     levelSize;
        Iterable<PlayerGameInfo> playersInfo;

        public GetResponse(final IGame game) {
            turn = game.getCurrentTurn();
            levelSize = game.getLevelSize();
            playersInfo = FluentIterable.from( game.getPlayers() ).transform( new Function<IPlayer, PlayerGameInfo>() {
                @Nullable
                @Override
                public PlayerGameInfo apply(@Nullable final IPlayer input) {
                    return game.getController().getPlayerGameInfo( input );
                }
            } );
        }
    }
}
