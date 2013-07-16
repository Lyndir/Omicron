package com.lyndir.omicron.api.model;

/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class GroundLevel extends Level {

    public GroundLevel(final Size levelSize, final Game game) {

        super( levelSize, LevelType.GROUND, game );
    }
}