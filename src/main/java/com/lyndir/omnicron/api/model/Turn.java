package com.lyndir.omnicron.api.model;

import org.jetbrains.annotations.Nullable;


public class Turn {

    @Nullable
    private final Turn previousTurn;

    public Turn(@Nullable final Turn previousTurn) {

        this.previousTurn = previousTurn;
    }

    @Nullable
    public Turn getPreviousTurn() {

        return previousTurn;
    }
}
