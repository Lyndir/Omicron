package com.lyndir.omicron.api.model;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.lyndir.lhunath.opal.system.error.InternalInconsistencyException;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.Nullable;


/**
 * @author lhunath, 2013-08-17
 */
public abstract class IncompatibleStateException extends RuntimeException {

    protected IncompatibleStateException(final String format, final Object... args) {
        super( String.format( format, args ) );
    }

    public static <O> O assertNotNull(final O object, final Class<? extends IncompatibleStateException> exceptionClass,
                                      final Object... args)
            throws IncompatibleStateException {
        if (object != null)
            return object;

        try {
            throw exceptionClass.getConstructor(
                    FluentIterable.from( Lists.newArrayList( args ) ).transform( new Function<Object, Class<?>>() {
                        @Nullable
                        @Override
                        public Class<?> apply(final Object input) {
                            return input.getClass();
                        }
                    } ).toList().toArray( new Class<?>[args.length] ) ).newInstance( args );
        }
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new InternalInconsistencyException( "Fix the constructor of: " + exceptionClass, e );
        }
    }

    public static void assertState(final boolean validState, final Class<? extends IncompatibleStateException> exceptionClass,
                                   final Object... args)
            throws IncompatibleStateException {
        if (validState)
            return;

        try {
            throw exceptionClass.getConstructor(
                    FluentIterable.from( Lists.newArrayList( args ) ).transform( new Function<Object, Class<?>>() {
                        @Nullable
                        @Override
                        public Class<?> apply(final Object input) {
                            return input.getClass();
                        }
                    } ).toList().toArray( new Class<?>[args.length] ) ).newInstance( args );
        }
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new InternalInconsistencyException( "Fix the constructor of: " + exceptionClass, e );
        }
    }
}
