package com.lyndir.omnicron.api.model;

import com.lyndir.lhunath.opal.system.util.MetaObject;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class Size extends MetaObject {

    private final int width;
    private final int height;

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

        return coordinate.getU() >= 0 && coordinate.getV() >= 0 && coordinate.getU() < width && coordinate.getV() < height;
    }
}
