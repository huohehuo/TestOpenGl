package quseit.testopengl.Triangle;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2017/9/5.
 */

public class TrgSurfaceView extends GLSurfaceView {
    // Tracks the orientation of the phone. This would be a head pose if this was a VR application.
    // See the {@link Sensor.TYPE_ROTATION_VECTOR} section of {@link SensorEvent.values} for details
    // about the specific sensor that is used. It's important to note that this sensor defines the
    // Z-axis as point up and the Y-axis as pointing toward what the phone believes to be magnetic
    // north. Google VR's coordinate system defines the Y-axis as pointing up and the Z-axis as
    // pointing toward the user. This requires a 90-degree rotation on the X-axis to convert between
    // the two coordinate systems.
    private final SensorManager sensorManager;
    private final Sensor orientationSensor;
    private final PhoneOrientationListener phoneOrientationListener;
    private float[] phoneInWorldSpaceMatrix = new float[16];

    // Tracks the orientation of the physical controller in its properly centered Start Space.
    TrgRenderer renderer;
    /**
     * See {@link #resetYaw}
     */
    private float[] startFromSensorTransformation;
    private float[] controllerInStartSpaceMatrix = new float[16];
    public TrgSurfaceView(Context context){
        super(context);
        // 创建一个OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        //设置Renderer到GLSurfaceView
        renderer = new TrgRenderer();
        setRenderer(renderer);
        // Render the view only when there is a change in the drawing data
        //仅在你的绘制数据发生变化时才在视图中进行绘制操作
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        phoneOrientationListener = new PhoneOrientationListener();
    }

    public TrgSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 创建一个OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        //设置Renderer到GLSurfaceView
        renderer = new TrgRenderer();
        setRenderer(renderer);
        // Render the view only when there is a change in the drawing data
        //仅在你的绘制数据发生变化时才在视图中进行绘制操作
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        phoneOrientationListener = new PhoneOrientationListener();
    }

    //传感器监听
    private class PhoneOrientationListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            SensorManager.getRotationMatrixFromVector(phoneInWorldSpaceMatrix, event.values);
            if (startFromSensorTransformation == null) {
                // Android's hardware硬件 uses radians弧度, but OpenGL uses degrees角度. Android uses
                // [yaw偏度, pitch倾斜, roll滚动起伏] for the order of elements in the orientation array.
                float[] orientationRadians =
                        SensorManager.getOrientation(phoneInWorldSpaceMatrix, new float[3]);
                startFromSensorTransformation = new float[3];
                for (int i = 0; i < 3; ++i) {
                    startFromSensorTransformation[i] = (float) Math.toDegrees(orientationRadians[i]);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
    @Override
    public boolean onTouchEvent(MotionEvent e) {
// MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }
                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }
                renderer.setAngle(
                        renderer.getAngle() +
                                ((dx + dy) * TOUCH_SCALE_FACTOR));
                requestRender();
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
}