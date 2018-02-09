package com.example.sky.opengles;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.KeyEvent;

/**
 * Created by Sky on 2/7/2018.
 */

public class GLSurface extends GLSurfaceView {

    private final GLRenderer mRenderer;

    SGFParser parser;
    private int[][] goban;
    private int step = 0;

    public GLSurface(Context context) {
        super(context);

        goban = new int[19][19];

        setEGLContextClientVersion(2);

        mRenderer = new GLRenderer(context);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public void setupGoban(int[][] goban) {
        mRenderer.setupGoban(goban);
    }

    @Override
    public void onPause() {
        super.onPause();
        mRenderer.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mRenderer.onResume();
    }
}
