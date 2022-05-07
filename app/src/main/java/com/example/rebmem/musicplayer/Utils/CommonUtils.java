package com.example.rebmem.musicplayer.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import java.util.Random;

/**
 * This is an utility class which has functions that will be used across the application.
 * @author Sathya Thameem
 * **/
public class CommonUtils {

    /**
     * This method is used to add animation on the image (Album cover) of the song.
     * **/
    public static void addAnimation(ImageView image){
        ObjectAnimator objAnimator = ObjectAnimator.ofFloat(image,"rotation",0f,360f);
        objAnimator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objAnimator);
        animatorSet.start();
    }

    /**
     * This method is used to generate a random number.
     * In this app, the random number generated is used to
     * fetch a song when shuffle button is on.
     * **/
    public static int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i+1);
     }

     /**
      * This method returns byte array , the image cover for the song path passed as a parameter
      * @param uri
      * **/
    public static byte[] getMetaData(@NonNull Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        return retriever.getEmbeddedPicture();
     }

     /**
      * This method converts the duration to mm:ss format
      * @param duration
      * **/
    public static String createTimeFormat(int duration){
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time+=min+":";
        if(sec < 10){
            time+="0";
        }
        time+=sec;
        return time;
    }



}
