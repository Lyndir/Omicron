package com.lyndir.omicron.api.util;

import javax.annotation.Nullable;


/**
 * @author lhunath, 2013-08-17
 */
public enum Maybool {

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
    };

    public abstract boolean isTrue();

    public abstract boolean isKnown();

    /**
     * @return YES if value is true, NO if value is false.
     */
    public static Maybool from(final boolean value) {
        return value? YES: NO;
    }

    /**
     * @return UNKNOWN if value is null, YES if value is true, NO if value is false.
     */
    public static Maybool fromNullable(@Nullable final Boolean value) {
        return value == null? UNKNOWN : value? YES: NO;
    }
}
