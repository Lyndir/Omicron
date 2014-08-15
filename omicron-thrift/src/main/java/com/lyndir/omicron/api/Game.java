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
import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;
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
        return new Level(
                thrift().getLevels().stream().filter( level -> level.getType().ordinal() == levelType.ordinal() ).findFirst().get() );
    }

    @Override
    public Deque<Turn> getTurns() {
        return thrift().getTurns().stream().map( this::cast ).collect( Collectors.toCollection( LinkedList::new ) );
    }

    @Override
    public ImmutableList<? extends ILevel> getLevels() {
        return FluentIterable.from( thrift().getLevels() ).transform( Level::new ).toList();
    }

    @Override
    public ImmutableList<Player> getPlayers() {
        return FluentIterable.from( thrift().getPlayers() ).transform( Player::new ).toList();
    }

    @Override
    public ImmutableSet<? extends IPlayer> getReadyPlayers() {
        return ImmutableSet.copyOf( thrift().getReadyPlayers().stream().map( Player::new ).iterator() );
    }

    @Override
    public boolean isRunning() {
        return thrift().isRunning();
    }

    @Override
    public Size getLevelSize() {
        return cast( thrift().getLevelSize() );
    }
}
