package com.example.patrick.loopytunesand;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrick on 04.01.2016.
 */

public class LatencyMetronome implements Runnable {
    private List<MetronomePreClick> preClickListener = new ArrayList<MetronomePreClick>();
    int bpm;
    Handler handler;
    boolean running = false;
    long barTimeNano;


    public LatencyMetronome(int bpm) {
        this.bpm = bpm;
        Log.d("BPM", String.valueOf(bpm));
        handler = new Handler();
        bpmToNano();
    }

    @Override
    public void run() {
        while (true) {
            runAudioLatencyMetronome();

        }

    }
    int i = 0;
    private void runAudioLatencyMetronome() {
        long startTime1 = System.currentTimeMillis();
        long startTime2 = System.currentTimeMillis();
        long a = 0;
        long b= 0;
        while (running) {
            if (i > 0) {
                long curTime = System.currentTimeMillis();
                if (curTime - startTime1 >= barTimeNano) {
                    //Log.d("TIMECUR", String.valueOf(curTime - startTime));
                    //Log.d("ClickTriggeredA", String.valueOf(System.currentTimeMillis()));
                    startTime1 = curTime;
                    preClick();
                    a = System.currentTimeMillis();
                    //Log.d("METROONE", String.valueOf(System.currentTimeMillis()));
                }
            }

            if (i == 0) {
                long curTime = System.currentTimeMillis();
                if (curTime - startTime2 >= barTimeNano) {
                    //Log.d("TIMECUR", String.valueOf(curTime - startTime));
                    //Log.d("ClickTriggeredA", String.valueOf(System.currentTimeMillis()));
                    startTime2 = curTime;
                    startTime1 = System.currentTimeMillis();
                    preClick();   i++;
                    //Log.d("METRODIF", String.valueOf(startTime2));
                     //Log.d("METROZERO", String.valueOf(System.currentTimeMillis()));
                }
            }

        }
    }



    public void startMetronome() {
        if (!running) {
            Log.d("START", "START");
            running = true;

        }
    }

    public void stopMetronome() {
        if (running)
            running = false;
    }

    public void setBarTimeNano() {
        barTimeNano -= 90;
    }

    private void bpmToNano() {
        long barTimeInMs = 60000 / bpm;
        barTimeNano = barTimeInMs;
    }


    public void addMetronomePreClickListener(MetronomePreClick listener) {
        preClickListener.add(listener);
    }


    void preClick() {
        for (MetronomePreClick listener : preClickListener) {
            listener.metronomePreClick();
        }
    }
}
