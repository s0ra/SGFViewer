package com.example.sky.opengles;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.reflect.Array;
import java.security.Key;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GLSurface glSurfaceView;
    private GLRenderer mRenderer;

    SGFParser parser;
    private int[][] goban;
    private int step = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int readfilePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readfilePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

//        SGFParser sgf = new SGFParser();
//        sgf.parseStep(1,1);

        // Goban


        glSurfaceView = new GLSurface(this);

        SquareLayout layout = (SquareLayout) findViewById(R.id.goban);
        SquareLayout.LayoutParams glParams = new SquareLayout.LayoutParams(SquareLayout.LayoutParams.MATCH_PARENT, SquareLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(glSurfaceView, glParams);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)){

            if (step == 0) {
                Log.d("step", "0");
                goban = new int[19][19];
                parser = new SGFParser("sgf/leela_001.sgf");
                goban = parser.getGoban(step);
                step += 1;
            } else {
                Log.d("step", Integer.toString(step));
                goban = parser.getGoban(step);

                for (int i = 0; i < 19; i++) {
                    for (int j = 0; j < 19; j++) {
//                        Log.d("goban", Integer.toString(goban[i][j]));
                    }
                }
                step += 1;
            }
            glSurfaceView.setupGoban(goban);
            return true;
        }
        return false;
    }


        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (this.isInMultiWindowMode()) {
                glSurfaceView.onResume();
            } else {
                glSurfaceView.onPause();
            }
        } else {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }
}
