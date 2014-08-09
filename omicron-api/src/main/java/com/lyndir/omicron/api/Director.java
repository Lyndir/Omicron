package com.lyndir.omicron.api;

import com.lyndir.lhunath.opal.system.util.TypeUtils;
import com.lyndir.omicron.api.core.IGame;


/**
 * @author lhunath, 2013-08-18
 */
public interface Director {

    Director CORE_DIRECTOR = TypeUtils.<Director>newInstance( "com.lyndir.omicron.api.CoreDirector" ).get();
    Director REST_DIRECTOR = TypeUtils.<Director>newInstance( "com.lyndir.omicron.api.RESTDirector" ).get();

    IGame.IBuilder gameBuilder();
}
