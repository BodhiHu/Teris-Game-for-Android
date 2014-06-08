package com.shawnhu.mechanicsgames.tetris;

/**
 * Created by shawnhu on 5/28/14.
 */
public interface GameListener {
    void onStarted();
    void onPaused();
    void onRun();
    void onGameOver();
    void onUpdateScore(int score);
}
