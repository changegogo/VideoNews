package feicuiedu.com.videoplayer.part;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.IOException;

import feicuiedu.com.videoplayer.R;
import feicuiedu.com.videoplayer.full.VideoViewActivity;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import timber.log.Timber;

/**
 * 使用 MediaPlayer + SurfaceView 实现视频播放。
 * <p/>
 * 最简单的播放控制：在视频上放一个播放/暂停按钮，一个进度条和一个全屏按钮。
 * <p/>
 * 使用此控件前，必须设置视频地址，即{@link #setVideoPath(String)}。
 */
public class SimpleVideoPlayer extends RelativeLayout implements SurfaceHolder.Callback {


    private static final int PROGRESS_MAX = 1000;

    private String videoPath;

    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private ImageView ivPreview;
    private ImageButton btnToggle;
    private ProgressBar progressBar;

    private boolean isPlaying; // 是否正在播放，用于更新进度条的Handler

    private boolean autoStart = true; // 准备完成后是否自动开始播放，只在首次进入页面时为true

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isPlaying) {
                // 每200毫秒更新一次播放进度
                progressBar.setProgress((int) (mediaPlayer.getCurrentPosition()
                        * PROGRESS_MAX / mediaPlayer.getDuration()));
                handler.sendEmptyMessageDelayed(0, 200);
            }
        }
    };

    public SimpleVideoPlayer(Context context) {
        this(context, null);
    }

    public SimpleVideoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void onResume() {
        initMediaPlayer();
        prepareMediaPlayer();
    }

    public void onPause() {
        pauseMediaPlayer();
        releaseMediaPlayer();
    }


    @Override public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Timber.i("surfaceCreated");
    }

    @Override public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Timber.i("surfaceChanged");
    }

    @Override public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Timber.i("surfaceDestroyed");
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public ImageView getPreviewView() {
        return ivPreview;
    }

    // 视图初始化，只在构造方法中调用一次
    private void init() {
        Vitamio.isInitialized(getContext());
        LayoutInflater.from(getContext()).inflate(R.layout.view_simple_video_player, this, true);

        initSurfaceView();
        initControllerViews();
    }

    // 初始化SurfaceView
    private void initSurfaceView() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        // 注意：Vitamio使用SurfaceView播放时必须设置这个参数！
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
    }

    // 初始化自定义的简单播放控制视图
    private void initControllerViews() {
        // 预览图片
        ivPreview = (ImageView) findViewById(R.id.ivPreview);

        // 设置 播放/暂停 按钮
        btnToggle = (ImageButton) findViewById(R.id.btnToggle);
        btnToggle.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    pauseMediaPlayer();
                } else {
                    startMediaPlayer();
                }
            }
        });

        // 设置进度条
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(PROGRESS_MAX);

        // 设置全屏播放按钮
        ImageButton btnFullScreen = (ImageButton) findViewById(R.id.btnFullScreen);
        btnFullScreen.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View view) {
                VideoViewActivity.open(getContext(), videoPath);
            }
        });

    }

    // 初始化MediaPlayer，设置一系列监听器
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer(getContext());
        mediaPlayer.setDisplay(surfaceHolder);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override public void onPrepared(MediaPlayer mp) {
                // 自动开始播放视频
                if (autoStart) {
                    startMediaPlayer();
                    autoStart = false;
                }

            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override public boolean onError(MediaPlayer mp, int what, int extra) {
                prepareMediaPlayer();
                return true;
            }
        });

        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                if (width != 0 && height != 0) {
                    // 宽填充屏幕，同时保持视频长宽比，计算目标高度
                    int videoWidth = surfaceView.getWidth();
                    int videoHeight = videoWidth * height / width;

                    // 设置Surface的固定尺寸
                    surfaceHolder.setFixedSize(width, height);

                    // 更新SurfaceView的尺寸
                    ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
                    layoutParams.width = videoWidth;
                    layoutParams.height = videoHeight;
                    surfaceView.setLayoutParams(layoutParams);
                }
            }
        });

        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override public boolean onInfo(MediaPlayer mp, int what, int extra) {

                if (what == MediaPlayer.MEDIA_INFO_FILE_OPEN_OK) { // Vitamio 5.0 必须加上这两句代码才能播放在线视频
                    long bufferSize = mediaPlayer.audioTrackInit();
                    mediaPlayer.audioInitedOk(bufferSize);
                    return true;
                }
                return false;
            }
        });

    }

    // 准备播放，同时更新UI状态
    private void prepareMediaPlayer() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(videoPath);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepareAsync();

            ivPreview.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            // TODO: 提示用户发生错误，并尝试从错误中恢复
            Timber.e(e, "prepareMediaPlayer");
        }
    }

    // 开始播放，同时更新UI状态
    private void startMediaPlayer() {
        ivPreview.setVisibility(View.INVISIBLE);
        mediaPlayer.start();
        isPlaying = true;
        handler.sendEmptyMessage(0);
        btnToggle.setImageResource(R.drawable.ic_pause);
    }

    // 暂停播放，同时更新UI状态
    private void pauseMediaPlayer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

        handler.removeMessages(0);
        isPlaying = false;
        btnToggle.setImageResource(R.drawable.ic_play_arrow);
    }

    // 释放MediaPlayer，同时更新UI状态
    private void releaseMediaPlayer() {
        progressBar.setProgress(0);
        mediaPlayer.release();
        mediaPlayer = null;
    }

}
