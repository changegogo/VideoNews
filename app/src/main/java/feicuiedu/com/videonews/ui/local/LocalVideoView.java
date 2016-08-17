package feicuiedu.com.videonews.ui.local;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import feicuiedu.com.videonews.R;
import feicuiedu.com.videoplayer.full.VideoViewActivity;

/**
 * 本地视频的单项视图，用于{@link LocalVideoAdapter}中。
 */
public class LocalVideoView extends FrameLayout {

    @BindView(R.id.ivPreview) ImageView ivPreview; // 视频预览图
    @BindView(R.id.tvVideoName) TextView tvVideoName; // 视频名称

    private String filePath; // 当前本地视频的文件路径

    public LocalVideoView(Context context) {
        this(context, null);
    }

    public LocalVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocalVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // 点击则开启全屏播放
    @OnClick
    public void onClick() {
        VideoViewActivity.open(getContext(), filePath);
    }

    /**
     * 将视图和游标上当前行的数据绑定
     */
    public void bind(Cursor cursor) {
        String videoName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
        tvVideoName.setText(videoName);
        filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));

        // 清除旧的预览图
        ivPreview.setImageBitmap(null);
    }

    @UiThread
    public void setPreview(@Nullable Bitmap bitmap) {
        ivPreview.setImageBitmap(bitmap);
    }

    /**
     * 设置预览图，此方法可以在后台线程调用
     * @param filePath 此预览图对应的视频路径
     * @param bitmap 预览图片
     */
    public void setPreview(@NonNull final String filePath, final Bitmap bitmap) {
        // 如果路径不匹配，说明当前视图不再需要显示这张预览图
        if (!filePath.equals(this.filePath)) return;

        post(new Runnable() {
            @Override public void run() {
                // 二次确认，防止加载错误的图片
                if (!filePath.equals(LocalVideoView.this.filePath)) return;
                ivPreview.setImageBitmap(bitmap);
            }
        });
    }

    public String getFilePath() {
        return filePath;
    }

    // 初始化：填充和绑定视图
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_local_video, this, true);
        ButterKnife.bind(this);
    }


}
