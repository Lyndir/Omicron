package com.lyndir.omicron.api;

import com.lyndir.lhunath.opal.system.util.ConversionUtils;
import java.util.Objects;


/**
 * @author lhunath, 2013-08-13
 */
public class ChangeInt {

    private final int from;
    private final int to;

    ChangeInt(final int from, final int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public int hashCode() {
        return Objects.hash( from, to );
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ChangeInt))
            return false;

        ChangeInt o = (ChangeInt) obj;
        return from == o.from && to == o.to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int delta() {
        return getTo() - getFrom();
    }

    public static From from(final Integer from) {
        return from( ConversionUtils.toIntegerNN( from ) );
    }

    public static From from(final int from) {
        return new From( from );
    }

    public static class From {

        private final int from;

        From(final int from) {
            this.from = from;
        }

        public ChangeInt to(final Integer to) {
            return to( ConversionUtils.toIntegerNN( to ) );
        }

        public ChangeInt to(final int to) {
            return new ChangeInt( from, to );
        }
    }
}
