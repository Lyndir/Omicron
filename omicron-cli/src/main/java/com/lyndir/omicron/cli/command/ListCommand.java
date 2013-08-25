package com.lyndir.omicron.cli.command;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.lyndir.omicron.api.model.*;
import com.lyndir.omicron.api.view.PlayerGameInfo;
import com.lyndir.omicron.cli.OmicronCLI;
import java.util.*;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "list", abbr = "ls", desc = "Enumerate certain types of game objects.")
public class ListCommand extends Command {

    public ListCommand(final OmicronCLI omicron) {
        super( omicron );
    }

    @SubCommand(abbr = "p", desc = "Enumerate all players in the game.")
    public void players(final Iterator<String> tokens) {

        final Optional<IGameController> gameController = getOmicron().getGameController();
        if (!gameController.isPresent()) {
            err( "No game is running.  Create one with the 'create' command." );
            return;
        }

        List<PlayerGameInfo> playerGameInfos = new LinkedList<>( gameController.get().listPlayerGameInfo() );
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
    public void objects(final Iterator<String> tokens) {

        final Optional<IGameController> gameController = getOmicron().getGameController();
        if (!gameController.isPresent()) {
            err( "No game is running.  Create one with the 'create' command." );
            return;
        }

        final Optional<IPlayer> localPlayerOptional = getOmicron().getLocalPlayer();
        if (!localPlayerOptional.isPresent()) {
            err( "No local player in the game." );
            return;
        }
        final IPlayer localPlayer = localPlayerOptional.get();

        ImmutableList.Builder<IGameObject> gameObjectBuilder = ImmutableList.builder();
        for (final IPlayer player : gameController.get().listPlayers())
            gameObjectBuilder.addAll( player.getController().iterateObservableObjects( localPlayer ) );

        inf( "%5s | %20s | (%7s: %3s, %3s) | %s", "ID", "player", "type", "u", "v", "type" );
        for (final IGameObject gameObject : gameObjectBuilder.build()) {
            ITile location = gameObject.checkLocation().get();
            inf( "%5s | %20s | (%7s: %3d, %3d) | %s", //
                 gameObject.getObjectID(), ifNotNullElse( IPlayer.class, gameObject.getOwner().orNull(), "-" ).getName(),
                 location.getLevel().getType().getName(), location.getPosition().getU(), location.getPosition().getV(),
                 gameObject.getType().getTypeName() );
        }
    }
}
