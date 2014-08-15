package com.lyndir.omicron.api;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;
import static com.lyndir.omicron.api.Security.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.math.Vec2;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.util.Maybe;
import java.util.*;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
@ObjectMeta(useFor = { })
public class Tile extends MetaObject implements ITile {

    @Nullable
    private       GameObject contents;
    @ObjectMeta(useFor = ObjectMeta.For.all)
    private final Vec2       position;
    @ObjectMeta(useFor = ObjectMeta.For.all)
    private final Level      level;
    @ObjectMeta(useFor = ObjectMeta.For.all)
    private final Map<ResourceType, Integer> resourceQuantities = Collections.synchronizedMap( new EnumMap<>( ResourceType.class ) );

    Tile(final Vec2 position, final Level level) {
        this.position = position;
        this.level = level;
    }

    Tile(final int x, final int y, final Level level) {
        this( Vec2.create( x, y ), level );
    }

    @Override
    public int hashCode() {
        return Objects.hash( position, level );
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Tile))
            return false;

        Tile o = (Tile) obj;
        return isEqual( position, o.position ) && isEqual( level, o.level );
    }

    @Override
    @Nonnull
    public Maybe<? extends IGameObject> getContents() {
        if (!isGod() && !currentPlayer().canObserve( this ).isTrue())
            // Cannot observe tile.
            return Maybe.unknown();

        return Maybe.ofNullable( contents );
    }

    void setContents(@Nullable final GameObject contents) {
        if (contents != null)
            Preconditions.checkState( this.contents == null || this.contents.equals( contents ),
                                      "Cannot put object on tile that is not empty: %s", this );

        replaceContents( contents );
    }

    void replaceContents(@SuppressWarnings("ParameterHidesMemberVariable") @Nullable final GameObject contents) {
        Change.From<IGameObject> contentsChange = Change.<IGameObject>from( this.contents );

        this.contents = contents;
        if (contents != null)
            contents.setLocation( this );

        Security.currentGame().getController().fireIfObservable( this ) //
                .onTileContents( this, contentsChange.to( this.contents ) );
    }

    @Override
    public Vec2 getPosition() {
        return position;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    void setResourceQuantity(final ResourceType resourceType, final int resourceQuantity) {
        Preconditions.checkArgument( resourceQuantity >= 0, "Resource quantity cannot be less than zero: %s", resourceQuantity );
        ChangeInt.From quantityChange;
        if (resourceQuantity > 0)
            quantityChange = ChangeInt.from( resourceQuantities.put( resourceType, resourceQuantity ) );
        else
            quantityChange = ChangeInt.from( resourceQuantities.remove( resourceType ) );

        Security.currentGame().getController().fireIfObservable( this ) //
                .onTileResources( this, resourceType, quantityChange.to( resourceQuantity ) );
    }

    void addResourceQuantity(final ResourceType resourceType, final int resourceQuantity) {
        setResourceQuantity( resourceType, ifNotNullElse( resourceQuantities.get( resourceType ), 0 ) + resourceQuantity );
    }

    @Override
    public ImmutableMap<ResourceType, Maybe<Integer>> getQuantitiesByResourceType() {
        boolean observable = isGod() || currentPlayer().canObserve( this ).isTrue();

        ImmutableMap.Builder<ResourceType, Maybe<Integer>> builder = ImmutableMap.builder();
        for (final ResourceType resourceType : ResourceType.values())
            if (observable)
                builder.put( resourceType, Maybe.of( ifNotNullElse( resourceQuantities.get( resourceType ), 0 ) ) );
            else
                builder.put( resourceType, Maybe.unknown() );

        return Maps.immutableEnumMap( builder.build() );
    }

    @Override
    public Optional<? extends IPlayer> getOwner() {
        return contents == null? Optional.empty(): contents.getOwner();
    }

    @Override
    public Maybe<? extends ITile> getLocation() {
        boolean observable = !isGod() && !currentPlayer().canObserve( this ).isTrue();
        return observable? Maybe.of( this ): Maybe.unknown();
    }

    public static Tile cast(final ITile tile) {
        return (Tile) tile;
    }

    @SuppressWarnings("unchecked")
    public static Iterable<Tile> cast(final Iterable<? extends ITile> neighbours) {
        return (Iterable<Tile>) neighbours;
    }
}
