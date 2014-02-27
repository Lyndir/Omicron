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

import static com.lyndir.lhunath.opal.system.util.StringUtils.strf;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.inject.*;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.lyndir.lhunath.opal.jpa.Persist;
import com.lyndir.lhunath.opal.jpa.PersistFilter;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.omicron.webapp.data.service.*;
import com.lyndir.omicron.webapp.resource.*;
import com.lyndir.lhunath.opal.json.GsonJsonProvider;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import java.net.URI;
import java.util.Iterator;
import javax.persistence.Persistence;


/**
 * @author lhunath
 */
public class GuiceContext extends GuiceServletContextListener {

    static final Logger logger = Logger.get( GuiceContext.class );

    private static final String PATH_APP      = "/app/*";
    private static final String PATH_APP_REST = "/app/rest/*";

    @Override
    protected Injector getInjector() {
        return Guice.createInjector( Stage.DEVELOPMENT, new ServiceModule(), new ServletModule() {
            @Override
            protected void configureServlets() {
                logger.dbg( "Configuring persistence filter" );
                Persist persistence;
                @SuppressWarnings("CallToSystemGetenv") // Specifically for Heroku
                String databaseURL = System.getenv( "DATABASE_URL" );
                if (databaseURL != null) {
                    // Heroku container.
                    URI dbURI = URI.create( databaseURL );

                    // Determine username and password.
                    Iterator<String> userInfoIt = Splitter.on( ':' ).split( dbURI.getUserInfo() ).iterator();
                    String username = userInfoIt.next();
                    String password = userInfoIt.next();

                    // Determine JDBC connection URL.
                    String scheme = dbURI.getScheme();
                    Preconditions.checkState( "postgres".equals( scheme ), "Unsupported database provider: %s", scheme );
                    String url = strf( "jdbc:postgresql://%s:%d%s", dbURI.getHost(), dbURI.getPort(), dbURI.getPath() );

                    // Build JPA connection properties.
                    ImmutableMap.Builder<String, String> properties = ImmutableMap.builder();
                    properties.put( "javax.persistence.jdbc.url", url );
                    properties.put( "javax.persistence.jdbc.user", username );
                    properties.put( "javax.persistence.jdbc.password", password );
                    properties.put( "javax.persistence.jdbc.driver", "org.postgresql.Driver" );
                    properties.put( "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect" );
                    properties.put( "hibernate.hbm2ddl.auto", "update" );
                    properties.put( "hibernate.show_sql", "false" );
                    persistence = new Persist( Persistence.createEntityManagerFactory( Persist.DEFAULT_UNIT, properties.build() ) );
                } else
                    persistence = new Persist();
                filter( PATH_APP ).through( new PersistFilter( persistence ) );

                logger.dbg( "Configuring API services" );
                bind( StateManager.class );
                bind( SessionManager.class );

                logger.dbg( "Configuring API resources" );
                serve( PATH_APP_REST ).with( GuiceContainer.class ); // Jersey
                bind( GsonJsonProvider.class );
                bind( UserResource.class );
                bind( GameResource.class );
                bind( GameBuilderResource.class );
            }
        } );
    }
}
