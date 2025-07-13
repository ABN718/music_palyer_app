package com.example.musicplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import static com.example.musicplayer.AlbumDetailsAdapter.albummfiles;
import static com.example.musicplayer.ApplictionClass.ACTION_NEXT;
import static com.example.musicplayer.ApplictionClass.ACTION_PLAY;
import static com.example.musicplayer.ApplictionClass.ACTION_PREVIOUS;
import static com.example.musicplayer.ApplictionClass.CHANNEL_ID_2;
import static com.example.musicplayer.MainActivity.musicFiles;
import static com.example.musicplayer.MainActivity.repeatboolean;
import static com.example.musicplayer.MainActivity.shuffleboolean;
import static com.example.musicplayer.MusicAdapter.mfiles;

public class PlayerActivity extends AppCompatActivity
        implements  ActionPlaying , ServiceConnection  {

    TextView song_name , artist_name , duration_played , duration_total;
    ImageView cover_art , nextbtn , prevbtn , backbtn , shufflebtn , repeatbtn;
    FloatingActionButton playpuasebtn;
    SeekBar seekBar;
    public AudioManager audioManager;
    int position = -1;
    public static ArrayList<MusicFiles> listsongs = new ArrayList<>();
    public static Uri uri;
    //public static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread , prevThread , nextThread;
    MusicService musicService;
    MediaSessionCompat mediaSessionCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();

        audioControl();

        mediaSessionCompat = new MediaSessionCompat(getBaseContext() , "My Audio");
        initviews();
        getIntentMethod();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(musicService != null && b)
                {
                    musicService.seekTo(i*1000);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
            public void run() {
                if(musicService != null)
                {
                    int mcurrentposition = musicService.getCurrentPosition() / 1000;
                    seekBar.setProgress(mcurrentposition);
                    duration_played.setText(formattedTime(mcurrentposition));
                }
                handler.postDelayed(this , 1000);
            }
        });
        shufflebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shuffleboolean)
                {
                    shuffleboolean = false ;
                    shufflebtn.setImageResource(R.drawable.ic_baseline_shuffle_24);
                }
                else
                {
                    shuffleboolean = true ;
                    shufflebtn.setImageResource(R.drawable.ic_baseline_shuffle_on);
                }

            }
        });
        repeatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(repeatboolean)
                {
                    repeatboolean = false ;
                    repeatbtn.setImageResource(R.drawable.ic_baseline_repeat);
                }
                else
                {
                    repeatboolean = true;
                    repeatbtn.setImageResource(R.drawable.ic_baseline_repeat_one_24);
                }


            }
        });
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this , MusicService.class);
        bindService(intent , this , BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    // on click (play- pause & next & prev ) buttons Methods
    private void prevThreadBtn() {
        prevThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                prevbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prevbtnclicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void nextThreadBtn() {
        nextThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                nextbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextbtnclicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void playThreadBtn() {
        playThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                playpuasebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playpuasebtnclicked();
                    }
                });
            }
        };
        playThread.start();
    }

    public void prevbtnclicked() {
        if(musicService.isPlaying())
        {
            musicService.stop();
            musicService.release();
            if(shuffleboolean && ! repeatboolean)
            {
                position = ((position - 1) < 0 ? (listsongs.size() -1) : (position -1));
            }
            else if(!shuffleboolean && !repeatboolean)
            {
                position = ((position - 1) < 0 ? (listsongs.size() -1) : (position -1));
            }

            uri = Uri.parse(listsongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);

            song_name.setText(listsongs.get(position).getTitle());
            artist_name.setText(listsongs.get(position).getArtist());

            seekBar.setMax(musicService.getDuration() /1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService != null)
                    {
                        int mcurrentposition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mcurrentposition);
                    }
                    handler.postDelayed(this , 1000);
                }
            });
            musicService.OnCompleted();
            //showNotification(R.drawable.ic_baseline_pause);
            playpuasebtn.setBackgroundResource(R.drawable.ic_baseline_pause);
            musicService.start();

        }
        else{
            musicService.stop();
            musicService.release();
            position = ((position - 1) < 0 ? (listsongs.size() -1) : (position -1));
            uri = Uri.parse(listsongs.get(position).getPath());
           musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listsongs.get(position).getTitle());
            artist_name.setText(listsongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() /1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService != null)
                    {
                        int mcurrentposition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mcurrentposition);
                    }
                    handler.postDelayed(this , 1000);
                }
            });
            musicService.OnCompleted();
            //showNotification(R.drawable.ic_baseline_play_arrow_24);
            playpuasebtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    public void nextbtnclicked() {
        if(musicService.isPlaying())
        {
            musicService.stop();
            musicService.release();
            if(shuffleboolean && ! repeatboolean)
            {
                position = getrandom(listsongs.size() -1);
            }
            else if(!shuffleboolean && !repeatboolean)
            {
                position = ((position + 1) % listsongs.size());
            }
            uri = Uri.parse(listsongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);

            song_name.setText(listsongs.get(position).getTitle());
            artist_name.setText(listsongs.get(position).getArtist());

            seekBar.setMax(musicService.getDuration() /1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService != null)
                    {
                        int mcurrentposition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mcurrentposition);
                    }
                    handler.postDelayed(this , 1000);
                }
            });
            musicService.OnCompleted();
            //showNotification(R.drawable.ic_baseline_pause);
            playpuasebtn.setBackgroundResource(R.drawable.ic_baseline_pause);
            musicService.start();

        }
        else{
            musicService.stop();
            musicService.release();
            if(shuffleboolean && ! repeatboolean)
            {
                position = getrandom(listsongs.size() -1);
            }
            else if(!shuffleboolean && !repeatboolean)
            {
                position = ((position + 1) % listsongs.size());
            }
            uri = Uri.parse(listsongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listsongs.get(position).getTitle());
            artist_name.setText(listsongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() /1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService != null)
                    {
                        int mcurrentposition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mcurrentposition);
                    }
                    handler.postDelayed(this , 1000);
                }
            });
            musicService.OnCompleted();
           // showNotification(R.drawable.ic_baseline_play_arrow_24);
            playpuasebtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    public void playpuasebtnclicked() {
        if(musicService.isPlaying())
        {
            playpuasebtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            //showNotification(R.drawable.ic_baseline_play_arrow_24);
            musicService.pause();
            seekBar.setMax(musicService.getDuration() /1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService != null)
                    {
                        int mcurrentposition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mcurrentposition);
                    }
                    handler.postDelayed(this , 1000);
                }
            });
        }
        else
        {
            //showNotification(R.drawable.ic_baseline_pause);
            playpuasebtn.setImageResource( R.drawable.ic_baseline_pause);
            musicService.start();
            seekBar.setMax(musicService.getDuration() /1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService != null)
                    {
                        int mcurrentposition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mcurrentposition);
                    }
                    handler.postDelayed(this , 1000);
                }
            });
        }
    }

