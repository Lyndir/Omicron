package com.lyndir.omicron.cli.command;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.ifNotNullElse;

import com.google.common.base.*;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterators;
import com.lyndir.lhunath.opal.math.Vec2;
import com.lyndir.lhunath.opal.math.Vec2;
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
@CommandGroup(name = "fire", abbr = "f", desc = "Fire weapons at a target.")
public class FireCommand extends Command {

    public FireCommand(final OmicronCLI omicron) {
        super( omicron );
    }

    @Override
    public void evaluate(final Iterator<String> tokens) {

        Optional<IGameController> gameController = getOmicron().getGameController();
        if (!gameController.isPresent()) {
            err( "No game is running.  Create one with the 'create' command." );
            return;
        }

        Optional<IPlayer> localPlayerOptional = getOmicron().getLocalPlayer();
        if (!localPlayerOptional.isPresent()) {
            err( "No local player in the game." );
            return;
        }
        IPlayer localPlayer = localPlayerOptional.get();

        String objectIDArgument = Iterators.getNext( tokens, null );
        if (objectIDArgument == null) {
            err( "Missing objectID.  Syntax: objectID dX dY [level]" );
            return;
        }
        if ("help".equals( objectIDArgument )) {
            inf( "Usage: objectID dX dY [level]" );
            inf( "    objectID: The ID of the object to fire with (see list objects)." );
            inf( "          dU: The delta from the current position's x to the target x." );
            inf( "          dV: The delta from the current position's y to the target y." );
            inf( "      weapon: The index of the weapon to fire with (optional, default=0 (primary))." );
            inf( "       level: The level into which to target the weapon (optional, default=current)." );
            return;
        }

        String duArgument = Iterators.getNext( tokens, null );
        if (duArgument == null) {
            err( "Missing dU.  Syntax: objectID dX dY [level]" );
            return;
        }
        String dvArgument = Iterators.getNext( tokens, null );
        if (dvArgument == null) {
            err( "Missing dV.  Syntax: objectID dX dY [level]" );
            return;
        }

        int objectId = ConversionUtils.toIntegerNN( objectIDArgument );
        int dx = ConversionUtils.toIntegerNN( duArgument );
        int dy = ConversionUtils.toIntegerNN( dvArgument );

        // Find the game object for the given ID.
        Maybe<? extends IGameObject> maybeObject = localPlayer.getController().getObject( objectId );
        if (maybeObject.presence() != Maybe.Presence.PRESENT) {
            err( "No observable object with ID: %s", objectId );
            return;
        }
        IGameObject gameObject = maybeObject.get();
        ITile location = gameObject.checkLocation().get();

        String weaponIndexOrLevelArgument = Iterators.getNext( tokens, null );
        Optional<Integer> optionalWeaponIndex = ConversionUtils.toInteger( weaponIndexOrLevelArgument );
        int weaponIndex = 0;
        if (optionalWeaponIndex.isPresent()) {
            weaponIndex = optionalWeaponIndex.get();
            weaponIndexOrLevelArgument = Iterators.getNext( tokens, null );
        }

        final String levelArgument = ifNotNullElse( weaponIndexOrLevelArgument, location.getLevel().getType().getName() );
        Optional<? extends ILevel> level = FluentIterable.from( gameController.get().listLevels() ).firstMatch( new Predicate<ILevel>() {
            @Override
            public boolean apply(final ILevel input) {

                return input.getType().getName().equalsIgnoreCase( levelArgument );
            }
        } );
        if (!level.isPresent()) {
            err( "No such level in this game: %s", levelArgument );
            return;
        }

        // Check to see if it's mobile by finding its mobility module.
        Optional<IWeaponModule> optionalWeapon = gameObject.getModule( PublicModuleType.WEAPON, weaponIndex );
        if (!optionalWeapon.isPresent()) {
            if (weaponIndex == 0)
                err( "Object has no weapons: %s", gameObject );
            else
                err( "Object has no weapon at index %d: %s", weaponIndex, gameObject );
            return;
        }
        IWeaponModule weaponModule = optionalWeapon.get();

        // Find the target tile.
        Vec2 targetCoordinate = location.getPosition().translate( dx, dy );
        Optional<? extends ITile> target = level.get().getTile( targetCoordinate );
        if (!target.isPresent()) {
            err( "No tile in level: %s, at position: %s", level.get().getType().getName(), targetCoordinate );
            return;
        }

        // Fire at the target.
        try {
            if (!weaponModule.fireAt( target.get() )) {
                err( "Firing not possible." );
                return;
            }

            inf( "Fired at: %s", target.get().checkContents() );
        }
        catch (final IWeaponModule.OutOfRangeException ignored) {
            err( "Couldn't fire, target out of range: %s", target.get() );
        }
        catch (final IWeaponModule.OutOfRepeatsException ignored) {
            err( "Couldn't fire, weapon out of repeats." );
        }
        catch (final IWeaponModule.OutOfAmmunitionException ignored) {
            err( "Couldn't fire, weapon out of ammunition." );
        }
    }
}
