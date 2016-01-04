package com.example.patrick.loopytunesand;

import android.os.Environment;

import java.io.File;

/**
 * Created by Patrick on 04.01.2016.
 */
public final class Absolutes {
    public static final File DIRECTORY =  new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/LoopyTunesHD");
}
