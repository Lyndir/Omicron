package com.lyndir.omicron.cli;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.util.ConversionUtils;
import com.lyndir.omicron.api.controller.MobilityModule;
import com.lyndir.omicron.api.model.*;
import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "move", abbr = "mv", desc = "Move game objects around in the level.")
public class MoveCommand extends Command {

    @Override
    public void evaluate(final OmicronCLI omicron, final Iterator<String> tokens) {

        String objectIDArgument = Iterators.getNext( tokens, null );
        if (objectIDArgument == null) {
            err( "Missing objectID.  Syntax: objectID side/level" );
            return;
        }
        if ("help".equals( objectIDArgument )) {
            inf( "Usage: objectID side/level" );
            inf( "    objectID: The ID of the object to move (see list objects)." );
            inf( "  side/level: The side of the object's current tile or level to move to." );
            return;
        }

        int objectId = ConversionUtils.toIntegerNN( objectIDArgument );
        String sideArgument = Iterators.getNext( tokens, null );
        if (sideArgument == null) {
            err( "Missing side/level.  Syntax: objectID side/level" );
            return;
        }

        Optional<Coordinate.Side> side = Coordinate.Side.forName( sideArgument );
        if (!side.isPresent()) {
            err( "No such side/level: %s.  Valid sides are: %s, valid levels are: %s", side, //
                 FluentIterable.from( ImmutableList.copyOf( Coordinate.Side.values() ) )
                               .transform( new Function<Coordinate.Side, String>() {
                                   @Override
                                   public String apply(final Coordinate.Side input) {

                                       return input.name();
                                   }
                               } ), //
                 FluentIterable.from( ImmutableList.copyOf( LevelType.values() ) ).transform( new Function<LevelType, String>() {
                     @Override
                     public String apply(final LevelType input) {

                         return input.name();
                     }
                 } ) );
            return;
        }

        // Find the game object for the given ID.
        Optional<GameObject> optionalObject = omicron.getLocalPlayer().getController().getObject( omicron.getLocalPlayer(), objectId );
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
        mobilityModule.move( omicron.getLocalPlayer(), side.get() );
        inf( "Object is now at: %s", gameObject.getLocation() );
    }
}
