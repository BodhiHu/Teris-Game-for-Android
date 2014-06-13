package com.shawnhu.mechanicsgames.tetris;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class PlayActivity extends ActionBarActivity
        implements GameListener,
        GestureDetector.OnGestureListener {

    TetrisView mTetrisView;
    Bundle mTetrisStats = null;
    Button mButtonCtl;
    TextView mTextScore;
    ImageView NeXT_View;
    GestureDetectorCompat mGestureListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        /*
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        */

        mTextScore = (TextView) findViewById(R.id.textScore);
        NeXT_View  = (ImageView) findViewById(R.id.viewNextTetrimino);
        mTetrisView = (TetrisView) findViewById(R.id.tetrisView);
        mTetrisView.setGameListener(this);
        mGestureListener = new GestureDetectorCompat(this, this);
        mTetrisView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureListener.onTouchEvent(event);
                Toast.makeText(getApplicationContext(), MotionEvent.actionToString(event.getAction()), Toast.LENGTH_SHORT).show();
                //return true to let android notify futher actions after ACTION_DOWN
                return true;
            }
        });

        mButtonCtl = (Button) findViewById(R.id.buttonCtl);
        mButtonCtl.setEnabled(false);
        if (mButtonCtl != null) {
            mButtonCtl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence btnStr = mButtonCtl.getText();
                    if (btnStr.toString() == "Play") {
                        mTetrisView.runTetris();
                    }
                    else if (btnStr.toString() == "Pause") {
                            mTetrisView.pauseTetris();
                    }
                }
            });
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mTetrisStats = savedInstanceState;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mTetrisView.saveGame(mTetrisStats);
        outState = mTetrisStats;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            if (mTetrisView.mState == TetrisView.State.IDLE) {
                mTetrisView.startTetris(mTetrisStats);
            }
        } else {
            if (mTetrisView.mState == TetrisView.State.RUNNING) {
                mTetrisView.pauseTetris();
            }
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        moveTetris(e1, e2, 1);
        return true;
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }
    @Override
    public void onShowPress(MotionEvent e) {
    }
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        int x = (int) e.getX() / mTetrisView.mTileViewInfo.mTileSize;
        //TODO: get an instance of Tetriminos in TetrisView
        int dir = (x > Tetriminos.mX) ? TetrisView.RIGHT : TetrisView.LEFT;
        int steps = Math.abs(x-Tetriminos.mX);
        moveTetris(dir, steps);
        return true;
    }
    @Override
    public void onLongPress(MotionEvent e) {
    }


    @Override
    public void onStarted() {
        mButtonCtl.setText("Play");
        mButtonCtl.setEnabled(true);
    }
    @Override
    public void onRun() {
        mButtonCtl.setText("Pause");
    }
    @Override
    public void onPaused() {
        mButtonCtl.setText("Play");
    }
    @Override
    public void onUpdateScore(int score) {
        if (mTextScore != null) {
            mTextScore.setText("Score: " + score);
        }
    }
    @Override
    public void onGameOver() {
        //Toast.makeText()
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Tetris")
                .setMessage("Game Over")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTetrisView.startTetris(null);
                    }

                })
                .setCancelable(false)
                .create();

        dialog.show();
    }
    @Override
    public void onNeXT() {
        final Bitmap NeXT = mTetrisView.NeXT();
        NeXT_View.postDelayed(new Runnable() {
            @Override
            public void run() {
                NeXT_View.setImageBitmap(NeXT);
            }
        }, mTetrisView.mRefreshDelay*10);
    }

    public int moveTetris(int dir, int steps) {
        switch (dir) {
            case TetrisView.UP:
                mTetrisView.rotateTetrimino();
                return 0;
            case TetrisView.DOWN:
                mTetrisView.mvTetrimino(TetrisView.DOWN, TetrisView.TileViewInfo.mYTileCount);
                return 0;
            case TetrisView.RIGHT:
                mTetrisView.mvTetrimino(TetrisView.RIGHT, steps);
                return 0;
            case TetrisView.LEFT:
                mTetrisView.mvTetrimino(TetrisView.LEFT, steps);
                return 0;
            default:
                return -1;
        }
    }
    public int moveTetris(MotionEvent e1, MotionEvent e2, int steps) {
        switch (getScrollDir(e1, e2)) {
            case TetrisView.UP:
                mTetrisView.rotateTetrimino();
                return 0;
            case TetrisView.DOWN:
                mTetrisView.mvTetrimino(TetrisView.DOWN, TetrisView.TileViewInfo.mYTileCount);
                return 0;
            case TetrisView.RIGHT:
                mTetrisView.mvTetrimino(TetrisView.RIGHT, steps);
                return 0;
            case TetrisView.LEFT:
                mTetrisView.mvTetrimino(TetrisView.LEFT, steps);
                return 0;
            default:
                return -1;
        }
    }
    public int getScrollDir(MotionEvent e1, MotionEvent e2) {
        float start_x = e1.getX();
        float start_y = e1.getY();
        float end_x   = e2.getX();
        float end_y   = e2.getY();

        float vec_x = end_x - start_x;
        float vec_y = end_y - start_y;
        if (Math.abs(vec_x) > Math.abs(vec_y)) {
            //horizontal
            if (vec_x > 0) {
                return TetrisView.RIGHT;
            } else {
                return TetrisView.LEFT;
            }

        } else {
            //vertical
            if (vec_y > 0) {
                return TetrisView.DOWN;
            } else {
                return TetrisView.UP;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();
        //if (id == R.id.action_settings) {
        //    return true;
        //}
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_play, container, false);
            return rootView;
        }
    }
}
