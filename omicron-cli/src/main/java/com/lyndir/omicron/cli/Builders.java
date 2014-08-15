package com.lyndir.omicron.cli;

import com.lyndir.omicron.api.IGame;
import javax.annotation.Nullable;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class Builders {

    @Nullable
    private IGame.IBuilder gameBuilder;

    @Nullable
    public IGame.IBuilder getGameBuilder() {

        return gameBuilder;
    }

    public void setGameBuilder(@Nullable final IGame.IBuilder gameBuilder) {

        this.gameBuilder = gameBuilder;
    }
}
