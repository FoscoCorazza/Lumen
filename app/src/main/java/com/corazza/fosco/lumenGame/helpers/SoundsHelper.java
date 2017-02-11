package com.corazza.fosco.lumenGame.helpers;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.RawRes;
import android.util.Log;

import com.corazza.fosco.lumenGame.R;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SoundsHelper {

    private boolean title = false;
    private boolean level = false;
    private MediaPlayer themeMPlayer, eraserMPlayer;
    private static SoundsHelper sharedInstance;

    public static SoundsHelper getInstance(){
        if(sharedInstance == null) sharedInstance = new SoundsHelper();
        return sharedInstance;
    }


    private void play_theme(final Context context, @RawRes int res){

        if(themeMPlayer != null)
        {
            themeMPlayer.release();
            themeMPlayer = null;
        }

        themeMPlayer = MediaPlayer.create(context, res);
        themeMPlayer.setLooping(true);
        themeMPlayer.start();
        setVolume();
    }

    public void play_title_theme(final Context context){
        if(!isPlaying() || level) {
            if (level) themeMPlayer.stop();
            play_theme(context, R.raw.blip_stream);
        }
        title = true;
        level = false;
    }

    public void play_level_theme(final Context context){
        if(!isPlaying() || title) {
            if (title) themeMPlayer.stop();
            play_theme(context, R.raw.all_of_us);
        }
        level = true;
        title = false;
    }

    public void play_split(final Context context){
        play_effect(context, R.raw.split);
    }

    public void play_unite(final Context context){
        play_effect(context, R.raw.unite);
    }

    public void play_star(final Context context){
        play_effect(context, R.raw.pick_star);
    }

    public void play_bulb(final Context context){
        play_effect(context, R.raw.pick_bulb);
    }

    public void play_error(final Context context){
        play_effect(context, R.raw.error);
    }


    private void play_effect(final Context context, @RawRes int res){
        if(active) {
            MediaPlayer effectMPlayer = MediaPlayer.create(context, res);
            effectMPlayer.setVolume(0.5f, 0.5f);
            effectMPlayer.start();
            effectMPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.stop();
                    mediaPlayer.release();

                }
            });
        }
    }

    private boolean playingErasing = false;
    public void play_eraser(final Context context) {
        if(!playingErasing) {
            playingErasing = true;
            if (eraserMPlayer != null) {
                eraserMPlayer.release();
                eraserMPlayer = null;
            }

            eraserMPlayer = MediaPlayer.create(context, R.raw.eraser);
            eraserMPlayer.setLooping(true);
            eraserMPlayer.start();
            setVolume();
        }
    }

    public void stop_eraser(){
        if(eraserMPlayer != null && eraserMPlayer.isPlaying()) eraserMPlayer.stop();
        playingErasing = false;
    }


    public void pauseTheme(){
        if(themeMPlayer != null && themeMPlayer.isPlaying()) themeMPlayer.pause();
    }


    public void resumeTheme(){
        if(themeMPlayer != null && !themeMPlayer.isPlaying()) themeMPlayer.start();
    }

    public boolean isPlaying(){
        return themeMPlayer != null && themeMPlayer.isPlaying();
    }

    private boolean active = true;
    public void swap() {
        active = !active;
        setVolume();
    }

    private void setVolume() {
        float volume = active ? 1 : 0;
        themeMPlayer.setVolume(volume, volume);
    }


}
