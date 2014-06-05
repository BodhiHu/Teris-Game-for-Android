package com.shawnhu.mechanicsgames.tetris;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;


public class PlayActivity extends ActionBarActivity {

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
                        mButtonCtl.setText("Pause");
                    }
                    else if (btnStr.toString() == "Pause") {
                            mTetrisView.pauseTetris();
                            mButtonCtl.setText("Start");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
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
