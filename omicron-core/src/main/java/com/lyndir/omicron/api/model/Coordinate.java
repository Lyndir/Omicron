package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.lhunath.opal.system.util.ObjectMeta;
import java.util.Objects;


/**
 * <pre>
 *                 -v
 *               .   .   .   .   .   .
 *                 .   .   .   .   .
 *               .   .   Nw  Ne  .   .
 *            -u   .   W   o   E   .   +u
 *               .   .   Sw  Se  .   .
 *                 .   .   .   .   .
 *               .   .   .   .   .   .
 *                                 +v
 *
 *             u   v
 *        o  = 0 , 0
 *        Nw = 0 , -1
 *        Se = 0 , 1
 *        E  = 1 , 0
 *        W  = -1, 0
 *        Nw = 1 , -1
 *        Sw = -1, 1
 * </pre>
 *
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class Coordinate extends MetaObject {

    private final int  u;
    private final int  v;
    @ObjectMeta(ignoreFor = ObjectMeta.For.toString)
    private final Size wrapSize;


    public enum Side {
        NW( 0, -1 ), NE( 1, -1 ), W( -1, 0 ), E( 1, 0 ), SW( -1, 1 ), SE( 0, 1 );

        private final int du, dv;

        Side(final int du, final int dv) {
            this.du = du;
            this.dv = dv;
        }

        /**
         * Use this side as a delta from a given coordinate to find the adjacent coordinate.
         *
         * @param source The source coordinate.
         *
         * @return The destination coordinate.  This is the coordinate to the side of the source coordinate expressed by this instance.
         */
        public Coordinate delta(final Coordinate source) {
            return source.delta( du, dv );
        }

        public int getDu() {
            return du;
        }

        public int getDv() {
            return dv;
        }

        public static Optional<Side> forName(final String name) {
            for (final Side side : values())
                if (side.name().equalsIgnoreCase( name ))
                    return Optional.of( side );

            return Optional.absent();
        }
    }

    public Coordinate(final int u, final int v, final Size wrapSize) {
        this.u = u;
        this.v = v;
        this.wrapSize = wrapSize;
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    public Size getWrapSize() {
        return wrapSize;
    }

    private int getDU(final Coordinate other) {
        int du = other.getU() - u;

        // Take wrapping into account.
        int width = wrapSize.getWidth();
        if (du > width / 2)
            du -= width;
        else if (du < -width / 2)
            du += width;

        return du;
    }

    private int getDV(final Coordinate other) {
        int dv = other.getV() - v;

        // Take wrapping into account.
        int height = wrapSize.getHeight();
        if (dv > height / 2)
            dv -= height;
        else if (dv < -height / 2)
            dv += height;

        return dv;
    }

    public Coordinate delta(final int du, final int dv) {
        return new Coordinate( (wrapSize.getWidth() + u + du) % wrapSize.getWidth(), //
                               (wrapSize.getHeight() + v + dv) % wrapSize.getHeight(), wrapSize );
    }

    public Coordinate neighbour(final Side side) {
        return side.delta( this );
    }

    public int distanceTo(final Coordinate other) {
        int du = getDU( other );
        int dv = getDV( other );

        return (Math.abs( du ) + Math.abs( dv ) + Math.abs( du + dv )) / 2;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Coordinate))
            return false;

        Coordinate o = (Coordinate) obj;
        return u == o.u && v == o.v && wrapSize.equals( o.wrapSize );
    }

    @Override
    public int hashCode() {
        return Objects.hash( u, v, wrapSize );
    }
}
