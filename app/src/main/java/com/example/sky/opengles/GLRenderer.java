package com.example.sky.opengles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;

/**
 * Created by Sky on 1/27/2018.
 */

public class GLRenderer implements GLSurface.Renderer {

    private final float[] mtrxProjection = new float[16];
    private final float[] mtrxView = new float[16];
    private final float[] mtrxProjectionAndView = new float[16];

    public static float vertices[];
    public static short indices[];
    public static float uvs[];
    public FloatBuffer vertexBuffer;
    public ShortBuffer drawListBuffer;
    public FloatBuffer uvBuffer;

    float mScreenWidth;
    float mScreenHeight;

    final float mMarginPercentage = 0.03125f;
    float mGobanMargin;
    float mStoneDiameter;
    float mGobanGridWidth;

    int[][] mGoban;

    Context mContext;
    long mLastTime;
    int mProgram;

    public GLRenderer(Context mContext) {
        this.mContext = mContext;
    }

    public void onPause() {

    }

    public void onResume() {

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Set the clear color to black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1);

        mGoban = new int[19][19];
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                mGoban[i][j] = 0;
            }
        }
        setupGoban(mGoban);

        // Create the shaders
        int vertexShader = graphicTools.loadShader(GLES20.GL_VERTEX_SHADER, graphicTools.vs_SolidColor);
        int fragmentShader = graphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, graphicTools.fs_SolidColor);

        graphicTools.sp_SolidColor = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(graphicTools.sp_SolidColor, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(graphicTools.sp_SolidColor, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(graphicTools.sp_SolidColor);                  // creates OpenGL ES program executables

        vertexShader = graphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
                graphicTools.vs_Image);
        fragmentShader = graphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER,
                graphicTools.fs_Image);

        graphicTools.sp_Image = GLES20.glCreateProgram();
        GLES20.glAttachShader(graphicTools.sp_Image, vertexShader);
        GLES20.glAttachShader(graphicTools.sp_Image, fragmentShader);
        GLES20.glLinkProgram(graphicTools.sp_Image);

        // Set our shader programm
        GLES20.glUseProgram(graphicTools.sp_Image);
    }

    public void setupGoban(int[][] goban) {
        mGoban = goban;
        vertices = new float[362*12];
        indices = new short[362*6];

        for(int cy=0; cy < 19; cy++) {
            for(int cx=0; cx < 19; cx++) {
                int i = cy*19+cx+1;
                float x = cx * mGobanGridWidth + mGobanMargin - mStoneDiameter / 2f;
                float y= cy * mGobanGridWidth + mGobanMargin - mStoneDiameter / 2f;

                vertices[(i*12)] = x;
                vertices[(i*12) + 1] = y;
                vertices[(i*12) + 2] = 0.0f;
                vertices[(i*12) + 3] = x;
                vertices[(i*12) + 4] = y + mStoneDiameter;
                vertices[(i*12) + 5] = 0.0f;
                vertices[(i*12) + 6] = x + mStoneDiameter;
                vertices[(i*12) + 7] = y + mStoneDiameter;
                vertices[(i*12) + 8] = 0.0f;
                vertices[(i*12) + 9] = x + mStoneDiameter;
                vertices[(i*12) + 10] = y;
                vertices[(i*12) + 11] = 0.0f;
            }
        }

        vertices[0] = 0.0f;
        vertices[1] = 0.0f;
        vertices[2] = 0.0f;
        vertices[3] = 0.0f;
        vertices[4] = mScreenWidth;
        vertices[5] = 0.0f;
        vertices[6] = mScreenWidth;
        vertices[7] = mScreenWidth;
        vertices[8] = 0.0f;
        vertices[9] = mScreenWidth;
        vertices[10] = 0.0f;
        vertices[11] = 0.0f;

        int last = 0;
        for(int i=0;i<362;i++)
        {
            indices[(i*6)] = (short) (last);
            indices[(i*6) + 1] = (short) (last + 1);
            indices[(i*6) + 2] = (short) (last + 2);
            indices[(i*6) + 3] = (short) (last);
            indices[(i*6) + 4] = (short) (last + 2);
            indices[(i*6) + 5] = (short) (last + 3);

            last = last + 4;
        }

        uvs = new float[362*4*2];

        for(int cy=0; cy < 19; cy++) {
            for(int cx=0; cx < 19; cx++) {
                int i = cy * 19 + cx + 1;

                if (goban[cy][cx] == 1) {
                    uvs[(i * 8)] = 512f / 1112f;
                    uvs[(i * 8) + 1] = 0.0f;

                    uvs[(i * 8) + 2] = 512f / 1112f;
                    uvs[(i * 8) + 3] = 300f / 512f;

                    uvs[(i * 8) + 4] = 812f / 1112f;
                    uvs[(i * 8) + 5] = 300f / 512f;

                    uvs[(i * 8) + 6] = 812f / 1112f;
                    uvs[(i * 8) + 7] = 0.0f;

                } else if (goban[cy][cx] == 2) {
                    uvs[(i * 8)] = 812f / 1112f;
                    uvs[(i * 8) + 1] = 0.0f;

                    uvs[(i * 8) + 2] = 812f / 1112f;
                    uvs[(i * 8) + 3] = 300f / 512f;

                    uvs[(i * 8) + 4] = 1.0f;
                    uvs[(i * 8) + 5] = 300f / 512f;

                    uvs[(i * 8) + 6] = 1.0f;
                    uvs[(i * 8) + 7] = 0.0f;
                } else {
                    uvs[(i * 8)] = 1f;
                    uvs[(i * 8) + 1] = 1f;
                    uvs[(i * 8) + 2] = 1f;
                    uvs[(i * 8) + 3] = 1f;
                    uvs[(i * 8) + 4] = 1f;
                    uvs[(i * 8) + 5] = 1f;
                    uvs[(i * 8) + 6] = 1f;
                    uvs[(i * 8) + 7] = 1f;
                }
            }
        }

        uvs[0] = 0.0f;
        uvs[1] = 0.0f;

        uvs[2] = 0.0f;
        uvs[3] = 1f;

        uvs[4] = 512f/1112f;
        uvs[5] = 1f;

        uvs[6] = 512f/1112f;
        uvs[7] = 0.0f;

        loadImage();
    }


    public void addStones(int cx, int cy) {
        float x = cx * mGobanGridWidth + mGobanMargin;
        float y= cy * mGobanGridWidth + mGobanMargin;
        vertices = new float[] {
                x, y, 0.0f,
                x, y + mStoneDiameter, 0.0f,
                x + mStoneDiameter, y + mStoneDiameter, 0.0f,
                x + mStoneDiameter, y, 0.0f,
        };
        indices = new short[] {0, 1, 2, 0, 2, 3};
        uvs = new float[] {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };

        loadImage();
    }

    private void loadImage() {
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);

        int[] texturenames = new int[1];
        GLES20.glGenTextures(1, texturenames, 0);
        int id = mContext.getResources().getIdentifier("drawable/goset", null,
                mContext.getPackageName());
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

        bmp.recycle();

        ByteBuffer bb2 = ByteBuffer.allocateDirect(vertices.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        vertexBuffer = bb2.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        // We need to know the current width and height.
        mScreenWidth = width;
        mScreenHeight = height;
        mGobanMargin = mScreenWidth * mMarginPercentage;
        mStoneDiameter = (mScreenWidth - mGobanMargin * 2.0f) / 18.0f * 0.90f;
        mGobanGridWidth = (mScreenWidth - mGobanMargin * 2.0f) / 18.0f;
        // Redo the Viewport, making it fullscreen.
        setupGoban(mGoban);
        GLES20.glViewport(0, 0, (int)mScreenWidth, (int)mScreenHeight);

        // Clear our matrices
        for(int i=0;i<16;i++)
        {
            mtrxProjection[i] = 0.0f;
            mtrxView[i] = 0.0f;
            mtrxProjectionAndView[i] = 0.0f;
        }

        // Setup our screen width and height for normal sprite translation.
        Matrix.orthoM(mtrxProjection, 0, 0f, mScreenWidth, 0.0f, mScreenHeight, 0, 50);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        Render(mtrxProjectionAndView);
    }

    private void Render(float[] m) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT |
        GLES20.GL_DEPTH_BUFFER_BIT);
        int mPositionHandle = GLES20.glGetAttribLocation(graphicTools.sp_Image, "vPosition");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);

        int mTexCoordLoc = GLES20.glGetAttribLocation(graphicTools.sp_Image,
                "a_texCoord" );
        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray ( mTexCoordLoc );

        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer ( mTexCoordLoc, 2, GLES20.GL_FLOAT,
                false,
                0, uvBuffer);
        // Get handle to shape's transformation matrix
//        int mtrxhandle = GLES20.glGetUniformLocation(graphicTools.sp_SolidColor, "uMVPMatrix");

        // Apply the projection and view transformation
//        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);

        // Draw the triangle
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
//                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
//        GLES20.glDisableVertexAttribArray(mPositionHandle);

        // Get handle to shape's transformation matrix
        int mtrxhandle = GLES20.glGetUniformLocation(graphicTools.sp_Image,
                "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);

        // Get handle to textures locations
        int mSamplerLoc = GLES20.glGetUniformLocation (graphicTools.sp_Image,
                "s_texture" );

        // Set the sampler texture unit to 0, where we have saved the texture.
        GLES20.glUniform1i ( mSamplerLoc, 0);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }
}
