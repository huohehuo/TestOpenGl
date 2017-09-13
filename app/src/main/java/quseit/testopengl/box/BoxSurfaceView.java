package quseit.testopengl.box;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by Administrator on 2017/9/13.
 */

public class BoxSurfaceView extends GLSurfaceView{
    BoxRenderer boxRenderer;
    public BoxSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        //设置Renderer到GLSurfaceView
        boxRenderer = new BoxRenderer();
        setRenderer(boxRenderer);
        // Render the view only when there is a change in the drawing data
        //仅在你的绘制数据发生变化时才在视图中进行绘制操作
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
