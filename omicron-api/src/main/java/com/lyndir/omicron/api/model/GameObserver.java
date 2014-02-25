package com.lyndir.omicron.api.model;

import com.google.common.base.Optional;
import com.lyndir.omicron.api.Authenticated;
import com.lyndir.omicron.api.model.*;
import com.lyndir.omicron.api.util.Maybool;
import javax.annotation.Nonnull;


/**
 * <i>10 16, 2012</i>
 *
 * @author lhunath
 */
public interface GameObserver {

    /**
     * Check whether the current object can observe given observable.
     *
     * @param observable The observable that this observer is trying to see.
     *
     * @return true if the current player is able and allowed to observe the target.
     */
    @Authenticated
    Maybool canObserve(@Nonnull GameObservable observable)
            throws Security.NotAuthenticatedException;

    /**
     * Enumerate the tiles this observer can observe.
     *
     * @return All the tiles observable both by this observer and the current player.
     */
    @Nonnull
    @Authenticated
    Iterable<? extends ITile> listObservableTiles()
            throws Security.NotAuthenticatedException, Security.NotObservableException;
}
