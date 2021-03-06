package com.guowei.diverse.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.guowei.diverse.R;
import com.guowei.diverse.util.ScreenUtil;

import cn.jzvd.JZDataSource;
import cn.jzvd.JzvdStd;

/**
 * 文件： CustomJzvdStd
 * 描述： 自定义Jzvd播放器UI
 * 作者： YangJunQuan   2018-9-13.
 */

public class CustomJzvdStd extends JzvdStd {

    private Context mContext;


    //view  新增的UI组件
    private ImageView videoShare;           //右上角【分享】功能
    private ImageView videoLike;            //右上角【点赞】功能
    private ImageView videoMoreAction;      //右上角【更多】功能
    private ImageView videoMinScreen;       //全屏状态下左上角的【退出全屏】功能

    private ProgressBar videoVolumeProgress;
    private ProgressBar videoLightProgress;

    private RelativeLayout rightContainer;   // 布局左侧，声音UI容器
    private RelativeLayout leftContainer;    // 布局右侧，亮度UI容器
    private LinearLayout progressContainer;

    private TextView tvForwardIcon;
    private TextView tvCurrentTime;
    private TextView tvTotalTime;

    public CustomJzvdStd(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    public CustomJzvdStd(Context mContext, AttributeSet attrs) {
        super(mContext, attrs);
        this.mContext = mContext;
    }

    @Override
    public int getLayoutId() {
        return R.layout.custom_jz_layout;
    }


    //布局里新增加的UI初始化
    @Override
    public void init(Context context) {
        super.init(context);
        videoLike = findViewById(R.id.video_like);
        videoShare = findViewById(R.id.video_share);
        videoMoreAction = findViewById(R.id.video_more_action);
        videoMinScreen = findViewById(R.id.video_min_screen);
        rightContainer = findViewById(R.id.layout_right);
        leftContainer = findViewById(R.id.layout_left);

        progressContainer = findViewById(R.id.layout_progress);
        videoVolumeProgress = findViewById(R.id.video_volume_bar);
        videoLightProgress = findViewById(R.id.video_light_bar);


        tvForwardIcon = findViewById(R.id.tvForward);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);


        videoLike.setOnClickListener(this);
        videoShare.setOnClickListener(this);
        videoMoreAction.setOnClickListener(this);
        videoMinScreen.setOnClickListener(this);

    }


