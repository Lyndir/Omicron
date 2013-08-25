package com.lyndir.omicron.api.model.error;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.lyndir.lhunath.opal.system.error.InternalInconsistencyException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.Nullable;


/**
 * @author lhunath, 2013-08-17
 */
public abstract class ExceptionUtils {

    public static <E extends OmicronException> void assertState(final boolean validState, final Class<E> exceptionClass,
                                                                final Object... args)
            throws E {
        if (!validState)
            fail( exceptionClass, args );
    }

    public static <E extends OmicronSecurityException> void assertSecure(final boolean secureState, final Class<E> exceptionClass,
                                                                         final Object... args)
            throws E {
        if (!secureState)
            fail( exceptionClass, args );
    }

    private static <E extends Exception> void fail(final Class<E> exceptionClass, final Object... args)
            throws E {
        try {
            Constructor<? extends Exception> constructor = exceptionClass.getDeclaredConstructor(
                    FluentIterable.from( Lists.newArrayList( args ) ).transform( new Function<Object, Class<?>>() {
                        @Nullable
                        @Override
                        public Class<?> apply(final Object input) {
                            return input.getClass();
                        }
                    } ).toList().toArray( new Class<?>[args.length] ) );
            constructor.setAccessible( true );

            throw exceptionClass.cast( constructor.newInstance( args ) );
        }
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new InternalInconsistencyException( "Fix the constructor of: " + exceptionClass, e );
        }
    }
}
