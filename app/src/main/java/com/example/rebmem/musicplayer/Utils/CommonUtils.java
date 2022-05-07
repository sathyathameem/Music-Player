package com.example.rebmem.musicplayer.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.widget.ImageView;
import androidx.annotation.NonNull;


import java.util.Random;

public class CommonUtils {

    public static void addAnimation(ImageView image){
        ObjectAnimator objAnimator = ObjectAnimator.ofFloat(image,"rotation",0f,360f);
        objAnimator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objAnimator);
        animatorSet.start();
    }

    public static int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i+1);
     }

    public static byte[] getMetaData(@NonNull Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        return retriever.getEmbeddedPicture();
     }

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
