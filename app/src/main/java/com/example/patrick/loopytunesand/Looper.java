package com.example.patrick.loopytunesand;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


interface MetronomeClick {
    void metronomeClick();
}

interface NoMetronomeClick {
    void noMetronomeClick();
}

public class Looper extends AppCompatActivity implements MetronomeClick {
    private Button startMetronome, stopMetronome, stopRec;
    private Button sampleButtons[];
    private int bpm, beatCount, loopCount, clickedLoopCount;
    private Metronome m;
    private Thread metronomeThread, resetSampleThread;
    private Recorder r;
    private Player p;
    private TextView metronomeTV;
    private ArrayList<Sample> samples = new ArrayList<Sample>();
    private boolean button1Rec = false;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looper);


        if (!Absolutes.DIRECTORY.exists()) {
            if (!Absolutes.DIRECTORY.mkdir()) ; //directory is created;
        }
        getBPM();
        init();
        initListeners();
    }

    private void init() {
        m = new Metronome(bpm);
        startMetronome = (Button) findViewById(R.id.start_metronome);
        stopMetronome = (Button) findViewById(R.id.stop_metronome);
        metronomeTV = (TextView) findViewById(R.id.metronome_tv);
        stopRec = (Button) findViewById(R.id.stop_rec);
        beatCount = 1;
        loopCount = 0;
        r = new Recorder();
        p = new Player();
        sampleButtons = new Button[]{(Button) findViewById(R.id.sample_one)};
        metronomeThread = new Thread(m);
        metronomeThread.start();
        m.addMetronomeClickListener(this);
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
                //r.startRecording();
                clickedLoopCount = loopCount;
                button1Rec = true;
            }
        });
        stopRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r.stopRecording();
                addSample();
               /* try{
                    p.playSample(sample);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }*/

            }
        });
    }

    private void addSample() {
        Sample sample = new Sample(r.getSamplePath());
        samples.add(sample);

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
        //Log.d("CLICK", "CLICK");
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // This code will always run on the UI thread, therefore is safe to modify UI elements.

                if (beatCount == 1) {
                    loopCount++;
                }
                metronomeTV.setText(String.valueOf(beatCount));
            /*    System.out.println(button1Rec);
                System.out.println(beatCount);
                System.out.println(loopCount);*/
                if (beatCount == 4) {
                    beatCount = 0;
                    stopSamples();
                    if (loopCount == 1) {
                        m.setBarTimeNano();
                    }
                    playSamples();

                }
                if (clickedLoopCount + 1 == loopCount && button1Rec) {
                    if (!isRecording) {
                        r.startRecording();
                        isRecording = true;
                    }
                }
                if (clickedLoopCount + 2 == loopCount && button1Rec) {
                    r.stopRecording();
                    addSample();
                    isRecording = false;
                    button1Rec = false;
                }

                beatCount++;
            }
        });

    }

    private void stopSamples() {
        p.stopSample(samples);
    }

    private void playSamples() {
        p.playSamples(samples);
    }

    public void pr(String msg) {
        Log.d(msg, msg);

    }
}
