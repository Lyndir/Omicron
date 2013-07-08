package com.lyndir.omnicron.api.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.omnicron.api.controller.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * <i>10 15, 2012</i>
 *
 * @author lhunath
 */
public abstract class PlayerObject extends GameObject {

    private final Player owner;

    protected PlayerObject(final String typeName, final Player owner, final Tile location, final Module... modules) {

        super( typeName, owner.nextObjectID(), location, modules );

        this.owner = owner;
    }

    @NotNull
    @Override
    public final Player getPlayer() {

        return owner;
    }
}
