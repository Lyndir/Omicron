package com.lyndir.omicron.webapp.data.service.impl.jpa;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.lyndir.lhunath.opal.jpa.Persist;


/**
 * @author lhunath
 */
@Singleton
public class JPAProvider implements Provider<Persist> {

    @Override
    public Persist get() {
        return Persist.persistence();
    }
}
