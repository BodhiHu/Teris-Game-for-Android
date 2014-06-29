package com.shawnhu.mechanicsgames.tetris;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.shawnhu.mechanicsgames.tetris.util.SystemUiHider;


public class EiffelBaloonActivity extends ActionBarActivity {

    static final String TAG = "EiffelBaloonActivity";
    SystemUiHider mSystemUiHider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eiffel_baloon);

        View contentView = findViewById(R.id.spashView);
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, SystemUiHider.FLAG_FULLSCREEN);
        mSystemUiHider.setup();
        mSystemUiHider.hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().hide();
        }

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(4000);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    ;
                } finally {
                    startActivity(new Intent(EiffelBaloonActivity.this,
                                  PlayActivity.class));
                }

                finish();
            }
        };

        t.start();
    }

    Handler mHandler = new Handler();
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(EiffelBaloonActivity.this,
                    PlayActivity.class));

            finish();
        }
    };
    void delayedPost() {
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, 4000);
    }
}
