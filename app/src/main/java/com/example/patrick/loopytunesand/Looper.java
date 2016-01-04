package com.example.patrick.loopytunesand;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static android.app.PendingIntent.getActivity;

interface MetronomeClick {
    void metronomeClick();
}

interface NoMetronomeClick {
    void noMetronomeClick();
}

public class Looper extends AppCompatActivity implements MetronomeClick, NoMetronomeClick {
    Button startMetronome, stopMetronome;
    Button sampleButtons[];
    int bpm;
    Metronome m;
    Thread metronomeThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looper);

        getBPM();
        init();
        initListeners();
    }

    private void init() {
        m = new Metronome(bpm);
        startMetronome = (Button) findViewById(R.id.start_metronome);
        stopMetronome = (Button) findViewById(R.id.stop_metronome);
        sampleButtons = new Button[]{(Button)findViewById(R.id.sample_one), (Button)findViewById(R.id.sample_two), (Button)findViewById(R.id.sample_three)};
        metronomeThread = new Thread(m);
        metronomeThread.start();
        m.addMetronomeClickListener(this);
        m.addMetronomenNoClickListener(this);
    }

    private void initListeners() {
        startMetronome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.startMetronome();
                startMetronome.setEnabled(false);
                stopMetronome.setEnabled(true);
            }
        });
        stopMetronome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("STOP", "STOP");
                m.stopMetronome();
                startMetronome.setEnabled(true);
                stopMetronome.setEnabled(false);
            }
        });
        sampleButtons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("FIRST", "FIRST");
            }
        });
    }

    private void getBPM() {
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        String bpm_str = extras.getString("bpm_value");
        bpm = Integer.valueOf(bpm_str);
        Log.d("BPM", String.valueOf(bpm));
    }

    @Override
    public void metronomeClick() {
        Log.d("CLICK", "CLICK");
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // This code will always run on the UI thread, therefore is safe to modify UI elements.
                ObjectAnimator colorFade = ObjectAnimator.ofObject(getApplication(),"backgroundColor", new ArgbEvaluator(), Color.argb(255, 255, 255, 255), 0xff000000);
                colorFade.setDuration(7000);
                colorFade.start();
            }
        });

    }

    @Override
    public void noMetronomeClick() {

    }
}
