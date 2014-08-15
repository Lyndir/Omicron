package com.lyndir.omicron.api.util;

import javax.annotation.Nullable;


/**
 * @author lhunath, 2013-08-17
 */
public interface Maybool {

    boolean isTrue();

    boolean isKnown();

    static Maybool yes() {
        return Value.YES;
    }

    static Maybool no() {
        return Value.NO;
    }

    static Maybool unknown() {
        return Value.UNKNOWN;
    }

    /**
     * @return YES if value is true, NO if value is false.
     */
    static Maybool from(final boolean value) {
        return value? yes(): no();
    }

    /**
     * @return UNKNOWN if value is null, YES if value is true, NO if value is false.
     */
    static Maybool fromNullable(@Nullable final Boolean value) {
        return value == null? unknown(): value? yes(): no();
    }

    enum Value implements Maybool {
        YES {
            @Override
            public boolean isTrue() {
                return true;
            }

            @Override
            public boolean isKnown() {
                return true;
            }
        }, NO {
            @Override
            public boolean isTrue() {
                return false;
            }

            @Override
            public boolean isKnown() {
                return true;
            }
        }, UNKNOWN {
            @Override
            public boolean isTrue() {
                return false;
            }

            @Override
            public boolean isKnown() {
                return false;
            }
        }
    }
}
