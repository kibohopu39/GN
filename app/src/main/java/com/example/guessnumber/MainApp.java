package com.example.guessnumber;

import android.app.Application;

public class MainApp extends Application {
    public static int guesstimes;
    public static int guessplayingStage=1;
    @Override
    public void onCreate() {
        super.onCreate();
    }

}
