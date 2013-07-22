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

package com.lyndir.omicron.cli.view;

import com.lyndir.lhunath.opal.system.util.MetaObject;


/**
 * @author lhunath, 2013-07-20
 */
public class Rectangle extends MetaObject {

    private final int top;
    private final int right;
    private final int bottom;
    private final int left;

    public Rectangle() {
        this( 0, 0, 0, 0 );
    }

    public Rectangle(final int top, final int right, final int bottom, final int left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public Coordinate getOrigin() {
        return new Coordinate( getLeft(), getTop() );
    }

    public Size getSize() {
        return new Size( getRight() - getLeft(), getBottom() - getTop() );
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

    public int getLeft() {
        return left;
    }

    public Rectangle translate(final int x, final int y) {
        return new Rectangle( getTop() + y, getRight() + x, getBottom() + y, getLeft() + x );
    }

    public Rectangle translate(final Coordinate offset) {
        return translate( offset.getX(), offset.getY() );
    }

    public Rectangle shrink(final Rectangle shrinkBox) {
        return new Rectangle( getTop() + shrinkBox.getTop(), getRight() - shrinkBox.getRight(), getBottom() - shrinkBox.getBottom(),
                              getLeft() + shrinkBox.getLeft() );
    }

    public Rectangle size(final Size size) {
        return new Rectangle( getTop(), getLeft() + size.getWidth(), getTop() + size.getHeight(), getLeft() );
    }
}
