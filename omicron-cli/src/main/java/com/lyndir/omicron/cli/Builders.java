package com.lyndir.omicron.cli;

import com.lyndir.omicron.api.model.Game;
import org.jetbrains.annotations.Nullable;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class Builders {

    @Nullable
    private Game.Builder gameBuilder;

    @Nullable
    public Game.Builder getGameBuilder() {

        return gameBuilder;
    }

    public void setGameBuilder(@Nullable final Game.Builder gameBuilder) {

        this.gameBuilder = gameBuilder;
    }
}
