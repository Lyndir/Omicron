package com.lyndir.omicron.api;


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

    public double getFrom() {
        return from;
    }

    public double getTo() {
        return to;
    }

    public double delta() {
        return getTo() - getFrom();
    }

    public static From from(final double from) {
        return new From( from );
    }

    public static class From {

        private final double from;

        From(final double from) {
            this.from = from;
        }

        public ChangeDbl to(final double to) {
            return new ChangeDbl( from, to );
        }
    }
}
