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
import com.google.common.base.Supplier;
import com.google.common.collect.*;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.terminal.Terminal;
import com.lyndir.lanterna.view.*;
import com.lyndir.lanterna.view.Coordinate;
import com.lyndir.lhunath.opal.system.util.NNFunctionNN;
import com.lyndir.lhunath.opal.system.util.PredicateNN;
import com.lyndir.omicron.api.model.GameController;
import com.lyndir.omicron.api.model.GameListener;
import com.lyndir.omicron.api.model.*;
import com.lyndir.omicron.api.model.Size;
import com.lyndir.omicron.cli.OmicronCLI;
import java.util.Collection;
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
    private String         backgroundPattern;
    private boolean        hasUnits;

    public MapView(@Nonnull final LevelType levelType) {
        this.levelType = levelType;
    }

    @Override
    protected void onReady() {
        super.onReady();

        OmicronCLI.get().addGameListener( new GameListener() {
            @Override
            public void onNewTurn(final Turn currentTurn) {
                if (!hasUnits)
                    setHomeOffset();
            }
        } );
    }

    @Override
    protected void drawForeground(final Screen screen) {
        super.drawForeground( screen );

        Optional<GameController> gameController = OmicronCLI.get().getGameController();
        if (!gameController.isPresent())
            return;

        final Optional<Player> localPlayerOptional = OmicronCLI.get().getLocalPlayer();
        if (!localPlayerOptional.isPresent())
            return;
        final Player localPlayer = localPlayerOptional.get();

        // Create an empty grid.
        Size levelSize = gameController.get().getGame().getLevel( getLevelType() ).getSize();
        Table<Integer, Integer, Tile> grid = HashBasedTable.create( levelSize.getHeight(), levelSize.getWidth() );

        // Iterate observable tiles and populate the grid.
        for (final Tile tile : localPlayer.listObservableTiles()) {
            Coordinate coordinate = positionToMapCoordinate( tile.getPosition() );
            grid.put( coordinate.getY(), coordinate.getX(), tile );
        }

        // Draw grid in view.
        Box contentBox = getContentBoxOnScreen();
        com.lyndir.lanterna.view.Size contentSize = contentBox.getSize();
        for (int x = contentBox.getLeft(); x <= contentBox.getRight(); ++x)
            for (int y = contentBox.getTop(); y <= contentBox.getBottom(); ++y) {
                int v = y - contentBox.getTop() + getOffset().getY();
                int u = x - contentBox.getLeft() + getOffset().getX();
                if (!levelSize.isInBounds( new com.lyndir.omicron.api.model.Coordinate( u, v, levelSize ) ))
                    continue;

                Optional<GameObject> contents = Optional.absent();
                Terminal.Color bgColor = getBackgroundColor();

                Tile tile = grid.get( v, u );
                if (tile != null) {
                    contents = tile.getContents();
                    bgColor = levelTypeColors.get( tile.getLevel().getType() );

                    for (final ResourceType resourceType : ResourceType.values())
                        if (tile.getResourceQuantity( resourceType ) > 0)
                            bgColor = resourceTypeColors.get( resourceType );
                }

                screen.putString( x + (y % 2 == 0? 0: 1), y, contents.isPresent()? contents.get().getType().getTypeName().substring( 0, 1 ): " ",
                                  getMapColor(), bgColor, ScreenCharacterStyle.Bold );
            }

        // Draw off-screen warning labels.
        Inset offScreen = new Inset( Math.max( 0, getOffset().getY() ),
                                     Math.max( 0, levelSize.getWidth() - contentSize.getWidth() - getOffset().getX() + 1 ),
                                     Math.max( 0, levelSize.getHeight() - contentSize.getHeight() - getOffset().getY() - 1 ),
                                     Math.max( 0, getOffset().getX() ) );
        int centerX =
                contentBox.getLeft() + (levelSize.getWidth() - offScreen.getHorizontal()) / 2 - getOffset().getX() + offScreen.getLeft();
        int centerY = contentBox.getTop() + (levelSize.getHeight() - offScreen.getVertical()) / 2 - getOffset().getY() + offScreen.getTop();
        centerX = Math.min( contentBox.getRight() - 3, Math.max( contentBox.getLeft(), centerX ) );
        centerY = Math.min( contentBox.getBottom() - 1, Math.max( contentBox.getTop() + 1, centerY ) );
        if (offScreen.getTop() > 0)
            screen.putString( centerX, contentBox.getTop(), //
                              String.format( "%+d", offScreen.getTop() ), getInfoTextColor(), getInfoBackgroundColor() );
        if (offScreen.getRight() > 0) {
            String label = String.format( "%+d", offScreen.getRight() );
            screen.putString( contentBox.getRight() - label.length(), centerY, //
                              label, getInfoTextColor(), getInfoBackgroundColor() );
        }
        if (offScreen.getBottom() > 0)
            screen.putString( centerX, contentBox.getBottom(), //
                              String.format( "%+d", offScreen.getBottom() ), getInfoTextColor(), getInfoBackgroundColor() );
        if (offScreen.getLeft() > 0)
            screen.putString( contentBox.getLeft(), centerY, //
                              String.format( "%+d", offScreen.getLeft() ), getInfoTextColor(), getInfoBackgroundColor() );
    }

    @Override
    protected boolean onKey(final Key key) {
        if (key.getKind() == Key.Kind.ArrowUp && key.isCtrlPressed()) {
            setOffset( getOffset().translate( 0, -1 ) );
            return true;
        }
        if (key.getKind() == Key.Kind.ArrowDown && key.isCtrlPressed()) {
            setOffset( getOffset().translate( 0, 1 ) );
            return true;
        }
        if (key.getKind() == Key.Kind.ArrowLeft && key.isCtrlPressed()) {
            setOffset( getOffset().translate( -1, 0 ) );
            return true;
        }
        if (key.getKind() == Key.Kind.ArrowRight && key.isCtrlPressed()) {
            setOffset( getOffset().translate( 1, 0 ) );
            return true;
        }
        if (key.getKind() == Key.Kind.Home && key.isCtrlPressed()) {
            setHomeOffset();
            return true;
        }

        return false;
    }

    private void setHomeOffset() {
        Optional<Player> localPlayerOptional = OmicronCLI.get().getLocalPlayer();
        Collection<GameObject> gameObjects = ImmutableSet.of();
        if (localPlayerOptional.isPresent())
            gameObjects = localPlayerOptional.get().getObjects();

        setOffset( FluentIterable.from( gameObjects ).filter( new PredicateNN<GameObject>() {
            @Override
            public boolean apply(@Nonnull final GameObject input) {
                // Only game objects in this map's displayed level.
                return input.getLocation().getLevel().getType() == getLevelType();
            }
        } ).transform( new NNFunctionNN<GameObject, Coordinate>() {
            @Nonnull
            @Override
            public Coordinate apply(@Nonnull final GameObject input) {
                // Transform game objects into their offset from the center of the map.
                hasUnits = true;
                Box contentBox = getContentBoxOnScreen();
                return positionToMapCoordinate( input.getLocation().getPosition() ).translate( -contentBox.getSize().getWidth() / 2,
                                                                                               -contentBox.getSize().getHeight() / 2 );
            }
        } ).first().or( new Supplier<Coordinate>() {
            @Override
            public Coordinate get() {
                // If there is no game object in this level, go to the map's center.
                hasUnits = false;
                Box contentBox = getContentBoxOnScreen();
                return new Coordinate( contentBox.getSize().getWidth() / 2, contentBox.getSize().getHeight() / 2 );
            }
        } ) );
    }

    private static Coordinate positionToMapCoordinate(final com.lyndir.omicron.api.model.Coordinate position) {
        int v = position.getV();
        int u = (position.getU() + v / 2) % position.getWrapSize().getWidth();
        return new Coordinate( u, v );
    }

    @Override
    public String getBackgroundPattern() {
        return ifNotNullElse( backgroundPattern, getTheme().mapBgPattern() );
    }

    @Override
    public void setBackgroundPattern(final String backgroundPattern) {
        this.backgroundPattern = backgroundPattern;
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

    @Nonnull
    public Coordinate getOffset() {
        return offset;
    }

    public void setOffset(@Nonnull final Coordinate offset) {
        this.offset = offset;
    }
}
