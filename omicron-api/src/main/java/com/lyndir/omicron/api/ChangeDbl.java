package com.lyndir.omicron.api;

import com.lyndir.lhunath.opal.system.util.ConversionUtils;
import java.util.Objects;


/**
 * @author lhunath, 2013-08-13
 */
public class ChangeDbl {

    private final double from;
    private final double to;

    ChangeDbl(final double from, final double to) {
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
        if (!(obj instanceof ChangeDbl))
            return false;

        ChangeDbl o = (ChangeDbl) obj;
        return from == o.from && to == o.to;
    }

    public double getFrom() {
        return from;
    }

    public double getTo() {
        return to;
    }

    public double delta() {
        return getTo() - getFrom();
    }

    public static From from(final Double from) {
        return from( ConversionUtils.toDoubleNN( from ) );
    }

    public static From from(final double from) {
        return new From( from );
    }

    public static class From {

        private final double from;

        From(final double from) {
            this.from = from;
        }

        public ChangeDbl to(final Double to) {
            return to( ConversionUtils.toDoubleNN( to ) );
        }

        public ChangeDbl to(final double to) {
            return new ChangeDbl( from, to );
        }
    }
}
