package com.corazza.fosco.lumenGame.helpers;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.corazza.fosco.lumenGame.R;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SoundsHelper {

    private static MediaPlayer effectMPlayer;
    private static MediaPlayer themeMPlayer;
    private static SoundsHelper sharedInstance;

    private int iVolume;
    private final static int INT_VOLUME_MAX = 100;
    private final static int INT_VOLUME_MIN = 0;
    private final static float FLOAT_VOLUME_MAX = 1;
    private final static float FLOAT_VOLUME_MIN = 0;

    public static SoundsHelper getInstance(){
        if(sharedInstance == null) sharedInstance = new SoundsHelper();
        return sharedInstance;
    }

    private SoundsHelper(){}

    public void playSound(Context context) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(context, soundUri);
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        }
    }

    private int [] too_many_lumens_audio = {
            R.raw.too_many_balls_01, R.raw.too_many_balls_02, R.raw.too_many_balls_03,
            R.raw.too_many_balls_04, R.raw.too_many_balls_05, R.raw.too_many_balls_06,
            R.raw.too_many_balls_07};

    public void play_too_many_lumens(final Context context){
        int random = new Random().nextInt(too_many_lumens_audio.length-1);

        if(effectMPlayer != null)
        {
            effectMPlayer.release();
            effectMPlayer = null;
        }

        effectMPlayer = MediaPlayer.create(context, too_many_lumens_audio[random]);
        effectMPlayer.setVolume(0.5f, 0.5f);
        effectMPlayer.start();

    }

    public void play_theme(final Context context){

        if(themeMPlayer != null)
        {
            themeMPlayer.release();
            themeMPlayer = null;
        }

        themeMPlayer = MediaPlayer.create(context, R.raw.theme);
        themeMPlayer.setLooping(true);
        themeMPlayer.start();
    }

    public void play_communication_sound(Context context, final boolean release){

        MediaPlayer mp = MediaPlayer.create(context, R.raw.communication_sound);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override public void onCompletion(MediaPlayer mp) { if(release) mp.release();}
        });
        mp.start();

    }

    public void play_communication_sound(Context context, MediaPlayer.OnCompletionListener completionListener){

        MediaPlayer mp = MediaPlayer.create(context, R.raw.communication_sound);
        mp.setOnCompletionListener(completionListener);
        mp.start();

    }


    public void pauseTheme(){
        if(themeMPlayer!= null && !themeMPlayer.isPlaying()) themeMPlayer.pause();
    }


    public void resumeTheme(){
        if(themeMPlayer != null) {
            if(!themeMPlayer.isPlaying()) themeMPlayer.start();
            //play(500, themeMPlayer);
        }
    }

    public boolean isPlaying(){
        return themeMPlayer != null && themeMPlayer.isPlaying();
    }


    public void play(int fadeDuration, final MediaPlayer mediaPlayer)
    {

        //Set current volume, depending on fade or not
        if (fadeDuration > 0)
            iVolume = INT_VOLUME_MIN;
        else
            iVolume = INT_VOLUME_MAX;

        updateVolume(0, mediaPlayer);

        //Play music
        if(!mediaPlayer.isPlaying()) mediaPlayer.start();

        //Start increasing volume in increments
        if(fadeDuration > 0)
        {
            final Timer timer = new Timer(true);
            TimerTask timerTask = new TimerTask()
            {
                @Override
                public void run()
                {
                    updateVolume(1, mediaPlayer);
                    if (iVolume == INT_VOLUME_MAX)
                    {
                        timer.cancel();
                        timer.purge();
                    }
                }
            };

            // calculate delay, cannot be zero, set to 1 if zero
            int delay = fadeDuration/INT_VOLUME_MAX;
            if (delay == 0) delay = 1;

            timer.schedule(timerTask, delay, delay);
        }
    }

    public void pause(int fadeDuration, final MediaPlayer mediaPlayer)
    {
        //Set current volume, depending on fade or not
        if (fadeDuration > 0)
            iVolume = INT_VOLUME_MAX;
        else
            iVolume = INT_VOLUME_MIN;

        updateVolume(0, mediaPlayer);

        //Start increasing volume in increments
        if(fadeDuration > 0)
        {
            final Timer timer = new Timer(true);
            TimerTask timerTask = new TimerTask()
            {
                @Override
                public void run()
                {
                    updateVolume(-1, mediaPlayer);
                    if (iVolume == INT_VOLUME_MIN)
                    {
                        //Pause music
                        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                        timer.cancel();
                        timer.purge();
                    }
                }
            };

            // calculate delay, cannot be zero, set to 1 if zero
            int delay = fadeDuration/INT_VOLUME_MAX;
            if (delay == 0) delay = 1;

            timer.schedule(timerTask, delay, delay);
        }
    }

    private void updateVolume(int change, MediaPlayer mediaPlayer)
    {
        //increment or decrement depending on type of fade
        iVolume = iVolume + change;

        //ensure iVolume within boundaries
        if (iVolume < INT_VOLUME_MIN)
            iVolume = INT_VOLUME_MIN;
        else if (iVolume > INT_VOLUME_MAX)
            iVolume = INT_VOLUME_MAX;

        //convert to float value
        float fVolume = 1 - ((float) Math.log(INT_VOLUME_MAX - iVolume) / (float) Math.log(INT_VOLUME_MAX));

        //ensure fVolume within boundaries
        if (fVolume < FLOAT_VOLUME_MIN)
            fVolume = FLOAT_VOLUME_MIN;
        else if (fVolume > FLOAT_VOLUME_MAX)
            fVolume = FLOAT_VOLUME_MAX;

        mediaPlayer.setVolume(fVolume, fVolume);
    }

}
