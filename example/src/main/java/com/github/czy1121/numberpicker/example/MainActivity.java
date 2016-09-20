package com.github.czy1121.numberpicker.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.czy1121.numberpicker.NumberPicker;

public class MainActivity extends AppCompatActivity {

    NumberPicker npValue;
    NumberPicker npStep;

    TextView txtValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        npValue = (NumberPicker) findViewById(R.id.np);
        npStep = (NumberPicker) findViewById(R.id.np_step);

        txtValue = (TextView) findViewById(R.id.txt_value);

        npStep.setOnValueChangedListener(new NumberPicker.OnValueChangedListener() {
            @Override
            public void onValueChanged(NumberPicker view, int value) {
                npValue.init(value, -10, 100, npValue.getValue());
                npValue.notifyValueChanged();
            }
        });
        npValue.setOnValueChangedListener(new NumberPicker.OnValueChangedListener() {
            @Override
            public void onValueChanged(NumberPicker view, int value) {
                txtValue.setText("" + value);
            }
        });
        npStep.setValue(1);

        View.OnClickListener onQuick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                case R.id.v_min: npValue.setValue(-10);break;
                case R.id.v10: npValue.setValue(10);break;
                case R.id.v25: npValue.setValue(25);break;
                case R.id.v50: npValue.setValue(50);break;
                case R.id.v_max: npValue.setValue(100);break;
                }
            }
        };
        findViewById(R.id.v_min).setOnClickListener(onQuick);
        findViewById(R.id.v10).setOnClickListener(onQuick);
        findViewById(R.id.v25).setOnClickListener(onQuick);
        findViewById(R.id.v50).setOnClickListener(onQuick);
        findViewById(R.id.v_max).setOnClickListener(onQuick);
    }
}
