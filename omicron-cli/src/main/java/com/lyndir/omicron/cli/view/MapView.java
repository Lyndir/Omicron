/*
 * Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.lyndir.omicron.cli.view;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;

import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.terminal.Terminal;
import com.lyndir.omicron.api.controller.GameController;
import com.lyndir.omicron.api.model.*;
import com.lyndir.omicron.api.model.Size;
import com.lyndir.omicron.cli.OmicronCLI;
import java.util.Map;
import javax.annotation.Nonnull;


/**
 * @author lhunath, 2013-07-21
 */
public class MapView extends View {

    private static final Map<LevelType, Terminal.Color>    levelTypeColors    = //
            ImmutableMap.of( LevelType.GROUND, Terminal.Color.GREEN, LevelType.SKY, Terminal.Color.CYAN, LevelType.SPACE,
                             Terminal.Color.BLUE );
    private static final Map<ResourceType, Terminal.Color> resourceTypeColors = //
            ImmutableMap.of( ResourceType.FUEL, Terminal.Color.RED, ResourceType.METALS, Terminal.Color.WHITE, ResourceType.SILICON,
                             Terminal.Color.YELLOW, ResourceType.RARE_ELEMENTS, Terminal.Color.MAGENTA );

    @Nonnull
    private Coordinate offset = new Coordinate( 0, 0 );
    private LevelType      levelType;
    private Terminal.Color mapColor;

    public MapView(@Nonnull final LevelType levelType) {
        this.levelType = levelType;
    }

    @Override
    protected void drawForeground(final Screen screen) {
        super.drawForeground( screen );

        Optional<GameController> gameController = OmicronCLI.get().getGameController();
        if (!gameController.isPresent())
            return;

        // Create an empty grid.
        Size size = gameController.get().getGame().getLevel( getLevelType() ).getSize();
        Table<Integer, Integer, Tile> grid = HashBasedTable.create( size.getHeight(), size.getWidth() );

        // Iterate observable tiles and populate the grid.
        for (final Tile tile : OmicronCLI.get().getLocalPlayer().listObservableTiles( OmicronCLI.get().getLocalPlayer() )) {
            int v = tile.getPosition().getV();
            int u = (tile.getPosition().getU() + v / 2) % size.getWidth();
            grid.put( v, u, tile );
        }

        // Draw grid in view.
        Rectangle contentBox = getContentBoxOnScreen();
        for (int x = contentBox.getLeft(); x <= contentBox.getRight(); ++x)
            for (int y = contentBox.getTop(); y <= contentBox.getBottom(); ++y) {
                Tile tile = grid.get( y + offset.getY(), x + offset.getX() );
                if (tile == null)
                    continue;

                Optional<GameObject> contents = tile.getContents();

                Terminal.Color bgColor = levelTypeColors.get( tile.getLevel().getType() );
                for (final ResourceType resourceType : ResourceType.values())
                    if (tile.getResourceQuantity( resourceType ) > 0)
                        bgColor = resourceTypeColors.get( resourceType );

                screen.putString( x + (y % 2 == 0? 0: 1), y, contents.isPresent()? contents.get().getTypeName().substring( 0, 1 ): " ",
                                  getMapColor(), bgColor, ScreenCharacterStyle.Bold );
            }
    }

    public Terminal.Color getMapColor() {
        return ifNotNullElse( mapColor, getTheme().mapFg() );
    }

    public void setMapColor(final Terminal.Color mapColor) {
        this.mapColor = mapColor;
    }

    public LevelType getLevelType() {
        return levelType;
    }

    public void setLevelType(final LevelType levelType) {
        this.levelType = levelType;
    }
}
