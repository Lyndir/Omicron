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

package com.lyndir.omicron.cli;

import com.googlecode.lanterna.terminal.Terminal;


/**
 * @author lhunath, 2013-07-19
 */
public enum CLIThemes implements CLITheme {
    DEFAULT;

    @Override
    public Terminal.Color fg() {
        return Terminal.Color.WHITE;
    }

    @Override
    public Terminal.Color bg() {
        return Terminal.Color.BLACK;
    }

    @Override
    public String bgPattern() {
        return " ";
    }

    @Override
    public Terminal.Color barFg() {
        return Terminal.Color.WHITE;
    }

    @Override
    public Terminal.Color barBg() {
        return Terminal.Color.DEFAULT;
    }

    @Override
    public Terminal.Color textFg() {
        return Terminal.Color.WHITE;
    }

    @Override
    public Terminal.Color mapFg() {
        return Terminal.Color.BLACK;
    }
}
