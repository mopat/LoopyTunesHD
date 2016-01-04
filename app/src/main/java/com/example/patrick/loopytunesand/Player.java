package com.example.patrick.loopytunesand;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
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
    Thread stopThread, playThread;
    long lag, st;

    public void stopSample(final ArrayList<Sample> samples) {
        stopThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("STATE", "STOP");
                st = System.currentTimeMillis();
                for (int i = 0; i < samples.size(); i++) {
                    Sample sample = samples.get(i);

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

                Log.d("SAMPLE", "PLAY");

                for (int i = 0; i < samples.size(); i++) {
                    Sample sample = samples.get(i);
                    System.out.println(sample + " " + sample.getSampleAt().getState());
                    AudioTrack at = sample.getSampleAt();
                    byte[] byteData = sample.getByteData();
                    if (at != null) {

// Write the byte array t            at.stop();
                        at.play();
                        at.write(byteData, 0, byteData.length);
                    } else
                        Log.d("TCAudio", "audio track is not initialised ");
                }
                lag = System.currentTimeMillis() - st;
                Log.d("LATENCY", String.valueOf(lag));
            }
        });
        playThread.start();
    }
}
