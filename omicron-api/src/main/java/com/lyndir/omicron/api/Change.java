package com.lyndir.omicron.api;

import java.util.Objects;
import javax.annotation.Nullable;


/**
 * @author lhunath, 2013-08-13
 */
public class Change<T> {

    private final T from;
    private final T to;

    Change(@Nullable final T from, @Nullable final T to) {
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
        if (!(obj instanceof Change))
            return false;

        Change<?> o = (Change<?>) obj;
        return Objects.equals( from, o.from ) && Objects.equals( to, o.to );
    }

    @Nullable
    public T getFrom() {
        return from;
    }

    @Nullable
    public T getTo() {
        return to;
    }

    public static <T> From<T> from(@Nullable final T from) {
        return new From<>( from );
    }

    public static class From<T> {

        private final T from;

        From(@Nullable final T from) {
            this.from = from;
        }

        @Nullable
        public T getFrom() {
            return from;
        }

        public Change<T> to(@Nullable final T to) {
            return new Change<>( from, to );
        }
    }
}
