package com.lyndir.omicron.api.model;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;


public class PlayerKey implements Serializable {

    private static final Random RANDOM = new SecureRandom();

    private final byte[] key = new byte[64];

    public PlayerKey() {
        RANDOM.nextBytes( key );
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof PlayerKey))
            return false;

        return Arrays.equals( key, ((PlayerKey) obj).key );
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode( key );
    }

    @Override
    public String toString() {
        return String.format( "{PlayerKey: %d}", hashCode() );
    }
}
