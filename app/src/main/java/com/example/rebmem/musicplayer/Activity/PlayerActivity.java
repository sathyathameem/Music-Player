package com.example.rebmem.musicplayer.Activity;

import static com.example.rebmem.musicplayer.Activity.MainActivity.repeatBoolean;
import static com.example.rebmem.musicplayer.Activity.MainActivity.shuffleBoolean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rebmem.musicplayer.Model.SongFile;
import com.example.rebmem.musicplayer.R;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    //Initialize all the elements in the activity
    Button  btnNext, btnPrev, btnFastForward, btnFastRewind, btnShuffle, btnRepeat;
    TextView txtSongName, durationPlayed, durationTotal;
    SeekBar seekBar;
    FloatingActionButton btnPlayPause;
    BarVisualizer barVisualizer;
    ImageView albumCover,btnBack;
    int position = -1;
    static ArrayList<SongFile> songsList = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    Thread updateSeekbar, playThread,prevThread,nextThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
        getIntentMethod();
        handleSeekBar();
        mediaPlayer.setOnCompletionListener(this);
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffleBoolean){
                    shuffleBoolean = false;
                    btnShuffle.setBackgroundResource(R.drawable.ic_shuffle_off);
                }else{
                    shuffleBoolean = true;
                    btnShuffle.setBackgroundResource(R.drawable.ic_shuffle_on);
                }
            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatBoolean){
                    repeatBoolean = false;
                    btnRepeat.setBackgroundResource(R.drawable.ic_repeat_off);
                }else{
                    repeatBoolean = true;
                    btnRepeat.setBackgroundResource(R.drawable.ic_repeat);
                }
            }
        });

        btnFastForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });

        btnFastRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    private void setUpVisualizer() {
        int audioSessionId = mediaPlayer.getAudioSessionId();
        Log.e("Session id",audioSessionId+"******");
        if(audioSessionId != -1) barVisualizer.setAudioSessionId(audioSessionId);
    }

    public void addAnimation(View view){
        ObjectAnimator objAnimator = ObjectAnimator.ofFloat(albumCover,"rotation",0f,360f);
        objAnimator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objAnimator);
        animatorSet.start();
    }

    public String createTime(int duration){
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

    private void getIntentMethod() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        position = intent.getIntExtra("position",-1);
        //songsList = songFiles;
        songsList = (ArrayList) bundle.getParcelableArrayList("songs");
        txtSongName.setText(songsList.get(position).getTitle());
        txtSongName.setSelected(true);
        if(songsList != null){
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            uri = uri.parse(songsList.get(position).getPath());
        }

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
         }
        mediaPlayer = mediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();
        addAnimation(albumCover);
        metaData(uri);
        setUpVisualizer();

    }

    private void initViews() {
        txtSongName = findViewById(R.id.txtSongName);
        durationPlayed = findViewById(R.id.txtSongStart);
        durationTotal = findViewById(R.id.txtSongEnd);
        albumCover = findViewById(R.id.albumCover);
        btnNext = findViewById(R.id.nextbutton);
        btnPrev = findViewById(R.id.prevbutton);
        btnShuffle = findViewById(R.id.shuffle);
        btnRepeat = findViewById(R.id.repeat);
        btnPlayPause = findViewById(R.id.play_pause);
        btnFastForward = findViewById(R.id.ffbutton);
        btnFastRewind = findViewById(R.id.frbutton);
        seekBar = findViewById(R.id.seekbar);
        barVisualizer = findViewById(R.id.bar);
    }

    private void handleSeekBar(){
        updateSeekbar = new Thread(){
            @Override
            public void run(){
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while(currentPosition < totalDuration){
                    try{
                        sleep(3000);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    }
                    catch(IllegalStateException | InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        seekBar.setMax(mediaPlayer.getDuration());
        updateSeekbar.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    durationPlayed.setText(createTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        String endTime = createTime(mediaPlayer.getDuration());
        durationTotal.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                durationPlayed.setText(currentTime);
                handler.postDelayed(this, delay);

            }
        },delay);

    }

    private void metaData(@NonNull Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        byte[] cover = retriever.getEmbeddedPicture();
        if(cover != null){
            Glide.with(this).asBitmap().load(cover).into(albumCover);
        }else{
            Glide.with(this).asBitmap().load(R.drawable.music_note_round).into(albumCover);
        }

    }

    @Override
    protected void onResume() {
        playThreadButton();
        prevThreadButton();
        nextThreadButton();
        super.onResume();
    }

    private void nextThreadButton() {
        nextThread = new Thread(){
            @Override
            public void run() {
                super.run();
                btnNext.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        nextButtonClicked();
                    }
                });
            }

        };
        nextThread.start();
    }

    private void prevThreadButton() {
        prevThread = new Thread(){
            @Override
            public void run() {
                super.run();
                btnPrev.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        prevButtonClicked();
                    }
                });
            }

        };
        prevThread.start();
    }

    private void playThreadButton() {
        playThread = new Thread(){
            @Override
            public void run() {
                super.run();
                btnPlayPause.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        playPauseButtonClicked();
                    }
                });
            }

        };
        playThread.start();
    }

    private void playPauseButtonClicked() {
        if (mediaPlayer.isPlaying()) {
            btnPlayPause.setImageResource(R.drawable.ic_play);
            mediaPlayer.pause();
            handleSeekBar();
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            addAnimation(albumCover);
            handleSeekBar();
        }
    }

    private void prevButtonClicked() {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean){
                position = getRandom(songsList.size()-1);
            }else if (!shuffleBoolean && !repeatBoolean){
                position = (position - 1) < 0 ? (songsList.size()-1) : (position - 1);
            }
            uri = Uri.parse(songsList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            txtSongName.setText(songsList.get(position).getTitle());
            txtSongName.setSelected(true);
            addAnimation(albumCover);
            durationTotal.setText(createTime(mediaPlayer.getDuration()));
            mediaPlayer.setOnCompletionListener(this);
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            handleSeekBar();
            setUpVisualizer();
        }else{
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean){
                position = getRandom(songsList.size()-1);
            }else if (!shuffleBoolean && !repeatBoolean){
                position = (position - 1) < 0 ? (songsList.size()-1) : (position - 1);
            }
            uri = Uri.parse(songsList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            txtSongName.setText(songsList.get(position).getTitle());
            txtSongName.setSelected(true);
            addAnimation(albumCover);
            durationTotal.setText(createTime(mediaPlayer.getDuration()));
            mediaPlayer.setOnCompletionListener(this);
            btnPlayPause.setImageResource(R.drawable.ic_play);
            mediaPlayer.start();
            handleSeekBar();
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            setUpVisualizer();
        }
    }

    private void nextButtonClicked() {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean){
                position = getRandom(songsList.size()-1);
            }else if (!shuffleBoolean && !repeatBoolean){
                position = ((position + 1) % songsList.size());
            }
            // If repeat button is true - same song (from same position) will be played.
            uri = Uri.parse(songsList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            txtSongName.setText(songsList.get(position).getTitle());
            txtSongName.setSelected(true);
            addAnimation(albumCover);
            durationTotal.setText(createTime(mediaPlayer.getDuration()));
            mediaPlayer.setOnCompletionListener(this);
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            handleSeekBar();
            setUpVisualizer();
        }else{
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean){
                position = getRandom(songsList.size()-1);
            }else if (!shuffleBoolean && !repeatBoolean){
                position = ((position + 1) % songsList.size());
            }
            uri = Uri.parse(songsList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            txtSongName.setText(songsList.get(position).getTitle());
            txtSongName.setSelected(true);
            addAnimation(albumCover);
            durationTotal.setText(createTime(mediaPlayer.getDuration()));
            mediaPlayer.setOnCompletionListener(this);
            btnPlayPause.setImageResource(R.drawable.ic_play);
            mediaPlayer.start();
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            handleSeekBar();
            setUpVisualizer();
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i+1);

    }

    @Override
    protected void onDestroy() {
        if(barVisualizer != null){
            barVisualizer.release();
        }
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        super.onDestroy();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextButtonClicked();
    }


}