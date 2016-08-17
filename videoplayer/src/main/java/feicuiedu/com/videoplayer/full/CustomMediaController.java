package feicuiedu.com.videoplayer.full;


import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import feicuiedu.com.videoplayer.R;
import io.vov.vitamio.widget.MediaController;

/**
 * 继承{@link MediaController}，实现自定义的视频播放控制器。
 * <p/>
 * 重写{@link #makeControllerView()}方法，提供自定义的视图，视图规则如下：
 * <ul>
 * <li/>SeekBar的id必须是mediacontroller_seekbar
 * <li/>播放/暂停按钮的id必须是mediacontroller_play_pause
 * <li/>当前时间的id必须是mediacontroller_time_current
 * <li/>总时间的id必须是mediacontroller_time_total
 * <li/>视频名称的id必须是mediacontroller_file_name
 * <li/>drawable资源中必须有pause_button和play_button
 * </ul>
 */
public class CustomMediaController extends MediaController {

    private MediaPlayerControl mediaPlayerControl;

    private OnClickListener onScreenShotListener;
    private OnClickListener onAdjustScreenListener;

    private GestureDetector gestureDetector; // 用于监控滑动手势，从而调整亮度和音量
    private final AudioManager audioManager; // 用来调整音量
    private final Window window; // 用来调整亮度

    private final int maxVolume; // 最大音量
    private int currentVolume; // 滑动手势开始时的音量
    private float currentBrightness; // 滑动手势开始时的亮度 (0.0f - 1.0f)，如果是负数，代表自动调整亮度


    public CustomMediaController(Context context) {
        super(context);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // TODO：如果此控件是在Dialog或PopupWindow中使用，这句代码会报ClassCastException，需要找一个更好的办法获取当前窗口
        window = ((Activity) context).getWindow();

        // 设置默认亮度
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = 0.5f;
        window.setAttributes(layoutParams);
    }

    // 重写这个方法来自定义MediaController的视图
    @Override protected View makeControllerView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_custom_video_controller, this);
        initView(view);
        return view;
    }

    // 父类的MediaPlayerControl是私有的，重写这个方法在子类保存一份，以便使用
    @Override public void setMediaPlayer(MediaPlayerControl player) {
        super.setMediaPlayer(player);
        this.mediaPlayerControl = player;
    }

    // 设置按下截屏的监听器
    public void setOnScreenShotListener(OnClickListener onClickListener) {
        this.onScreenShotListener = onClickListener;
    }

    // 设置按下屏幕调整的监听器(填充屏幕/保持原比例)
    public void setOnAdjustScreenListener(OnClickListener onClickListener) {
        this.onAdjustScreenListener = onClickListener;
    }


    private void initView(View view) {

        // 设置快进按钮
        ImageButton btnFastForward = (ImageButton) view.findViewById(R.id.btnFastForward);
        btnFastForward.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View view) {
                long position = mediaPlayerControl.getCurrentPosition();
                position += 15000;
                mediaPlayerControl.seekTo(position);
            }
        });

        // 设置快退按钮
        ImageButton btnFastRewind = (ImageButton) view.findViewById(R.id.btnFastRewind);
        btnFastRewind.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View view) {
                long position = mediaPlayerControl.getCurrentPosition();
                position -= 5000;
                mediaPlayerControl.seekTo(position);
            }
        });

        // 设置调整屏幕按钮
        ImageButton btnAdjustScreen = (ImageButton) view.findViewById(R.id.btnAdjustScreen);
        btnAdjustScreen.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View view) {
                if (onAdjustScreenListener != null) onAdjustScreenListener.onClick(view);
            }
        });

        // 设置截屏按钮
        ImageButton btnScreenShot = (ImageButton) view.findViewById(R.id.btnScreenShot);
        btnScreenShot.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View view) {
                if (onScreenShotListener != null) onScreenShotListener.onClick(view);
            }
        });

        // 设置触屏调整屏幕亮度(左边)和音量(右边)
        final View adjustView = view.findViewById(R.id.adjustView);
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float startX = e1.getX();
                float startY = e1.getY();

                float width = adjustView.getWidth();
                float height = adjustView.getHeight();

                // 垂直移动距离占整个视图高度的比例
                float percentage = (startY - e2.getY()) / height;

                if (startX < width / 5) { // 如果是屏幕左边的1/5，则调整亮度
                    adjustBrightness(percentage);
                    return true;
                } else if (startX > width * 4 / 5) { // 如果是屏幕右边的1/5，则调整音量
                    adjustVolume(percentage);
                    return true;
                }
                return false;
            }
        });
        adjustView.setOnTouchListener(new OnTouchListener() {
            @Override public boolean onTouch(View view, MotionEvent motionEvent) {

                gestureDetector.onTouchEvent(motionEvent);

                // 按下事件代表滑动手势开始
                // 使用motionEvent.getAction() & MotionEvent.ACTION_MASK是为了过滤掉多点触屏事件
                if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    currentBrightness = window.getAttributes().screenBrightness;
                }

                // 触屏期间不让控制器自动消失
                CustomMediaController.this.show();
                return true;
            }
        });

    }

    // 调整音量
    private void adjustVolume(float percentage) {

        // 计算目标音量
        int targetVolume = (int) (percentage * maxVolume) + currentVolume;
        targetVolume = targetVolume > maxVolume ? maxVolume : targetVolume;
        targetVolume = targetVolume < 0 ? 0 : targetVolume;

        // 设置音量，并显示系统音量UI控件
        // TODO: 使用自定义控件代替系统音量控件
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, AudioManager.FLAG_SHOW_UI);
    }

    // 调整亮度
    private void adjustBrightness(float percentage) {

        // 计算目标亮度
        float targetBrightness = percentage + currentBrightness;
        targetBrightness = targetBrightness > 1.0f ? 1.0f : targetBrightness;
        targetBrightness = targetBrightness < 0.01f ? 0.01f : targetBrightness;

        // 设置屏幕亮度
        // TODO: 使用自定义控件显示当前屏幕亮度
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = targetBrightness;
        window.setAttributes(layoutParams);
    }
}
