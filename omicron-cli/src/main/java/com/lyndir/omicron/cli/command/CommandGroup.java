package com.lyndir.omicron.cli.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * <i>10 10, 2012</i>
 *
 * @author lhunath
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandGroup {

    String name();
    String abbr();
    String desc();
    Class<? extends Command> parent() default RootCommand.class;
}
