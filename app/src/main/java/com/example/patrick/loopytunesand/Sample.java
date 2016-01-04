package com.example.patrick.loopytunesand;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Patrick on 04.01.2016.
 */
public class Sample {
    private AudioTrack at;
    private byte[] byteData;
    private int bufferSize;
    private String filePath;

    public Sample(String filePath) {
        // We keep temporarily filePath globally as we have only two sample sounds now..
        if (filePath != null) {
            this.filePath = filePath;

//Reading the file..
            byte[] byteData = null;
            File file = new File(filePath); // for ex. path= "/sdcard/samplesound.pcm" or "/sdcard/samplesound.wav"
            byteData = new byte[(int) file.length()];
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                in.read(byteData);
                in.close();

            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
// Set and push to audio track..
            int bufferSize = android.media.AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
            this.at = at;
            this.byteData = byteData;
            Log.d("buffersize", String.valueOf(bufferSize));
            this.bufferSize = bufferSize;
        }
    }

    public AudioTrack getSampleAt() {
        return at;
    }

    public byte[] getByteData() {
        return byteData;
    }

    public int getBufferSize(){
        return bufferSize;
    }

    public String filePath(){
        return filePath;
    }
}
