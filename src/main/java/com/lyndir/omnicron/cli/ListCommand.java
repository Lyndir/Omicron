package com.lyndir.omnicron.cli;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSortedSet;
import com.lyndir.omnicron.api.Player;
import com.lyndir.omnicron.api.PlayerGameInfo;
import java.util.*;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "list")
public class ListCommand extends Command {

    @SubCommand(description = "Enumerate all types of game objects the player can detect.")
    public void players(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        Set<Player> players = omnicron.getGame().getPlayers();
        Set<PlayerGameInfo> playersGameInfo = ImmutableSortedSet.copyOf( new Comparator<PlayerGameInfo>() {
            @Override
            public int compare(final PlayerGameInfo o1, final PlayerGameInfo o2) {

                return o1.getScore() > o2.getScore()? 1: o1.getScore() < o2.getScore()? -1: 0;
            }
        }, Collections2.transform( players, new Function<Player, PlayerGameInfo>() {
            @Override
            public PlayerGameInfo apply(final Player input) {

                return omnicron.getGame().getPlayerGameInfo( omnicron.getLocalPlayer(), input );
            }
        } ) );

        for (final PlayerGameInfo playerGameInfo : playersGameInfo) {
            inf( "%20s | %s%s", playerGameInfo.getScore(), playerGameInfo.getPlayer().getName(),
                 playerGameInfo.isDiscovered()? null: " <undiscovered>" );
        }
    }
}
