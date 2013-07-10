package com.lyndir.omnicron.cli;

import com.google.common.collect.*;
import com.lyndir.omnicron.api.model.*;
import java.util.Iterator;
import java.util.List;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "end", abbr = "e", desc = "Finish an operation.")
public class EndCommand extends Command {

    @SubCommand(abbr = "t", desc = "The current turn.")
    public void turn(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        if (omnicron.getGameController().setReady( omnicron.getLocalPlayer() ))
            inf( "%s ready.  New turn started.", omnicron.getLocalPlayer().getName() );
        else
            inf( "%s ready.  Not yet ready: %s", omnicron.getLocalPlayer().getName(),
                 Sets.difference( ImmutableSet.copyOf( omnicron.getGameController().listPlayers() ),
                                  omnicron.getGameController().listReadyPlayers() ) );
    }
}
