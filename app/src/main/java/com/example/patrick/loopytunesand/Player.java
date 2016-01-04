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

/**
 * Created by Patrick on 04.01.2016.
 */
public class Player {
    public void playSample(Sample sample) throws IOException {
        AudioTrack at = sample.getSampleAt();
        byte[] byteData = sample.getByteData();
        if (at != null) {
            at.play();
// Write the byte array to the track
            at.write(byteData, 0, byteData.length);
            at.stop();
            at.release();
        } else
            Log.d("TCAudio", "audio track is not initialised ");

    }
}
