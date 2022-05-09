package com.example.rebmem.musicplayer.Activity;

import static com.example.rebmem.musicplayer.Activity.MainActivity.repeatBoolean;
import static com.example.rebmem.musicplayer.Activity.MainActivity.shuffleBoolean;
import static java.lang.Thread.sleep;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.rebmem.musicplayer.Model.SongFile;
import com.example.rebmem.musicplayer.R;
import com.example.rebmem.musicplayer.Utils.CommonUtils;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * <p>This player activity class where the user can interact with the music player controls.
 * This is the activity where all the UI controls for Music player is placed.
 * This activity is presented to the user as full screen window
 * This activity is called from either the Song list fragment and favourite playlist fragment
 * {@inheritDoc}
 * </p>
 *
 * @author Sathya Thameem
 **/

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    static ArrayList<SongFile> songsList = new ArrayList<>();
    //Uniform resource identifier for each song
    static Uri uri;
    //Media player
    static MediaPlayer mediaPlayer;
    //Initialize all the elements in the activity
    Button btnNext, btnPrev, btnFastForward, btnFastRewind, btnShuffle, btnRepeat;
    TextView txtSongName, durationPlayed, durationTotal;
    SeekBar seekBar;
    FloatingActionButton btnPlayPause;
    BarVisualizer barVisualizer;
    ImageView albumCover;
    int position = -1;
    Thread updateSeekbar, playThread, prevThread, nextThread;
    Handler mHandler;
    Runnable mRunnable;

    /**
     * This is a overridden method where the activity is initialized
     * {@inheritDoc}
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This is called with a layout resource defining the UI
        setContentView(R.layout.activity_player);
        //This method has the resource locator for all the elements that will be interacted programmatically
        initViews();
        //This method gets the values / messages from the activity this activity is called
        getIntentMethod();
        //This method handles the seekbar which shows the progress the music is playing
        handleSeekBar();

        //Interface definition for a callback when Fastforward button is clicked.
        btnFastForward.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when the fast forward button is clicked
             * When the media player is playing, the click will set the current position
             * of the mediaplayer progress to 10 seconds.
             *  {@inheritDoc}
             * **/
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                }
            }
        });

        //Interface definition for a callback when Fastrewind button is clicked
        btnFastRewind.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when the fast rewind button is clicked
             * When the media player is playing, the click will set the current position
             * of the media player current position - 10 seconds.
             * {@inheritDoc}
             * **/
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
                }
            }
        });

        //Interface definition for a callback when the media player completes playing the current playback
        mediaPlayer.setOnCompletionListener(this);

        /**
         * Called when the shuffle button is clicked
         * This toggles the shuffle button based on its previous state
         * If the button is off mode, the click toggles to on (Shows On Button)
         * If the button is on mode, the click toggles to off (Shows Off Button)
         *
         * {@inheritDoc}
         * **/
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffleBoolean) {
                    shuffleBoolean = false;
                    btnShuffle.setBackgroundResource(R.drawable.ic_shuffle_off);
                } else {
                    shuffleBoolean = true;
                    btnShuffle.setBackgroundResource(R.drawable.ic_shuffle_on);
                }
            }
        });

        /**
         * Called when the repeat button is clicked
         * This toggles the repeat button based on its previous state
         * If the button is off mode, the click toggles to on (Shows On Button)
         * If the button is on mode, the click toggles to off (Shows Off Button)
         *
         * {@inheritDoc}
         * **/
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeatBoolean) {
                    repeatBoolean = false;
                    btnRepeat.setBackgroundResource(R.drawable.ic_repeat_off);
                } else {
                    repeatBoolean = true;
                    btnRepeat.setBackgroundResource(R.drawable.ic_repeat);
                }
            }
        });

        //The appTheme is set up in the manifest and this will sets the home up and displays
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //This is to include the home in the Action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mHandler = new Handler(); // A message queue to pass to the thread to check the user interaction

        //This will instruct what the thread running is supposed to do
        mRunnable = new Runnable() {
            /** Runnable thread - Running in the background, when the app is idle for the given time
             * Checks if the media player is running, the app goes screen off , but the music should
             * play. If the player is not running, the app screen goes off.
             * {@inheritDoc}
             * **/
            @Override
            public void run() {
                /** Checking if media player is running **/
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {

                    /** Tried the following to make the screen off - The performance is not good.. Hence commented
                     *  and needs to be improved
                     *  **/
                    //ContentResolver cr= getContentResolver();
                    //android.provider.Settings.System.putInt(cr,android.provider.Settings.System.SCREEN_OFF_TIMEOUT ,1000);
                    //android.provider.Settings.System.putInt(cr, Settings.System.,1000);

                    /** The following can be used but  "error: cannot find symbol method goToSleep(int)" happened.
                     * This was available in API 21, To use now, I have to sign the app using the platform's certificate. **/
                    //PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                    //pm.goToSleep(100);

                    /** To code the logic to identify the idle time for 15 seconds, tentatively, added an alert when there is
                     * no user interaction.
                     * **/
                    showIdleAlert();

                } else {
                    /** When the player is not playing the control goes back to the songs list **/
                    // TODO Auto-generated method stub
                    Toast.makeText(PlayerActivity.this, "The music player is idle from last 15 seconds, Hence navigating back to Songs list ",
                            Toast.LENGTH_LONG).show();
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    onBackPressed();
                }

            }
        };
        //sets the timer to check if the app is idle
        startHandler();
    }

    /**
     * This method is called whenever a key, touch, or trackball event is dispatched to the activity
     * This is used here to check if the app is idle and user has not interacted when the activity is running
     * This is to check if the device becomes idle.
     * {@inheritDoc}
     **/
    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        stopHandler();//stop first and then start
        startHandler();
    }

    //This method is called when the runnable thread to detect idleness need to be removed
    public void stopHandler() {
        mHandler.removeCallbacks(mRunnable);
    }

    //This method is called when the runnable thread need to be refreshed/restarted
    public void startHandler() {
        mHandler.postDelayed(mRunnable, 30000);
    }

    //This method is to get the audio session id from the media player and pass it to the visualizer
    private void setUpVisualizer() {
        int audioSessionId = mediaPlayer.getAudioSessionId();
        if (audioSessionId != -1) barVisualizer.setAudioSessionId(audioSessionId);
    }

    //Reference of all TextViews, Images, Buttons, SeekBar , Visualizer from XML layout to class
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

    /**
     * This method calls internally the getIntent() method that started this Player Activity.
     * The caller is the Main Activity which passes the following data and those data can be
     * retrieved using getIntent() in this current activity
     * - position of the song @int
     * - Songs list @ArrayList
     * From the position data,
     * - the song name at that position is extracted from the song object and set in the song name display text
     * - get the path to pass it as Uri to the media player
     * - the selected song from the main activity will be played and the play button image is replaced by pause image
     * - if the media player is playing,it is stopped and release to play the song selected by calling start method
     * - gets the metadata(e.g Album cover image) to set it up in the image placeholder
     * - sets up the Visualizer
     **/
    private void getIntentMethod() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        position = intent.getIntExtra("position", -1);
        songsList = (ArrayList) bundle.getParcelableArrayList("songs");

        //The following for loop is used for debugging
        for (SongFile song : songsList) {
            Log.e("Song Name &&&&&&&&&&&&&&&&&&&&&", song.getTitle());
        }

        if (songsList != null) {
            txtSongName.setText(songsList.get(position).getTitle());
            txtSongName.setSelected(true);
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(songsList.get(position).getPath());
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        CommonUtils.addAnimation(albumCover);
        metaData(uri);
        setUpVisualizer();

    }

    /**
     * This method is handle the progress in the seek bar. This is handled using a background thread running.
     * The thread is made to sleep for every .5 seconds before the current position of the media player is obtained
     * and sets the progress in the seekbar.
     * The maximum of the seekbar is set to the duration of the mediaplayer( the song that is set to media player)
     * When the thread starts and the seek bar is progressed, the completed portion will be shown in a different colour
     * for the user to visually see how much it is progressed
     * The manual actions happening on the seek bar are handled using setOnSeekBarChangeListener interface.
     * {@inheritDoc}
     **/

    private void handleSeekBar() {
        updateSeekbar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    } catch (IllegalStateException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        seekBar.setMax(mediaPlayer.getDuration());
        updateSeekbar.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        /**A callback that notifies user when the progress level has been changed.
         * This includes changes that were initiated by the user through a touch gesture or arrow key/trackball
         * as well as changes that were initiated programmatically.
         * {@inheritDoc}
         */
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //Called when the progress level has changed
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    durationPlayed.setText(CommonUtils.createTimeFormat(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            //Called when the user has finished a touch gesture. Also used to re enable advancing the seekbar
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        //End time of the seek bar is set
        String endTime = CommonUtils.createTimeFormat(mediaPlayer.getDuration());
        durationTotal.setText(endTime);

        /**
         *  Background Runnable thread using a handler.
         *  Running this thread every 500 milliseconds
         *  and updating the duration played with the current postion of the media player
         * **/
        final Handler handler = new Handler();
        final int delay = 500;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = CommonUtils.createTimeFormat(mediaPlayer.getCurrentPosition());
                durationPlayed.setText(currentTime);
                handler.postDelayed(this, delay);

            }
        }, delay);

    }

    /**
     * This is to show the album cover image of that song if there is one available
     * else default image set will be displayed.
     * Glide is a fast and efficient open source media management and
     * image loading framework for Android that wraps media decoding,
     * memory and disk caching, and resource pooling into a simple and easy to use interface.
     *
     * @param uri
     */
    private void metaData(@NonNull Uri uri) {
        byte[] cover = CommonUtils.getMetaData(uri);
        if (cover != null) {
            Glide.with(this).asBitmap().load(cover).into(albumCover);
        } else {
            Glide.with(this).asBitmap().load(R.drawable.music_note_round).into(albumCover);
        }
    }

    /**
     * This overridden method of the Activity when the current Activity
     * will start interacting with the user.
     * This method calls three methods that created three new threads
     * in the background waiting to listen to the user interaction
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        playThreadButton();
        prevThreadButton();
        nextThreadButton();
        super.onResume();
    }

    /**
     * The nextThread starts in the background when the media player is playing
     * and waiting to listen for the next button click event
     * {@inheritDoc}
     */
    private void nextThreadButton() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                btnNext.setOnClickListener(v -> nextButtonClicked());
            }

        };
        nextThread.start();
    }

    /**
     * The prevThread starts in the background when the media player is playing
     * and waiting to listen for the prev button click event
     * {@inheritDoc}
     */
    private void prevThreadButton() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                btnPrev.setOnClickListener(v -> prevButtonClicked());
            }

        };
        prevThread.start();
    }

    /**
     * The playThread starts in the background when the media player is playing
     * and waiting to listen for the play button click event
     * {@inheritDoc}
     */
    private void playThreadButton() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                btnPlayPause.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        playPauseButtonClicked();
                    }
                });
            }

        };
        playThread.start();
    }

    /**
     * This method is called from the PlayThread when the play/pause button is
     * clicked. When the media player is playing the player is paused and the button image
     * is set to play else media player is started and the image is set to pause
     * and handle seek bar is called to start a new seekbar thread for both cases.
     */
    private void playPauseButtonClicked() {
        if (mediaPlayer.isPlaying()) {
            btnPlayPause.setImageResource(R.drawable.ic_play);
            mediaPlayer.pause();
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            CommonUtils.addAnimation(albumCover);
        }
        handleSeekBar();
    }

    /**
     * This method is called from the prevThread when the prev button is
     * clicked. When the media player is playing, the player is stopped
     * and the media player will be released.
     * If the shuffle button is on , get random song from the song list
     * If the shuffle button is off , previous one in the list will be played,
     * last song in the list will be played if the current song is the first in the list.
     * If the repeat button is enabled, the position is not changed and the current song is played
     * Song name, image is set. Seekbar thread is started and vizualizer is set for the current song
     * and handle seek bar is called to start a new seekbar thread for both cases.
     */
    private void prevButtonClicked() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean) {
                position = CommonUtils.getRandom(songsList.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = (position - 1) < 0 ? (songsList.size() - 1) : (position - 1);
            }
            uri = Uri.parse(songsList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            txtSongName.setText(songsList.get(position).getTitle());
            txtSongName.setSelected(true);
            CommonUtils.addAnimation(albumCover);
            durationTotal.setText(CommonUtils.createTimeFormat(mediaPlayer.getDuration()));
            mediaPlayer.setOnCompletionListener(this);
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            handleSeekBar();
            setUpVisualizer();
        } else {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean) {
                position = CommonUtils.getRandom(songsList.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = (position - 1) < 0 ? (songsList.size() - 1) : (position - 1);
            }
            uri = Uri.parse(songsList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            txtSongName.setText(songsList.get(position).getTitle());
            txtSongName.setSelected(true);
            CommonUtils.addAnimation(albumCover);
            durationTotal.setText(CommonUtils.createTimeFormat(mediaPlayer.getDuration()));
            mediaPlayer.setOnCompletionListener(this);
            btnPlayPause.setImageResource(R.drawable.ic_play);
            mediaPlayer.start();
            handleSeekBar();
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            setUpVisualizer();
        }
    }

    /**
     * This method is called from the nextThread when the next button is
     * clicked. When the media player is playing, the player is stopped
     * and the media player will be released.
     * If the shuffle button is on , get random song from the song list
     * If the shuffle button is off , next one in the list will be played,
     * first song will be played if the current song is the last song in the list.
     * If the repeat button is enabled, the position is not changed and the current song is played
     * Song name, image is set. Seekbar thread is started and vizualizer is set for the current song
     * and handle seek bar is called to start a new seekbar thread for both cases.
     */
    private void nextButtonClicked() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean) {
                position = CommonUtils.getRandom(songsList.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % songsList.size());
            }
            // If repeat button is true - same song (from same position) will be played.
            uri = Uri.parse(songsList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            txtSongName.setText(songsList.get(position).getTitle());
            txtSongName.setSelected(true);
            CommonUtils.addAnimation(albumCover);
            durationTotal.setText(CommonUtils.createTimeFormat(mediaPlayer.getDuration()));
            mediaPlayer.setOnCompletionListener(this);
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            handleSeekBar();
            setUpVisualizer();
        } else {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean) {
                position = CommonUtils.getRandom(songsList.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % songsList.size());
            }
            uri = Uri.parse(songsList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            txtSongName.setText(songsList.get(position).getTitle());
            txtSongName.setSelected(true);
            CommonUtils.addAnimation(albumCover);
            durationTotal.setText(CommonUtils.createTimeFormat(mediaPlayer.getDuration()));
            mediaPlayer.setOnCompletionListener(this);
            btnPlayPause.setImageResource(R.drawable.ic_play);
            mediaPlayer.start();
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            handleSeekBar();
            setUpVisualizer();
        }
    }

    /**
     * This is the method called before the current activity
     * is destroyed. In this method, all the resources are released
     * and stopped. The shuffle button and repeat button is reset to false(off)
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        if (barVisualizer != null) {
            barVisualizer.release();
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        shuffleBoolean = false;
        repeatBoolean = false;
        super.onDestroy();
    }

    /**
     * Called when the end of a media source is reached during playback.
     *
     * @param mp {@inheritDoc}
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        nextButtonClicked();
    }

    /**
     * This method shows the Alert when there is no user interaction to inform the user
     * that the player is inactive for 15 seconds and to ask if the playback needs to be
     * continued
     * Yes - closes the alert and start the idle detecting thread and continue the playback
     * No - closes the alert and navigates back to the songs list (Main activity)
     */
    public void showIdleAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this, R.style.CustomDialogTheme);
        builder.setTitle("Idle Alert")
                .setMessage("The player is inactive for 30 seconds, Do you want to continue playing the song?")
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnPlayPause.setImageResource(R.drawable.ic_play);
                        mediaPlayer.stop();
                        //handleSeekBar();
                        dialog.cancel();
                        dialog.dismiss();
                        onBackPressed();
                        //stopHandler();//stop first and then start
                        //startHandler();

                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                        stopHandler();//stop first and then start
                        startHandler();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}