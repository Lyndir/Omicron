package com.lyndir.omnicron.cli;

import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public interface Command {

    /**
     * Evaluate the given tokens in the context of this command.
     *
     * @param omnicron
     * @param tokens The tokens given to this command in order to define how it should operate.
     */
    void evaluate(final OmnicronCLI omnicron, Iterator<String> tokens);
}
