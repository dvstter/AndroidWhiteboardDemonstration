package com.example.heminghang.hmhwhiteboard;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.heminghang.hmhwhiteboard.drawings.DrawingActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    /** Called when the activity is first created. */
    private EditText e ;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        DrawingActivity.serverAddr = "10.0.2.2";
        Intent drawIntent = new Intent(MainActivity.this, DrawingActivity.class);
        startActivity(drawIntent);

    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.drawBtn:
                e = (EditText)findViewById(R.id.editText1);
                DrawingActivity.serverAddr = new String(e.getText().toString());
                Intent drawIntent = new Intent(this, DrawingActivity.class);
                startActivity(drawIntent);
                break;
        }
    }
}
