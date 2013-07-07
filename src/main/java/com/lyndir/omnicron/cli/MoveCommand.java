package com.lyndir.omnicron.cli;

import com.lyndir.lhunath.opal.system.util.ConversionUtils;
import com.lyndir.omnicron.api.model.GameObject;
import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "mv")
public class MoveCommand extends Command {

    @Override
    public void evaluate(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        int objectId = ConversionUtils.toIntegerNN( tokens.next() );
        int du = ConversionUtils.toIntegerNN(tokens.next());
        int dv = ConversionUtils.toIntegerNN(tokens.next());

        GameObject object = omnicron.getLocalPlayer().getController().findObject( omnicron.getLocalPlayer(), objectId );
        if (object == null) {
            err( "No object found for: %s", objectId );
            return;
        }
        object.getController().move(omnicron.getLocalPlayer(), du, dv);
    }
}
