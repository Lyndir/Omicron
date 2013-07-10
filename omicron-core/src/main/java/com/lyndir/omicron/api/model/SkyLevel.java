package com.lyndir.omicron.api.model;

/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class SkyLevel extends Level {

    public SkyLevel(final Size levelSize, final Game game) {

        super( levelSize, LevelType.SKY, game );
    }
}
