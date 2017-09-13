package quseit.testopengl.Triangle;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2017/9/5.
 */

public class TrgRenderer implements GLSurfaceView.Renderer {
    private Triangle triangle;
//    private Square square;
    //调用一次，用来配置View的OpenGL ES环境
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // 初始化一个三角形
        triangle = new Triangle();
        // 初始化一个正方形
//        square = new Square();
        //设置背景的颜色
        GLES20.glClearColor(0.9f, 0.4f, 0.4f, 1.0f);
    }

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    //如果View的几何形态发生变化时会被调用，例如当设备的屏幕方向发生改变时
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        //填充一个投影变换矩阵
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }
//    private final float[] mMVPMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    //设置旋转值
    public volatile float mAngle;
    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    private final float[] tmpMatrix2 = new float[16];
    //每次重新绘制View时被调用
    @Override
    public void onDrawFrame(GL10 gl10) {
        // 清空屏幕颜色，并用之前glClearColor定义的颜色重新绘制
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        float[] scratch = new float[16];
        // 为三角形创建一个旋转变换
        // Create a rotation transformation for the triangle
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);
        // Create a rotation for the triangle
        // long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);//用于设置旋转

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);//用于设置旋转

        // Set the camera position (View matrix)设置相机视角
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

//        // 合并旋转矩阵到投影和相机视口矩阵
//        Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);
//// Phone's Z faces up. We need it to face toward the user.
//        Matrix.rotateM(tmpMatrix2, 0, 90, 1, 0, 0);
        // 画一个角度
        triangle.draw(scratch);
//        square.draw();

    }
}
