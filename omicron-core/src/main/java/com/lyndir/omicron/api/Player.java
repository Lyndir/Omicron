package com.lyndir.omicron.api;

import static com.lyndir.omicron.api.Security.currentPlayer;

import com.google.common.base.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.hash.*;
import com.lyndir.lhunath.opal.system.util.*;
import java.util.*;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public class Player extends MetaObject implements IPlayer {

    private static final String[] firstNames = { "Jack", "Daniel", "Derrick", "Yasmin", "Catherin", "Mary" };
    private static final String[] lastNames  = { "Taylor", "Smith", "Brown", "Wilson", "Jones", "Lee" };
    private static final Random   random     = new Random();

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final PlayerController controller = new PlayerController( this );

    private final long      playerID;
    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final PlayerKey key;
    private final String    name;
    @ObjectMeta(ignoreFor = ObjectMeta.For.toString)
    private final Color     primaryColor;
    @ObjectMeta(ignoreFor = ObjectMeta.For.toString)
    private final Color     secondaryColor;
    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private final Map<Long, GameObject> objects = Collections.synchronizedMap( new HashMap<>() );

    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private int score;
    @ObjectMeta(ignoreFor = ObjectMeta.For.all)
    private int nextObjectSeed;

    public Player(final long playerID, @Nullable final PlayerKey key, final String name, final Color primaryColor,
                  final Color secondaryColor) {
        this.playerID = playerID;
        this.key = key;
        this.name = name;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( playerID );
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        return obj instanceof IPlayer && playerID == ((IPlayer) obj).getPlayerID();
    }

    @Override
    @Nonnull
    public PlayerController getController() {
        return controller;
    }

    @Override
    public long getPlayerID() {
        return playerID;
    }

    boolean hasKey(final PlayerKey playerKey) {
        return ObjectUtils.isEqual( key, playerKey );
    }

    boolean isKeyLess() {
        return key == null;
    }

    @Override
    public boolean isCurrentPlayer() {
        return ObjectUtils.isEqual( this, currentPlayer() );
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Color getPrimaryColor() {
        return primaryColor;
    }

    @Override
    public Color getSecondaryColor() {
        return secondaryColor;
    }

    ImmutableSet<GameObject> getObjects() {
        return ImmutableSet.copyOf( objects.values() );
    }

    public static String randomName() {
        return Joiner.on( ' ' ).join( firstNames[random.nextInt( firstNames.length )], lastNames[random.nextInt( lastNames.length )] );
    }

    @Override
    public int getScore() {
        return score;
    }

    void setScore(final int score) {
        ChangeInt.From scoreChange = ChangeInt.from( this.score );

        this.score = score;

        getController().getGameController().fire().onPlayerScore( this, scoreChange.to( this.score ) );
    }

    long nextObjectID() {
        return Hashing.murmur3_128().newHasher().putLong( playerID ).putInt( nextObjectSeed++ ).hash().asLong();
    }

    @Nonnull
    Optional<GameObject> getObject(final long objectId) {
        return Optional.ofNullable( objects.get( objectId ) );
    }

    void removeObject(final IGameObject gameObject) {
        IGameObject lostObject = objects.remove( gameObject.getObjectID() );
        Preconditions.checkState( lostObject == null || lostObject == gameObject );

        if (lostObject != null)
            getController().getGameController()
                           .fireIfPlayer( player -> ObjectUtils.isEqual( this, player ) )
                           .onPlayerLostObject( this, lostObject );
    }

    void addObjects(final GameObject gameObject) {
        GameObject previousObject = objects.put( gameObject.getObjectID(), gameObject );
        Preconditions.checkState( previousObject == null || previousObject == gameObject );

        if (previousObject == null)
            getController().getGameController()
                           .fireIfPlayer( player -> ObjectUtils.isEqual( this, player ) )
                           .onPlayerGainedObject( this, gameObject );
    }

    void addObjects(final IGameObject... gameObjects) {
        for (final IGameObject gameObject : gameObjects)
            addObjects( gameObject );
    }

    @Override
    public ImmutableMap<Long, IGameObject> getObjectsByID() {
        return ImmutableMap.copyOf( objects );
    }

    static Player cast(final IPlayer player) {
        return (Player) player;
    }
}
