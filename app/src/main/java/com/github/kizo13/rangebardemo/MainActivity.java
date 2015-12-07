package com.github.kizo13.rangebardemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.kizo13.rangebar.RangeBar;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "RangeBarDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RangeBar bar = (RangeBar) findViewById(R.id.rangeBar);
        if (bar == null) return;
        bar.setMin(140f); bar.setMax(160f); bar.setValue(141f);

        RangeBar bar2 = (RangeBar) findViewById(R.id.rangeBar2);
        bar2.setMin(0f); bar2.setMax(100f); bar2.setValue(50f);

        RangeBar bar3 = (RangeBar) findViewById(R.id.rangeBar3);
        bar3.setMin(5f); bar3.setMax(10f); bar3.setValue(7.1f);
    }

}
