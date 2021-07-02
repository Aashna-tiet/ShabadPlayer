package org.asofat.shabadplayer;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AudioPlayer extends AppCompatActivity {

    String url; String songName;
    Button btn_play;
    MediaPlayer mediaPlayer = new MediaPlayer();
    String finalURL;
    TextView shabadName ;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        Intent msgInt = getIntent();
        url = msgInt.getStringExtra(MainActivity.EXTRA_MP3_URL);
        songName =  msgInt.getStringExtra(MainActivity.SongName);


        try {
            finalURL =  new URI(null, null, url, null).getRawPath();
            finalURL = "https://rssb.org"+finalURL;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        System.out.println("Url received : "+url);
        System.out.println("Url to be played : "+finalURL);

        shabadName = (TextView) findViewById(R.id.statusText);
        btn_play= findViewById(R.id.play);

        SongData details;

        shabadName.setText("Playing : ");

        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
    }


    public void  playMusic(View view)  {





        try {
           // Uri song_to_be_played = Uri.parse(finalURL);

           // mediaPlayer.setDataSource(this, song_to_be_played);
           mediaPlayer.setDataSource(finalURL);
           mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    public void pauseMusic(View view)
//    {
//        try {
//            super.onPause();
////            position = mediaPlayer.getCurrentPosition();
////           // mediaPlayer.pause();
////            stopMusic(view);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }

//    public void resumeMusic(View view)
//    {
//        try {
//            super.onResume();
//             //mediaPlayer.seekTo(position);
//             //mediaPlayer.start();
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//    }

    public void stopMusic(View view)
    {

        try{
            mediaPlayer.seekTo(0);
            mediaPlayer.reset();
           // mediaPlayer.release();
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
    }









}



