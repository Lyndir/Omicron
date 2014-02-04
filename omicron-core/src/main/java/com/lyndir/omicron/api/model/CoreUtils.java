package com.lyndir.omicron.api.model;

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

    @Nullable
    @SuppressWarnings("unchecked")
    static GameObject coreGO(@Nullable final IGameObject object) {
        return (GameObject) object;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    static Game coreG(@Nullable final IGame object) {
        return (Game) object;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    static Player coreP(@Nullable final IPlayer object) {
        return (Player) object;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    static List<Player> coreP(@Nullable final List<? extends IPlayer> object) {
        return (List<Player>) object;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    static Tile coreT(@Nullable final ITile object) {
        return (Tile) object;
    }

    static <M extends IModule> ModuleType<M> coreMT(final PublicModuleType<M> object) {
        return ModuleType.of( object );
    }

    @Nullable
    @SuppressWarnings("unchecked")
    static UnitType coreUT(@Nullable final IUnitType object) {
        return (UnitType) object;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    static VictoryConditionType coreVCT(@Nonnull final PublicVictoryConditionType object) {
        switch (object) {

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

    @Nonnull
    @SuppressWarnings("unchecked")
    static List<VictoryConditionType> coreVCT(@Nonnull final List<PublicVictoryConditionType> object) {
        return Lists.transform( object, new NNFunctionNN<PublicVictoryConditionType, VictoryConditionType>() {
            @Nonnull
            @Override
            public VictoryConditionType apply(@Nonnull final PublicVictoryConditionType input) {
                return coreVCT( input );
            }
        } );
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    static PublicVictoryConditionType publicVCT(@Nonnull final VictoryConditionType object) {
        switch (object) {

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
