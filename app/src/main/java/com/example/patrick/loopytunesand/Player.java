package com.example.patrick.loopytunesand;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Patrick on 04.01.2016.
 */
public class Player {
    Thread stopThread, playThread, th,tp;
    long lag, st;

    public Player(){
    }
    public void stopSample(final ArrayList<Sample> samples) {
        stopThread = new Thread(new Runnable() {
            @Override
            public void run() {

                st = System.currentTimeMillis();
                for (int i = 0; i < samples.size(); i++) {

                    Sample sample = samples.get(i);
                    Log.d("STATE", "STOP");
                    sample.getSampleAt().stop();
                    sample.getSampleAt().flush();
                    sample.getSampleAt().reloadStaticData();
                }
            }
        });
        stopThread.start();
    }

    public void playSamples(final ArrayList<Sample> samples) {
        playThread = new Thread(new Runnable() {
            @Override
            public void run() {



                for (int i = 0; i < samples.size(); i++) {
                    Sample sample = samples.get(i);
                   System.out.println(sample.getBufferSize());
                    System.out.println(sample + " " + sample.getSampleAt().getState());
                    final AudioTrack at = sample.getSampleAt();
                    final byte[] byteData = sample.getByteData();
                    if (at != null) {

// Write the byte array t            at.stop();

                        Log.d("SAMPLE", "PLAY");
                        st = System.currentTimeMillis();

                        Log.d("BUFFERLENGTH", String.valueOf(byteData.length));

                        th = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                at.play();
                                at.write(byteData, 0, byteData.length);
                            }
                        });
                        th.start();


                        lag = System.currentTimeMillis() - st;
                    } else
                        Log.d("TCAudio", "audio track is not initialised ");
                }

                Log.d("LATENCY", String.valueOf(lag));

            }
        });
        playThread.start();
    }
}
