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

import com.google.common.collect.*;
import com.lyndir.lhunath.opal.math.Size;
import com.lyndir.lhunath.opal.system.error.TodoException;
import com.lyndir.lhunath.opal.system.util.NNFunctionNN;
import com.lyndir.lhunath.opal.system.util.PredicateNN;
import com.lyndir.omicron.api.core.*;
import com.lyndir.omicron.api.thrift.ThriftObject;
import java.util.Deque;
import java.util.LinkedList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class Game extends ThriftObject<com.lyndir.omicron.api.thrift.Game> implements IGame {

    protected Game(final com.lyndir.omicron.api.thrift.Game thrift) {
        super( thrift );
    }

    @Override
    public int hashCode() {
        return System.identityHashCode( this );
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        return obj == this;
    }

    @Override
    public IGameController getController() {
        throw new TodoException();
    }

    @Override
    public ILevel getLevel(final LevelType levelType) {
        return new Level( Iterables.filter( thrift().getLevels(), new PredicateNN<com.lyndir.omicron.api.thrift.Level>() {
            @Override
            public boolean apply(@Nonnull final Level input) {
                return input.getType().ordinal() == levelType.ordinal();
            }
        } ).iterator().next() );
    }

    @Override
    public Deque<Turn> getTurns() {
        return new LinkedList<>(
                FluentIterable.from( thrift().getTurns() ).transform( new NNFunctionNN<com.lyndir.omicron.api.thrift.Turn, Turn>() {
                    @Nonnull
                    @Override
                    public Turn apply(@Nonnull final com.lyndir.omicron.api.thrift.Turn input) {
                        return new Turn( input );
                    }
                } ) );
    }

    @Override
    public ImmutableList<? extends ILevel> listLevels() {
        return FluentIterable.from( thrift().getLevels() ).transform( new NNFunctionNN<com.lyndir.omicron.api.thrift.Level, Level>() {
            @Nonnull
            @Override
            public Level apply(@Nonnull final com.lyndir.omicron.api.thrift.Level input) {
                return new Level( input );
            }
        } ).toList();
    }

    @Override
    public ImmutableList<Player> getPlayers() {
        return FluentIterable.from( thrift().getPlayers() ).transform( new NNFunctionNN<com.lyndir.omicron.api.thrift.Player, Player>() {
            @Nonnull
            @Override
            public Player apply(@Nonnull final com.lyndir.omicron.api.thrift.Player input) {
                return new Player( input );
            }
        } ).toList();
    }

    @Override
    public boolean isRunning() {
        return thrift().isRunning();
    }

    @Override
    public Size getLevelSize() {
        return new Size( thrift().getLevelSize() );
    }
}
