package com.lyndir.omnicron.api;

import com.google.common.collect.ImmutableSet;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.util.Random;
import java.util.Set;
import org.jetbrains.annotations.NotNull;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class Player extends MetaObject implements GameObserver {

    private static final String[] names  = { "Jack", "Daniel", "Derrick", "Yasmin", "Catherin", "Mary" };
    private static final Random   random = new Random();

    private final String name;
    private final Color  primaryColor;
    private final Color  secondaryColor;

    public Player(final String name, final Color primaryColor, final Color secondaryColor) {

        this.name = name;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }

    @Override
    public GameObserver getParent() {

        throw null;
    }

    @NotNull
    @Override
    public Player getPlayer() {

        return this;
    }

    @NotNull
    @Override
    public Set<Tile> getObservedTiles() {

        ImmutableSet.Builder<Tile> observedTilesBuilder = ImmutableSet.builder();
        for (final GameObject gameObject : Game.get().getGameObjectsForPlayer( this, this )) {
            observedTilesBuilder.addAll( gameObject.getObservedTiles() );
        }

        return observedTilesBuilder.build();
    }

    public String getName() {

        return name;
    }

    public Color getPrimaryColor() {

        return primaryColor;
    }

    public Color getSecondaryColor() {

        return secondaryColor;
    }

    public static String randomName() {

        return names[random.nextInt( names.length )];
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Player))
            return false;

        Player playerObj = (Player) obj;
        if (playerObj.getName().equals( name ))
            return true;
        if (playerObj.getPrimaryColor().equals( primaryColor ) && playerObj.getSecondaryColor().equals( secondaryColor ))
            return true;

        return false;
    }
}
