package com.lyndir.omnicron.cli;

import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import com.lyndir.lhunath.opal.system.util.ConversionUtils;
import com.lyndir.omnicron.api.controller.MobilityModule;
import com.lyndir.omnicron.api.model.GameObject;
import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "move", abbr = "mv", desc = "Move game objects around in the level.")
public class MoveCommand extends Command {

    @Override
    public void evaluate(final OmnicronCLI omnicron, final Iterator<String> tokens) {

        String objectIDArgument = Iterators.getNext( tokens, null );
        if (objectIDArgument == null) {
            err( "Missing objectID.  Syntax: objectID dU dV" );
            return;
        }
        if ("help".equals( objectIDArgument )) {
            inf( "Usage: objectID dU dV" );
            inf( "    objectID: The ID of the object to move (see list objects)." );
            inf( "          dU: The delta from the current position's u to the new u." );
            inf( "          dV: The delta from the current position's v to the new v." );
            return;
        }

        String duArgument = Iterators.getNext( tokens, null );
        if (duArgument == null) {
            err( "Missing dU.  Syntax: objectID dU dV" );
            return;
        }
        String dvArgument = Iterators.getNext( tokens, null );
        if (dvArgument == null) {
            err( "Missing dV.  Syntax: objectID dU dV" );
            return;
        }


        int objectId = ConversionUtils.toIntegerNN( objectIDArgument );
        int du = ConversionUtils.toIntegerNN( duArgument );
        int dv = ConversionUtils.toIntegerNN( dvArgument );

        // Find the game object for the given ID.
        Optional<GameObject> optionalObject = omnicron.getLocalPlayer().getController().getObject( omnicron.getLocalPlayer(), objectId );
        if (!optionalObject.isPresent()) {
            err( "No observable object with ID: %s", objectId );
            return;
        }
        GameObject gameObject = optionalObject.get();

        // Check to see if it's mobile by finding its mobility module.
        Optional<MobilityModule> optionalMobility = gameObject.getModule( MobilityModule.class );
        if (!optionalMobility.isPresent()) {
            err( "Object is not mobile: %s", gameObject );
            return;
        }
        MobilityModule mobilityModule = optionalMobility.get();

        // Move the object.
        mobilityModule.move( omnicron.getLocalPlayer(), du, dv );
        inf( "Object is now at: %s", gameObject.getLocation() );
    }
}
