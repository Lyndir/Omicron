package com.lyndir.omicron.cli.command;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.ifNotNullElse;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterators;
import com.lyndir.lhunath.opal.system.util.ConversionUtils;
import com.lyndir.omicron.api.controller.GameController;
import com.lyndir.omicron.api.controller.WeaponModule;
import com.lyndir.omicron.api.model.*;
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

        final Optional<GameController> gameController = getOmicron().getGameController();
        if (!gameController.isPresent()) {
            err( "No game is running.  Create one with the 'create' command." );
            return;
        }

        final Optional<Player> localPlayerOptional = getOmicron().getLocalPlayer();
        if (!localPlayerOptional.isPresent()) {
            err( "No local player in the game." );
            return;
        }
        final Player localPlayer = localPlayerOptional.get();

        String objectIDArgument = Iterators.getNext( tokens, null );
        if (objectIDArgument == null) {
            err( "Missing objectID.  Syntax: objectID dU dV [level]" );
            return;
        }
        if ("help".equals( objectIDArgument )) {
            inf( "Usage: objectID dU dV [level]" );
            inf( "    objectID: The ID of the object to fire with (see list objects)." );
            inf( "          dU: The delta from the current position's u to the target u." );
            inf( "          dV: The delta from the current position's v to the target v." );
            inf( "      weapon: The index of the weapon to fire with (optional, default=0 (primary))." );
            inf( "       level: The level into which to target the weapon (optional, default=current)." );
            return;
        }

        String duArgument = Iterators.getNext( tokens, null );
        if (duArgument == null) {
            err( "Missing dU.  Syntax: objectID dU dV [level]" );
            return;
        }
        String dvArgument = Iterators.getNext( tokens, null );
        if (dvArgument == null) {
            err( "Missing dV.  Syntax: objectID dU dV [level]" );
            return;
        }

        int objectId = ConversionUtils.toIntegerNN( objectIDArgument );
        int du = ConversionUtils.toIntegerNN( duArgument );
        int dv = ConversionUtils.toIntegerNN( dvArgument );

        // Find the game object for the given ID.
        Optional<GameObject> optionalObject = localPlayer.getController().getObject( localPlayer, objectId );
        if (!optionalObject.isPresent()) {
            err( "No observable object with ID: %s", objectId );
            return;
        }
        GameObject gameObject = optionalObject.get();

        String weaponIndexOrLevelArgument = Iterators.getNext( tokens, null );
        Optional<Integer> optionalWeaponIndex = ConversionUtils.toInteger( weaponIndexOrLevelArgument );
        int weaponIndex = 0;
        if (optionalWeaponIndex.isPresent()) {
            weaponIndex = optionalWeaponIndex.get();
            weaponIndexOrLevelArgument = Iterators.getNext( tokens, null );
        }

        final String levelArgument = ifNotNullElse( weaponIndexOrLevelArgument, gameObject.getLocation().getLevel().getType().getName() );
        Optional<Level> level = FluentIterable.from( gameController.get().listLevels() ).firstMatch( new Predicate<Level>() {
            @Override
            public boolean apply(final Level input) {

                return input.getType().getName().equalsIgnoreCase( levelArgument );
            }
        } );
        if (!level.isPresent()) {
            err( "No such level in this game: %s", levelArgument );
            return;
        }

        // Check to see if it's mobile by finding its mobility module.
        Optional<WeaponModule> optionalWeapon = gameObject.getModule( ModuleType.WEAPON, weaponIndex );
        if (!optionalWeapon.isPresent()) {
            if (weaponIndex == 0)
                err( "Object has no weapons: %s", gameObject );
            else
                err( "Object has no weapon at index %d: %s", weaponIndex, gameObject );
            return;
        }
        WeaponModule weaponModule = optionalWeapon.get();

        // Find the target tile.
        Coordinate targetCoordinate = gameObject.getLocation().getPosition().delta( du, dv );
        Optional<Tile> target = level.get().getTile( targetCoordinate );
        if (!(target.isPresent())) {
            err( "No tile in level: %s, at position: %s", level.get().getType().getName(), targetCoordinate );
            return;
        }

        // Fire at the target.
        weaponModule.fireAt( localPlayer, target.get() );
        Optional<GameObject> targetContents = target.get().getContents();
        inf( "Fired at: %s", targetContents.isPresent()? targetContents.get(): target );
    }
}
