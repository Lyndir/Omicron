package com.lyndir.omicron.api.model;

public class PlayerKey {

    @Override
    public boolean equals(final Object obj) {
        return obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return System.identityHashCode( this );
    }
}