// End of on click (play- pause & next & prev ) buttons Methods

    private int getrandom(int i) {

        Random random = new Random();
        return random.nextInt(i + 1);
    }

    private String formattedTime(int mcurrentposition) {
        String totleout  = "";
        String totlenew = "";
        String seconds = String.valueOf(mcurrentposition % 60);
        String mints = String.valueOf(mcurrentposition / 60);
        totleout = mints + ":" + seconds;
        totlenew = mints + ":" + "0" + seconds;
        if(seconds.length() == 1)
        {
            return totlenew;
        }
        else{
            return totleout;
        }
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position" , -1);
        String sender = getIntent().getStringExtra("sender");
        if(sender != null && sender.equals("albumDetails"))
        {
            listsongs = albummfiles;
        }
        else
        {
            listsongs = mfiles ;
        }
        if(listsongs != null)
        {
            playpuasebtn.setImageResource(R.drawable.ic_baseline_pause);
            uri = Uri.parse(listsongs.get(position).getPath());

        }
        //showNotification(R.drawable.ic_baseline_pause);
        Intent intent = new Intent(this  , MusicService.class);
        intent.putExtra("serviceposition" , position);
        startService(intent);

    }

    private void initviews() {
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        duration_played = findViewById(R.id.durationplyed);
        duration_total = findViewById(R.id.durationtotle);
        cover_art = findViewById(R.id.cover_art);
        nextbtn = findViewById(R.id.id_next);
        prevbtn = findViewById(R.id.id_prev);
        backbtn = findViewById(R.id.back_btn);
        shufflebtn = findViewById(R.id.id_shuffle);
        repeatbtn = findViewById(R.id.id_repeat);
        playpuasebtn = findViewById(R.id.play_puase);
        seekBar = findViewById(R.id.seek_ber);
    }
    // for photos and switching background color method
    private void metaData(Uri uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationtotal = Integer.parseInt(listsongs.get(position).getDuration()) /1000;
        duration_total.setText(formattedTime(durationtotal));
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap ;
        if(art != null)
        {
            bitmap = BitmapFactory.decodeByteArray(art , 0 ,  art.length);
            ImageAnimtion(this , cover_art , bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if(swatch != null)
                    {
                        ImageView gradient = findViewById(R.id.ImageViewGredient);
                        RelativeLayout mcontiner = findViewById(R.id.mcontiner);
                        gradient.setBackgroundResource(R.drawable.gredient_bg);
                        mcontiner.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP ,
                                new int[]{swatch.getRgb() , 0x000000000});
                        gradient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP ,
                                new int[]{swatch.getRgb() , swatch.getRgb()});
                        mcontiner.setBackground(gradientDrawableBg);
                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getBodyTextColor());

                    }
                    else
                    {
                        ImageView gradient = findViewById(R.id.ImageViewGredient);
                        RelativeLayout mcontiner = findViewById(R.id.mcontiner);
                        gradient.setBackgroundResource(R.drawable.gredient_bg);
                        mcontiner.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP ,
                                new int[]{0xff000000 , 0x000000000});
                        gradient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP ,
                                new int[]{0xff000000 , 0xff000000});
                        mcontiner.setBackground(gradientDrawableBg);
                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.DKGRAY);
                    }
                }
            });
        }
        else
        {
            Glide.with(this).asBitmap().load(R.drawable.test).into(cover_art);
        }
    }
    public void ImageAnimtion(Context context , ImageView imageView , Bitmap bitmap)
    {
        Animation animout = AnimationUtils.loadAnimation(context , android.R.anim.fade_out);
        Animation animin = AnimationUtils.loadAnimation(context , android.R.anim.fade_in);
        animout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animin.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animin);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animout);;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
        Toast.makeText(this, "connected" + musicService, Toast.LENGTH_SHORT).show();

        seekBar.setMax(musicService.getDuration() / 1000);
        metaData(uri);

        song_name.setText(listsongs.get(position).getTitle());
        artist_name.setText(listsongs.get(position).getArtist());
        musicService.OnCompleted();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;

    }
    void audioControl()
    {
        audioManager =(AudioManager) getSystemService(Context.AUDIO_SERVICE);

        AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int i) {

                if(i == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || i == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)
                {
                    musicService.pause();
                }
                else if(i == AudioManager.AUDIOFOCUS_GAIN)
                {
                    musicService.start();
                }
                else if(i == AudioManager.AUDIOFOCUS_LOSS)
                {
                    if(musicService.isPlaying())
                    {
                        musicService.release();
                    }
                }

            }
        };
        audioManager.requestAudioFocus(onAudioFocusChangeListener , AudioManager.STREAM_MUSIC , AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

    }


