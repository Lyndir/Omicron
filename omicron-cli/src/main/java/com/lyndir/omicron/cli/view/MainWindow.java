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

package com.lyndir.omicron.cli.view;

import com.lyndir.lanterna.view.LinearView;
import com.lyndir.omicron.cli.view.CommandView;
import com.lyndir.omicron.cli.view.DashboardView;


/**
 * @author lhunath, 2013-07-19
 */
public class MainWindow extends LinearView {

    private final DashboardView dashboardView;
    private final CommandView   commandView;

    public MainWindow() {

        super( Orientation.VERTICAL );

        addChild( dashboardView = new DashboardView() );
        addChild( commandView = new CommandView() );
    }
}
