package com.lyndir.omnicron.cli;

import com.lyndir.lhunath.opal.system.logging.Logger;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public abstract class Command {

    static final Logger logger = Logger.get( Command.class );

    /**
     * Evaluate the given tokens in the context of this command.
     *
     * @param omnicron The omnicron client that this command should control.
     * @param tokens   The tokens given to this command in order to define how it should operate.
     */
    public void evaluate(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        CommandGroup commandGroup = getClass().getAnnotation( CommandGroup.class );

        if (!tokens.hasNext()) {
            err( "Missing sub command." );
            inf( "Available sub commands are:" );
            enumerateSubCommands( commandGroup );

            return;
        }

        String subCommand = tokens.next();
        for (final Method method : getClass().getMethods()) {
            if (method.getAnnotation( SubCommand.class ) != null)
                if (method.getName().equals( subCommand )) {
                    try {
                        method.invoke( this, omnicron, tokens );
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw logger.bug( e );
                    }

                    return;
                }
        }
        if ("help".equals( subCommand )) {
            inf( "Available sub commands are:" );
            enumerateSubCommands( commandGroup );
            return;
        }

        err( "Don't know how to handle: %s", subCommand );
    }

    protected void dbg(final String format, final Object... args) {

        System.err.format( "%sdbg: ", commandPrefix() );
        System.err.format( format + '\n', args );
    }

    protected void inf(final String format, final Object... args) {

        System.err.format( "%s", commandPrefix() );
        System.err.format( format + '\n', args );
    }

    protected void err(final String format, final Object... args) {

        System.err.format( "%serr: ", commandPrefix() );
        System.err.format( format + '\n', args );
    }

    private String commandPrefix() {

        CommandGroup commandGroup = getClass().getAnnotation( CommandGroup.class );
        return commandGroup.name() + (commandGroup.name().isEmpty()? "": ": ");
    }

    private void enumerateSubCommands(final CommandGroup commandGroup) {

        for (final Method method : getClass().getMethods()) {
            SubCommand subCommand = method.getAnnotation( SubCommand.class );
            if (subCommand != null)
                inf( "    %s: %s", method.getName(), subCommand.description() );
        }
    }
}
