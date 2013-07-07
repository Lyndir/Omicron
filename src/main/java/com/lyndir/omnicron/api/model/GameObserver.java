package com.lyndir.omnicron.api.model;

import com.lyndir.omnicron.api.controller.GameObserverController;
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
    GameObserverController getController();
}
