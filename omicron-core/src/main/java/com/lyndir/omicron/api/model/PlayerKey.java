package com.lyndir.omicron.api.model;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;


public class PlayerKey {

    private static final Random RANDOM = new SecureRandom();

    private final byte[] key = new byte[64];

    public PlayerKey() {
        RANDOM.nextBytes( key );
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(getClass().isInstance( obj )))
            return false;

        return Arrays.equals( key, ((PlayerKey) obj).key );
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode( key );
    }
}
