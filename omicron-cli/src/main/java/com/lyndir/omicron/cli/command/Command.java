package com.lyndir.omicron.cli.command;

import com.google.common.base.Throwables;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.TypeUtils;
import com.lyndir.omicron.cli.OmicronCLI;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import org.reflections.Reflections;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public abstract class Command {

    static final Logger      logger             = Logger.get( Command.class );
    static final Reflections packageReflections = new Reflections( Command.class.getPackage().getName() );

    private static final IMarkerFactory markers = new BasicMarkerFactory();

    private final OmicronCLI omicron;

    protected Command(final OmicronCLI omicron) {
        this.omicron = omicron;
    }

    /**
     * Evaluate the given tokens in the context of this command.
     *
     * @param tokens The tokens given to this command in order to define how it should operate.
     */
    public void evaluate(final Iterator<String> tokens) {

        if (!tokens.hasNext()) {
            err( "Missing sub command." );
            help( tokens );
            return;
        }

        String subCommand = tokens.next();

        // Find the sub command to invoke by looking at our methods.
        for (final Method method : getClass().getMethods()) {
            SubCommand annotation = method.getAnnotation( SubCommand.class );
            if (annotation != null)
                if (method.getName().equals( subCommand ) || annotation.abbr().equals( subCommand )) {
                    try {
                        method.invoke( this, tokens );
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
            if (annotation.parent() == getClass() && annotation.name().equals( subCommand ) || annotation.abbr().equals( subCommand ))
                try {
                    commandGroup.getConstructor( OmicronCLI.class ).newInstance( omicron ).evaluate( tokens );
                    return;
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw Throwables.propagate( e );
                }
        }

        err( "Don't know how to handle: %s", subCommand );
    }

    public OmicronCLI getOmicron() {
        return omicron;
    }

    protected void dbg(final String format, final Object... args) {

        logger.dbg( markers.getMarker( commandPrefix() ), null, format, args );
    }

    protected void inf(final String format, final Object... args) {

        logger.inf( markers.getMarker( commandPrefix() ), null, format, args );
    }

    protected void err(final String format, final Object... args) {

        logger.err( markers.getMarker( commandPrefix() ), null, format, args );
    }

    private String commandPrefix() {

        CommandGroup commandGroup = getClass().getAnnotation( CommandGroup.class );
        return commandGroup.name() + (commandGroup.name().isEmpty()? "": ": ");
    }

    @SubCommand(abbr = "h", desc = "Enumerate all the sub commands of this command.")
    public void help(final Iterator<String> tokens) {

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

        for (final Map.Entry<String, String> commandDescriptionEntry : commandDescriptions.entrySet())
            inf( "    %s: %s", commandDescriptionEntry.getKey(), commandDescriptions.get( commandDescriptionEntry.getValue() ) );
    }
}
