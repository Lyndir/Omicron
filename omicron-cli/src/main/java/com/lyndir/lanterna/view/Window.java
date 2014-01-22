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
    private       boolean ready;

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
        // Initialize
        Screen screen = TerminalFacade.createScreen( TerminalFacade.createTextTerminal() );
        try {
            onStartup( screen );
            setRunning( true );
            while (isRunning()) {
                // Measure
                getRootView().measure( screen, new Box( 0, screen.getTerminalSize().getColumns(), screen.getTerminalSize().getRows(), 0 ) );
                if (!isReady())
                    fireReady();

                // Draw
                getRootView().draw( screen );
                screen.refresh();

                // Input
                for (Key key; (key = screen.readInput()) != null; )
                    getRootView().handleKey( key );
            }
        }
        finally {
            setRunning( false );
            onShutdown( screen );
        }
    }

    private void fireReady() {
        fireReadyView( getRootView() );
        onReady();
    }

    private static void fireReadyView(final View view) {
        for (final View child : view.getChildren()) {
            child.onReady();
            fireReadyView( child );
        }
    }

    /**
     * The view hierarchy has been fully measured for the first time and all views have been notified.
     */
    protected void onReady() {
        setReady( true );
    }

    protected void onStartup(final Screen screen) {
        // Start screen and resize to terminal size.
        screen.startScreen();
        while (screen.readInput() != null)
            ;
        screen.refresh();
    }

    protected void onShutdown(final Screen screen) {
        screen.stopScreen();
    }

    protected synchronized boolean isRunning() {
        return running;
    }

    private synchronized void setRunning(final boolean running) {
        this.running = running;
    }

    public synchronized boolean isReady() {
        return ready;
    }

    public synchronized void setReady(final boolean ready) {
        this.ready = ready;
    }

    public View getRootView() {
        return rootView;
    }
}
