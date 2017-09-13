package quseit.testopengl.Triangle;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by Administrator on 2017/9/13.
 */

public class SquareSurfaceView extends GLSurfaceView{
    SquareRenderer squareRenderer;
    public SquareSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        squareRenderer = new SquareRenderer();
        setRenderer(squareRenderer);
        // Render the view only when there is a change in the drawing data
        //仅在你的绘制数据发生变化时才在视图中进行绘制操作
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}
