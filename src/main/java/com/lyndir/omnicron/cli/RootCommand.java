package com.lyndir.omnicron.cli;

import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "")
public class RootCommand extends Command {

    @Override
    public void evaluate(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        if (!tokens.hasNext())
            // No command.
            return;

        super.evaluate( omnicron, tokens );
    }

    @SubCommand(description = "Build a new game object.")
    public void build(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        new BuildCommand().evaluate( omnicron, tokens );
    }

    @SubCommand(description = "Set properties on a built object.")
    public void set(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        new SetCommand().evaluate( omnicron, tokens );
    }

    @SubCommand(description = "Add objects to properties of a built object.")
    public void add(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        new AddCommand().evaluate( omnicron, tokens );
    }

    @SubCommand(description = "Remove objects from properties of a built object.")
    public void rm(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        new RemoveCommand().evaluate( omnicron, tokens );
    }

    @SubCommand(description = "Create a previously built game object.")
    public void create(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        new CreateCommand().evaluate( omnicron, tokens );
    }

    @SubCommand(description = "Enumerate certain types of game objects.")
    public void list(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        new ListCommand().evaluate( omnicron, tokens );
    }

    @SubCommand(description = "Shut down the omnicron CLI client.")
    public void exit(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        omnicron.setRunning( false );
    }

    @SubCommand(description = "Move game objects around in the level.")
    public void mv(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        new MoveCommand().evaluate( omnicron, tokens );
    }
}
