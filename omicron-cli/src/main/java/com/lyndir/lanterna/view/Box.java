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

/**
 * @author lhunath, 2013-07-23
 */
public class Box extends Rectangle {

    public Box() {
    }

    public Box(final int top, final int right, final int bottom, final int left) {
        super( top, right, bottom, left );
    }

    public Coordinate getOrigin() {
        return new Coordinate( getLeft(), getTop() );
    }

    public Coordinate getCenter() {
        return new Coordinate( getLeft() + getSize().getWidth() / 2, getTop() + getSize().getHeight() / 2 );
    }

    public Size getSize() {
        return new Size( getRight() - getLeft(), getBottom() - getTop() );
    }

    public Box translate(final int x, final int y) {
        return new Box( getTop() + y, getRight() + x, getBottom() + y, getLeft() + x );
    }

    public Box translate(final Coordinate offset) {
        return translate( offset.getX(), offset.getY() );
    }

    public Box shrink(final Inset inset) {
        return new Box( getTop() + inset.getTop(), getRight() - inset.getRight(), getBottom() - inset.getBottom(),
                              getLeft() + inset.getLeft() );
    }
}
