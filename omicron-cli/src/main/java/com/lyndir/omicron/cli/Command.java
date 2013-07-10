package com.lyndir.omicron.cli;

import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.TypeUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import org.reflections.Reflections;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public abstract class Command {

    static final Logger      logger             = Logger.get( Command.class );
    static final Reflections packageReflections = new Reflections( Command.class.getPackage().getName() );

    /**
     * Evaluate the given tokens in the context of this command.
     *
     * @param omicron The omicron client that this command should control.
     * @param tokens   The tokens given to this command in order to define how it should operate.
     */
    public void evaluate(final OmicronCLI omicron, final Iterator<String> tokens) {

        if (!tokens.hasNext()) {
            err( "Missing sub command." );
            help( omicron, tokens );
            return;
        }

        String subCommand = tokens.next();

        // Find the sub command to invoke by looking at our methods.
        for (final Method method : getClass().getMethods()) {
            SubCommand annotation = method.getAnnotation( SubCommand.class );
            if (annotation != null)
                if (method.getName().equals( subCommand ) || annotation.abbr().equals( subCommand )) {
                    try {
                        method.invoke( this, omicron, tokens );
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw logger.bug( e );
                    }

                    return;
                }
        }

        // Find the sub command to invoke by looking at other Command classes.
        for (final Class<? extends Command> commandGroup : packageReflections.getSubTypesOf( Command.class )) {
            CommandGroup annotation = commandGroup.getAnnotation( CommandGroup.class );
            if (annotation.parent() == getClass() && annotation.name().equals( subCommand ) || annotation.abbr().equals( subCommand )) {
                TypeUtils.newInstance( commandGroup ).evaluate( omicron, tokens );
                return;
            }
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

    @SubCommand(abbr = "h", desc = "Enumerate all the sub commands of this command.")
    public void help(final OmicronCLI omicron, final Iterator<String> tokens) {

        inf( "Available sub commands are:" );
        enumerateSubCommands();
    }

    private void enumerateSubCommands() {

        SortedMap<String, String> commandDescriptions = new TreeMap<>();

        for (final Method method : getClass().getMethods()) {
            SubCommand annotation = method.getAnnotation( SubCommand.class );
            if (annotation == null)
                // Not a sub command.
                continue;
            if ("help".equals( method.getName() ))
                // Hide special commands.
                continue;

            commandDescriptions.put( method.getName(), annotation.desc() );
        }

        for (final Class<? extends Command> commandGroup : packageReflections.getSubTypesOf( Command.class )) {
            CommandGroup annotation = commandGroup.getAnnotation( CommandGroup.class );
            if (annotation.parent() != getClass())
                // Only show help for sub commands of this command.
                continue;
            if (annotation.name().isEmpty() || "help".equals( annotation.name() ))
                // Hide special commands.
                continue;

            // Figure out the full name for this command group.
            commandDescriptions.put( String.format( "%s/%s", annotation.name(), annotation.abbr() ), annotation.desc() );
        }

        for (final String name : commandDescriptions.keySet())
            inf( "    %s: %s", name, commandDescriptions.get( name ) );
    }
}
