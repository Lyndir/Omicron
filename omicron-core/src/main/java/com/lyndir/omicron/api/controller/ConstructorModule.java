/*
 * Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.lyndir.omicron.api.controller;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;
import static com.lyndir.omicron.api.util.PathUtils.*;

import com.google.common.base.Optional;
import com.lyndir.lhunath.opal.system.util.*;
import com.lyndir.omicron.api.model.*;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;


public class ConstructorModule extends Module {

    private final int buildSpeed;
    private final Class<Module> buildsModule;

    public ConstructorModule(final int buildSpeed, final Class<Module> buildsModule) {
        this.buildSpeed = buildSpeed;
        this.buildsModule = buildsModule;
    }

    @Override
    public void onNewTurn() {

    }

    @Override
    public ModuleType<?> getType() {
        return ModuleType.CONSTRUCTOR;
    }
}
