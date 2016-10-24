package com.github.czy1121.numberstepper.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.github.czy1121.view.NumberStepper;


public class MainActivity extends AppCompatActivity {

    NumberStepper nsValue;
    NumberStepper nsStep;

    TextView txtValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nsValue = (NumberStepper) findViewById(R.id.np);
        nsStep = (NumberStepper) findViewById(R.id.np_step);

        txtValue = (TextView) findViewById(R.id.txt_value);

        nsStep.setOnValueChangedListener(new NumberStepper.OnValueChangedListener() {
            @Override
            public void onValueChanged(NumberStepper view, int value) {
                nsValue.init(value, -10, 100, nsValue.getValue());
                nsValue.notifyValueChanged();
            }
        });
        nsValue.setOnValueChangedListener(new NumberStepper.OnValueChangedListener() {
            @Override
            public void onValueChanged(NumberStepper view, int value) {
                txtValue.setText("" + value);
            }
        });
        nsStep.setValue(1);

        View.OnClickListener onQuick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                case R.id.v_min: nsValue.setValue(-10);break;
                case R.id.v10: nsValue.setValue(10);break;
                case R.id.v25: nsValue.setValue(25);break;
                case R.id.v50: nsValue.setValue(50);break;
                case R.id.v_max: nsValue.setValue(100);break;
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
