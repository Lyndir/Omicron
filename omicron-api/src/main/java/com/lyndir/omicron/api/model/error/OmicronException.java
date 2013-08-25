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
public abstract class OmicronException extends Exception {

    protected OmicronException(final String format, final Object... args) {
        super( String.format( format, args ) );
    }
}
