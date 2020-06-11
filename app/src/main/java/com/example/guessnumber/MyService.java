package com.example.guessnumber;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Binder;
import android.os.IBinder;

public class MyService extends Service {
    private final Binder mbinder=new LocalBinder();
    private MediaPlayer mediaPlayer;
    private SoundPool.Builder soundPoolBuilder;
    private SoundPool soundPool;
    private int btn_sound;
    public MyService() {
    }
    public class LocalBinder extends Binder{//建一個內部類別裡的方法,方法是回傳父類別物件,也就是自己
        public MyService getService(){
            return MyService.this;//傳回自己
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return mbinder;
    }

    String uriaudio="@raw/"+"au";

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer=MediaPlayer.create(this,R.raw.viewofvalley);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //建立用來建立soundpool的builder
        soundPoolBuilder=new SoundPool.Builder();
        soundPoolBuilder.setMaxStreams(5);
        soundPool=soundPoolBuilder.build();
        soundPoolLoad();
    }

    //啟動型
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String act=intent.getStringExtra("ACTION");
        if (act.equals("start")){
            if (mediaPlayer!=null) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setVolume(0.4f, 0.4f);
                }
            }
        }
        else if (act.equals("pause")){
            if (mediaPlayer!=null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer!=null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
        super.onDestroy();
    }

    public void soundPoolLoad(){
        btn_sound=soundPool.load(getApplicationContext(),R.raw.keydown,1);
    }

    public void playkeydown_sound(){
        soundPool.play(btn_sound ,1,1,1,0,1);
    }
}

