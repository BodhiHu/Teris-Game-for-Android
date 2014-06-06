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

    static final int DOWN = 0;
    static final int LEFT = 1;
    static final int RIGHT = 2;
    static final int UP = 3;

    private GameListener mGameListener;
    State mState;
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
            TetrisView.this.invalidate();
            TetrisView.this.updateTetrisView(GRAVITY);
        }

        public void delayedUpdate(long millis) {
            removeMessages(0);
            sendMessageDelayed(obtainMessage(0), millis);

        }
        public void pause() {
            removeMessages(0);
        }
        public void run() {
            delayedUpdate(mRefreshDelay);
        }

    }
    private RefreshHandler mRefreshHandler = new RefreshHandler();
    private static long mRefreshDelay = 600;
    private static final int UI = 0;
    private static final int GRAVITY = 0;
    protected void updateTetrisView(int how) {
        if (mState == State.RUNNING) {
            if (how == GRAVITY) {
                if (mvTetrimino(DOWN, 1) <= 0) {
                    if (!genNewTetrimino()) {
                        mState = State.OVER;
                        mGameListener.onGameOver();
                    }
                }
            }
        }
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

        invalidate();
        return true;
    }
    public boolean pauseTetris() {
        mRefreshHandler.pause();
        mGameListener.onPaused();
        mState = State.PAUSED;
        return true;
    }
    public boolean runTetris() {
        mRefreshHandler.run();
        mGameListener.onRun();
        mState = State.RUNNING;
        return true;
    }
    public int mvTetrimino(int direction, int steps) {
        int deltaDis;
        int greenDis;
        int oldX = mTetrimino.mX;
        int oldY = mTetrimino.mY;
        switch (direction) {
            case DOWN:
                deltaDis = deltaHeight(mTetrimino.mX, mTetrimino.mY);
                greenDis = deltaDis < steps ? deltaDis : steps;
                mTetrimino.setCoordinate(mTetrimino.mX, mTetrimino.mY + greenDis);
                if (greenDis == deltaDis) {
                    mergeTetris();
                }
                break;
            case LEFT:
                deltaDis = deltaWidthL(mTetrimino.mX, mTetrimino.mY);
                greenDis = deltaDis < steps ? deltaDis : steps;
                mTetrimino.setCoordinate(mTetrimino.mX - greenDis, mTetrimino.mY);
                break;
            case RIGHT:
                deltaDis = deltaWidthR(mTetrimino.mX, mTetrimino.mY);
                greenDis = deltaDis < steps ? deltaDis : steps;
                mTetrimino.setCoordinate(mTetrimino.mX + greenDis, mTetrimino.mY);
                break;
            default:
                greenDis = -1;
                break;
        }


        return greenDis;
    }
    public boolean rotateTetrimino() {
        mRefreshHandler.pause();
        mTetrimino.rotateTeriminoClockWise90(mTetrimino);
        if (isCollided(mTetrimino.mX, mTetrimino.mY)) {
            //TODO: ugly wordaround
            mTetrimino.rotateTeriminoClockWise90(mTetrimino);
            mTetrimino.rotateTeriminoClockWise90(mTetrimino);
            mTetrimino.rotateTeriminoClockWise90(mTetrimino);
            return false;
        }

        mRefreshHandler.run();
        updateTetrisView(UI);
        return true;
    }

    //@return: if false game over
    protected boolean genNewTetrimino() {
        int rn = RND.nextInt();
        rn %= Tetriminos.Tetrimino.totalTypes;
        mTetrimino = new Tetriminos(Tetriminos.Tetrimino.typeOfIdx(rn));
        calculateTetriminoStartXY();
        mTetrimino.setCoordinate(tetriminoStartX, tetriminoStartY);

        if (isCollided(mTetrimino.mX, mTetrimino.mY)) {
            return false;
        }

        return true;
    }
    private boolean isCollided(int X, int Y) {
        if ((X + mTetrimino.mWidth - 1) > (mTileViewInfo.mXTileCount - 1) ||
                (Y + mTetrimino.mHeight - 1) > (mTileViewInfo.mYTileCount - 1)) {
            return false;
        }
        for (int i = 0; i < mTetrimino.mHeight; i++) {
            for (int j = 0; j < mTetrimino.mWidth; j++) {
                if (mTetrimino.mTetrimino[i][j] == 0) {
                    continue;
                }

                int tile_row = i + Y;
                int tile_col = i + X;

                if (mTileViewInfo.mTileType[tile_row][tile_col] != EMPTY) {
                    return false;
                }
            }
        }

        return true;
    }
    private int deltaHeight(int X, int Y) {
        int minDeltaH = mTileViewInfo.mYTileCount - 1;
        for (int i = X; i < mTetrimino.mWidth + X; i++) {
            for (int j = mTetrimino.mHeight + Y - 1; j >= Y; j--) {
                if (mTileViewInfo.mTileType[j][i] != EMPTY) {
                    int deltaH = 0;
                    for (j += 1; j < mTileViewInfo.mYTileCount &&
                                    (mTileViewInfo.mTileType[j][i] == EMPTY); j++, deltaH++) {
                    }

                    minDeltaH = minDeltaH > deltaH ? deltaH : minDeltaH;
                    break;
                }

            }
        }

        return minDeltaH;
    }
    private int deltaWidthL(int X, int Y) {
        int minDeltaWL = mTileViewInfo.mXTileCount - 1;

        for (int i = Y; i < mTetrimino.mHeight + Y; i++) {
            for (int j = X; j < mTetrimino.mWidth + X; j++) {
                if (mTileViewInfo.mTileType[i][j] != EMPTY) {
                    int deltaWL = 0;
                    for (j -= 1; j >= 0 &&
                                 (mTileViewInfo.mTileType[i][j] == EMPTY); j--, deltaWL++) {
                    }

                    minDeltaWL = minDeltaWL > deltaWL ? deltaWL : minDeltaWL;
                    break;
                }
            }
        }

        return minDeltaWL;
    }
    private int deltaWidthR(int X, int Y) {
        int minDeltaWR = mTileViewInfo.mXTileCount - 1;

        for (int i = Y; i < mTetrimino.mHeight + Y; i++) {
            for (int j = X + mTetrimino.mWidth - 1; j >= X; j--) {
                if (mTileViewInfo.mTileType[i][j] != EMPTY) {
                    int deltaWR = 0;
                    for (j += 1; j < mTileViewInfo.mXTileCount &&
                                 (mTileViewInfo.mTileType[i][j] == EMPTY); j++, deltaWR++) {
                    }

                    minDeltaWR = minDeltaWR > deltaWR ? deltaWR : minDeltaWR;
                    break;
                }
            }
        }

        return minDeltaWR;
    }

    private void updateTetrimino(int oldX, int oldY) {
        for (int i = oldY; i < (oldY + mTetrimino.mHeight); i++) {
            for (int j = oldX; j < (oldX + mTetrimino.mWidth); j++) {
                setTileBmp(0, i, j);
                setTileColor(mTetrimino.mColor, (i-oldY+mTetrimino.mY), (j-oldX+mTetrimino.mX));
            }
        }

        updateTetrisView(UI);
    }
    private void calculateTetriminoStartXY() {
        tetriminoStartY = 0;
        tetriminoStartX = (mTileViewInfo.mXTileCount - mTetrimino.mWidth) / 2;
    }
    protected void mergeTetris() {
        int is_row_filled = 0xff;
        int score = 10;
        int totalScore = 0;
        float speed = 1;
        for (int i = 0; i < mTetrimino.mHeight; i++) {
            for (int j = 0; j < mTetrimino.mWidth; j++) {
                is_row_filled &= mTileViewInfo.mTileType[i][j];
            }
            if (is_row_filled != 0x00) {
                totalScore += score;
                score *= 1.1;
                speed *= 1.1;

                //let gone the filled row
                for (int k = 0; k < mTetrimino.mWidth; k++) {
                    setTileBmp(EMPTY, i, k);
                }
                //move down the top rows
                for (int l = i; l >= 0; l--) {
                    for (int m = 0; m <=mTetrimino.mWidth; m++) {
                        if (l > 0) {
                            mTileViewInfo.mTileType[l][m] = mTileViewInfo.mTileType[l-1][m];
                            mTileViewInfo.mTileInfo[l][m] = mTileViewInfo.mTileInfo[l-1][m];
                            mTileViewInfo.mTileNeedsRedraw[l][m] = true;
                        }
                        if (l == 0) {
                            setTileBmp(EMPTY, l, m);
                        }
                    }
                }

            }
        }

        mScore += totalScore;
        mGameListener.onUpdateScore(mScore);
        mRefreshDelay /= speed;
    }

}
