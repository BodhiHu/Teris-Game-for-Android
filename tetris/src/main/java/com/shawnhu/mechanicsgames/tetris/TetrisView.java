package com.shawnhu.mechanicsgames.tetris;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.security.InvalidParameterException;
import java.util.Random;


public class TetrisView extends TileView {
    static final String TETRIS_SCORE = "score";
    static final String TETRIS_STATE = "state";
    static final String TETRIS_CURRENT_TYPE = "current tetrimino type";
    static final String TETRIS_CURRENT_X = "current tetrimino x";
    static final String TETRIS_CURRENT_Y = "current tetrimino y";
    static final String TETRIS_VIEW  = "view";
    enum State {
        IDLE, STARTED, PAUSED, RUNNING, OVER;
        static int getVofState(State s) {
            switch (s) {
                case IDLE:
                    return 0;
                case STARTED:
                    return 1;
                case PAUSED:
                    return 2;
                case RUNNING:
                    return 3;
                case OVER:
                    return 4;
            }
            return -1;
        }

        static State getSofValue(int v) {
            switch (v) {
                case 0:
                    return IDLE;
                case 1:
                    return STARTED;
                case 2:
                    return PAUSED;
                case 3:
                    return RUNNING;
                case 4:
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
    State mState = State.IDLE;
    private int mScore;
    private static int tetriminoStartX;
    private static int tetriminoStartY;
    private int NeXT = -1;
    private Tetriminos mTetrimino;

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
    static long mRefreshDelay = 100;
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

    public boolean setGameListener(GameListener l) {
        mGameListener = l;
        return (mGameListener != null);
    }

    public void saveGame(Bundle out) {
        out.putInt(TETRIS_SCORE, mScore);
        out.putInt(TETRIS_STATE, State.getVofState(mState));
        out.putInt(TETRIS_CURRENT_TYPE, Tetriminos.Tetrimino.idxOfType(mTetrimino.mT));
        out.putInt(TETRIS_CURRENT_X, Tetriminos.mX);
        out.putInt(TETRIS_CURRENT_Y, Tetriminos.mY);
        out.putParcelable(TETRIS_VIEW, this.mTileViewInfo);

    }
    public boolean startTetris(Bundle savedState) {
        clearTiles();
        if (savedState != null) {
            mScore = savedState.getInt(TETRIS_SCORE);
            mState = State.getSofValue(savedState.getInt(TETRIS_STATE));
            mTetrimino.mT = Tetriminos.Tetrimino.typeOfIdx(savedState.getInt(TETRIS_CURRENT_TYPE));
            mTetrimino = new Tetriminos(mTetrimino.mT);
            mTetrimino.setCoordinate(savedState.getInt(TETRIS_CURRENT_X), savedState.getInt(TETRIS_CURRENT_Y));
            this.mTileViewInfo = savedState.getParcelable(TETRIS_VIEW);
        } else {
            mScore = 0;
            mState = State.STARTED;
        }

        mState = State.STARTED;
        invalidate();

        if (mGameListener != null) {
            mGameListener.onStarted();
        }
        return true;
    }
    public boolean pauseTetris() {
        mRefreshHandler.pause();
        mGameListener.onPaused();
        mState = State.PAUSED;
        return true;
    }
    public boolean runTetris() {
        if (mState == State.STARTED) {
            genNewTetrimino();
        }

        mRefreshHandler.run();
        mState = State.RUNNING;
        mGameListener.onRun();
        return true;
    }
    public int mvTetrimino(int direction, int steps) {
        int deltaDis;
        int greenDis;
        int oldX = Tetriminos.mX;
        int oldY = Tetriminos.mY;
        switch (direction) {
            case DOWN:
                deltaDis = deltaHeight(Tetriminos.mX, Tetriminos.mY);
                greenDis = deltaDis < steps ? deltaDis : steps;
                mTetrimino.setCoordinate(Tetriminos.mX, Tetriminos.mY + greenDis);
                if (greenDis == deltaDis) {
                    mergeTetris();
                }
                break;
            case LEFT:
                deltaDis = deltaWidthL(Tetriminos.mX, Tetriminos.mY);
                greenDis = deltaDis < steps ? deltaDis : steps;
                mTetrimino.setCoordinate(Tetriminos.mX - greenDis, Tetriminos.mY);
                break;
            case RIGHT:
                deltaDis = deltaWidthR(Tetriminos.mX, Tetriminos.mY);
                greenDis = deltaDis < steps ? deltaDis : steps;
                mTetrimino.setCoordinate(Tetriminos.mX + greenDis, Tetriminos.mY);
                break;
            default:
                greenDis = -1;
                break;
        }

        if (greenDis > 0) {
            updateTetriminoTiles(oldX, oldY, mTetrimino.mWidth, mTetrimino.mHeight);
        }

        return greenDis;
    }
    public void updateTetriminoTiles(int oldX, int oldY, int oldWidth, int oldHeight) {
        for (int r = oldY; r < oldY + oldHeight; r++) {
            for (int c = oldX; c < oldX + oldWidth; c++) {
                mTileViewInfo.mTileType[r][c] = EMPTY;
            }
        }

        for (int r = Tetriminos.mY; r < Tetriminos.mY + mTetrimino.mHeight; r++) {
            for (int c = Tetriminos.mX; c < Tetriminos.mX + mTetrimino.mWidth; c++) {
                if (mTetrimino.mTetrimino[r-Tetriminos.mY][c-Tetriminos.mX] == 1) {
                    mTileViewInfo.mTileType[r][c] = COLOR;
                    mTileViewInfo.mTileInfo[r][c] = mTetrimino.mColor;
                }
            }
        }
    }
    public boolean rotateTetrimino() {
        int oldX = Tetriminos.mX;
        int oldY = Tetriminos.mY;
        int oldWidth = mTetrimino.mWidth;
        int oldHeight = mTetrimino.mHeight;
        mRefreshHandler.pause();
        mTetrimino.rotateTeriminoClockWise90();
        if (isCollided(Tetriminos.mX, Tetriminos.mY)) {
            //TODO: ugly workaround
            mTetrimino.rotateTeriminoClockWise90();
            mTetrimino.rotateTeriminoClockWise90();
            mTetrimino.rotateTeriminoClockWise90();
            return false;
        }

        updateTetriminoTiles(oldX, oldY, oldWidth, oldHeight);

        mRefreshHandler.run();
        updateTetrisView(UI);
        return true;
    }

    //@return: if false game over
    protected boolean genNewTetrimino() {
        int rn;

        if (NeXT == -1) { //first run
            rn              = Math.abs(RND.nextInt());
            rn             %= Tetriminos.Tetrimino.totalTypes;
        } else {
            rn = NeXT;
        }

        NeXT  = Math.abs(RND.nextInt());
        NeXT %= Tetriminos.Tetrimino.totalTypes;

        mTetrimino = new Tetriminos(Tetriminos.Tetrimino.typeOfIdx(rn));
        calculateTetriminoStartXY();
        mTetrimino.setCoordinate(tetriminoStartX, tetriminoStartY);

        if (isCollided(Tetriminos.mX, Tetriminos.mY)) {
            return false;
        }

        for (int r = Tetriminos.mY; r < Tetriminos.mY + mTetrimino.mHeight; r++) {
            for (int c = Tetriminos.mX; c < Tetriminos.mX + mTetrimino.mWidth; c++) {
                if (mTetrimino.mTetrimino[r-Tetriminos.mY][c-Tetriminos.mX] == 1) {
                    mTileViewInfo.mTileType[r][c] = COLOR;
                    mTileViewInfo.mTileInfo[r][c] = mTetrimino.mColor;
                }
            }
        }
        //draw immediately
        invalidate();

        if (mGameListener != null) {
            mGameListener.onNeXT();
        }
        return true;
    }
    private boolean isCollided(int X, int Y) {
        if ((X + mTetrimino.mWidth - 1) > (mTileViewInfo.mXTileCount - 1) ||
                (Y + mTetrimino.mHeight - 1) > (mTileViewInfo.mYTileCount - 1)) {
            return true;
        }
        for (int i = 0; i < mTetrimino.mHeight; i++) {
            for (int j = 0; j < mTetrimino.mWidth; j++) {
                if (mTetrimino.mTetrimino[i][j] == 0) {
                    continue;
                }

                int tile_row = i + Y;
                int tile_col = i + X;

                if (mTileViewInfo.mTileType[tile_row][tile_col] != EMPTY) {
                    return true;
                }
            }
        }

        return false;
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
                setTileEmpty(i, j);
                setTileColor(mTetrimino.mColor, (i-oldY+Tetriminos.mY), (j-oldX+Tetriminos.mX));
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
                    setTileEmpty(i, k);
                }
                //move down the top rows
                for (int l = i; l >= 0; l--) {
                    for (int m = 0; m <= mTetrimino.mWidth; m++) {
                        if (l > 0) {
                            mTileViewInfo.mTileType[l][m] = mTileViewInfo.mTileType[l-1][m];
                            mTileViewInfo.mTileInfo[l][m] = mTileViewInfo.mTileInfo[l-1][m];
                        }
                        if (l == 0) {
                            setTileEmpty(l, m);
                        }
                    }
                }

            }
        }

        mScore += totalScore;
        if (totalScore != 0 && mGameListener != null) {
            mGameListener.onUpdateScore(mScore);
        }
        mRefreshDelay /= speed;
    }

    public Bitmap NeXT() {
        Tetriminos tt = new Tetriminos(Tetriminos.Tetrimino.typeOfIdx(NeXT));
        int width = tt.mWidth * TileViewInfo.mTileSize;
        int height = tt.mHeight * TileViewInfo.mTileSize;
        Bitmap NeXT_bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(NeXT_bmp);
        Paint paint = new Paint();
        paint.setColor(tt.mColor);
        Rect rect = new Rect();

        for (int r = 0; r < tt.mHeight; r++) {
            for (int c = 0; c < tt.mWidth; c++) {
                if (tt.mTetrimino[r][c] == 1) {
                    int left = c * TileViewInfo.mTileSize;
                    int top  = r * TileViewInfo.mTileSize;
                    int right= left + TileViewInfo.mTileSize - 1;
                    int bottom = top + TileViewInfo.mTileSize - 1;
                    rect.set(left + PADDING, top + PADDING, right - PADDING, bottom - PADDING);
                    canvas.drawRect(rect, paint);
                }
            }
        }

        return NeXT_bmp;
    }
}
