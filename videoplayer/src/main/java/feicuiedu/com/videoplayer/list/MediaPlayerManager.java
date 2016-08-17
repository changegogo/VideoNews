package feicuiedu.com.videoplayer.list;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Surface;

import java.io.IOException;
import java.util.ArrayList;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import timber.log.Timber;

/**
 * 此类用于管理视频的列表播放，列表中的所有项共用一个{@link MediaPlayer}，此类提供三对public方法给UI层调用：
 * <ol>
 * <li/>{@link #startPlayer}和{@link #stopPlayer}: 开始播放和停止播放
 * <li/>{@link #onResume}和{@link #onPause}: 初始化MediaPlayer和释放MediaPlayer
 * <li/>{@link #addPlaybackListener}和{@link #removeAllListeners}: 添加监听器和移除监听器
 * </ol>
 */
public class MediaPlayerManager {

    private static MediaPlayerManager sInstance;

    // 线程安全的单例模式
    public synchronized static MediaPlayerManager getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new MediaPlayerManager(context.getApplicationContext());
        }
        return sInstance;
    }

    private MediaPlayer mediaPlayer;

    // 视频Id，用来给UI层区分当前播放的是哪个视频
    private String videoId;

    private final ArrayList<OnPlaybackListener> listeners;

    // 使用Application Context，避免内存泄漏
    private final Context context;

    private long startTime;

    // 在某些机型上，没有调用过mediaPlayer.setDataSource(path)，直接调用mediaPlayer.release()，会导致崩溃。
    // 因此用一个布尔值判断是否需要释放
    private boolean needRelease = false;

    private MediaPlayerManager(Context context) {
        Vitamio.isInitialized(context);
        listeners = new ArrayList<>();
        this.context = context;
    }

    /**
     * 准备开始播放视频
     *
     * @param surface 播放此视频的Surface
     * @param path    视频路径，可以是文件地址，也可以是http协议的链接地址
     * @param videoId 视频唯一Id
     */
    public void startPlayer(@NonNull Surface surface, @NonNull String path, @NonNull String videoId) {

        // 简单控制一下，避免频繁开启和关闭造成的错误
        if (System.currentTimeMillis() - startTime < 300) return;
        startTime = System.currentTimeMillis();

        // 停止之前的播放
        if (this.videoId != null) {
            stopPlayer();
        }

        // 通知UI，视频准备播放
        this.videoId = videoId;
        for (OnPlaybackListener playbackListener : listeners) {
            playbackListener.onStartPlay(videoId);
        }

        try {
            needRelease = true;

            mediaPlayer.setDataSource(path);
            mediaPlayer.setSurface(surface);
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            Timber.e(e, "startPlayer");
            // TODO: 在出错时，重置MediaPlayer，通知UI界面做出合适的响应
        }
    }

    /**
     * 停止播放视频
     */
    public void stopPlayer() {

        if (videoId == null) return;

        // 通知UI，视频停止播放
        for (OnPlaybackListener playbackListener : listeners) {
            playbackListener.onStopPlay(videoId);
        }

        this.videoId = null;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
    }

    /**
     * UI界面获得焦点，初始化MediaPlayer，为播放做准备
     */
    public void onResume() {
        mediaPlayer = new MediaPlayer(context);
        setMediaPlayerListener(mediaPlayer);
    }

    /**
     * UI界面失去焦点，停止播放，释放MediaPlayer
     */
    public void onPause() {
        stopPlayer();
        if (needRelease) {
            mediaPlayer.release();
            needRelease = false;
        }
        mediaPlayer = null;
    }

    public void addPlaybackListener(@NonNull OnPlaybackListener listener) {
        listeners.add(listener);
    }

    /**
     * 移除所有监听器，避免内存泄漏，在UI界面onDestroy时调用。
     */
    public void removeAllListeners() {
        listeners.clear();
    }

    public @Nullable String getVideoId() {
        return videoId;
    }

    // 设置MediaPlayer的各种监听器
    private void setMediaPlayerListener(final MediaPlayer mediaPlayer) {
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override public boolean onInfo(MediaPlayer mp, int what, int extra) {

                switch (what) {
                    case MediaPlayer.MEDIA_INFO_FILE_OPEN_OK: //Vitamio需要添加这两句话才能播放在线视频，原因未知
                        long bufferSize = mediaPlayer.audioTrackInit();
                        mediaPlayer.audioInitedOk(bufferSize);
                        return true;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START: //通知UI，视频缓冲开始

                        if (mediaPlayer.isPlaying()) mediaPlayer.pause();

                        for (OnPlaybackListener listener : listeners) {
                            listener.onStartBuffering(videoId);
                        }
                        return true;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END: //通知UI，视频缓冲结束
                        mediaPlayer.start();
                        for (OnPlaybackListener listener : listeners) {
                            listener.onStopBuffering(videoId);
                        }
                        return true;
                }
                return false;
            }
        });

        // 视频准备完成后，设置缓冲区大小并开始播放
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override public void onPrepared(MediaPlayer mp) {
                mediaPlayer.setBufferSize(512 * 1024);
                mediaPlayer.start();
            }
        });

        // 视频播放到最后，停止播放并通知UI更新
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override public void onCompletion(MediaPlayer mp) {
                stopPlayer();
            }
        });

        // 获取到视频尺寸后，要通知UI更新
        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                if (width == 0 || height == 0) return;

                for (OnPlaybackListener listener : listeners) {
                    listener.onSizeMeasured(videoId, width, height);
                }
            }
        });
    }


    /**
     * 视图层实现此接口，并通过{@link #addPlaybackListener}注册监听，
     * 从而在播放状态发生变化时，能相应地更新UI。
     */
    public interface OnPlaybackListener {

        /**
         * 准备开始播放视频。
         *
         * @see MediaPlayerManager#startPlayer
         */
        void onStartPlay(String videoId);

        // 获取到视频尺寸
        void onSizeMeasured(String videoId, int width, int height);

        // 视频缓冲开始
        void onStartBuffering(String videoId);

        // 视频缓冲结束
        void onStopBuffering(String videoId);


        /**
         * 停止播放视屏
         *
         * @see MediaPlayerManager#stopPlayer
         */
        void onStopPlay(String videoId);
    }


}
