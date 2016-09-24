package com.xingstarx.canvas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.xingstarx.canvas.view.CustomView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button mStartView;
    private Button mStopView;
    private CustomView mCustomView;
    private SeekBar mSizeSeekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartView = (Button) findViewById(R.id.start);
        mStopView = (Button) findViewById(R.id.stop);
        mCustomView = (CustomView) findViewById(R.id.loading_view);
        mSizeSeekBar = (SeekBar) findViewById(R.id.size);
        mSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCustomView.setLineLength(progress / 100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
