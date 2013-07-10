package com.lyndir.omicron.cli;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * <i>10 10, 2012</i>
 *
 * @author lhunath
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommand {

    String abbr();
    String desc();
}
