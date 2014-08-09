package com.lyndir.omicron.cli.command;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.lyndir.omicron.api.core.*;
import com.lyndir.omicron.cli.OmicronCLI;
import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "end", abbr = "e", desc = "Finish an operation.")
public class EndCommand extends Command {

    public EndCommand(final OmicronCLI omicron) {
        super( omicron );
    }

    @SubCommand(abbr = "t", desc = "The current turn.")
    public void turn(final Iterator<String> tokens) {

        Optional<IGameController> gameController = getOmicron().getGameController();
        if (!gameController.isPresent()) {
            err( "No game is running.  Create one with the 'create' command." );
            return;
        }

        Optional<IPlayer> localPlayerOptional = getOmicron().getLocalPlayer();
        if (!localPlayerOptional.isPresent()) {
            err( "No local player in the game." );
            return;
        }

        IPlayer localPlayer = localPlayerOptional.get();
        if (gameController.get().setReady())
            inf( "%s ready.  New turn started.", localPlayer.getName() );
        else
            inf( "%s ready.  Not yet ready: %s", localPlayer.getName(),
                 Sets.difference( ImmutableSet.copyOf( gameController.get().listPlayers() ), gameController.get().listReadyPlayers() ) );
    }
}
