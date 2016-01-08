package com.example.patrick.loopytunesand;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
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

interface PlayerUpdate {
    void playerUpdate(byte[] playedBytes);
}

public class Looper extends AppCompatActivity implements MetronomeClick, MetronomePreClick, PlayerUpdate {
    private Button startMetronome, stopMetronome, emitClick;
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
    private Visualizer v;
    VisualizerView visualizerView;

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
        mp = MediaPlayer.create(this, R.raw.clickpcm);
        m = new Metronome(bpm);
        lm = new LatencyMetronome(bpm);
        c = getApplicationContext();
        clickSample = new Sample(Absolutes.DIRECTORY + "/click.pcm");
        clickList.add(clickSample);
        startMetronome = (Button) findViewById(R.id.start_metronome);
        stopMetronome = (Button) findViewById(R.id.stop_metronome);
        metronomeTV = (TextView) findViewById(R.id.metronome_tv);
        emitClick = (Button) findViewById(R.id.emit_click);
        beatCount = 0;
        loopCount = 0;
        r = new Recorder();
        p = new Player(c);
        p.addUpdateListener(this);
        sampleButtons = new Button[]{(Button) findViewById(R.id.sample_one)};
        metronomeThread = new Thread(m);
        metronomeThread.start();
        latencyMetronomeThread = new Thread(lm);
        latencyMetronomeThread.start();
        m.addMetronomeClickListener(this);
        lm.addMetronomePreClickListener(this);
        v = new Visualizer(1);
        visualizerView = (VisualizerView) findViewById(R.id.visualizer_view);
     /*   new Thread(new Runnable() {
            @Override
            public void run() {
                LoopMediaPlayer.create(getApplicationContext(), R.raw.sample);
            }
        }).start();
*/


    }

    private void calibrate() {
        r.prepareRecorder();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mp.start();
        r.startRecording();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        r.stopRecording();
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
                clickedLoopCount2 = loopCount2;
                button1Rec = true;
                r.prepareRecorder();
            }
        });
        emitClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calibrate();

            }
        });
    }

    private void addSample() {
        Sample sample = new Sample(r.getSamplePath());
        samples.add(sample);
        visualizerView.setEnabled(true);

        v = new Visualizer(sample.getSampleAt().getAudioSessionId());
        v.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        v.setEnabled(true);



        //samples.add(new Sample(Environment.getExternalStorageDirectory().getAbsolutePath() + "/LoopyTunesHD/sample.ogg"));
    }

    private void setupVisualizerFxAndUI(Sample sample, byte[] playedBytes) {


        visualizerView.updateVisualizer(playedBytes);

        // Create the Visualizer object and attach it to our media player.

        v.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        visualizerView.updateVisualizer(bytes);
                        Log.d("VISUALITZER", String.valueOf(bytes));
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                        Log.d("VISUALITZER", String.valueOf(bytes));
                    }

                    ;
                }, Visualizer.getMaxCaptureRate() / 2, true, false);


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
    int beatCount2 = 0;
    long kif = 0;

    @Override
    public void metronomeClick() {
        //Log.d("CLICK", "CLICK");
        pr("METROCLICK", String.valueOf(System.currentTimeMillis()));
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mp.isPlaying())
                    mp.stop();
                mp.start();
                beatCount++;
                if (beatCount == 5)
                    beatCount = 1;
                // This code will always run on the UI thread, therefore is safe to modify UI elements.
                if (beatCount == 1) {
                    loopCount++;
                }

                pr("METROCLICK", String.valueOf(System.currentTimeMillis() - kif));
                metronomeTV.setText(String.valueOf(beatCount));
                Log.d("COUNTBEAT", String.valueOf(beatCount));
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

                if (button1Rec && beatCount == 1) {
                    // if (!isRecording) {
                    //Log.d("ClickTriggeredA", String.valueOf(System.currentTimeMillis()));
                    //r.startRecording();
                    Log.d("RECORDINGSTARTHE", String.valueOf(System.currentTimeMillis()));
                    //isRecording = true;
                    //}
                }

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

    public void pr(String tag, String msg) {
        Log.d(tag, msg);

    }

    int loopCount2 = 0;
    int clickedLoopCount2 = 0;

    @Override
    public void metronomePreClick() {
        kif = System.currentTimeMillis();

        new Thread(new Runnable() {
            @Override
            public void run() {
                beatCount2++;
                if (beatCount2 == 5)
                    beatCount2 = 1;
                // This code will always run on the UI thread, therefore is safe to modify UI elements.
                if (beatCount2 == 1) {
                    loopCount2++;
                }
                if (beatCount2 == 1) {
                    Log.d("SPLAY", "Play");
                    stopSamples();
                    playSamples();
                    Log.d("METROONE", String.valueOf(System.currentTimeMillis()));

                }
                //pr("METROCLICK", String.valueOf(System.currentTimeMillis() - kif));

                Log.d("COUNTBEAT", String.valueOf(beatCount2));
                if (button1Rec && beatCount2 == 1) {
                    if (!isRecording) {
                        //Log.d("ClickTriggeredA", String.valueOf(System.currentTimeMillis()));
                        r.startRecording();
                        Log.d("RECORDINGSTART", String.valueOf(System.currentTimeMillis()));
                        //pr("METROLATENCY", String.valueOf(kif));
                        isRecording = true;
                    }
                }

            }
        }).start();
    }

    @Override
    public void playerUpdate(final byte[] playedBytes) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for (Sample s : samples) {

                    setupVisualizerFxAndUI(s, playedBytes);
                }
            }
        });


    }
}
