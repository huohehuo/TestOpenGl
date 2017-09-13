package quseit.testopengl.box;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import quseit.testopengl.App;
import quseit.testopengl.R;

/**
 * Created by Administrator on 2017/9/13.
 */

public class BoxRenderer implements GLSurfaceView.Renderer {

    protected float[] modelCube;
    protected float[] modelPosition;


    private static final String TAG = "BoxGame3DActivity";

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100.0f;

    private static final float CAMERA_Z = 0.01f;
    private static final float TIME_DELTA = 1.8f;

    private static final float YAW_LIMIT = 0.12f;
    private static final float PITCH_LIMIT = 0.12f;

    private static final int COORDS_PER_VERTEX = 3;

    // We keep the light always position just above the user.
    private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[] {0.0f, 2.0f, 0.0f, 1.0f};

    // Convenience vector for extracting the position from a matrix via multiplication.
    private static final float[] POS_MATRIX_MULTIPLY_VEC = {0, 0, 0, 1.0f};

    private static final float MIN_MODEL_DISTANCE = 3.0f;
    private static final float MAX_MODEL_DISTANCE = 7.0f;

    private static final String OBJECT_SOUND_FILE = "cube_sound.wav";
    private static final String SUCCESS_SOUND_FILE = "success.wav";

    private final float[] lightPosInEyeSpace = new float[4];
    //floor
    private FloatBuffer floorVertices;
    private FloatBuffer floorColors;
    private FloatBuffer floorNormals;
    //box1
    private FloatBuffer cubeVertices;
    private FloatBuffer cubeColors;
    private FloatBuffer cubeFoundColors;
    private FloatBuffer cubeNormals;

    private int cubeProgram;
    private int floorProgram;

    private int cubePositionParam;
    private int cubeNormalParam;
    private int cubeColorParam;
    private int cubeModelParam;
    private int cubeModelViewParam;
    private int cubeModelViewProjectionParam;
    private int cubeLightPosParam;

    private float[] camera;
    private float[] view;
    private float[] headView;
    private float[] modelViewProjection;
    private float[] modelView;
    private float[] modelFloor;

    private float[] tempPosition;
    private float[] headRotation;

    private float objectDistance = MAX_MODEL_DISTANCE / 2.0f;
    private float floorDepth = 20f;

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //设置背景的颜色
        GLES20.glClearColor(0.9f, 0.4f, 0.4f, 1.0f);
        modelCube = new float[16];
        camera = new float[16];
        view = new float[16];
        modelViewProjection = new float[16];
        modelView = new float[16];
        modelFloor = new float[16];
        tempPosition = new float[4];
        // Model first appears directly in front of user.
        modelPosition = new float[] {0.0f, 0.0f, -MAX_MODEL_DISTANCE / 2.0f};
        headRotation = new float[4];
        headView = new float[16];
    //提取shader文件中的代码
        int vertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.test_light_vertex);