/*
    void showNotification(int playBauseBtn)
    {
        Intent intent = new Intent(this , PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity
                (this , 0 , intent , 0);
////////////////////////////////////////////////////////////////////////////////////

        Intent previntent = new Intent(this , NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevpending = PendingIntent.getBroadcast
                (this , 0 , previntent , PendingIntent.FLAG_UPDATE_CURRENT);
//////////////////////////////////////////////////////////////////////////////////////////////////

        Intent pauseintent = new Intent(this , NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausepending = PendingIntent.getBroadcast
                (this , 0 , pauseintent , PendingIntent.FLAG_UPDATE_CURRENT);
//////////////////////////////////////////////////////////////////////////////////////////////////

        Intent nextintent = new Intent(this , NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent nextpending = PendingIntent.getBroadcast
                (this , 0 , nextintent , PendingIntent.FLAG_UPDATE_CURRENT);
//////////////////////////////////////////////////////////////////////////////////////////////////

        byte[] picture = null;
        picture = getAlbumArt(musicFiles.get(position).getPath());
        Bitmap thumb = null ;
        if(picture != null)
        {
            thumb = BitmapFactory.decodeByteArray(picture , 0 , picture.length);
        }
        else {
            thumb = BitmapFactory.decodeResource(getResources() , R.drawable.test);
        }
        Notification notification = new NotificationCompat.Builder(this , CHANNEL_ID_2)
                .setSmallIcon(playBauseBtn)
                .setLargeIcon(thumb)
                .setContentTitle(musicFiles.get(position).getTitle())
                .setContentText(musicFiles.get(position).getArtist())
                .addAction(R.drawable.ic_baseline_skip_previous_24 , "Previous" , prevpending)
                .addAction(playBauseBtn , "Pause" , pausepending)
                .addAction(R.drawable.ic_baseline_skip_next_24 , "Next" , nextpending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0 , notification);

    }
    private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

 */

}