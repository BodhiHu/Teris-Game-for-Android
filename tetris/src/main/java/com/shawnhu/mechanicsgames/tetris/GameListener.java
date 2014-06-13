package com.shawnhu.mechanicsgames.tetris;

import android.graphics.Bitmap;

/**
 * Created by shawnhu on 5/28/14.
 */
public interface GameListener {
    void onStarted();
    void onPaused();
    void onRun();
    void onGameOver();
    void onUpdateScore(int score);
    void onNeXT();
}
