package com.example.patrick.loopytunesand;

import android.media.audiofx.Visualizer;

/**
 * Created by Patrick on 08.01.2016.
 */
public class AudioVisualizer extends Visualizer {

    Visualizer v;
    int waveForm;


    /**
     * Class constructor.
     *
     * @param audioSession system wide unique audio session identifier. If audioSession
     *                     is not 0, the visualizer will be attached to the MediaPlayer or AudioTrack in the
     *                     same audio session. Otherwise, the Visualizer will apply to the output mix.
     * @throws UnsupportedOperationException
     * @throws RuntimeException
     */
    public AudioVisualizer(int audioSession) throws UnsupportedOperationException, RuntimeException {
        super(audioSession);
        v = new AudioVisualizer(audioSession);
    }

    public void createWaveForm(byte[] data){
        waveForm = v.getWaveForm(data);
    }
}
