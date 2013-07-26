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

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;


/**
 * @author lhunath, 2013-07-25
 */
public class Window {

    private final View    rootView;
    private       boolean running;

    public Window(final View rootView) {
        this.rootView = rootView;
    }

    public final void start() {
        new Thread( new Runnable() {
            @Override
            public void run() {
                loop();
            }
        } ).start();
    }

    private void loop() {
        Screen screen = new Screen( TerminalFacade.createTextTerminal() );
        screen.startScreen();
        screen.getTerminal().setCursorVisible( false );
        try {
            onStartup();
            View rootView = getRootView();

            while (isRunning()) {
                rootView.measure( screen );
                rootView.draw( screen );
                screen.refresh();

                // Check for input.
                for (Key key; (key = screen.readInput()) != null; )
                    rootView.handleKey( key );
            }
        }
        finally {
            onShutdown();
            screen.getTerminal().setCursorVisible( true );
            screen.stopScreen();
        }
    }

    protected void onStartup() {
        setRunning( true );
    }

    protected void onShutdown() {
        setRunning( false );
    }

    protected synchronized boolean isRunning() {
        return running;
    }

    protected synchronized void setRunning(final boolean running) {
        this.running = running;
    }

    public View getRootView() {
        return rootView;
    }
}
