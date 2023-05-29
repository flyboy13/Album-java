// https://stackoverflow.com/questions/68269286/not-able-to-load-com-github-smarteist-autoimageslider1-4-0
// https://youtube.com/watch?v=J1zCHTXjegI&feature=shares
package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import me.relex.circleindicator.CircleIndicator;

public class SlideShow extends Activity {
    ViewPager viewPager;
    ss_ImageAdapter imageAdapter;
    CircleIndicator circleIndicator;
    Intent intentCallMusicService;
    MediaPlayer player;

    private  List<Photos> dataForSlideShow;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ss_slide_show);

        viewPager = findViewById(R.id.ss_viewpager);
        circleIndicator = findViewById(R.id.circle_indicator);

        dataForSlideShow = getImageList();
        imageAdapter = new ss_ImageAdapter(this, dataForSlideShow);
        viewPager.setAdapter(imageAdapter);

        circleIndicator.setViewPager(viewPager);
        imageAdapter.registerDataSetObserver(
                circleIndicator.getDataSetObserver()
        );

        int i = 0;
        i = ThreadLocalRandom.current().nextInt(10);
        player = MediaPlayer.create(
                getApplicationContext(),
                (
                        i % 2 == 0
                        ? R.raw.wav_88_bpm_ab_maj
                                : R.raw.kamen_rider_ooo
                )
        );
        player.start();

        autoSlideImages();
    }

    private void autoSlideImages() {
        boolean isInvalidData = dataForSlideShow == null
                || dataForSlideShow.isEmpty()
                || viewPager == null;

        if (!isInvalidData)
        {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (!player.isPlaying())
                            {
                                finish();
                            }
                            int currentItem = viewPager.getCurrentItem();
                            int totalItem = dataForSlideShow.size();

                            currentItem = (currentItem + 1) % totalItem;
                            viewPager.setCurrentItem(currentItem);
                        }
                    });
                }
            }, 500, 3000);
        }
    }

    private List<Photos> getImageList() {
        return SlideShowData.list;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (timer != null)
        {
            timer.cancel();
            timer = null;

            player.stop();
            player.release();
            player = null;
        }
    }

}