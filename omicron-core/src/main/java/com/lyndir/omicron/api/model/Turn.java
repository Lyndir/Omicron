package com.lyndir.omicron.api.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class Turn {

    @Nullable
    private final Turn previousTurn;
    private final int  number;

    public Turn() {

        previousTurn = null;
        number = 0;
    }

    public Turn(@NotNull final Turn previousTurn) {

        this.previousTurn = previousTurn;
        number = previousTurn.getNumber() + 1;
    }

    @Nullable
    public Turn getPreviousTurn() {

        return previousTurn;
    }

    public int getNumber() {

        return number;
    }
}
