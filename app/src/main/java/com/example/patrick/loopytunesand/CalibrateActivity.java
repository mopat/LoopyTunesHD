package com.example.patrick.loopytunesand;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CalibrateActivity extends AppCompatActivity implements PlayerUpdate {
    MediaPlayer mp;
    private Button calibrateButton, testCalibrationButton;
    private VisualizerView visualizerViewOutput, visualizerViewInput;
    private Visualizer outputVisualizer, inputVisualizer, outputMixVisulaizer;
    List<Byte> bytesList = new ArrayList<Byte>();
    private Recorder r;
    private Player player;
    ArrayList<Sample> samples = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);

        init();
        initListeners();
        initOutputVisualizer();
        r.prepareRecorder();
    }

    private void init() {
        mp = MediaPlayer.create(this, R.raw.clickpcm);
        player = new Player(getApplicationContext());
        r = new Recorder();
        player.addUpdateListener(this);
        calibrateButton = (Button) findViewById(R.id.start_calibration_button);
        testCalibrationButton = (Button) findViewById(R.id.test_calibration_button);
        visualizerViewOutput = (VisualizerView) findViewById(R.id.output_visualizer);
        visualizerViewInput = (VisualizerView) findViewById(R.id.input_visualizer);
    }
    ArrayList<Byte> byties = new ArrayList<>();

    private byte[] outputtedBytes;
    private void initOutputVisualizer() {

        outputMixVisulaizer = new Visualizer(0);
        outputMixVisulaizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        outputMixVisulaizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                //visualizerViewOutput.updateVisualizer(bytes); // this is the where you put the pcm routine
                //visualizerViewOutput.setEnabled(false);
                //outputMixVisulaizer.setEnabled(false);
                for (Byte b : bytes)
                    byties.add(b);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);

    }


    private byte[] listToByte(ArrayList<Byte> bytes){
        byte[] bytes1 = new byte[bytes.size()];
        for(int i = 0; i < bytes.size(); i++){
            bytes1[i] = bytes.get(i);
        }
        return bytes1;
    }

    private void initListeners() {
        String clickFilePath = Absolutes.DIRECTORY + "/clicks.pcm";
        Sample s = new Sample(clickFilePath);

        samples.add(s);
        calibrateButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if (samples.size() > 1)
                        samples.remove(1);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            player.playSamples(samples, 0);
                            outputMixVisulaizer.setEnabled(true);
                        }
                    }).start();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            r.startRecording();
                        }
                    }).start();
                }
                return false;
            }
        });
/*        calibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setupVisualization();


           *//*     try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*//*

                if (samples.size() > 1)
                    samples.remove(1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        player.playSamples(samples, 0);
                        outputMixVisulaizer.setEnabled(true);
                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        r.prepareRecorder();
                        r.startRecording();
                    }
                }).start();
            }
        });*/
        testCalibrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r.prepareRecorder();
                player.stopSample(samples);
                player.playSamples(samples, 0);
            }
        });

    }

    @Override
    public void playerUpdate(final byte[] playedBytes) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("BYTIES", String.valueOf(playedBytes.length));
                r.stopRecording();
                outputtedBytes = listToByte(byties);
                visualizerViewOutput.updateVisualizer(outputtedBytes);

                //visualizerViewOutput.updateVisualizer(playedBytes);
                File f = new File(r.getSamplePath());

                byte[] byteArray = readContentIntoByteArray(f);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(Absolutes.DIRECTORY + "/recordedsample.pcm");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    fos.write(byteArray);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (samples.size() == 1) {
                    recordedSample = new Sample(Absolutes.DIRECTORY + "/recordedsample.pcm");
                    samples.add(recordedSample);
                    visualizerViewInput.updateVisualizer(byteArray);
                }

            }
        });

    }

    Sample recordedSample;

    private static byte[] readContentIntoByteArray(File file) {
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        int toCut = 0;
        byte[] cut = new byte[(int) file.length() - toCut];
        try {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.arraycopy(bFile, toCut, cut, 0, cut.length);
        return cut;
    }

    private void setupVisualizerFxAndUI(byte[] playedBytes) {


        // Create the Visualizer object and attach it to our media player.
/*
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
                }, Visualizer.getMaxCaptureRate() / 2, true, false);*/


    }

/*    private void setupVisualization() {
        setupVisualizerFxAndUI();
        outputVisualizer.setEnabled(true);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                //visualizerViewOutput.updateVisualizer(listToByteArray());
                outputVisualizer.setEnabled(false);


            }
        });
        mp.start();
    }

    private void setupVisualizerFxAndUI() {

        // Create the Visualizer object and attach it to our media player.
        outputVisualizer = new Visualizer(mp.getAudioSessionId());
        outputVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        outputVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {

                      for (Byte b : bytes){
                            bytesList.add(b);

                        }
;
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                        visualizerViewOutput.updateVisualizer(bytes);
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

    private byte[] listToByteArray() {
        byte[] byties = new byte[bytesList.size()];
        for (int i = 0; i < bytesList.size(); i++) {
            byties[i] = bytesList.get(i);

        }
        Log.d("BYTIES", String.valueOf(byties.length));
        return byties;
    }*/
}
