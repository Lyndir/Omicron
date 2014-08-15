package com.lyndir.omicron.api;

/**
 * @author lhunath, 2013-08-18
 */
public class CoreDirector implements Director {

    @Override
    public IGame.IBuilder gameBuilder() {
        return Game.builder();
    }
}
