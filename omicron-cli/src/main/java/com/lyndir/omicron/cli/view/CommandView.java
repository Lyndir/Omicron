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

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.lyndir.lanterna.view.*;
import com.lyndir.omicron.cli.OmicronCLI;
import javax.annotation.Nonnull;


/**
 * @author lhunath, 2013-07-20
 */
public class CommandView extends TitledView {

    private final TextView  logView;
    private final InputView commandInputView;

    public CommandView() {
        super( "Command Log" );

        LinearView content = new LinearView( LinearView.Orientation.VERTICAL );
        content.addChild( logView = new TextView() {
            @Override
            public String getText() {
                return Joiner.on( '\n' ).join( OmicronCLI.get().getLog() );
            }
        } );
        content.addChild( commandInputView = new CommandInputView() );
        commandInputView.setControlTextView( logView );
        addChild( content );
    }

    @Nonnull
    @Override
    public Optional<?> layoutValue(final LayoutParameter layoutParameter) {
        if (layoutParameter == LinearView.Parameters.DESIRED_SPACE)
            return Optional.of( 10 );

        return super.layoutValue( layoutParameter );
    }
}
