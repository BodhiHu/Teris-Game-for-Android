package com.shawnhu.mechanicsgames.tetris;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.security.InvalidParameterException;
import java.util.Random;


public class TetrisView extends TileView {
    static final String TETRIS_SCORE = "score";
    static final String TETRIS_STATE = "state";
    static final String TETRIS_VIEW  = "view";
    enum State {
        STARTED, PAUSED, RUNNING, OVER;
        static int getVofState(State s) {
            switch (s) {
                case STARTED:
                    return 0;
                case PAUSED:
                    return 1;
                case RUNNING:
                    return 2;
                case OVER:
                    return 3;
            }
            return -1;
        }

        static State getSofValue(int v) {
            switch (v) {
                case 0:
                    return STARTED;
                case 1:
                    return PAUSED;
                case 2:
                    return RUNNING;
                case 3:
                    return OVER;
            }

            throw new InvalidParameterException("valid range is from 0~3");
        }
    }

    private GameListener mGameListener;
    private State mState;
    private int mScore;

    private Random RND = new Random();

    public TetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TetrisView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            TetrisView.this.updateTetrisView();
            TetrisView.this.invalidate();
        }

        public void delayedUpdate(long millis) {
            removeMessages(0);
            sendMessageDelayed(obtainMessage(0), millis);

        }

    }
    private RefreshHandler mRefreshHandler = new RefreshHandler();
    private long mRefreshDelay = 600;
    protected void updateTetrisView() {
        updateTetris();
        mRefreshHandler.delayedUpdate(mRefreshDelay);
    }

    protected boolean initTetrisView() {
        clearTiles();
        return true;
    }

    public boolean setGameListener(GameListener l) {
        mGameListener = l;
        return (mGameListener != null);
    }

    public void saveGame(Bundle out) {
        out.putInt(TETRIS_SCORE, mScore);
        out.putInt(TETRIS_STATE, State.getVofState(mState));
        out.putParcelable(TETRIS_VIEW, this.mTileViewInfo);

    }
    protected boolean restoreTetris() {
    }
    public boolean startTetris(Bundle savedState) {
        initTetrisView();
        if (savedState != null) {
            mScore = savedState.getInt(TETRIS_SCORE);
            mState = State.getSofValue(savedState.getInt(TETRIS_STATE));
            this.mTileViewInfo = savedState.getParcelable(TETRIS_VIEW);
        } else {
            mScore = 0;
            mState = State.STARTED;
        }

        if (mGameListener != null) {
            mGameListener.onStarted();
        }

        mState = State.STARTED;
        return true;
    }
    public boolean pauseTetris() {
        return true;
    }
    public boolean runTetris() {
        return true;
    }
    protected void gameOver() {

    }

    protected boolean checkCollision() {
        return false;
    }
    //@return: if null game over
    protected Tetriminos newTetrimino() {
        int rn = RND.nextInt();
        rn %= Tetriminos.Tetrimino.totalTypes;
        return new Tetriminos(Tetriminos.Tetrimino.typeOfIdx(rn));
    }
    protected void updateTetris() {

    }
    protected void mergeTetris() {

    }

}
