package com.lyndir.omicron.api;

import com.lyndir.omicron.api.model.*;


/**
 * @author lhunath, 2013-08-18
 */
public class DirectorImpl implements Director {

    @Override
    public IGame.IBuilder gameBuilder() {
        return Game.builder();
    }
}
