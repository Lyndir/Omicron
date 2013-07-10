package com.lyndir.omicron.api.model;

/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class SpaceLevel extends Level {

    public SpaceLevel(final Size levelSize, final Game game) {

        super( levelSize, LevelType.SPACE, game );
    }
}
