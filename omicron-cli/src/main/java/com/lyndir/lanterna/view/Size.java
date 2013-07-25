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

import com.googlecode.lanterna.terminal.TerminalSize;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class Size extends MetaObject {

    private final int width;
    private final int height;

    public Size() {
        this( 0, 0 );
    }

    public Size(final TerminalSize terminalSize) {
        this( terminalSize.getColumns(), terminalSize.getRows() );
    }

    public Size(final int width, final int height) {

        this.width = width;
        this.height = height;
    }

    public int getWidth() {

        return width;
    }

    public int getHeight() {

        return height;
    }

    public boolean isInBounds(final Coordinate coordinate) {

        return coordinate.getX() >= 0 && coordinate.getY() >= 0 && coordinate.getX() < width && coordinate.getY() < height;
    }

    public static Size max(@Nullable final Size size1, @Nonnull final Size size2) {

        if (size1 == null)
            return size2;

        return new Size( Math.max( size1.getWidth(), size2.getWidth() ), Math.max( size1.getHeight(), size2.getHeight() ) );
    }

    @Override
    public int hashCode() {

        return Objects.hash( width, height );
    }

    @Override
    public boolean equals(@Nullable final Object obj) {

        if (!(obj instanceof Size))
            return false;

        Size o = (Size) obj;
        return width == o.width && height == o.height;
    }
}
