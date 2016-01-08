package com.example.patrick.loopytunesand;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class CalibrateActivity extends AppCompatActivity implements PlayerUpdate {
    MediaPlayer mp;
    private Button calibrateButton;
    private VisualizerView visualizerViewOutput, visualizerViewInput;
    private Visualizer outputVisualizer, inputVisualizer;
    List<Byte> bytesList = new ArrayList<Byte>();
    private Recorder r;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);

        init();
        initListeners();
    }

    private void init() {
        mp = MediaPlayer.create(this, R.raw.clickpcm);
        player = new Player(getApplicationContext());
        r = new Recorder();
        player.addUpdateListener(this);
        calibrateButton = (Button) findViewById(R.id.start_calibration_button);
        visualizerViewOutput = (VisualizerView) findViewById(R.id.output_visualizer);
        visualizerViewInput = (VisualizerView) findViewById(R.id.input_visualizer);
    }

    private void initListeners() {
        String clickFilePath = Absolutes.DIRECTORY + "/yo.pcm";
        Sample s = new Sample(clickFilePath);
        final ArrayList<Sample> samples = new ArrayList<>();
        samples.add(s);
        r.prepareRecorder();
        calibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setupVisualization();
                player.playSamples(samples, 0);

                r.startRecording();
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

                visualizerViewOutput.updateVisualizer(playedBytes);
                File f = new File(r.getSamplePath());
                byte[] byteArray = readContentIntoByteArray(f);
                visualizerViewInput.updateVisualizer(byteArray);
            }
        });

    }
    private static byte[] readContentIntoByteArray(File file)
    {
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        try
        {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
            for (int i = 0; i < bFile.length; i++)
            {
                System.out.print((char) bFile[i]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bFile;
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