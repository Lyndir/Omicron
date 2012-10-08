package com.lyndir.omnicron.cli;

import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class NewCommand implements Command {

    @Override
    public void evaluate(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        if (!tokens.hasNext()) {
            System.err.println("new: Missing object to create.");
            return;
        }

        String objectClass = tokens.next();
        if ("game".equals( objectClass ))
            new NewGameCommand().evaluate( omnicron, tokens );
        else
            System.err.format("new: Don't know how to create: %s\n", objectClass);
    }
}
