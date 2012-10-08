package com.lyndir.omnicron.api;

/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@SuppressWarnings("ParameterHidesMemberVariable") // IDEA doesn't understand setters that return this.
public class Game {

    public final GroundLevel ground;
    public final SkyLevel    sky;
    public final SpaceLevel  space;

    public static class Builder {

        private Size worldSize = new Size( 100, 100 );

        public Game build() {

            return new Game( worldSize );
        }

        public Size getWorldSize() {

            return worldSize;
        }

        public Builder setWorldSize(final Size worldSize) {

            this.worldSize = worldSize;

            return this;
        }
    }

    public static Builder builder() {

        return new Builder();
    }

    public Game(final Size worldSize) {

        ground = new GroundLevel( worldSize );
        sky = new SkyLevel( worldSize );
        space = new SpaceLevel( worldSize );
    }
}
