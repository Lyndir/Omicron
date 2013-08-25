package com.lyndir.omicron.api;

import com.lyndir.lhunath.opal.system.util.TypeUtils;
import com.lyndir.omicron.api.model.IGame;


/**
 * @author lhunath, 2013-08-18
 */
public interface Director {

    Director DIRECTOR = TypeUtils.newInstance( "com.lyndir.omicron.api.DirectorImpl" );

    IGame.IBuilder gameBuilder();
}
