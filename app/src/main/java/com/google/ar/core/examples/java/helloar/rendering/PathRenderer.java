package com.google.ar.core.examples.java.helloar.rendering;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.examples.java.helloar.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 * Created by Chris on 26.10.2017.
 */

public class PathRenderer {

    private static final String TAG = PathRenderer.class.getSimpleName();

    private int pathProgram;
    private int mvpLocation;
    private int posLocation;

    private FloatBuffer buffer;

    /**
     * Allocates and initializes OpenGL resources needed by the background renderer.  Must be
     * called on the OpenGL thread, typically in
     * {@link GLSurfaceView.Renderer#onSurfaceCreated(GL10, EGLConfig)}.
     *
     * @param context Needed to access shader source.
     */
    public void createOnGlThread(Context context) {


        int vertexShader = ShaderUtil.loadGLShader(TAG, context,
                GLES20.GL_VERTEX_SHADER, R.raw.vertex);
        int fragmentShader = ShaderUtil.loadGLShader(TAG, context,
                GLES20.GL_FRAGMENT_SHADER, R.raw.fragment);

        pathProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(pathProgram, vertexShader);
        GLES20.glAttachShader(pathProgram, fragmentShader);
        GLES20.glLinkProgram(pathProgram);
        GLES20.glUseProgram(pathProgram);

        ShaderUtil.checkGLError(TAG, "Program creation");

        mvpLocation = GLES20.glGetUniformLocation(pathProgram, "u_ModelViewProjection");
        posLocation = GLES20.glGetAttribLocation(pathProgram, "pos");

        ShaderUtil.checkGLError(TAG, "Program parameters");
    }

    /**
     * Draws the AR background image.  The image will be drawn such that virtual content rendered
     * with the matrices provided by {@link Frame#getViewMatrix(float[], int)} and
     * {@link Session#getProjectionMatrix(float[], int, float, float)} will accurately follow
     * static physical objects.  This must be called <b>before</b> drawing virtual content.
     *
     * @param frame The last {@code Frame} returned by {@link Session#update()}.
     */
    public void draw(Frame frame, ArrayList<Anchor> anchors, float[] mvp) {
        if(anchors.size() <= 2) {
            return;
        }

        float[] coords = new float[anchors.size()*3];
        int i = 0;
        for(Anchor anchor: anchors) {
            Pose pose = anchor.getPose();
            coords[i] = pose.tx();
            i++;
            coords[i] = pose.ty();
            i++;
            coords[i] = pose.tz();
            i++;
        }

        ByteBuffer bbCoords = ByteBuffer.allocateDirect(coords.length*4);
        bbCoords.order(ByteOrder.nativeOrder());
        buffer = bbCoords.asFloatBuffer();
        buffer.put(coords);
        buffer.position(0);

        ByteBuffer bbMVP = ByteBuffer.allocateDirect(mvp.length * 4);
        bbMVP.order(ByteOrder.nativeOrder());
        FloatBuffer mvpBuffer = bbMVP.asFloatBuffer();
        mvpBuffer.put(mvp);
        mvpBuffer.position(0);

        ShaderUtil.checkGLError(TAG, "1");

        GLES20.glUseProgram(pathProgram);
        ShaderUtil.checkGLError(TAG, "2");

        GLES20.glUniformMatrix4fv(mvpLocation, 1, false, mvpBuffer);
        ShaderUtil.checkGLError(TAG, "3");


        // Enable vertex arrays
        GLES20.glEnableVertexAttribArray(posLocation);
        ShaderUtil.checkGLError(TAG, "4");
        // Set the vertex positions.
        GLES20.glVertexAttribPointer(posLocation, 3, GLES20.GL_FLOAT, false, 0, buffer);
        ShaderUtil.checkGLError(TAG, "5");

        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, coords.length / 3);
        ShaderUtil.checkGLError(TAG, "6");

        // Disable vertex arrays
        GLES20.glDisableVertexAttribArray(posLocation);

        ShaderUtil.checkGLError(TAG, "7");
    }
}
