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

import com.google.common.collect.ImmutableMap;
import javax.annotation.Nonnull;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class Player extends ThriftObject<com.lyndir.omicron.api.thrift.Player> implements IPlayer {

    public Player(final com.lyndir.omicron.api.thrift.Player thrift) {
        super( thrift );
    }

    @Override
    public long getPlayerID() {
        // TODO
        return 0;
    }

    @Override
    public String getName() {
        // TODO
        return null;
    }

    @Override
    public Color getPrimaryColor() {
        // TODO
        return null;
    }

    @Override
    public Color getSecondaryColor() {
        // TODO
        return null;
    }

    @Override
    public int getScore() {
        // TODO
        return 0;
    }

    @Override
    public ImmutableMap<Long, IGameObject> getObjectsByID() {
        // TODO
        return null;
    }

    @Override
    public boolean isCurrentPlayer() {
        // TODO
        return false;
    }

    @Nonnull
    @Override
    public IPlayerController getController() {
        // TODO
        return null;
    }
}
