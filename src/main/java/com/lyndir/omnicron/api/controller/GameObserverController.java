package com.lyndir.omnicron.api.controller;

import com.lyndir.omnicron.api.model.*;
import com.lyndir.omnicron.api.view.PlayerGameInfo;


public interface GameObserverController {

    boolean canObserve(Player currentPlayer, Tile tile);
}
