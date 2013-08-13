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

package com.lyndir.omicron.api.model;

/**
 * @author lhunath, 2013-07-25
 */
@SuppressWarnings("UnusedParameters")
public abstract class GameListener {

    /**
     * Called when the given player has ended his turn.
     *
     * @param readyPlayer The player who's just ended his turn.
     */
    public void onPlayerReady(final Player readyPlayer) {
    }

    /**
     * Called when a new turn has begun in the game.
     *
     * @param currentTurn The new turn that has just commenced.
     */
    public void onNewTurn(final Turn currentTurn) {
    }

    /**
     * Called when a module's parameters have changed.
     *
     * @param module The module whose parameters have changed.
     */
    public void onChange(final Module module) {
    }

    /**
     * Called when an object's parameters have changed.
     *
     * @param object The object whose parameters have changed.
     */
    public void onChange(final GameObject object) {
    }

    /**
     * Called when a tile's parameters have changed.
     *
     * @param tile The tile whose parameters have changed.
     */
    public void onChange(final Tile tile) {
    }

    /**
     * Called when a player's parameters have changed.
     *
     * @param player The player whose parameters have changed.
     */
    public void onChange(final Player player) {
    }
}
