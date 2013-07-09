package com.lyndir.omnicron.api.model;

import com.lyndir.lhunath.opal.system.util.MetaObject;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class Coordinate extends MetaObject {

    private final int  u;
    private final int  v;
    private final Size wrapSize;

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

    private int getDU(final Coordinate other) {

        int du = u - other.getU();
        if (du > wrapSize.getWidth() / 2)
            du = wrapSize.getWidth() - du;

        return du;
    }

    private int getDV(final Coordinate other) {

        int dv = v - other.getV();
        if (dv > wrapSize.getHeight() / 2)
            dv = wrapSize.getHeight() - dv;

        return dv;
    }

    public Coordinate delta(final int du, final int dv) {

        return new Coordinate( (wrapSize.getWidth() + u + du) % wrapSize.getWidth(), (wrapSize.getHeight() + v + dv) % wrapSize.getHeight(),
                               wrapSize );
    }

    public int distanceTo(final Coordinate other) {

        int du = getDU( other );
        int dv = getDV( other );

        return (Math.abs(du) + Math.abs(dv) + Math.abs(du + dv)) / 2;
    }

    public Coordinate getNW() {

        return delta( 0, -1 );
    }

    public Coordinate getNE() {

        return delta( 1, -1 );
    }

    public Coordinate getW() {

        return delta( -1, 0 );
    }

    public Coordinate getE() {

        return delta( 1, 0 );
    }

    public Coordinate getSW() {

        return delta( -1, 1 );
    }

    public Coordinate getSE() {

        return delta( 0, 1 );
    }
}
