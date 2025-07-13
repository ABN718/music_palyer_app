package com.example.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import static com.example.musicplayer.PlayerActivity.listsongs;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ActionPlaying actionPlaying;
    ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    int position = -1;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //Log.e("Bind" , "Method");
        return mBinder;
    }

    public class MyBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("serviceposition" , -1);
        if(myPosition != -1)
        {
            platMedia(myPosition);
        }
        return START_STICKY;

    }

    private void platMedia(int startPosition) {
        musicFiles = listsongs;
        position = startPosition;
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(musicFiles != null)
            {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        }
        else{
            createMediaPlayer(position);
            mediaPlayer.start();

        }

    }

    void start(){
        mediaPlayer.start();
    }
    boolean isPlaying()
    {
       return mediaPlayer.isPlaying();
    }
    void stop()
    {
        mediaPlayer.stop();
    }
    void release()
    {
        mediaPlayer.release();
    }
    void pause()
    {
        mediaPlayer.pause();
    }
    int getDuration()
    {
        return mediaPlayer.getDuration();
    }
    void seekTo(int position)
    {
        mediaPlayer.seekTo(position);
    }
    int getCurrentPosition()
    {
        return mediaPlayer.getCurrentPosition();
    }
    void createMediaPlayer(int position)
    {
        uri = Uri.parse(musicFiles.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getBaseContext() , uri);
    }
    void OnCompleted()
    {
        mediaPlayer.setOnCompletionListener(this);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
       if(actionPlaying != null)
       {
           actionPlaying. nextbtnclicked();
       }
        createMediaPlayer(position);
        mediaPlayer.start();
        OnCompleted();
    }
}
