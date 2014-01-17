package com.lyndir.omicron.api.model;

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
        if (obj == this)
            return true;
        if (!(obj instanceof Size))
            return false;

        Size o = (Size) obj;
        return width == o.width && height == o.height;
    }
}
