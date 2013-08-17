package com.lyndir.omicron.api.model;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.ChangeInt;
import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


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

    private final int       playerID;
    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final PlayerKey key;
    private final String    name;
    @ObjectMeta(ignoreFor = ObjectMeta.For.toString)
    private final Color     primaryColor;
    @ObjectMeta(ignoreFor = ObjectMeta.For.toString)
    private final Color     secondaryColor;
    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final Map<Integer, GameObject> objects = new HashMap<>();

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private int score;
    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private int nextObjectID;

    public Player(final int playerID, @Nullable final PlayerKey key, final String name, final Color primaryColor,
                  final Color secondaryColor) {
        this.playerID = playerID;
        this.key = key;
        this.name = name;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }

    @Nonnull
    public PlayerController getController() {
        return controller;
    }

    @Authenticated
    @Override
    public boolean canObserve(@Nonnull final Tile location) {
        return getController().canObserve( location );
    }

    @Authenticated
    @Nonnull
    @Override
    public Iterable<Tile> listObservableTiles() {
        return getController().listObservableTiles();
    }

    @Nonnull
    @Override
    public Optional<Player> getOwner() {
        return Optional.of( this );
    }

    public int getPlayerID() {
        return playerID;
    }

    public boolean hasKey(final PlayerKey playerKey) {
        return ObjectUtils.isEqual( key, playerKey );
    }

    public boolean isKeyLess() {
        return key == null;
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

    Collection<GameObject> getObjects() {
        return objects.values();
    }

    public static String randomName() {
        return Joiner.on( ' ' ).join( firstNames[random.nextInt( firstNames.length )], lastNames[random.nextInt( lastNames.length )] );
    }

    public int getScore() {
        return score;
    }

    void setScore(final int score) {
        ChangeInt.From scoreChange = ChangeInt.from( this.score );

        this.score = score;

        getController().getGameController().fireFor( null ).onPlayerScore( this, scoreChange.to( this.score ) );
    }

    int nextObjectID() {
        return nextObjectID++;
    }

    @Nonnull
    Optional<GameObject> getObject(final int objectId) {
        return Optional.fromNullable( objects.get( objectId ) );
    }

    void removeObject(final GameObject gameObject) {
        objects.remove( gameObject.getObjectID() );

        getController().getGameController().fireFor( new PredicateNN<Player>() {
            @Override
            public boolean apply(@Nonnull final Player input) {
                return ObjectUtils.isEqual( input, Player.this );
            }
        } ).onPlayerLostObject( this, gameObject );
    }

    void addObject(final GameObject gameObject) {
        objects.put( gameObject.getObjectID(), gameObject );

        getController().getGameController().fireFor( new PredicateNN<Player>() {
            @Override
            public boolean apply(@Nonnull final Player input) {
                return ObjectUtils.isEqual( input, Player.this );
            }
        } ).onPlayerGainedObject( this, gameObject );
    }
}
