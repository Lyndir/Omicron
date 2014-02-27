/*
 * Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.lyndir.omicron.api.util;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * @author lhunath, 2013-08-10
 */
public abstract class Maybe<T> {

    public static <T> Maybe<T> absent() {
        return new Absent<>();
    }

    public static <T> Maybe<T> unknown() {
        return new Unknown<>();
    }

    /**
     * @return {@link Presence#ABSENT} if reference is null, otherwise {@link Presence#PRESENT}.
     */
    public static <T> Maybe<T> fromNullable(@Nullable final T reference) {
        return reference == null? Maybe.<T>absent(): new Present<>( reference );
    }

    /**
     * @return {@link Presence#ABSENT} if reference is {@link Optional#absent()}, otherwise {@link Presence#PRESENT}.
     */
    public static <T> Maybe<T> fromOptional(final Optional<T> reference) {
        return reference.isPresent()? new Present<>( reference.get() ): Maybe.<T>absent();
    }

    /**
     * @return Always {@link Presence#PRESENT}.
     */
    public static <T> Maybe<T> of(@Nonnull final T reference) {
        return new Present<>( reference );
    }

    public abstract Presence presence();

    @Nonnull
    public abstract T get();

    @Nullable
    public T orNull() {
        if (presence() == Presence.PRESENT)
            return get();

        return null;
    }

    public abstract String toString();

    public enum Presence {
        ABSENT, UNKNOWN, PRESENT
    }


    private static class Absent<T> extends Maybe<T> {

        @Override
        public int hashCode() {
            return Objects.hash( presence() );
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof Absent;
        }

        @Override
        public Presence presence() {
            return Presence.ABSENT;
        }

        @Nonnull
        @Override
        public T get() {
            throw new IllegalStateException( "Cannot get() an absent reference." );
        }

        @Override
        public String toString() {
            return "<absent>";
        }
    }


    private static class Unknown<T> extends Maybe<T> {

        @Override
        public int hashCode() {
            return Objects.hash( presence() );
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof Unknown;
        }

        @Override
        public Presence presence() {
            return Presence.UNKNOWN;
        }

        @Nonnull
        @Override
        public T get() {
            throw new IllegalStateException( "Cannot get() an unknown reference." );
        }

        @Override
        public String toString() {
            return "<unknown>";
        }
    }


    private static class Present<T> extends Maybe<T> {

        private final T reference;

        private Present(final T reference) {
            this.reference = Preconditions.checkNotNull( reference, "Missing object for present reference." );
        }

        @Override
        public int hashCode() {
            return Objects.hash( presence(), reference );
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof Present && Objects.equals( reference, ((Present<?>) obj).reference );
        }

        @Override
        public Presence presence() {
            return Presence.PRESENT;
        }

        @Nonnull
        @Override
        public T get() {
            return reference;
        }

        @Override
        public String toString() {
            return String.format( "<present: %s>", reference.toString() );
        }
    }
}
