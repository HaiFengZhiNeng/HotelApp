package com.fanfan.hotel.common.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fanfan.hotel.R;
import com.fanfan.hotel.common.base.BaseActivity;
import com.fanfan.hotel.service.api.OnlineData;
import com.fanfan.hotel.service.cache.Config;
import com.fanfan.hotel.service.cache.DataCache;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyuanyuan on 2017/12/15.
 */

public abstract class BarBaseActivity extends IMBaseActivity {

    protected Toolbar toolbar;

    @Override
    protected void initView() {
        backLayout();
        toolbar();
        rollPager();
    }


    private void backLayout() {
        final LinearLayout backLayout = findViewById(R.id.back_layout);
        if (backLayout != null) {
//            Glide.with(this)
//                    .load(R.drawable.frament_background)
//                    .asBitmap()
//                    .into(new SimpleTarget<Bitmap>(Constants.displayWidth / 2, Constants.displayHeight / 2) {
//                        @Override
//                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                            Drawable drawable = new BitmapDrawable(resource);
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                                linearLayout.setBackground(drawable);
//                            }
//                        }
//                    });
        }
    }

    private void toolbar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void rollPager() {
        RollPagerView rollView = findViewById(R.id.roll_view);
        if (rollView != null) {
            rollView.setPlayDelay(3000);
            rollView.setAnimationDurtion(1000);
            rollView.setAdapter(new MainNormalAdapter());
            rollView.setHintView(new ColorPointHintView(this, Color.YELLOW, Color.WHITE));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setResult() {
    }

    protected class MainNormalAdapter extends StaticPagerAdapter {

        private int[] imgs = {
                R.mipmap.roll1,
                R.mipmap.roll2,
                R.mipmap.roll3,
                R.mipmap.roll4,
                R.mipmap.roll5,
                R.mipmap.roll6,
                R.mipmap.roll7,
                R.mipmap.roll8,
        };

        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setImageResource(imgs[position]);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }

        @Override
        public int getCount() {
            return imgs.length;
        }
    }

    public String resFoFinal(int id) {
        String[] arrResult = getResources().getStringArray(id);
        return arrResult[new Random().nextInt(arrResult.length)];
    }

    public int resFoInter(String[] res) {
        return new Random().nextInt(res.length);
    }
}
