package com.lyndir.omicron.cli;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;

import com.google.common.collect.ImmutableList;
import com.lyndir.omicron.api.controller.GameController;
import com.lyndir.omicron.api.model.GameObject;
import com.lyndir.omicron.api.model.Player;
import com.lyndir.omicron.api.view.PlayerGameInfo;
import java.util.*;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "list", abbr = "ls", desc = "Enumerate certain types of game objects.")
public class ListCommand extends Command {

    @SubCommand(abbr = "p", desc = "Enumerate all players in the game.")
    public void players(final OmicronCLI omicron, final Iterator<String> tokens) {

        final GameController gameController = omicron.getGameController();
        if (gameController == null) {
            err( "No game is running.  Create one with the 'create' command." );
            return;
        }

        List<PlayerGameInfo> playerGameInfos = new LinkedList<>( gameController.listPlayerGameInfo( omicron.getLocalPlayer() ) );
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

    @SubCommand(abbr = "o", desc = "Enumerate all types of game objects the player can detect.")
    public void objects(final OmicronCLI omicron, final Iterator<String> tokens) {

        final GameController gameController = omicron.getGameController();
        if (gameController == null) {
            err( "No game is running.  Create one with the 'create' command." );
            return;
        }

        ImmutableList.Builder<GameObject> gameObjectBuilder = ImmutableList.builder();
        for (final Player player : gameController.listPlayers())
            gameObjectBuilder.addAll( player.getController().iterateObservableObjects( omicron.getLocalPlayer() ) );

        inf( "%5s | %20s | (%7s: %3s, %3s) | %s", "ID", "player", "type", "u", "v", "type" );
        for (final GameObject gameObject : gameObjectBuilder.build())
            inf( "%5s | %20s | (%7s: %3d, %3d) | %s", //
                 gameObject.getObjectID(), ifNotNullElse( Player.class, gameObject.getPlayer(), "-" ).getName(),
                 gameObject.getLocation().getLevel().getType().getName(), gameObject.getLocation().getPosition().getU(),
                 gameObject.getLocation().getPosition().getV(), gameObject.getTypeName() );
    }
}
