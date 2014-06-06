package com.shawnhu.mechanicsgames.tetris;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;


public class PlayActivity extends ActionBarActivity implements GameListener {

    TetrisView mTetrisView;
    Bundle mTetrisStats;
    Button mButtonCtl;
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

        mTetrisView = findViewById(R.id.tetrisView);
        mButtonCtl = findViewById(R.id.buttonCtl);
        if (mButtonCtl != null) {
            mButtonCtl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence btnStr = mButtonCtl.getText();
                    if (btnStr.toString() == "Start") {
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
            mTetrisView.startTetris(mTetrisStats);
        } else {
            mTetrisView.pauseTetris();
        }
    }

    public void onStarted() {
        mButtonCtl.setText("Pause");
        mTetrisView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_SCROLL:
                        moveTetris(event);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
        return;
    }

    public int moveTetris(MotionEvent e) {
        switch (getScrollDir(e)) {
            case TetrisView.UP:
                mTetrisView.rotateTetrimino();
                return 0;
            case TetrisView.DOWN:
                mTetrisView.mvTetrimino(TetrisView.DOWN, 1);
                return 0;
            case TetrisView.RIGHT:
                mTetrisView.mvTetrimino(TetrisView.RIGHT, 1);
                return 0;
            case TetrisView.LEFT:
                mTetrisView.mvTetrimino(TetrisView.LEFT, 1);
                return 0;
            default:
                return -1;
        }
    }
    public int getScrollDir(MotionEvent me) {
        if (me.getAction() != MotionEvent.ACTION_SCROLL) {
            return -1;
        }

        float start_x = me.getHistoricalX(0);
        float start_y = me.getHistoricalY(0);
        float end_x   = me.getX();
        float end_y   = me.getY();

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
