package com.lyndir.omicron.cli;

import com.google.common.collect.*;
import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "end", abbr = "e", desc = "Finish an operation.")
public class EndCommand extends Command {

    @SubCommand(abbr = "t", desc = "The current turn.")
    public void turn(final OmicronCLI omicron, final Iterator<String> tokens) {

        if (omicron.getGameController().setReady( omicron.getLocalPlayer() ))
            inf( "%s ready.  New turn started.", omicron.getLocalPlayer().getName() );
        else
            inf( "%s ready.  Not yet ready: %s", omicron.getLocalPlayer().getName(),
                 Sets.difference( ImmutableSet.copyOf( omicron.getGameController().listPlayers() ),
                                  omicron.getGameController().listReadyPlayers() ) );
    }
}
