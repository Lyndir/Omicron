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

package com.lyndir.lanterna.view;

import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.util.Objects;
import javax.annotation.Nullable;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class Coordinate extends MetaObject {

    private final int x;
    private final int y;

    public Coordinate() {
        this( 0, 0 );
    }

    public Coordinate(final int x, final int y) {

        this.x = x;
        this.y = y;
    }

    public int getX() {

        return x;
    }

    public int getY() {

        return y;
    }

    public Coordinate translate(final int dx, final int dy) {
        return new Coordinate( x + dx, y + dy );
    }

    @Override
    public int hashCode() {

        return Objects.hash( x, y );
    }

    @Override
    public boolean equals(@Nullable final Object obj) {

        if (!(obj instanceof Coordinate))
            return false;

        Coordinate o = (Coordinate) obj;
        return x == o.x && y == o.y;
    }
}
