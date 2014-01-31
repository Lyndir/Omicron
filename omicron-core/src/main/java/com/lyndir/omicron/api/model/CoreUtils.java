package com.lyndir.omicron.api.model;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.lyndir.lhunath.opal.system.error.InternalInconsistencyException;
import com.lyndir.lhunath.opal.system.util.NNFunctionNN;
import edu.umd.cs.findbugs.annotations.DefaultAnnotationForMethods;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;


/**
 * @author lhunath, 2013-08-24
 */
@Nonnull
public abstract class CoreUtils {

    @SuppressWarnings("unchecked")
    static GameObject coreGO(final IGameObject object) {
        return (GameObject) object;
    }

    @SuppressWarnings("unchecked")
    static Game coreG(final IGame object) {
        return (Game) object;
    }

    @SuppressWarnings("unchecked")
    static Player coreP(final IPlayer object) {
        return (Player) object;
    }

    @SuppressWarnings("unchecked")
    static List<Player> coreP(final List<? extends IPlayer> object) {
        return (List<Player>) object;
    }

    @SuppressWarnings("unchecked")
    static Tile coreT(final ITile object) {
        return (Tile) object;
    }

    static <M extends IModule> ModuleType<M> coreMT(final PublicModuleType<M> object) {
        return ModuleType.of(object);
    }

    @SuppressWarnings("unchecked")
    static UnitType coreUT(final IUnitType object) {
        return (UnitType) object;
    }

    @SuppressWarnings("unchecked")
    static VictoryConditionType coreVCT(final PublicVictoryConditionType object) {
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

    @SuppressWarnings("unchecked")
    static List<VictoryConditionType> coreVCT(final List<PublicVictoryConditionType> object) {
        return Lists.transform( object, new NNFunctionNN<PublicVictoryConditionType, VictoryConditionType>() {
            @Nonnull
            @Override
            public VictoryConditionType apply(@Nonnull final PublicVictoryConditionType input) {
                return coreVCT( input );
            }
        } );
    }

    @SuppressWarnings("unchecked")
    static PublicVictoryConditionType publicVCT(final VictoryConditionType object) {
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
