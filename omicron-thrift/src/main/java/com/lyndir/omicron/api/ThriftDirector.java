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

import com.lyndir.lhunath.opal.math.Size;
import com.lyndir.omicron.api.Director;
import com.lyndir.omicron.api.GameListener;
import com.lyndir.omicron.api.core.Color;
import com.lyndir.omicron.api.core.*;
import java.util.Collection;
import java.util.List;


/**
 * @author lhunath, 2014-08-07
 */
public class ThriftDirector implements Director {

    @Override
    public IGame.IBuilder gameBuilder() {

        return new IGame.IBuilder() {
            @Override
            public IGame build()
                    throws Security.NotAuthenticatedException {
                // TODO
                return null;
            }

            @Override
            public Size getLevelSize() {
                // TODO
                return null;
            }

            @Override
            public IGame.IBuilder setLevelSize(final Size levelSize) {
                // TODO
                return null;
            }

            @Override
            public Collection<IPlayer> getPlayers() {
                // TODO
                return null;
            }

            @Override
            public IGame.IBuilder setPlayer(final PlayerKey playerKey, final String name, final Color primaryColor,
                                            final Color secondaryColor) {
                // TODO
                return null;
            }

            @Override
            public IGame.IBuilder addPlayer(final IPlayer player) {
                // TODO
                return null;
            }

            @Override
            public List<PublicVictoryConditionType> getVictoryConditions() {
                // TODO
                return null;
            }

            @Override
            public IGame.IBuilder addVictoryCondition(final PublicVictoryConditionType victoryCondition) {
                // TODO
                return null;
            }

            @Override
            public Integer getTotalPlayers() {
                // TODO
                return null;
            }

            @Override
            public IGame.IBuilder setTotalPlayers(final Integer totalPlayers) {
                // TODO
                return null;
            }

            @Override
            public IGame.IBuilder addGameListener(final GameListener gameListener) {
                // TODO
                return null;
            }

            @Override
            public IGame.GameResourceConfig getResourceConfig() {
                // TODO
                return null;
            }

            @Override
            public IGame.IBuilder setResourceConfig(final IGame.GameResourceConfig resourceConfig) {
                // TODO
                return null;
            }

            @Override
            public IGame.GameUnitConfig getUnitConfig() {
                // TODO
                return null;
            }

            @Override
            public IGame.IBuilder setUnitConfig(final IGame.GameUnitConfig unitConfig) {
                // TODO
                return null;
            }

            @Override
            public int nextPlayerID() {
                // TODO
                return 0;
            }
        };
    }
}
