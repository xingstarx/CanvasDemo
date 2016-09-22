package com.xingstarx.canvas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.xingstarx.canvas.view.CustomView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button mStartView;
    private Button mStopView;
    private CustomView mCustomView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartView = (Button) findViewById(R.id.start);
        mStopView = (Button) findViewById(R.id.stop);
        mCustomView = (CustomView) findViewById(R.id.loading_view);

        mStartView.setOnClickListener(this);
        mStopView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                mCustomView.start();
                break;
            case R.id.stop:
                mCustomView.stop();
                break;
        }
    }
}
