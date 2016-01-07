package com.example.patrick.loopytunesand;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    private LatencyMetronome lm;
    private Thread metronomeThread, latencyMetronomeThread;
    private Recorder r;
    private Player p;
    private TextView metronomeTV;
    private ArrayList<Sample> samples = new ArrayList<Sample>();
    private boolean button1Rec = false;
    private boolean isRecording = false;
    Context c;
    private MediaPlayer mp;
    private ArrayList<Sample> clickList = new ArrayList<>();
    private Sample clickSample;

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
        lm = new LatencyMetronome(bpm);
        c = getApplicationContext();
        clickSample = new Sample(Absolutes.DIRECTORY + "/click.pcm");
        clickList.add(clickSample);
        startMetronome = (Button) findViewById(R.id.start_metronome);
        stopMetronome = (Button) findViewById(R.id.stop_metronome);
        metronomeTV = (TextView) findViewById(R.id.metronome_tv);
        stopRec = (Button) findViewById(R.id.stop_rec);
        beatCount = 0;
        loopCount = 0;
        r = new Recorder();
        p = new Player(c);
        sampleButtons = new Button[]{(Button) findViewById(R.id.sample_one)};
        metronomeThread = new Thread(m);
        metronomeThread.start();
        latencyMetronomeThread = new Thread(lm);
        latencyMetronomeThread.start();
        m.addMetronomeClickListener(this);
        lm.addMetronomePreClickListener(this);
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
                lm.startMetronome();
                startMetronome.setEnabled(false);
                stopMetronome.setEnabled(true);
            }
        });
        stopMetronome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("STOP", "STOP");
                m.stopMetronome();
                lm.stopMetronome();
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
    int beatCount2 = 1;
    int kif = 0;

    @Override
    public void metronomeClick() {
        //Log.d("CLICK", "CLICK");
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mp.isPlaying())
                    mp.stop();
                //  mp.start();
                beatCount++;
                // This code will always run on the UI thread, therefore is safe to modify UI elements.
                if (beatCount == 1) {
                    loopCount++;
                }


                metronomeTV.setText(String.valueOf(beatCount));
                Log.d("COUNTCLICKED", String.valueOf(clickedLoopCount+2));
                Log.d("COUNTLOOP", String.valueOf(loopCount));
                if (clickedLoopCount + 2 == loopCount && button1Rec) {
                    isRecording = false;
                    button1Rec = false;
                    //Log.d("ClickTriggeredB", String.valueOf(System.currentTimeMillis()));
                    r.stopRecording();
                    Log.d("COUNTRECORDINGSTOP", String.valueOf(System.currentTimeMillis()));
                    Log.d("RECORDINGSTOP", String.valueOf(System.currentTimeMillis()));
                    addSample();

                }
                if (beatCount == 4) {
                    Log.d("SPLAY", "Play");
                    stopSamples();
                    playSamples();
                    Log.d("METROONE", String.valueOf(System.currentTimeMillis()));

                }
                if (beatCount == 4)
                    beatCount = 0;

            /*    System.out.println(button1Rec);
                System.out.println(beatCount);
                System.out.println(loopCount);*/



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

        p.playSamples(samples, beatCount);


    }

    public void pr(String msg) {
        Log.d(msg, msg);

    }

    @Override
    public void metronomePreClick() {
        //Log.d("METROLATENCY", String.valueOf(System.currentTimeMillis()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                long t = System.currentTimeMillis() - dif;
                Log.d("dif", String.valueOf(t));
                Log.d("METROZERO", String.valueOf(System.currentTimeMillis()));
                if (button1Rec) {
                    if (!isRecording) {
                        //Log.d("ClickTriggeredA", String.valueOf(System.currentTimeMillis()));
                        r.startRecording();
                        Log.d("RECORDINGSTART", String.valueOf(System.currentTimeMillis()));
                        isRecording = true;
                    }
                }
            }
        }).start();
    }
}
