package com.lyndir.omicron.api.model;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;

import com.google.common.base.*;
import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.util.Maybe;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@ObjectMeta(useFor = { })
public class Tile extends MetaObject {

    @Nullable
    private       GameObject contents;
    @ObjectMeta(useFor = ObjectMeta.For.all)
    private final Coordinate position;
    @ObjectMeta(useFor = ObjectMeta.For.all)
    private final Level      level;
    @ObjectMeta(useFor = ObjectMeta.For.all)
    private final Map<ResourceType, Integer> resourceQuantities = new EnumMap<>( ResourceType.class );

    Tile(final Coordinate position, final Level level) {
        this.position = position;
        this.level = level;
    }

    Tile(final int u, final int v, final Level level) {
        this( new Coordinate( u, v, level.getSize() ), level );
    }

    @Nonnull
    public Optional<GameObject> getContents() {
        return Optional.fromNullable( contents );
    }

    @Nonnull
    public Maybe<GameObject> checkContents() {
        if (!Security.isAuthenticated() || !Security.getCurrentPlayer().canObserve( this ))
            return Maybe.unknown();

        return Maybe.fromNullable( contents );
    }

    void setContents(@Nullable final GameObject contents) {
        if (contents != null)
            Preconditions.checkState( this.contents == null, "Cannot put object on tile that is not empty: %s", this );

        this.contents = contents;

        getLevel().getGame().getController().fireFor( new PredicateNN<Player>() {
            @Override
            public boolean apply(@Nonnull final Player input) {
                return input.canObserve( Tile.this );
            }
        } ).onChange( this );
    }

    public Coordinate getPosition() {
        return position;
    }

    public Level getLevel() {
        return level;
    }

    void setResourceQuantity(final ResourceType resourceType, final int resourceQuantity) {
        Preconditions.checkArgument( resourceQuantity >= 0, "Resource quantity cannot be less than zero: %s", resourceQuantity );
        if (resourceQuantity > 0)
            resourceQuantities.put( resourceType, resourceQuantity );
        else
            resourceQuantities.remove( resourceType );

        getLevel().getGame().getController().fireFor( new PredicateNN<Player>() {
            @Override
            public boolean apply(@Nonnull final Player input) {
                return input.canObserve( Tile.this );
            }
        } ).onChange( this );
    }

    void addResourceQuantity(final ResourceType resourceType, final int resourceQuantity) {
        setResourceQuantity( resourceType, ifNotNullElse( resourceQuantities.get( resourceType ), 0 ) + resourceQuantity );
    }

    Optional<Integer> getResourceQuantity(final ResourceType resourceType) {
        return Optional.fromNullable( resourceQuantities.get( resourceType ) );
    }

    public Maybe<Integer> checkResourceQuantity(final ResourceType resourceType) {
        if (!Security.isAuthenticated() || !Security.getCurrentPlayer().canObserve( this ))
            return Maybe.unknown();

        return Maybe.fromNullable( resourceQuantities.get( resourceType ) );
    }

    @Nonnull
    Tile neighbour(final Coordinate.Side side) {
        return level.getTile( getPosition().neighbour( side ) ).get();
    }

    ImmutableList<Tile> neighbours() {
        ImmutableList.Builder<Tile> neighbours = ImmutableList.builder();
        for (final Coordinate.Side side : Coordinate.Side.values())
            neighbours.add( neighbour( side ) );

        return neighbours.build();
    }

    boolean contains(@Nonnull final GameObserver target) {
        if (contents == null)
            return false;

        if (ObjectUtils.isEqual( contents, target ))
            return true;

        Optional<Player> owner = contents.getOwner();
        if (owner.isPresent())
            return ObjectUtils.isEqual( owner.get(), target ) || ObjectUtils.isEqual( owner.get().getController(), target );

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( position, level );
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Tile))
            return false;

        Tile o = (Tile) obj;
        return ObjectUtils.isEqual( position, o.position ) && ObjectUtils.isEqual( level, o.level );
    }

    boolean isAccessible() {
        return checkContents().presence() == Maybe.Presence.ABSENT;
    }
}
