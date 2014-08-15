package com.lyndir.omicron.webapp.resource;

import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;
import com.lyndir.lhunath.opal.math.Size;
import com.lyndir.lhunath.opal.system.util.NNFunctionNN;
import com.lyndir.omicron.api.*;
import com.lyndir.omicron.api.view.PlayerGameInfo;
import com.lyndir.omicron.webapp.data.service.StateManager;
import edu.umd.cs.findbugs.annotations.*;
import javax.annotation.Nonnull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;


/**
 * @author lhunath, 2013-10-15
 */
@Path("/game")
public class GameResource {

    private final StateManager   stateManager;

    @Inject
    public GameResource(final StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @GET
    @Path("{gameID}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
    public Response get(@PathParam("gameID") final long gameID) {
        // Response.
        return Response.ok( new GetResponse( stateManager.getGame( gameID ) ) ).build();
    }

    @POST
    public Response post(@QueryParam( "gameBuilderID" ) final long gameBuilderID, final UriInfo uriInfo) {
        IGame.IBuilder gameBuilder = stateManager.getGameBuilder(gameBuilderID);

        // Handle.
        long gameID = stateManager.addGame( gameBuilder.build() );
        stateManager.dropAndRedirectGameBuilder( gameBuilderID, uriInfo.getAbsolutePathBuilder().path( "{gameID}" ).build( gameID ) );

        // Response.
        return Response.created( UriBuilder.fromPath( "{gameID}" ).build( gameID ) ).build();
    }

    @SuppressFBWarnings({ "URF_UNREAD_FIELD" })
    public static class GetResponse {

        final Turn                     turn;
        final Size                     levelSize;
        final Iterable<PlayerGameInfo> playersInfo;

        public GetResponse(final IGame game) {
            turn = game.getTurns();
            levelSize = game.getLevelSize();
            playersInfo = FluentIterable.from( game.getPlayers() ).transform( new NNFunctionNN<IPlayer, PlayerGameInfo>() {
                @Nonnull
                @Override
                public PlayerGameInfo apply(@Nonnull final IPlayer player) {
                    return game.getController().getPlayerGameInfo( player );
                }
            } );
        }
    }
}
