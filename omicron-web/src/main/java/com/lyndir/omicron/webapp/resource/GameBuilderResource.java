package com.lyndir.omicron.webapp.resource;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import com.lyndir.lhunath.opal.math.Size;
import com.lyndir.lhunath.opal.system.util.NNFunctionNN;
import com.lyndir.omicron.api.Director;
import com.lyndir.omicron.api.core.*;
import com.lyndir.omicron.webapp.data.User;
import com.lyndir.omicron.webapp.data.service.*;
import edu.umd.cs.findbugs.annotations.*;
import java.lang.SuppressWarnings;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;


/**
 * @author lhunath, 2013-10-15
 */
@Path("/game/build")
public class GameBuilderResource {

    private final SessionManager sessionManager;
    private final StateManager   stateManager;

    @Inject
    public GameBuilderResource(final SessionManager sessionManager, final StateManager stateManager) {
        this.sessionManager = sessionManager;
        this.stateManager = stateManager;
    }

    @GET
    @Path("{gameBuilderID}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
    public Response get(@PathParam("gameBuilderID") final long gameBuilderID) {
        IGame.IBuilder gameBuilder = stateManager.getGameBuilder( gameBuilderID );

        // Response.
        return Response.ok( new GetResponse( gameBuilder ) ).build();
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
    public Response post(final GameBuilderRequest input) {
        long gameBuilderID = stateManager.addGameBuilder( Director.CORE_DIRECTOR.gameBuilder() );

        // Handle.
        input.handle( Preconditions.checkNotNull( stateManager.getGameBuilder( gameBuilderID ) ), sessionManager.getUser() );

        // Response.
        return Response.created( UriBuilder.fromPath( "{gameBuilderID}" ).build( gameBuilderID ) ).build();
    }

    @PUT
    @Path("{gameBuilderID}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
    public Response put(@PathParam("gameBuilderID") final long gameBuilderID, final GameBuilderRequest input) {
        IGame.IBuilder gameBuilder = stateManager.getGameBuilder( gameBuilderID );

        // Handle.
        input.handle( gameBuilder, sessionManager.getUser() );

        // Response.
        return Response.ok().build();
    }

    @SuppressFBWarnings({ "URF_UNREAD_FIELD" })
    public static class GetResponse {

        final Size                             levelSize;
        final Collection<HumanPlayer>          players;
        final List<PublicVictoryConditionType> victoryConditions;
        final IGame.GameResourceConfig         resourceConfig;
        final IGame.GameUnitConfig             unitConfig;

        public GetResponse(final IGame.IBuilder gameBuilder) {
            levelSize = gameBuilder.getLevelSize();
            players = Collections2.transform( gameBuilder.getPlayers(), new NNFunctionNN<IPlayer, HumanPlayer>() {
                @Nonnull
                @Override
                public HumanPlayer apply(@Nonnull final IPlayer player) {
                    return new HumanPlayer( player );
                }
            } );
            victoryConditions = gameBuilder.getVictoryConditions();
            resourceConfig = gameBuilder.getResourceConfig();
            unitConfig = gameBuilder.getUnitConfig();
        }
    }


    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    public static class GameBuilderRequest {

        Size                         levelSize;
        Integer                      totalPlayers;
        HumanPlayer                  player;
        PublicVictoryConditionType[] victoryConditions;
        IGame.GameResourceConfigs    resourceConfig;
        IGame.PublicGameUnitConfigs  unitConfig;

        public void handle(@Nonnull final IGame.IBuilder gameBuilder, @Nonnull final User user) {
            if (levelSize != null)
                gameBuilder.setLevelSize( levelSize );
            if (totalPlayers != null)
                gameBuilder.setTotalPlayers( totalPlayers );
            if (victoryConditions != null)
                for (final PublicVictoryConditionType victoryCondition : victoryConditions)
                    gameBuilder.addVictoryCondition( victoryCondition );
            if (player != null)
                gameBuilder.setPlayer( user.getPlayerKey(), player.name, player.primaryColor, player.secondaryColor );
            if (resourceConfig != null)
                gameBuilder.setResourceConfig( resourceConfig );
            if (unitConfig != null)
                gameBuilder.setUnitConfig( unitConfig.get() );
        }
    }


    public static class HumanPlayer {

        public HumanPlayer() {
        }

        public HumanPlayer(final IPlayer player) {
            name = player.getName();
            primaryColor = player.getPrimaryColor();
            secondaryColor = player.getSecondaryColor();
        }

        String name;
        Color  primaryColor;
        Color  secondaryColor;
    }
}
