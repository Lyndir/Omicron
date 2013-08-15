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

package com.lyndir.omicron.api;

import com.lyndir.omicron.api.model.*;
import javax.annotation.Nullable;


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

    public void onBaseDamaged(final BaseModule baseModule, final ChangeInt damage) {
    }

    public void onTileContents(final Tile tile, final Change<GameObject> contents) {
    }

    public void onTileResources(final Tile tile, final ResourceType resourceType, final ChangeInt resourceQuantity) {
    }

    public void onPlayerScore(final Player player, final ChangeInt score) {
    }

    public void onPlayerGainedObject(final Player player, final GameObject gameObject) {
    }

    public void onPlayerLostObject(final Player player, final GameObject gameObject) {
    }

    public void onUnitCaptured(final GameObject gameObject, final Change<Player> owner) {
    }

    public void onUnitMoved(final GameObject gameObject, final Change<Tile> location) {
    }

    public void onUnitDied(final GameObject gameObject) {
    }

    public void onContainerStockChanged(final ContainerModule containerModule, final ChangeInt stock) {
    }

    public void onMobilityLeveled(final MobilityModule mobilityModule, final Tile location, final ChangeDbl remainingSpeed) {
    }

    public void onMobilityMoved(final MobilityModule mobilityModule, final Tile location, final ChangeDbl remainingSpeed) {
    }

    public void onConstructorWorked(final ConstructorModule constructorModule, final ChangeInt remainingSpeed) {
    }

    public void onConstructorTargeted(final ConstructorModule constructorModule, final Change<GameObject> target) {
    }

    public void onConstructionSiteWorked(final ConstructorModule.ConstructionSite constructionSite, final ModuleType<?> moduleType,
                                         final ChangeInt remainingWork) {
    }

    public void onWeaponFired(final GameObject gameObject, final Tile target, final ChangeInt repeated, final ChangeInt ammunition) {
    }

    public void onGameStarted(final Game game) {
    }

    public void onGameEnded(final Game game, @Nullable final Player victor) {
    }
}