    @Override
    public void setUp(JZDataSource jzDataSource, int screen) {
        super.setUp(jzDataSource, screen);


        //修改了FULLSCREEN_ORIENTATION 参数为ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        //使播放器点击全屏后强制全屏并且是横屏的，
        //默认情况点击全屏后是竖屏的，并且根据重力感应调整屏幕方向。
        FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;


        //重置4种状态下UI组件的视觉变化
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {

            //隐藏
            backButton.setVisibility(View.GONE);
            batteryTimeLayout.setVisibility(View.GONE);

            //显示
            leftContainer.setVisibility(VISIBLE);
            rightContainer.setVisibility(VISIBLE);
            progressContainer.setVisibility(VISIBLE);

            fullscreenButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.ic_action_min_screen));
            videoMinScreen.setVisibility(View.VISIBLE);

        } else if (currentScreen == SCREEN_WINDOW_NORMAL || currentScreen == SCREEN_WINDOW_LIST) {

            //隐藏
            rightContainer.setVisibility(GONE);
            leftContainer.setVisibility(GONE);
            progressContainer.setVisibility(GONE);
            titleTextView.setVisibility(View.GONE);
            videoMinScreen.setVisibility(View.GONE);
            batteryTimeLayout.setVisibility(View.GONE);

            //显示
            backButton.setVisibility(View.VISIBLE);
            fullscreenButton.setImageResource(R.mipmap.ic_action_full_screen);

        }

    }


    //所有点击事件的处理
    @Override
    public void onClick(View v) {
        super.onClick(v);

        //非全屏状态点击 回退按钮R.id.back 退出当前播放Activity
        if (currentScreen == SCREEN_WINDOW_NORMAL && v.getId() == R.id.back) {
            ((Activity) mContext).finish();
        }


        //新增UI组件的点击事件
        switch (v.getId()) {
            case R.id.video_like:
                Toast.makeText(getContext(), "like", Toast.LENGTH_SHORT).show();
                break;
            case R.id.video_share:
                Toast.makeText(getContext(), "share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.video_more_action:
                Toast.makeText(getContext(), "more_action", Toast.LENGTH_SHORT).show();
                break;
            case R.id.video_min_screen:
                if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
                    return;
                }
                if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                    backPress();
                }  //quit fullscreen
                break;
                default:

        }
    }


    //修改播放、暂停、重播 R.id.start 图标
    @Override
    public void updateStartImage() {
        if (currentState == CURRENT_STATE_PLAYING) {
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(R.mipmap.ic_player_pause);
            replayTextView.setVisibility(INVISIBLE);
        } else if (currentState == CURRENT_STATE_ERROR) {
            startButton.setVisibility(INVISIBLE);
            replayTextView.setVisibility(INVISIBLE);
        } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(R.mipmap.ic_player_reload);
            replayTextView.setVisibility(GONE);

        } else {
            startButton.setImageResource(R.mipmap.ic_player_play);
            replayTextView.setVisibility(INVISIBLE);
        }
    }


    //修改播放按钮大小
    @Override
    public void changeStartButtonSize(int size) {
        size = ScreenUtil.INSTANCE.dip2px(70);
        ViewGroup.LayoutParams lp = startButton.getLayoutParams();
        lp.height = size;
        lp.width = size;
    }


    //控制新增UI部分的显隐
    @Override
    public void setAllControlsVisiblity(int topCon, int bottomCon, int startBtn, int loadingPro,
                                        int thumbImg, int bottomPro, int retryLayout) {
        super.setAllControlsVisiblity(topCon, bottomCon, startBtn, loadingPro, thumbImg, bottomPro, retryLayout);

        progressContainer.setVisibility(GONE);
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            rightContainer.setVisibility(topCon);
            leftContainer.setVisibility(topCon);
        }


    }


    //一定时间内不点击屏幕自动隐藏UI任务
    @Override
    public void dissmissControlView() {
        if (currentState != CURRENT_STATE_NORMAL
                && currentState != CURRENT_STATE_ERROR
                && currentState != CURRENT_STATE_AUTO_COMPLETE) {
            post(new Runnable() {
                @Override
                public void run() {
                    progressContainer.setVisibility(View.INVISIBLE);
                    rightContainer.setVisibility(View.INVISIBLE);
                    leftContainer.setVisibility(View.INVISIBLE);
                    bottomContainer.setVisibility(View.INVISIBLE);
                    topContainer.setVisibility(View.INVISIBLE);
                    startButton.setVisibility(View.INVISIBLE);
                    if (clarityPopWindow != null) {
                        clarityPopWindow.dismiss();
                    }
                    if (currentScreen != SCREEN_WINDOW_TINY) {
                        bottomProgressBar.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }


    //调整亮度时UI效果
    @Override
    public void showBrightnessDialog(int brightnessPercent) {

        leftContainer.setVisibility(VISIBLE);
        if (brightnessPercent > 100) {
            brightnessPercent = 100;
        } else if (brightnessPercent < 0) {
            brightnessPercent = 0;
        }
        videoLightProgress.setProgress(brightnessPercent);
        onCLickUiToggleToClear();
    }


    //调整声音时UI效果
    @Override
    public void showVolumeDialog(float deltaY, int volumePercent) {
        rightContainer.setVisibility(VISIBLE);
        if (volumePercent > 100) {
            volumePercent = 100;
        } else if (volumePercent < 0) {
            volumePercent = 0;
        }
        videoVolumeProgress.setProgress(volumePercent);
        onCLickUiToggleToClear();
    }


    @Override
    public void showProgressDialog(float deltaX, String seekTime, long seekTimePosition, String totalTime, long totalTimeDuration) {
        progressContainer.setVisibility(VISIBLE);
        tvCurrentTime.setText(seekTime);
        tvTotalTime.setText(" / " + totalTime);
        if (deltaX > 0) {
            tvForwardIcon.setText(R.string.fast_forward);
        } else {
            tvForwardIcon.setText(R.string.rewind);
        }
        onCLickUiToggleToClear();
    }


    @Override
    public void changeUiToComplete() {
        super.changeUiToComplete();

    }
}
