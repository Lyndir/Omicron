package com.lyndir.omicron.cli.command;

import com.lyndir.lanterna.view.Rectangle;
import com.lyndir.lhunath.opal.math.Size;
import com.lyndir.lhunath.opal.math.Vec2;
import com.lyndir.omicron.cli.OmicronCLI;
import com.lyndir.omicron.cli.view.MapView;
import java.util.*;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@CommandGroup(name = "view", abbr = "v", desc = "Perform operations on the view area.")
public class ViewCommand extends Command {

    public ViewCommand(final OmicronCLI omicron) {
        super( omicron );
    }

    @SubCommand(abbr = "c", desc = "Center the view on a specific target.")
    public void center(final Iterator<String> tokens) {

        if (!tokens.hasNext()) {
            MapView mapView = getOmicron().getWindow().getView().getDashboard().getMap();
            inf( "View is centered at: %s", mapView.getCenterTile() );
        }
    }
}
