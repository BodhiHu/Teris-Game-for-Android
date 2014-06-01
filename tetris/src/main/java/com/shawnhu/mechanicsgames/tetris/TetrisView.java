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
    static final String TETRIS_CURRENT = "current tetrimino"
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
    private static Tetriminos mTetrimino;
    private static int tetriminoStartX;
    private static int tetriminoStartY;

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
    private static final long mRefreshDelay = 600;
    protected void updateTetrisView() {
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
        out.putInt(TETRIS_CURRENT, Tetriminos.Tetrimino.idxOfType(mTetrimino.mT));
        out.putParcelable(TETRIS_VIEW, this.mTileViewInfo);

    }
    protected boolean restoreTetris() {
    }
    public boolean startTetris(Bundle savedState) {
        initTetrisView();
        if (savedState != null) {
            mScore = savedState.getInt(TETRIS_SCORE);
            mState = State.getSofValue(savedState.getInt(TETRIS_STATE));
            mTetrimino.mT = Tetriminos.Tetrimino.typeOfIdx(savedState.getInt(TETRIS_CURRENT));
            this.mTileViewInfo = savedState.getParcelable(TETRIS_VIEW);
        } else {
            mScore = 0;
            mState = State.STARTED;
        }

        if (mGameListener != null) {
            mGameListener.onStarted();
        }

        mState = State.STARTED;

        updateTetrisView();
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
    //@return: if false game over
    protected boolean genNewTetrimino() {
        int rn = RND.nextInt();
        rn %= Tetriminos.Tetrimino.totalTypes;
        mTetrimino = new Tetriminos(Tetriminos.Tetrimino.typeOfIdx(rn));
        calculateTetriminoStartXY();
        mTetrimino.setCoordinate(tetriminoStartX, tetriminoStartY);

        return true;
    }
    private boolean isCollided() {
        for (int i = 0; i < mTetrimino.mHeight; i++) {
            for (int j = 0; j < mTetrimino.mWidth; j++) {
                if (mTetrimino.mTetrimino[i][j] == 0) {
                    continue;
                }

                int tile_row = i + mTetrimino.mY;
                int tile_col = i + mTetrimino.mX;

                if (mTileViewInfo.mTileType[tile_row][tile_col] != EMPTY) {
                    return false;
                }
            }
        }

        return true;
    }
    private void calculateTetriminoStartXY() {
        tetriminoStartY = 0;
        tetriminoStartX = (mTileViewInfo.mXTileCount - mTetrimino.mWidth) / 2;
    }
    protected void mergeTetris() {

    }

}
