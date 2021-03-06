package com.example.patrick.loopytunesand;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.String;


/**
 * Created by Patrick on 04.01.2016.
 */
public class Player {
    Thread stopThread, playThread, th, tp;
    long lag, st;
    Context context;
    int intSize = android.media.AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT);
    AudioManager am;
    private List<PlayerUpdate> updateListeners = new ArrayList<PlayerUpdate>();

    public Player(Context context) {
        this.context = context;
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void addUpdateListener(PlayerUpdate listener) {
        updateListeners.add(listener);
    }

    void update(byte[] playedBytes) {
        for (PlayerUpdate listener : updateListeners) {
            listener.playerUpdate(playedBytes);
        }
    }

    public void stopSample(final ArrayList<Sample> samples) {
        stopThread = new Thread(new Runnable() {
            @Override
            public void run() {

                st = System.currentTimeMillis();
                for (int i = 0; i < samples.size(); i++) {

                    Sample sample = samples.get(i);
               /*     Log.d("STATE", "STOP");
                    sample.getSampleAt().stop();
                    sample.getSampleAt().flush();
                    sample.getSampleAt().reloadStaticData();*/
                    sample.getSampleAt().flush();
                    sample.getSampleAt().setPlaybackHeadPosition(0);
                }
            }
        });
        stopThread.start();
    }

    long c;

    public void playSamples(final ArrayList<Sample> samples, final int beatcount) {

        for (int i = 0; i < samples.size(); i++) {
            final Sample sample = samples.get(i);
            new Thread(new Runnable() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void run() {

                   /* FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(sample.filePath());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    File l = new File(sample.filePath());
              System.out.println(sample.getBufferSize());
                    System.out.println(sample + " " + sample.getSampleAt().getState());
                    int intSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);
                    AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT, intSize, AudioTrack.MODE_STREAM);
                    try {
                        while (fis.read(sample.getByteData())!=-1) {



                            if (at != null) {
                                at.write(sample.getByteData(), 0, sample.getBufferSize());
                                at.play();
                                System.out.println("PLAY");


                            }
                            try {
                                fis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
               /*     final AudioTrack at = sample.getSampleAt();
                    final byte[] byteData = sample.getByteData();
                    if (at != null) {

// Write the byte array t            at.stop();

                        Log.d("SAMPLE", "PLAY");
                        st = System.currentTimeMillis();

                        Log.d("BUFFERLENGTH", String.valueOf(byteData.length));

                        th = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < 4; i+=114434/4) {
                                    at.setPlaybackHeadPosition(0);
                                    at.play();

                                    at.write(byteData, 0, byteData.length);
                                    at.flush();
                                }

                                //at.setLoopPoints(0, samples.get(0).getBufferSize()/2, -1);

                                Log.d("etPlaybackHeadPosition()", String.valueOf(at.getPlaybackHeadPosition()));
                            }
                        });
                        th.start();

                        lag = System.currentTimeMillis() - st;
                    } else
                        Log.d("TCAudio", "audio track is not initialised ");*/



/*//Reading the file..
 if (sample.filePath() == null)
                        return;

                    byte[] byteData = null;
                    File file = null;
                    file = new File(sample.filePath()); // for ex. path= "/sdcard/samplesound.pcm" or "/sdcard/samplesound.wav"
                    byteData = new byte[(int) file.length()];
                    FileInputStream in = null;
                    try {
                        in = new FileInputStream( file );
                        try {
                            in.read( byteData );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
                        e.printStackTrace();
                    }
// Set and push to audio track..
                    int intSize = android.media.AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);
                    AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 16000, AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT, intSize, AudioTrack.MODE_STREAM);
                    if (at!=null) {
                        at.play();
// Write the byte array to the track
                        at.write(byteData, 0, byteData.length);
                        at.stop();
                        at.release()
                    }
                    else
                        Log.d("TCAudio", "audio track is not initialised ");;*/

                    // We keep temporarily filePath globally as we have only two sample sounds now..
                    if (sample.filePath() == null)
                        return;


                    final AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT, intSize, AudioTrack.MODE_STREAM);

                    if (at == null) {
                        Log.d("TCAudio", "audio track is not initialised ");
                        return;
                    }
                    int latency = 0;

                    Log.d("getOutputLatency", String.valueOf(latency));
                    int count = 128; // 512 kb
//Reading the file..
                    byte[] byteData = null;
                    File file = null;
                    file = new File(sample.filePath());

                    byteData = sample.getByteData();
                    BufferedInputStream bis = null;
                    FileInputStream in = null;
                    DataInputStream dis = null;
                    try {
                        in = new FileInputStream(file);

                        bis = new BufferedInputStream(in);
                        dis = new DataInputStream(bis);
                    } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    int skip = 28224;
                   /*     if(beatcount == 1)
                            skip = 0;
                        if(beatcount == 2)
                            skip = 70784;
                        if(beatcount == 3)
                            skip = 70784*2;
                        if(beatcount == 4)
                            skip = 70784*3;*/

                    try {
                        //in.skip(7880);
                        in.skip(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int bytesread = 0, ret = 0;
                    int size = (int) file.length();
                    Log.d("FILELENGTH", String.valueOf(size));
                    long a = 1;
                    int i = 0;

                    while (bytesread <= size) {
                        if (i == 0) {
                            a = System.currentTimeMillis();
                            c = System.currentTimeMillis();
                            // Log.d("CURRRI", String.valueOf(c));
                        }


                        try {
                            ret = bis.read(byteData, 0, count);
                            // Log.d("BYTESREAD", String.valueOf(ret));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (ret != -1) { // Write the byte array to the track

                            Long g = System.currentTimeMillis();
                            // Log.d("ClickTriggeredA", String.valueOf(System.currentTimeMillis()));
                            byte[] readBytes = new byte[ret];
                            System.arraycopy(byteData, 0, readBytes, 0, readBytes.length);
                            //update(readBytes);
                            at.write(byteData, 0, count);
                            at.play();


                            //at.write(sample.getByteBuffer(), size, AudioTrack.WRITE_NON_BLOCKING, System.currentTimeMillis());
                            //at.play();


                            bytesread += ret;
                        } else {
                            long d = System.currentTimeMillis() - a;
                            Log.d("SKIPPING", String.valueOf(d));
                            break;
                        }
                    }
                    i++;
                    update(byteData);
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    at.stop();
                    at.release();
                }

            }).start();


        }
        Log.d("LATENCY", String.valueOf(lag));
    }
}
