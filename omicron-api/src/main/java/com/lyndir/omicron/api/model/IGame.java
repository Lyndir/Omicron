package com.lyndir.omicron.api.model;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.math.Size;
import com.lyndir.lhunath.opal.system.util.EnumUtils;
import com.lyndir.lhunath.opal.system.util.TypeUtils;
import com.lyndir.omicron.api.GameListener;
import java.util.Collection;
import java.util.List;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@SuppressWarnings("ParameterHidesMemberVariable") // IDEA doesn't understand setters that return this.
public interface IGame {

    IGameController getController();

    ILevel getLevel(LevelType levelType);

    Turn getCurrentTurn();

    ImmutableList<? extends ILevel> listLevels();

    ImmutableList<? extends IPlayer> getPlayers();

    boolean isRunning();

    Size getLevelSize();

    interface IBuilder {

        IGame build()
                throws Security.NotAuthenticatedException;

        Size getLevelSize();

        IBuilder setLevelSize(Size levelSize);

        Collection<IPlayer> getPlayers();

        IBuilder setPlayer(PlayerKey playerKey, String name, Color primaryColor, Color secondaryColor);

        IBuilder addPlayer(IPlayer player);

        List<PublicVictoryConditionType> getVictoryConditions();

        IBuilder addVictoryCondition(PublicVictoryConditionType victoryCondition);

        Integer getTotalPlayers();

        IBuilder setTotalPlayers(Integer totalPlayers);

        IBuilder addGameListener(GameListener gameListener);

        GameResourceConfig getResourceConfig();

        IBuilder setResourceConfig(GameResourceConfig resourceConfig);

        GameUnitConfig getUnitConfig();

        IBuilder setUnitConfig(GameUnitConfig unitConfig);

        int nextPlayerID();
    }


    interface GameResourceConfig {

        int quantity(ResourceType resourceType);

        int quantityPerTile(ResourceType resourceType);

        int puddleSize(ResourceType resourceType);
    }


    interface GameUnitConfig {

        void addUnits(IGame game, IPlayer player);
    }


    enum GameResourceConfigs implements GameResourceConfig {
        NONE( 0, 0, 0 ),
        SCARCE( 1500, 20, 1 ),
        PLENTY( 5000, 30, 2 ),
        LOTS( 20000, 40, 5 ),
        EXCESSIVE( 1000000, 100, 5 );

        private final int quantity;
        private final int quantityPerTile;
        private final int puddleSize;

        GameResourceConfigs(final int quantity, final int quantityPerTile, final int puddleSize) {
            this.quantity = quantity;
            this.quantityPerTile = quantityPerTile;
            this.puddleSize = puddleSize;
        }

        @Override
        public int quantity(final ResourceType resourceType) {
            return quantity;
        }

        @Override
        public int quantityPerTile(final ResourceType resourceType) {
            return quantityPerTile;
        }

        @Override
        public int puddleSize(final ResourceType resourceType) {
            return puddleSize;
        }
    }


    enum PublicGameUnitConfigs {
        NONE,
        BASIC;

        public GameUnitConfig get() {
            return (GameUnitConfig) EnumUtils.unsafeEnumNamed(
                    TypeUtils.loadClass( "com.lyndir.omicron.api.model.Game.GameUnitConfigs" ).get(), name() );
        }
    }
}
