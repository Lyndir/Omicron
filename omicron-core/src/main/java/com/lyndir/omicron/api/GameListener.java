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

    /**
     * A unit's base module has taken damage.
     *
     * @param baseModule The module that received the damage.
     * @param damage     The total amount of damage the module had and has incurred.
     */
    public void onBaseDamaged(final BaseModule baseModule, final ChangeInt damage) {
    }

    /**
     * The contents of a tile has changed.
     *
     * @param tile     The tile whose contents has changed.
     * @param contents The contents of the tile before and after the change.
     */
    public void onTileContents(final Tile tile, final Change<GameObject> contents) {
    }

    /**
     * The amount of resources of a specific type has changed for a tile.
     *
     * @param tile             The tile whose resources have changed.
     * @param resourceType     The type of resource on the tile that was affected.
     * @param resourceQuantity The total amount of resource of the given type available at the tile before and after the change.
     */
    public void onTileResources(final Tile tile, final ResourceType resourceType, final ChangeInt resourceQuantity) {
    }

    /**
     * The score of a player has changed.
     *
     * @param player The player whose score was affected.
     * @param score  The total score the player had and has.
     */
    public void onPlayerScore(final Player player, final ChangeInt score) {
    }

    /**
     * An object has come under the control of a player.
     *
     * The object may be a newly constructed object or may have been captured from another player.
     *
     * @param player     The player who gained control of the object.
     * @param gameObject The object that has come under the control of the player.
     */
    public void onPlayerGainedObject(final Player player, final GameObject gameObject) {
    }

    /**
     * An object is no longer under the control of a player.
     *
     * The object may have been destroyed or may have been captured by another player.
     *
     * @param player     The player who lost control of the object.
     * @param gameObject The object that is no longer under the control of the player.
     */
    public void onPlayerLostObject(final Player player, final GameObject gameObject) {
    }

    /**
     * The object has come under the control of a new player.
     *
     * The object may be a newly constructed object, may have been destroyed or may have been captured by another player.
     *
     * @param gameObject The object whose owner changed.
     * @param owner      The previous and new owner of the object.
     */
    public void onUnitCaptured(final GameObject gameObject, final Change<Player> owner) {
    }

    /**
     * The object's location has changed.
     *
     * @param gameObject The object whose location changed.
     * @param location   The previous and new location of the object.
     */
    public void onUnitMoved(final GameObject gameObject, final Change<Tile> location) {
    }

    /**
     * The object has been destroyed.
     *
     * It may have been destroyed as a result of a player's action or it may be a construction site that completed its work.
     *
     * @param gameObject The object that has been destroyed.
     */
    public void onUnitDied(final GameObject gameObject) {
    }

    /**
     * A unit's container module has received or lost an amount of stock.
     *
     * @param containerModule The module that incurred the stock change.
     * @param stock           The total amount of resources stocked in this container before and after the change.
     */
    public void onContainerStockChanged(final ContainerModule containerModule, final ChangeInt stock) {
    }

    /**
     * A unit's mobility module has performed a leveling action.
     *
     * @param mobilityModule The module that performed the action.
     * @param location       The location of the module's unit before and after the change.
     * @param remainingSpeed The total remaining speed of the module before and after the change.
     */
    public void onMobilityLeveled(final MobilityModule mobilityModule, final Change<Tile> location, final ChangeDbl remainingSpeed) {
    }

    /**
     * A unit's mobility module has performed a movement action.
     *
     * @param mobilityModule The module that performed the action.
     * @param location       The location of the module's unit before and after the change.
     * @param remainingSpeed The total remaining speed of the module before and after the change.
     */
    public void onMobilityMoved(final MobilityModule mobilityModule, final Change<Tile> location, final ChangeDbl remainingSpeed) {
    }

    /**
     * A unit's constructor module has performed a unit of work.
     *
     * @param constructorModule The module that performed the work.
     * @param remainingSpeed    The total remaining speed of the module before and after the change.
     */
    public void onConstructorWorked(final ConstructorModule constructorModule, final ChangeInt remainingSpeed) {
    }

    /**
     * A unit's constructor module has changed its work target.
     *
     * @param constructorModule The module that performed the work.
     * @param target            The target of the constructor before and after the change.
     */
    public void onConstructorTargeted(final ConstructorModule constructorModule, final Change<GameObject> target) {
    }

    /**
     * A unit of work has been performed on a construction site.
     *
     * @param constructionSite The construction site where the unit of work was completed.
     * @param moduleType       The type of module that was worked on at the site.
     * @param remainingWork    The total amount of remaining work to complete the construction of the given module type at the site before
     *                         and after the change.
     */
    public void onConstructionSiteWorked(final ConstructorModule.ConstructionSite constructionSite, final ModuleType<?> moduleType,
                                         final ChangeInt remainingWork) {
    }

    /**
     * A unit's weapon module has fired at a target.
     *
     * @param weaponModule The module that fired at the target.
     * @param target       The target location that was fired at by the module.
     * @param repeated     The total amount of repeats that this weapon has performed before and after the action.
     * @param ammunition   The total amount of ammunition that is remaining for this weapon before and after the action.
     */
    public void onWeaponFired(final WeaponModule weaponModule, final Tile target, final ChangeInt repeated, final ChangeInt ammunition) {
    }

    /**
     * The first turn of a game has begun.
     *
     * @param game The game whose first turn has begun.
     */
    public void onGameStarted(final Game game) {
    }

    /**
     * A victory condition has triggered the end of this game.
     *
     * @param game             The game whose course has ended.
     * @param victoryCondition The victory condition that caused the game to end.
     * @param victor           The player that was declared the victor of the game by the condition.
     */
    public void onGameEnded(final Game game, final VictoryConditionType victoryCondition, @Nullable final Player victor) {
    }
}
