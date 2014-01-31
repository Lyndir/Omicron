package com.lyndir.omicron.webapp.resource;

import static com.lyndir.lhunath.opal.system.util.StringUtils.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import com.lyndir.lhunath.opal.system.util.NNFunctionNN;
import com.lyndir.lhunath.opal.system.util.URLUtils;
import com.lyndir.omicron.api.model.*;
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
    public Response get(@PathParam("gameBuilderID") final long gameBuilderID)
            throws EmailAddressUnavailableException {
        IGame.IBuilder gameBuilder = stateManager.getGameBuilder( gameBuilderID );

        // Response.
        return Response.ok( new GetResponse( gameBuilder ) ).build();
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
    public Response post(final GameBuilderRequest input)
            throws EmailAddressUnavailableException {
        long gameBuilderID = stateManager.addGameBuilder( PublicGame.builder() );

        // Handle.
        input.handle( Preconditions.checkNotNull( stateManager.getGameBuilder( gameBuilderID ) ), sessionManager.getUser() );

        // Response.
        return Response.created( UriBuilder.fromPath( "{gameBuilderID}" ).build( gameBuilderID ) ).build();
    }

    @PUT
    @Path("{gameBuilderID}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
    public Response put(@PathParam("gameBuilderID") final long gameBuilderID, final GameBuilderRequest input)
            throws EmailAddressUnavailableException {
        IGame.IBuilder gameBuilder = stateManager.getGameBuilder( gameBuilderID );

        // Handle.
        input.handle( gameBuilder, sessionManager.getUser() );

        // Response.
        return Response.ok().build();
    }

    @SuppressFBWarnings({ "URF_UNREAD_FIELD" })
    public static class GetResponse {

        Size                             levelSize;
        Collection<HumanPlayer>          players;
        List<PublicVictoryConditionType> victoryConditions;
        IGame.GameResourceConfig         resourceConfig;
        IGame.GameUnitConfig             unitConfig;

        public GetResponse(final IGame.IBuilder gameBuilder) {
            levelSize = gameBuilder.getLevelSize();
            players = Collections2.transform( gameBuilder.getPlayers(), new NNFunctionNN<IPlayer, HumanPlayer>() {
                @Nonnull
                @Override
                public HumanPlayer apply(@Nonnull final IPlayer input) {
                    return new HumanPlayer( input );
                }
            } );
            victoryConditions = gameBuilder.getVictoryConditions();
            resourceConfig = gameBuilder.getResourceConfig();
            unitConfig = gameBuilder.getUnitConfig();
        }
    }


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

        public HumanPlayer(final IPlayer input) {
            name = input.getName();
            primaryColor = input.getPrimaryColor();
            secondaryColor = input.getSecondaryColor();
        }

        String name;
        Color  primaryColor;
        Color  secondaryColor;
    }
}
