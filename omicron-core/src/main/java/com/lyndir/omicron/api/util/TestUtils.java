package com.lyndir.omicron.api.util;

import com.google.common.collect.Lists;
import com.lyndir.omicron.api.controller.Module;
import com.lyndir.omicron.api.model.*;
import java.util.List;


/**
 * @author lhunath, 2013-07-16
 */
public abstract class TestUtils {

    public static Game   staticGame;
    public static Player staticPlayer;

    static {
        init();
    }

    public static void init() {
        Game.Builder builder = Game.builder();
        staticPlayer = new Player( builder.nextPlayerID(), new PlayerKey(), "testPlayer", Color.Template.randomColor(),
                                   Color.Template.randomColor() );
        builder.setResourceConfig( Game.GameResourceConfigs.NONE );
        builder.setUnitConfig( Game.GameUnitConfigs.NONE );
        builder.getPlayers().add( staticPlayer );
        staticGame = builder.build();
    }

    public static UnitType testUnitType(final Module... modules) {
        return new UnitType() {
            @Override
            public String getTypeName() {
                return "Test Unit";
            }

            @Override
            public int getComplexity() {
                return 0;
            }

            @Override
            public List<Module> createModules() {
                return Lists.newArrayList( modules );
            }

            @Override
            public String toString() {
                return String.format( "{%s: %s}", getTypeName(), createModules() );
            }
        };
    }

    public static PlayerObject createUnit(final UnitType unitType) {
        return createUnit( unitType, 0, 0 );
    }

    public static PlayerObject createUnit(final UnitType unitType, final int u, final int v) {
        return createUnit( unitType, staticGame, staticPlayer, u, v );
    }

    public static PlayerObject createUnit(final UnitType unitType, final Game game, final Player player, final int u, final int v) {
        return new PlayerObject( unitType, player, game.getLevel( LevelType.GROUND ).getTile( u, v ).get() );
    }
}
