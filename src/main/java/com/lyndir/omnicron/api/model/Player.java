package com.lyndir.omnicron.api.model;

import com.google.common.base.Joiner;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.lhunath.opal.system.util.ObjectMeta;
import com.lyndir.omnicron.api.controller.PlayerController;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class Player extends MetaObject implements GameObserver {

    private static final String[] firstNames = { "Jack", "Daniel", "Derrick", "Yasmin", "Catherin", "Mary" };
    private static final String[] lastNames  = { "Taylor", "Smith", "Brown", "Wilson", "Jones", "Lee" };
    private static final Random   random     = new Random();

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final PlayerController controller = new PlayerController( this );

    private final int    playerID;
    private final String name;
    private final Color  primaryColor;
    private final Color  secondaryColor;
    private final Map<Integer, GameObject> objects = new HashMap<>();

    private int score;
    private int nextObjectID;

    public Player(final int playerID, final String name, final Color primaryColor, final Color secondaryColor) {

        this.playerID = playerID;
        this.name = name;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }

    @Override
    public GameObserver getParent() {

        return null;
    }

    @NotNull
    @Override
    public Player getPlayer() {

        return this;
    }

    @NotNull
    @Override
    public PlayerController getController() {

        return controller;
    }

    public int getPlayerID() {

        return playerID;
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

    public Collection<GameObject> getObjects() {

        return objects.values();
    }

    public static String randomName() {

        return Joiner.on( ' ' ).join( firstNames[random.nextInt( firstNames.length )], lastNames[random.nextInt( lastNames.length )] );
    }

    public int getScore() {

        return score;
    }

    public void setScore(final int score) {

        this.score = score;
    }

    public int nextObjectID() {

        return nextObjectID++;
    }

    @Nullable
    public GameObject getObject(final int objectId) {

        return objects.get( objectId );
    }

    public void addObject(final BaseGameObject gameObject) {

        objects.put( gameObject.getObjectID(), gameObject );
    }
}
