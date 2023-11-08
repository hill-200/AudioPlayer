package com.gwallaz.audioplayer;

import static com.gwallaz.audioplayer.MainActivity.model;
import static com.gwallaz.audioplayer.MainActivity.repeat;
import static com.gwallaz.audioplayer.MainActivity.shuffle;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class MusicPlaying extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    ImageView backButton,menuButton,coverArt,musicShuffle,musicPlayPrevious,musicPlayNext,musicRepeat;
    TextView songName,songArtist,totalTime,timeRemaining;
    SeekBar seekBar;
    FloatingActionButton playPause;
    int position = -1;
    static ArrayList<Model> listsongs = new  ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    Thread playThread,previousThread,nextThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_playing);
        backButton = findViewById(R.id.back_button);
        menuButton = findViewById(R.id.menu_button);
        coverArt = findViewById(R.id.cover_art);
        musicShuffle = findViewById(R.id.music_shuffle);
        musicPlayPrevious = findViewById(R.id.music_previous);
        musicPlayNext = findViewById(R.id.music_next);
        musicRepeat = findViewById(R.id.music_repeat);
        playPause = findViewById(R.id.play_pause);
        songName = findViewById(R.id.song_name);
        songArtist = findViewById(R.id.song_artist);
        totalTime = findViewById(R.id.total_time);
        timeRemaining = findViewById(R.id.time_remaining);
        seekBar = findViewById(R.id.seekbar);
        mediaPlayer.setOnCompletionListener(this);

        getIntentMethod();
        songName.setText(listsongs.get(position).getTitle());
        songArtist.setText(listsongs.get(position).getArtist());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(mediaPlayer != null && fromUser){

                    mediaPlayer.seekTo(progress * 1000);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        MusicPlaying.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    totalTime.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });

        musicShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffle){
                    shuffle = false;
                    musicShuffle.setImageResource(R.drawable.music_shuffle);

                }else{
                    shuffle = true;
                    musicShuffle.setImageResource(R.drawable.baseline_shuffle_on_24);
                }

            }
        });

        musicRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(repeat){
                    repeat = false;
                    musicRepeat.setImageResource(R.drawable.music_repeat);
                }else{
                    repeat = true;
                    musicRepeat.setImageResource(R.drawable.baseline_repeat_on_24);
                }


            }
        });

    }

    private String formattedTime(int mCurrentPosition) {

        String totalOut ;
        String totalNew ;
        String seconds = String.valueOf(mCurrentPosition / 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes +":"+ seconds;
        totalNew = minutes +":"+"0"+ seconds;
        if(seconds.length() == 1){
            return totalNew;
        }else{
            return totalOut;
        }

    }

    public void getIntentMethod(){
        position = getIntent().getIntExtra("position",-1);
        listsongs = model;
        if(listsongs != null){
            playPause.setImageResource(R.drawable.music_pause);
            uri = Uri.parse(listsongs.get(position).getPath());
        }
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(this,uri);
            mediaPlayer.start();
        }else{
        mediaPlayer = MediaPlayer.create(this,uri);
        mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        metadata(uri);


    }

    private void metadata(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int totalduration = Integer.parseInt(listsongs.get(position).getDuration() )/1000;
        timeRemaining.setText(formattedTime(totalduration));
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if(art != null){
            Glide.with(this)
                    .asBitmap()
                    .load(art)
                    .into(coverArt);
            bitmap = BitmapFactory.decodeByteArray(art,0, art.length);

            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable @androidx.annotation.Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if(swatch != null){
                        ImageView gradient = findViewById(R.id.image_gradient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawable1 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),swatch.getRgb()});
                        songName.setTextColor(swatch.getTitleTextColor());
                        songArtist.setTextColor(swatch.getBodyTextColor());

                    }else{
                        ImageView gradient = findViewById(R.id.image_gradient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawable1 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0xff000000});
                        songName.setTextColor(Color.WHITE);
                        songArtist.setTextColor(Color.DKGRAY);

                    }

                }
            });

        }else
        {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.music2)
                    .into(coverArt);
            ImageView gradient = findViewById(R.id.image_gradient);
            RelativeLayout mContainer = findViewById(R.id.mContainer);
            gradient.setBackgroundResource(R.drawable.gradient_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            songName.setTextColor(Color.WHITE);
            songArtist.setTextColor(Color.DKGRAY);


        }
    }

    @Override
    protected void onResume() {
        playThreadBtn();
        nextThreadBtn();
        previousThreadBtn();
        super.onResume();
    }

    private void previousThreadBtn() {
        previousThread = new Thread(){
            @Override
            public void run() {
                super.run();
                musicPlayPrevious.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        previousButtonClicked();
                    }
                });
            }
        };
        previousThread.start();

    }

    private void nextThreadBtn() {
        nextThread = new Thread(){
            @Override
            public void run() {
                super.run();
                musicPlayNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playNextButtonClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void playThreadBtn() {
        playThread = new Thread(){
            @Override
            public void run() {
                super.run();
                playPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }
    private void playPauseBtnClicked(){
        if(mediaPlayer.isPlaying()){
            playPause.setImageResource(R.drawable.music_play);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            MusicPlaying.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);

                }


            });
        }else{
            playPause.setImageResource(R.drawable.music_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            MusicPlaying.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);

                }


            });
        }

    }
    private void playNextButtonClicked(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffle && !repeat){
                position = getRandom(listsongs.size() - 1);
            } else if (!shuffle && !repeat) {
                position = ((position + 1) % listsongs.size());
            }
            uri = Uri.parse(listsongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(this,uri);
            songName.setText(listsongs.get(position).getTitle());
            songArtist.setText(listsongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            MusicPlaying.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);

                }


            });
            playPause.setImageResource(R.drawable.music_pause);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }else{
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffle && !repeat){
                position = getRandom(listsongs.size() - 1);
            } else if (!shuffle && !repeat) {
                position = ((position + 1) % listsongs.size());
            }

            uri = Uri.parse(listsongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(this,uri);
            songName.setText(listsongs.get(position).getTitle());
            songArtist.setText(listsongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            MusicPlaying.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);

                }


            });
            playPause.setImageResource(R.drawable.music_play);
            mediaPlayer.setOnCompletionListener(this);

        }

    }

    private int getRandom(int i) {

        Random random = new Random();
        return  random.nextInt(i + 1);
    }

    private void previousButtonClicked(){

        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffle && !repeat){
                position = getRandom(listsongs.size() - 1);
            } else if (!shuffle && !repeat) {

                position = ((position - 1) < 1 ? (listsongs.size() - 1) : (position - 1));
            }
            uri = Uri.parse(listsongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(this,uri);
            songName.setText(listsongs.get(position).getTitle());
            songArtist.setText(listsongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            MusicPlaying.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);

                }


            });
            playPause.setImageResource(R.drawable.music_pause);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }else{
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffle && !repeat){
                position = getRandom(listsongs.size() - 1);
            } else if (!shuffle && !repeat) {
                position = ((position - 1) < 1 ? (listsongs.size() - 1) : (position - 1));
            }
            uri = Uri.parse(listsongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(this,uri);
            songName.setText(listsongs.get(position).getTitle());
            songArtist.setText(listsongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            MusicPlaying.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);

                }


            });
            playPause.setImageResource(R.drawable.music_play);
            mediaPlayer.setOnCompletionListener(this);

        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        playNextButtonClicked();
        if(mediaPlayer != null){
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }
}