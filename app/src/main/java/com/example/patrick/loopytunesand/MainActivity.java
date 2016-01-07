package com.example.patrick.loopytunesand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    Button startButton;
    EditText bpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initListeners();
        bpm.setText("80");
    }

    private void init() {
        startButton = (Button) findViewById(R.id.start_button);
        bpm = (EditText) findViewById(R.id.bpm);
    }
    private void initListeners(){
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Looper.class);
                i.putExtra("bpm_value", String.valueOf(bpm.getText()));
                startActivity(i);
            }
        });
    }
}
