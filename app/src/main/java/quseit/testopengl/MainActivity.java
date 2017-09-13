package quseit.testopengl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import quseit.testopengl.Triangle.TriangleActivity;
import quseit.testopengl.boxx.TutorialPartI;
import quseit.testopengl.squarelt.TutorialPartV;
import quseit.testopengl.tutorial.TutorialPartVI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        handler.sendEmptyMessage(1);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            findViewById(R.id.button5).performClick();
        }
    };
    public void btn1(View view){
        startActivity(new Intent(MainActivity.this, TriangleActivity.class));
    }
    public void btn2(View view){
        startActivity(new Intent(MainActivity.this, TutorialPartI.class));
    }
    public void btn3(View view){
        startActivity(new Intent(MainActivity.this, TutorialPartV.class));
    }
    public void btn4(View view){
        startActivity(new Intent(MainActivity.this, TutorialPartVI.class));
    }
}
