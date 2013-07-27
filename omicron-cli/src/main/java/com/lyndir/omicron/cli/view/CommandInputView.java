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

import com.google.common.base.Splitter;
import com.lyndir.lanterna.view.InputView;
import com.lyndir.omicron.cli.OmicronCLI;
import com.lyndir.omicron.cli.command.RootCommand;
import java.util.regex.Pattern;


/**
 * @author lhunath, 2013-07-24
 */
public class CommandInputView extends InputView {

    private static final Splitter commandSplitter = Splitter.on( Pattern.compile( "\\s+" ) ).omitEmptyStrings().trimResults();

    @Override
    protected void onEnterText(final String text) {
        logger.inf( getPromptText() + text );

        new RootCommand( OmicronCLI.get() ).evaluate( commandSplitter.split( text ).iterator() );
    }
}
