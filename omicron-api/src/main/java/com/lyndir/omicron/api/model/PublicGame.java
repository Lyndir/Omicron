package com.lyndir.omicron.api.model;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.math.Size;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.omicron.api.GameListener;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@SuppressWarnings("ParameterHidesMemberVariable") // IDEA doesn't understand setters that return this.
public class PublicGame extends MetaObject implements IGame {

    static final Logger logger = Logger.get( PublicGame.class );

    private final IGame core;

    PublicGame(final IGame core) {

        this.core = core;
    }

    @Override
    public int hashCode() {
        return core.hashCode();
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj instanceof PublicGame)
            return core.equals( ((PublicGame) obj).core );

        return core.equals( obj );
    }

    @Override
    public IGameController getController() {
        return core.getController();
    }

    @Override
    public ILevel getLevel(final LevelType levelType) {
        return core.getLevel( levelType );
    }

    @Override
    public Turn getCurrentTurn() {
        return core.getCurrentTurn();
    }

    @Override
    public ImmutableList<? extends ILevel> listLevels() {
        return core.listLevels();
    }

    @Override
    public ImmutableList<? extends IPlayer> getPlayers() {
        return core.getPlayers();
    }

    @Override
    public boolean isRunning() {
        return core.isRunning();
    }

    @Override
    public Size getLevelSize() {
        return core.getLevelSize();
    }

    public static class Builder implements IBuilder {

        private final IBuilder core;

        Builder(final IBuilder core) {
            this.core = core;
        }

        @Override
        public IGame build() {
            return core.build();
        }

        @Override
        public Size getLevelSize() {
            return core.getLevelSize();
        }

        @Override
        public IBuilder setLevelSize(final Size levelSize) {
            return core.setLevelSize( levelSize );
        }

        @Override
        public Collection<IPlayer> getPlayers() {
            return core.getPlayers();
        }

        @Override
        public IBuilder addPlayer(final IPlayer player) {
            return core.addPlayer( player );
        }

        @Override
        public IBuilder setPlayer(final PlayerKey playerKey, final String name, final Color primaryColor, final Color secondaryColor) {
            return core.setPlayer( playerKey, name, primaryColor, secondaryColor );
        }

        @Override
        public List<PublicVictoryConditionType> getVictoryConditions() {
            return core.getVictoryConditions();
        }

        @Override
        public IBuilder addVictoryCondition(final PublicVictoryConditionType victoryCondition) {
            return core.addVictoryCondition( victoryCondition );
        }

        @Override
        public Integer getTotalPlayers() {
            return core.getTotalPlayers();
        }

        @Override
        public IBuilder setTotalPlayers(final Integer totalPlayers) {
            return core.setTotalPlayers( totalPlayers );
        }

        @Override
        public IBuilder addGameListener(final GameListener gameListener) {
            return core.addGameListener( gameListener );
        }

        @Override
        public GameResourceConfig getResourceConfig() {
            return core.getResourceConfig();
        }

        @Override
        public IBuilder setResourceConfig(final GameResourceConfig resourceConfig) {
            return core.setResourceConfig( resourceConfig );
        }

        @Override
        public GameUnitConfig getUnitConfig() {
            return core.getUnitConfig();
        }

        @Override
        public IBuilder setUnitConfig(final GameUnitConfig unitConfig) {
            return core.setUnitConfig( unitConfig );
        }

        @Override
        public int nextPlayerID() {
            return core.nextPlayerID();
        }
    }
}
