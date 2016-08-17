package feicuiedu.com.videoplayer.full;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import feicuiedu.com.videoplayer.R;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;
import timber.log.Timber;

/**
 * 使用VideoView全屏播放视频的Activity
 */
public class VideoViewActivity extends AppCompatActivity {

    private static final String KEY_VIDEO_PATH = "KEY_VIDEO_PATH";

    /**
     * 开启此Activity，videoPath可以是文件地址，也可以是url。
     */
    public static void open(Context context, String videoPath) {
        Intent intent = new Intent(context, VideoViewActivity.class);
        intent.putExtra(KEY_VIDEO_PATH, videoPath);
        context.startActivity(intent);
    }

    private VideoView videoView;
    private CustomMediaController customMediaController;
    private MediaPlayer mediaPlayer;
    private int videoLayout = VideoView.VIDEO_LAYOUT_SCALE; // 默认VideoView布局方式，放大并保持长宽比

    private ImageView ivLoading; // 缓冲时显示的加载图片
    private ObjectAnimator loadingAnimator; // 缓冲时显示的属性动画，作用于ivLoading

    private TextView tvBufferInfo; // 缓冲信息，用来显示下面的downloadSpeed和bufferPercent
    private int downloadSpeed; // 缓冲时的下载速度
    private int bufferPercent; // 缓冲百分比


    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 设置窗口背景色
        getWindow().setBackgroundDrawableResource(android.R.color.black);

        setContentView(R.layout.activity_video_view);
    }

    @Override public void onContentChanged() {
        super.onContentChanged();

        initBufferViews();
        initMediaController();
        initVideoView();
    }

    @SuppressWarnings("deprecation")
    @Override protected void onResume() {
        super.onResume();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

        if (pm.isScreenOn()) { // 避免手动灭屏时，视频自动播放
            videoView.setVideoPath(getIntent().getStringExtra(KEY_VIDEO_PATH));
            showBufferViews();
        }
    }

    @Override protected void onPause() {
        super.onPause();
        videoView.stopPlayback();

    }

    // 缓冲相关控件的初始化
    private void initBufferViews(){
        tvBufferInfo = (TextView) findViewById(R.id.tvBufferInfo);
        ivLoading = (ImageView) findViewById(R.id.ivLoading);
        ivLoading.setVisibility(View.INVISIBLE);
        tvBufferInfo.setVisibility(View.INVISIBLE);
        loadingAnimator = ObjectAnimator.ofFloat(ivLoading, "rotation", 0f, 360f);
        loadingAnimator.setDuration(1000);
        loadingAnimator.setRepeatCount(ObjectAnimator.INFINITE);
    }

    // 视频控制器的初始化
    private void initMediaController() {
        customMediaController = new CustomMediaController(this);

        customMediaController.setOnAdjustScreenListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                // 切换VideoView的布局方式(填充屏幕/保持原比例)
                videoLayout = (videoLayout == VideoView.VIDEO_LAYOUT_SCALE) ?
                        VideoView.VIDEO_LAYOUT_STRETCH : VideoView.VIDEO_LAYOUT_SCALE;
                videoView.setVideoLayout(videoLayout, 0);
            }
        });

        customMediaController.setOnScreenShotListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                try {
                    // 截取视频当前帧
                    Bitmap bitmap = mediaPlayer.getCurrentFrame();

                    // 创建存储截屏的文件夹
                    String dirPath = Environment.getExternalStorageDirectory() + File.separator + "VideoNews";
                    File dir = new File(dirPath);
                    //noinspection ResultOfMethodCallIgnored
                    dir.mkdir();

                    String path = dirPath + File.separator + System.currentTimeMillis() + ".jpg";

                    // 存储截屏
                    boolean success = saveBitmap(bitmap, new File(path));

                    String msg;
                    if (success) {
                        Timber.d("ScreenShot save to %s", path);
                        msg = "ScreenShot Success!";
                    } else {
                        msg = "ScreenShot fail!";
                    }
                    Toast.makeText(VideoViewActivity.this, msg, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    // 在某些机型上，MediaPlayer.getCurrentFrame会报Buffer not large enough for pixels异常
                    // 这可能是Vitamio自身的一个bug
                    Toast.makeText(VideoViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // VideoView的初始化
    private void initVideoView(){
        videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setMediaController(customMediaController);
        videoView.setKeepScreenOn(true);
        videoView.requestFocus();

        videoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override public void onBufferingUpdate(MediaPlayer mp, int percent) {
                // 更新当前缓冲的百分比
                bufferPercent = percent;
                updateBufferInfo();
            }
        });

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START: // 开始缓冲
                        if (videoView.isPlaying()) {
                            videoView.pause();
                        }
                        showBufferViews();
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END: // 结束缓冲
                        videoView.start();
                        hideBufferViews();
                        break;
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED: // 缓冲时下载速率
                        downloadSpeed = extra;
                        updateBufferInfo();
                        break;
                }
                return true;
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override public void onPrepared(MediaPlayer mp) {
                mediaPlayer = mp;
                // 设置缓冲区大小(缓冲区填充完后才会开始播放)，默认值就是1024 * 1024(1M)
                mediaPlayer.setBufferSize(1024 * 1024);
            }
        });
    }

    // 显示缓冲视图
    private void showBufferViews() {
        ivLoading.setVisibility(View.VISIBLE);
        loadingAnimator.start();
        tvBufferInfo.setVisibility(View.VISIBLE);
        downloadSpeed = 0;
        bufferPercent = 0;
    }

    // 隐藏缓冲视图
    private void hideBufferViews() {
        ivLoading.setVisibility(View.INVISIBLE);
        loadingAnimator.cancel();
        tvBufferInfo.setVisibility(View.INVISIBLE);
    }

    // 更新缓冲信息
    private void updateBufferInfo() {
        String info = String.format(Locale.CHINA, "%d%%, %dkb/s", bufferPercent, downloadSpeed);
        tvBufferInfo.setText(info);
    }


    // 将Bitmap存储到文件中
    private static boolean saveBitmap(Bitmap bitmap, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
