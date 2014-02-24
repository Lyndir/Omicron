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

package com.lyndir.lanterna.view;

import com.google.common.collect.ImmutableList;
import com.lyndir.omicron.cli.OmicronCLI;
import com.lyndir.omicron.cli.command.*;
import com.lyndir.omicron.cli.view.OmicronView;


/**
 * @author lhunath, 2013-07-25
 */
public class OmicronWindow extends Window {

    private final OmicronView view;

    public OmicronWindow() {
        super( new OmicronView() );

        view = (OmicronView) getRootView();
    }

    @Override
    protected void onReady() {
        super.onReady();

        new BuildCommand( OmicronCLI.get() ).game( ImmutableList.<String>of().iterator() );
        new AddGameCommand( OmicronCLI.get() ).player( ImmutableList.of( "Simon,red,red" ).iterator() );
        new CreateCommand( OmicronCLI.get() ).game( ImmutableList.<String>of().iterator() );
    }

    @Override
    protected synchronized boolean isRunning() {
        return super.isRunning() && OmicronCLI.get().isRunning();
    }

    public OmicronView getView() {
        return view;
    }
}
