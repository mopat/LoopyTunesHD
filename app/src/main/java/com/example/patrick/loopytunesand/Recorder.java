package com.example.patrick.loopytunesand;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Patrick on 04.01.2016.
 */
public class Recorder {
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread, stopRecordingThread = null;
    private boolean isRecording = false, enableRecording = false;
    private String samplePath = null;
    long recordTime, st;
    int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
            RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

    int BufferElements2Rec = bufferSize / 2; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format
    long a;
    File sample;

    public void prepareRecorder() {
        System.out.print("STARTRECORD");

        try {
            sample = File.createTempFile("smp", ".pcm", Absolutes.DIRECTORY);
        } catch (IOException e) {
            Log.e("ERROR", "sdcard access error");
            return;
        }
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);


        recorder.startRecording();

        recordingThread = new Thread(new Runnable() {
            public void run() {
                isRecording = true;
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
    }

    public void startRecording() {
        Log.d("RECREADY", "STARTREC");
        a = System.currentTimeMillis();
        recordingThread.start();
        st=System.currentTimeMillis();
    }

    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    private void writeAudioDataToFile() {
        // Write the output audio in byte

        samplePath = sample.getAbsolutePath();
        short sData[] = new short[BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(samplePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.print("STOPRECORD");
        long b = System.currentTimeMillis() - a;

        while (isRecording) {
            // gets the voice output from microphone to byte format

            Log.d("ENABLED", String.valueOf(enableRecording));

            recorder.read(sData, 0, BufferElements2Rec);

            //System.out.println("Short wirting to file" + sData.toString());
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer

                byte bData[] = short2byte(sData);


                Log.d("WRITEDIF", String.valueOf(b));
                os.write(bData, 0, BufferElements2Rec * BytesPerElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void stopRecording() {
        stopRecordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // stops the recording activity
                if (null != recorder) {
                    isRecording = false;
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                    recordingThread = null;
                    enableRecording = false;
                }
                recordTime = System.currentTimeMillis() - st;
                Log.d("RECORDTIME", String.valueOf(recordTime));
            }
        });
        stopRecordingThread.start();
    }

    public void enableRecording() {
        enableRecording = true;
    }

    public void disableRecoding() {
        enableRecording = false;
    }


    public String getSamplePath() {
        return samplePath;
    }
}