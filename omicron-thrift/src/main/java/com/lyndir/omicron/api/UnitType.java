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

package com.lyndir.omicron.api;

import com.google.common.collect.ImmutableList;
import com.lyndir.omicron.api.core.IUnitType;


/**
 * @author lhunath, 2013-08-03
 */
public interface UnitType extends IUnitType {

    @Override
    String getTypeName();

    @Override
    int getConstructionWork();

    @Override
    ImmutableList<? extends Module> createModules();
}
