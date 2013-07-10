package com.lyndir.omnicron.api.model;

import com.google.common.base.*;
import com.lyndir.lhunath.opal.system.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


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

    public Tile(final Coordinate position, final Level level) {

        this.position = position;
        this.level = level;

        level.putTile( position, this );
        level.getTile( position.neighbour( Coordinate.Side.NW ) ).get();
        level.getTile( position.neighbour( Coordinate.Side.NE ) ).get();
        level.getTile( position.neighbour( Coordinate.Side.W ) ).get();
        level.getTile( position.neighbour( Coordinate.Side.E ) ).get();
        level.getTile( position.neighbour( Coordinate.Side.SW ) ).get();
        level.getTile( position.neighbour( Coordinate.Side.SE ) ).get();
    }

    public Optional<GameObject> getContents() {

        return Optional.fromNullable( contents );
    }

    public void setContents(@Nullable final GameObject contents) {

        if (contents != null)
            Preconditions.checkState( !getContents().isPresent(), "Cannot put object on tile that is not empty: %s", this );

        this.contents = contents;
    }

    public Coordinate getPosition() {

        return position;
    }

    public Level getLevel() {

        return level;
    }

    @NotNull
    public Tile neighbour(final Coordinate.Side side) {

        return level.getTile( getPosition().neighbour( side ) ).get();
    }

    public boolean contains(@NotNull final GameObserver target) {

        if (contents == null)
            return false;

        if (ObjectUtils.isEqual( contents, target ))
            return true;

        Player player = contents.getPlayer();
        if (player != null)
            return ObjectUtils.isEqual( player.getController(), target );

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
}
