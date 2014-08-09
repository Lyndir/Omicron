package com.lyndir.omicron.api.core;

import com.lyndir.omicron.api.Director;
import com.lyndir.omicron.api.core.*;


/**
 * @author lhunath, 2013-08-18
 */
public class CoreDirector implements Director {

    @Override
    public IGame.IBuilder gameBuilder() {
        return Game.builder();
    }
}
