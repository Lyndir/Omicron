/*
 *   Copyright 2010, Maarten Billemont
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
package com.lyndir.omicron.webapp.listener;

import com.google.inject.*;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.lyndir.omicron.model.ServiceModule;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;


/**
 * <h2>{@link OmicronGuiceContext}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Jan 11, 2010</i> </p>
 *
 * @author lhunath
 */
public class OmicronGuiceContext extends GuiceServletContextListener {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Injector getInjector() {

        return Guice.createInjector( Stage.DEVELOPMENT, new ServiceModule(), new ServletModule() {

            @Override
            protected void configureServlets() {

            }
        } );
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {

//        ifNotNull( Injector.class, get( servletContextEvent.getServletContext() ) ).getInstance( ObjectContainer.class ).close();

        super.contextDestroyed( servletContextEvent );
    }

    /**
     * @param servletContext The request's servlet context.
     *
     * @return The Guice {@link Injector} that was added to the given {@link ServletContext} on initialization.
     */
    public static Injector get(final ServletContext servletContext) {

        return (Injector) servletContext.getAttribute( Injector.class.getName() );
    }
}
