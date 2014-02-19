package com.lyndir.omicron.cli.command;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.math.*;
import com.lyndir.lhunath.opal.system.util.ConversionUtils;
import com.lyndir.omicron.api.model.*;
import com.lyndir.omicron.api.util.Maybe;
import com.lyndir.omicron.cli.OmicronCLI;
import java.util.Iterator;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "move", abbr = "mv", desc = "Move game objects around in the level.")
public class MoveCommand extends Command {

    public MoveCommand(final OmicronCLI omicron) {
        super( omicron );
    }

    @Override
    public void evaluate(final Iterator<String> tokens) {

        Optional<IPlayer> localPlayerOptional = getOmicron().getLocalPlayer();
        if (!localPlayerOptional.isPresent()) {
            err( "No local player in the game." );
            return;
        }
        IPlayer localPlayer = localPlayerOptional.get();

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

        Optional<Side> side = Side.forName( sideArgument );
        if (!side.isPresent()) {
            err( "No such side/level: %s.  Valid sides are: %s, valid levels are: %s", side, //
                 FluentIterable.from( ImmutableList.copyOf( Side.values() ) ).transform( new Function<Side, String>() {
                     @Override
                     public String apply(final Side input) {

                         return input.name();
                     }
                 } ), //
                 FluentIterable.from( ImmutableList.copyOf( LevelType.values() ) ).transform( new Function<LevelType, String>() {
                     @Override
                     public String apply(final LevelType input) {

                         return input.name();
                     }
                 } )
            );
            return;
        }

        // Find the game object for the given ID.
        Maybe<? extends IGameObject> maybeObject = localPlayer.getController().getObject( objectId );
        if (maybeObject.presence() != Maybe.Presence.PRESENT) {
            err( "No observable object with ID: %s", objectId );
            return;
        }
        IGameObject gameObject = maybeObject.get();

        // Check to see if it's mobile by finding its mobility module.
        Optional<IMobilityModule> optionalMobility = gameObject.getModule( PublicModuleType.MOBILITY, 0 );
        if (!optionalMobility.isPresent()) {
            err( "Object is not mobile: %s", gameObject );
            return;
        }
        IMobilityModule mobilityModule = optionalMobility.get();

        Maybe<? extends ITile> maybeLocation = mobilityModule.getGameObject().checkLocation();
        if (maybeLocation.presence() != Maybe.Presence.PRESENT) {
            err( "Object's location is not known: %s", gameObject );
            return;
        }
        ITile location = maybeLocation.get();

        Vec2 targetPosition = location.getPosition().translate( side.get().getDelta() );
        Optional<? extends ITile> targetLocation = location.getLevel().getTile( targetPosition );
        if (!targetLocation.isPresent()) {
            err( "No tile at that side for position: %s.", targetPosition );
            return;
        }

        // Move the object.
        IMobilityModule.IMovement movement = mobilityModule.movement( targetLocation.get() );
        if (!movement.isPossible()) {
            err( "This movement is not possible: %s.", movement );
            return;
        }

        try {
            movement.execute();
            inf( "Object is now at: %s", gameObject.checkLocation() );
        }
        catch (IModule.ImpossibleException | IModule.InvalidatedException e) {
            throw Throwables.propagate( e );
        }
    }
}
