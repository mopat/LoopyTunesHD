package com.example.patrick.loopytunesand;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
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

import android.net.Uri;

import java.util.ArrayList;


interface MetronomeClick {
    void metronomeClick();
}

interface MetronomePreClick {
    void metronomePreClick();
}

public class Looper extends AppCompatActivity implements MetronomeClick, MetronomePreClick {
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
    Context c;
    private MediaPlayer mp;

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
        mp = MediaPlayer.create(this, R.raw.click);
        m = new Metronome(bpm);
        c = getApplicationContext();
        startMetronome = (Button) findViewById(R.id.start_metronome);
        stopMetronome = (Button) findViewById(R.id.stop_metronome);
        metronomeTV = (TextView) findViewById(R.id.metronome_tv);
        stopRec = (Button) findViewById(R.id.stop_rec);
        beatCount = 1;
        loopCount = 0;
        r = new Recorder();
        p = new Player(c);
        sampleButtons = new Button[]{(Button) findViewById(R.id.sample_one)};
        metronomeThread = new Thread(m);
        metronomeThread.start();
        m.addMetronomeClickListener(this);
        m.addMetronomePreClickListener(this);
     /*   new Thread(new Runnable() {
            @Override
            public void run() {
                LoopMediaPlayer.create(getApplicationContext(), R.raw.sample);
            }
        }).start();
*/


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
                r.prepareRecorder();
            }
        });
        stopRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r.enableRecording();

            }
        });
    }

    private void addSample() {
        Sample sample = new Sample(r.getSamplePath());
        samples.add(sample);
        //samples.add(new Sample(Environment.getExternalStorageDirectory().getAbsolutePath() + "/LoopyTunesHD/sample.ogg"));
    }

    private void getBPM() {
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        String bpm_str = extras.getString("bpm_value");
        bpm = Integer.valueOf(bpm_str);
        Log.d("BPM", String.valueOf(bpm));
        MediaPlayer m = MediaPlayer.create(this, R.raw.sample);
        Log.d("'SAMPLELENGTH", String.valueOf(m.getDuration()));
    }

    long dif;

    @Override
    public void metronomeClick() {

        Log.d("CLICK", "CLICK");
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mp.start();
                // This code will always run on the UI thread, therefore is safe to modify UI elements.
                long t = System.currentTimeMillis() - dif;
                Log.d("dif", String.valueOf(t));
                if (beatCount == 1) {
                    loopCount++;
                }
                if (clickedLoopCount + 1 == loopCount && button1Rec) {
                    if (!isRecording) {
                        r.startRecording();
                        isRecording = true;
                        Log.d("RECORD", "RECORD");
                    }
                }
                if (clickedLoopCount + 2 == loopCount && button1Rec) {
                    isRecording = false;
                    button1Rec = false;
                    r.stopRecording();
                    addSample();
                    Log.d("SPLAY", "ADD");

                }
                metronomeTV.setText(String.valueOf(beatCount));
                if (beatCount == 4) {
                    beatCount = 0;
                    //stopSamples();

                }
                if (beatCount == 1) {
                    //stopSamples();
                    if (loopCount == 3) {
                        //m.setBarTimeNano();
                    }
                    Log.d("SPLAY", "Play");
                    playSamples();

                }
            /*    System.out.println(button1Rec);
                System.out.println(beatCount);
                System.out.println(loopCount);*/


                beatCount++;
            }
        });

    }

    private void stopSamples() {
        p.stopSample(samples);
    }

    private void playSamples() {

          /*  AudioTrack at = samples.get(0).getSampleAt();
            at.reloadStaticData();
            at.setLoopPoints( 0, samples.get(0).getBufferSize(), -1 );
*/

        p.playSamples(samples);


    }

    public void pr(String msg) {
        Log.d(msg, msg);

    }

    @Override
    public void metronomePreClick() {
/*        new Thread(new Runnable() {
            @Override
            public void run() {
                dif = System.currentTimeMillis();
                Log.d("DIF", String.valueOf(dif));
                Log.d("PRECLICK", "PRECLICK");
            }
        }).start();*/
    }
}
