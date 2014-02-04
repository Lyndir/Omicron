package com.lyndir.omicron.api.model;

import com.lyndir.lhunath.opal.system.error.InternalInconsistencyException;
import com.lyndir.lhunath.opal.system.util.TypeUtils;
import com.lyndir.omicron.api.model.error.OmicronException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * @author lhunath, 2013-08-17
 */
public abstract class IncompatibleStateException extends Exception {

    protected IncompatibleStateException(final String format, final Object... args) {
        super( String.format( format, args ) );
    }

    public static <O, E extends OmicronException> O assertNotNull(final O object, final Class<E> exceptionClass,
                                                                            final Object... args)
            throws E {
        if (object != null)
            return object;

        try {
            Constructor<E> constructor = TypeUtils.getConstructor( exceptionClass, args );
            constructor.setAccessible( true );

            throw exceptionClass.cast( constructor.newInstance( args ) );
        }
        catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new InternalInconsistencyException( "Fix the constructor of: " + exceptionClass, e );
        }
    }

    public static <E extends OmicronException> void assertState(final boolean validState, final Class<E> exceptionClass,
                                                                          final Object... args)
            throws E {
        if (validState)
            return;

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
