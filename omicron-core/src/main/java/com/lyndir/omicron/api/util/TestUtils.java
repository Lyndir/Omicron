package com.lyndir.omicron.api.util;

import com.lyndir.omicron.api.controller.GameObjectController;
import com.lyndir.omicron.api.controller.Module;
import com.lyndir.omicron.api.model.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * @author lhunath, 2013-07-16
 */
public abstract class TestUtils {

    public static final Game   staticGame;
    public static final Player staticPlayer;

    static {
        // Make a new game and player.
        Game.Builder builder = Game.builder();
        staticPlayer = new Player( builder.nextPlayerID(), new PlayerKey(), "testPlayer", Color.Template.randomColor(),
                                   Color.Template.randomColor() );
        builder.getPlayers().add( staticPlayer );
        staticGame = builder.build();
    }

    public static PlayerObject createObjectForModules(final Module... modules) {

        return createObjectForModules( 0, 0, modules );
    }

    public static PlayerObject createObjectForModules(final int u, final int v, final Module... modules) {

        return createObjectForModules( staticGame, staticPlayer, u, v, modules );
    }

    public static PlayerObject createObjectForModules(final Game game, final Player player, final int u, final int v,
                                                      final Module... modules) {

        // Create and assign a game object to each module.
        Tile location = game.getLevel( LevelType.GROUND ).getTile( u, v ).get();
        return new PlayerObject( "TestGameObject", player, location, modules ) {
            @Nonnull
            @Override
            public GameObjectController<? extends GameObject> getController() {

                return new GameObjectController<GameObject>( this ) {
                    @Nullable
                    @Override
                    public Player getPlayer() {
                        return null;
                    }
                };
            }
        };
    }
}
