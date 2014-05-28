package com.shawnhu.mechanicsgames.tetris;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;


public class TetrisView extends TileView {
    static final String TETRIS_SCORE = "score";
    enum State {
        STARTED, PAUSED, RUNNING, OVER,
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
        mScore = 0;

        return true;
    }

    public boolean setGameListener(GameListener l) {
        mGameListener = l;
        return (mGameListener != null);
    }

    public void saveGame(Bundle out) {

    }
    public boolean startTetris(Bundle savedState) {
        if (savedState != null) {
        }
        mScore = 0;

        if (mGameListener != null) {
            mGameListener.onStarted();
        }
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
