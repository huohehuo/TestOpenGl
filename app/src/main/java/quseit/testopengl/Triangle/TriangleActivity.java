package quseit.testopengl.Triangle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import quseit.testopengl.R;

public class TriangleActivity extends AppCompatActivity {
    TrgSurfaceView myGLSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        myGLSurfaceView = new TrgSurfaceView(this);
        setContentView(R.layout.activity_open_gl);
//        setContentView(myGLSurfaceView);
    }
}