//        int gridShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.test_grid_fragment);
        int passthroughShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.test_passthrough_fragment);
        //申请底层空间
        ByteBuffer bbVertices = ByteBuffer.allocateDirect(BoxData.CUBE_COORDS.length * 4);
        bbVertices.order(ByteOrder.nativeOrder());
        //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
        cubeVertices = bbVertices.asFloatBuffer();
        cubeVertices.put(BoxData.CUBE_COORDS);
        cubeVertices.position(0);

        //未处于视线中心的五颜六色方块
        ByteBuffer bbColors = ByteBuffer.allocateDirect(BoxData.CUBE_COLORS.length * 4);
        bbColors.order(ByteOrder.nativeOrder());
        cubeColors = bbColors.asFloatBuffer();
        cubeColors.put(BoxData.CUBE_COLORS);
        cubeColors.position(0);


        //处于视线中心时的方块颜色（全为yellow）
        ByteBuffer bbFoundColors =
                ByteBuffer.allocateDirect(BoxData.CUBE_FOUND_COLORS.length * 4);
        bbFoundColors.order(ByteOrder.nativeOrder());
        cubeFoundColors = bbFoundColors.asFloatBuffer();
        cubeFoundColors.put(BoxData.CUBE_FOUND_COLORS);
        cubeFoundColors.position(0);

        //正常状态下的正多面体
        ByteBuffer bbNormals = ByteBuffer.allocateDirect(BoxData.CUBE_NORMALS.length * 4);
        bbNormals.order(ByteOrder.nativeOrder());
        cubeNormals = bbNormals.asFloatBuffer();
        cubeNormals.put(BoxData.CUBE_NORMALS);
        cubeNormals.position(0);

        //创建一个空的OpenGLES程序
        cubeProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(cubeProgram, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(cubeProgram, passthroughShader);
        //连接到着色器程序
        GLES20.glLinkProgram(cubeProgram);
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(cubeProgram);

        //获取着色器程序中，指定为attribute类型变量的id。
        //获取指向着色器中aPosition的index
        cubePositionParam = GLES20.glGetAttribLocation(cubeProgram, "a_Position");
        cubeNormalParam = GLES20.glGetAttribLocation(cubeProgram, "a_Normal");
        cubeColorParam = GLES20.glGetAttribLocation(cubeProgram, "a_Color");


        //获取着色器程序中，指定为uniform类型变量的id。
        cubeModelParam = GLES20.glGetUniformLocation(cubeProgram, "u_Model");
        cubeModelViewParam = GLES20.glGetUniformLocation(cubeProgram, "u_MVMatrix");
        cubeModelViewProjectionParam = GLES20.glGetUniformLocation(cubeProgram, "u_MVP");
        cubeLightPosParam = GLES20.glGetUniformLocation(cubeProgram, "u_LightPos");

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
// 清空屏幕颜色，并用之前glClearColor定义的颜色重新绘制
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Apply the eye transformation to the camera.
//        Matrix.multiplyMM(view, 0, null, 0, camera, 0);

        // Set the position of the light
        Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);

        // Build the ModelView and ModelViewProjection matrices
        // for calculating cube position and light.
//        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);
        Matrix.multiplyMM(modelView, 0, view, 0, modelCube, 0);
//        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        drawCube();
    }

    public void drawCube() {
        //使用shader程序
        GLES20.glUseProgram(cubeProgram);

        GLES20.glUniform3fv(cubeLightPosParam, 1, lightPosInEyeSpace, 0);

        // Set the Model in the shader, used to calculate lighting
        //// 将最终变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(cubeModelParam, 1, false, modelCube, 0);

        // Set the ModelView in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(cubeModelViewParam, 1, false, modelView, 0);

        // Enable vertex arrays
        GLES20.glEnableVertexAttribArray(cubePositionParam);
        // Set the position of the cube
        GLES20.glVertexAttribPointer(
                cubePositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, cubeVertices);

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(cubeModelViewProjectionParam, 1, false, modelViewProjection, 0);

        // Set the normal positions of the cube, again for shading
        GLES20.glVertexAttribPointer(cubeNormalParam, 3, GLES20.GL_FLOAT, false, 0, cubeNormals);
//        GLES20.glVertexAttribPointer(cubeColorParam, 4, GLES20.GL_FLOAT, false, 0,
//                isLookingAtObject() ? cubeFoundColors : cubeColors);

        GLES20.glEnableVertexAttribArray(cubeNormalParam);
        GLES20.glEnableVertexAttribArray(cubeColorParam);
        // 图形绘制
        //第一个参数表示绘制方式，第二个参数表示偏移量，第三个参数表示顶点个数
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);

        // Disable vertex arrays
        GLES20.glDisableVertexAttribArray(cubePositionParam);
        GLES20.glDisableVertexAttribArray(cubeNormalParam);
        GLES20.glDisableVertexAttribArray(cubeColorParam);

    }
    //获取raw文件中的代码数据
    private String readRawTextFile(int resId) {
        Log.e("读取错误：","进入");
        InputStream inputStream = App.getContext().getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("读取错误：",e.toString());
        }
        return null;
    }
    //设置着色器
    private int loadGLShader(int type, int resId) {
        String code = readRawTextFile(resId);
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shader;
    }
}
