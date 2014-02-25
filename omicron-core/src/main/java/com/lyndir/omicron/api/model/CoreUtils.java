package com.lyndir.omicron.api.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.lyndir.lhunath.opal.system.error.InternalInconsistencyException;
import com.lyndir.lhunath.opal.system.util.NNFunctionNN;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * @author lhunath, 2013-08-24
 */
@Nonnull
public abstract class CoreUtils {

    @SuppressWarnings("unchecked")
    static GameObject coreGO(final IGameObject object) {
        return (GameObject) checkNotNull( object );
    }

    @SuppressWarnings("unchecked")
    @Nullable static GameObject coreGON(@Nullable final IGameObject object) {
        return (GameObject) object;
    }

    @SuppressWarnings("unchecked")
    static Game coreG(final IGame object) {
        return (Game) checkNotNull( object );
    }

    @SuppressWarnings("unchecked")
    static Player coreP(final IPlayer object) {
        return (Player) checkNotNull( object );
    }

    @SuppressWarnings("unchecked")
    static List<Player> coreP(final List<? extends IPlayer> object) {
        return (List<Player>) checkNotNull( object );
    }

    @SuppressWarnings("unchecked")
    static Tile coreT(final ITile object) {
        return (Tile) checkNotNull( object );
    }

    static <M extends IModule> ModuleType<M> coreMT(final PublicModuleType<M> object) {
        return ModuleType.of( checkNotNull( object ) );
    }

    @SuppressWarnings("unchecked")
    static UnitType coreUT(final IUnitType object) {
        return (UnitType) checkNotNull( object );
    }

    @SuppressWarnings("unchecked")
    static VictoryConditionType coreVCT(final PublicVictoryConditionType object) {
        switch (checkNotNull( object )) {

            case SUPREMACY:
                return VictoryConditionType.SUPREMACY;
            case MIGRATION:
                return VictoryConditionType.MIGRATION;
            case MIGHT:
                return VictoryConditionType.MIGHT;
            case CAPTURE:
                return VictoryConditionType.CAPTURE;
        }

        throw new InternalInconsistencyException( "Unsupported type: " + object );
    }

    @SuppressWarnings("unchecked")
    static List<VictoryConditionType> coreVCT(final List<PublicVictoryConditionType> object) {
        return Lists.transform( checkNotNull( object ), new NNFunctionNN<PublicVictoryConditionType, VictoryConditionType>() {
            @Nonnull
            @Override
            public VictoryConditionType apply(@Nonnull final PublicVictoryConditionType input) {
                return coreVCT( input );
            }
        } );
    }

    @SuppressWarnings("unchecked")
    static PublicVictoryConditionType publicVCT(final VictoryConditionType object) {
        switch (checkNotNull( object )) {

            case SUPREMACY:
                return PublicVictoryConditionType.SUPREMACY;
            case MIGRATION:
                return PublicVictoryConditionType.MIGRATION;
            case MIGHT:
                return PublicVictoryConditionType.MIGHT;
            case CAPTURE:
                return PublicVictoryConditionType.CAPTURE;
        }

        throw new InternalInconsistencyException( "Unsupported type: " + object );
    }
}
