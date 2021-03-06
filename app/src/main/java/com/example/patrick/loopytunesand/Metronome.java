package com.example.patrick.loopytunesand;

import android.util.Log;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrick on 04.01.2016.
 */

public class Metronome implements Runnable {
    private List<MetronomeClick> clickListeners = new ArrayList<MetronomeClick>();
    int bpm;
    Handler handler;
    boolean running = false;
    long barTimeNano;


    public Metronome(int bpm) {
        this.bpm = bpm;
        Log.d("BPM", String.valueOf(bpm));
        handler = new Handler();
        bpmToNano();
    }

    @Override
    public void run() {
        while (true) {
            runMetronome();
        }
    }

    public void runMetronome() {
        long startTime = System.currentTimeMillis();
        while (running) {
            long curTime = System.currentTimeMillis();
            if (curTime - startTime >= barTimeNano) {
                // Log.d("TIMECUR", String.valueOf(curTime - startTime));
                //Log.d("ClickTriggeredA", String.valueOf(System.currentTimeMillis()));
                startTime = curTime;
                click();
               // Log.d("METRONORMAL", String.valueOf(System.currentTimeMillis()));
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

    public void addMetronomeClickListener(MetronomeClick listener) {
        clickListeners.add(listener);
    }


    void click() {
        for (MetronomeClick listener : clickListeners) {
            listener.metronomeClick();
        }
    }

}
