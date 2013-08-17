package com.lyndir.omicron.api.util;

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
}
