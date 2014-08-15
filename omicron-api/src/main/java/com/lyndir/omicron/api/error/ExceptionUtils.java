package com.lyndir.omicron.api.error;

import com.lyndir.lhunath.opal.system.error.InternalInconsistencyException;
import com.lyndir.lhunath.opal.system.util.TypeUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


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
            Constructor<E> constructor = TypeUtils.getConstructor( exceptionClass, args );
            constructor.setAccessible( true );

            throw exceptionClass.cast( constructor.newInstance( args ) );
        }
        catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new InternalInconsistencyException( "Fix the constructor of: " + exceptionClass, e );
        }
    }
}
