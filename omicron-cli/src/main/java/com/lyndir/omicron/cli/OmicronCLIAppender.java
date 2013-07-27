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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;


/**
 * @author lhunath, 2013-07-27
 */
public class OmicronCLIAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(final ILoggingEvent eventObject) {

        StringBuilder logMessage = new StringBuilder();
        if (!Level.INFO.equals( eventObject.getLevel() ))
            logMessage.append( '[' ).append( eventObject.getLevel().levelStr ).append( "] " );
        if (eventObject.getMarker() != null )
            logMessage.append( eventObject.getMarker() );
        logMessage.append( eventObject.getFormattedMessage() );

        OmicronCLI.get().getLog().add( logMessage.toString() );
    }
}
