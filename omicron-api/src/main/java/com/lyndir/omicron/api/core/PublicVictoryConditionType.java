package com.lyndir.omicron.api.core;

/**
 * @author lhunath, 2013-08-14
 */
public enum PublicVictoryConditionType {
    /**
     * The player that remains the last standing with live units ends the game a victor.
     */
    SUPREMACY,

    /**
     * The player that successfully launches and defends a migration ship to Omicron ends the game a victor.
     */
    MIGRATION,

    /**
     * The player that maintains a score of 10k more than that of all others for 10 turns ends the game a victor.
     */
    MIGHT,

    /**
     * The player that repairs and activates a global insurgency device and successfully defends it for 30 turns ends the game a victor.
     */
    CAPTURE
}
