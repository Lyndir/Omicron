package com.lyndir.omnicron.api;

/**
 * <i>10 16, 2012</i>
 *
 * @author lhunath
 */
public class PlayerGameInfo {

    private final Player player;
    private final boolean discovered;
    private final int score;

    public PlayerGameInfo(final Player player, final boolean discovered, final int score) {

        this.player = player;
        this.discovered = discovered;
        this.score = score;
    }

    public Player getPlayer() {

        return player;
    }

    public boolean isDiscovered() {

        return discovered;
    }

    public int getScore() {

        return score;
    }

    public static PlayerGameInfo discovered(final Player player, final Integer score) {

        return new PlayerGameInfo( player, true, score );
    }

    public static PlayerGameInfo undiscovered(final Player player) {

        return new PlayerGameInfo( player, false, 0 );
    }
}
