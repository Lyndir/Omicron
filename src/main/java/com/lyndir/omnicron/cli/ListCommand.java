package com.lyndir.omnicron.cli;

import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class ListCommand implements Command {

    @Override
    public void evaluate(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        if (!tokens.hasNext()) {
            System.err.println("list: Missing target to list.");
            return;
        }

        String target = tokens.next();
        if ("all".equals( target ))
            all(omnicron);
        else
            System.err.format("list: Don't know how to list: %s\n", target);
    }

    private void all(final OmnicronCLI omnicron) {

    }
}
