package com.lyndir.omnicron.cli;

import com.google.common.collect.*;
import com.lyndir.omnicron.api.controller.GameController;
import com.lyndir.omnicron.api.model.*;
import com.lyndir.omnicron.api.view.PlayerGameInfo;
import java.util.*;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "list", abbr = "ls", desc = "Enumerate certain types of game objects.")
public class ListCommand extends Command {

    @SubCommand(description = "Enumerate all players in the game.")
    public void players(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        final GameController gameController = omnicron.getGameController();
        if (gameController == null) {
            err( "No game is running.  Create one with the 'create' command." );
            return;
        }

        List<PlayerGameInfo> playerGameInfos = new LinkedList<>( gameController.listPlayerGameInfo( omnicron.getLocalPlayer() ) );
        Collections.sort( playerGameInfos, new Comparator<PlayerGameInfo>() {
            @Override
            public int compare(final PlayerGameInfo o1, final PlayerGameInfo o2) {

                return o1.getScore() > o2.getScore()? 1: o1.getScore() < o2.getScore()? -1: 0;
            }
        } );

        inf( "%20s | %s", "score", "name" );
        for (final PlayerGameInfo playerGameInfo : playerGameInfos)
            inf( "%20s | %s%s", playerGameInfo.getScore(), playerGameInfo.getPlayer().getName(),
                 playerGameInfo.isDiscovered()? "": " <undiscovered>" );
    }

    @SubCommand(description = "Enumerate all types of game objects the player can detect.")
    public void objects(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        final GameController gameController = omnicron.getGameController();
        if (gameController == null) {
            err( "No game is running.  Create one with the 'create' command." );
            return;
        }

        ImmutableList.Builder<GameObject> gameObjectBuilder = ImmutableList.builder();
        for (final Player player : gameController.listPlayers())
            gameObjectBuilder.addAll( player.getController().iterateObservableObjects( omnicron.getLocalPlayer() ) );

        inf( "%5s | %20s | (%7s: %3s, %3s) | %s", "ID", "player", "type", "u", "v", "type" );
        for (final GameObject gameObject : gameObjectBuilder.build())
            inf( "%5s | %20s | (%7s: %3d, %3d) | %s", gameObject.getObjectID(), gameObject.getPlayer().getName(),
                 gameObject.getLocation().getLevel().getName(), gameObject.getLocation().getPosition().getU(),
                 gameObject.getLocation().getPosition().getV(), gameObject.getTypeName() );
    }
}
