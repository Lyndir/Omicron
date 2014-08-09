package com.lyndir.omicron.api.core;

import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.omicron.api.ChangeInt;
import com.lyndir.omicron.api.GameListener;


/**
 * @author lhunath, 2013-08-14
 */
public enum VictoryConditionType {
    SUPREMACY {
        @Override
        void install(final Game game) {
            game.getController().addInternalGameListener( new GameListener() {
                @Override
                public void onPlayerLostObject(final IPlayer player, final IGameObject gameObject) {
                    if (!game.isRunning())
                        return;

                    Player supremePlayer = null;
                    for (final Player aPlayer : game.getPlayers())
                        if (!aPlayer.getObjects().isEmpty())
                            if (supremePlayer == null)
                                supremePlayer = aPlayer;
                            else
                                return;

                    game.getController().end( SUPREMACY, supremePlayer );
                }
            } );
        }
    },
    MIGRATION {
        @Override
        void install(final Game game) {
            // TODO
        }
    },
    MIGHT {
        public static final int MIGHT_SCORE_THRESHOLD = 10000;

        @Override
        void install(final Game game) {
            game.getController().addInternalGameListener( new GameListener() {
                private IPlayer mightyPlayer;
                private Turn mightySince;

                @Override
                public void onNewTurn(final Turn currentTurn) {
                    if (!game.isRunning())
                        return;

                    if (mightyPlayer != null)
                        if (currentTurn.getNumber() - mightySince.getNumber() >= 10)
                            game.getController().end( MIGHT, mightyPlayer );
                }

                @Override
                public void onPlayerScore(final IPlayer player, final ChangeInt score) {
                    if (mightyPlayer != null) {
                        // There is a mighty player, check if this player's new score dethrones him.
                        if (ObjectUtils.isEqual( mightyPlayer, player ))
                            return;

                        if (mightyPlayer.getScore() - score.getTo() < MIGHT_SCORE_THRESHOLD) {
                            // Score gap closed, mighty player dethroned.
                            mightyPlayer = null;
                            mightySince = null;
                        }
                    }

                    if (mightyPlayer == null) {
                        // There is no mighty player.  Check if a player has become mighty.
                        IPlayer mightiestPlayer = null;
                        boolean mightiestIsMighty = true;
                        for (final IPlayer aPlayer : game.getPlayers()) {
                            if (mightiestPlayer == null)
                                mightiestPlayer = aPlayer;
                            else if (aPlayer.getScore() > mightiestPlayer.getScore()) {
                                // aPlayer is mightier than current mightiest player.  Check if that also makes him mighty.
                                mightiestIsMighty = aPlayer.getScore() - mightiestPlayer.getScore() >= MIGHT_SCORE_THRESHOLD;
                                mightiestPlayer = aPlayer;
                            }
                        }
                        if (mightiestPlayer != null && mightiestIsMighty) {
                            // We found a mighty player.
                            mightyPlayer = mightiestPlayer;
                            mightySince = game.getTurns();
                        }
                    }
                }
            } );
        }
    },
    CAPTURE {
        @Override
        void install(final Game game) {
            // TODO
        }
    };

    abstract void install(Game game);
}
