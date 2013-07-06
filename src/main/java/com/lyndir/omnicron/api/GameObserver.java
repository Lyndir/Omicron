package com.lyndir.omnicron.api;

import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * <i>10 16, 2012</i>
 *
 * @author lhunath
 */
public interface GameObserver {

    @Nullable
    GameObserver getParent();

    @NotNull
    Player getPlayer();

    @NotNull
    Set<Tile> getObservedTiles();
}
