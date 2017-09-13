package quseit.testopengl.box;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BoxActivity extends AppCompatActivity {
    BoxSurfaceView boxSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boxSurfaceView = new BoxSurfaceView(this);

        setContentView(boxSurfaceView);
//        setContentView(R.layout.activity_box);
    }
}
