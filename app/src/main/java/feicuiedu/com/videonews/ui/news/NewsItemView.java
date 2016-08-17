package feicuiedu.com.videonews.ui.news;


import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import feicuiedu.com.videonews.R;
import feicuiedu.com.videonews.bombapi.model.entity.NewsEntity;
import feicuiedu.com.videonews.commons.CommonUtils;
import feicuiedu.com.videonews.ui.base.PagerItemView;
import feicuiedu.com.videonews.ui.comments.CommentsActivity;
import feicuiedu.com.videoplayer.list.MediaPlayerManager;
import feicuiedu.com.videoplayer.list.ScalableTextureView;
import timber.log.Timber;

/**
 * 新闻列表的单项视图，使用 MediaPlayer + TextureView 播放视频。
 * <p>
 * 视频在{@link #onSurfaceTextureDestroyed(SurfaceTexture)}时自动停止播放。
 * <p>
 * 此视图使用{@link MediaPlayerManager.OnPlaybackListener}来监控播放状态的变化，从而更新UI。
 */
public class NewsItemView extends PagerItemView<NewsEntity> implements TextureView.SurfaceTextureListener,
        MediaPlayerManager.OnPlaybackListener {

    @BindView(R.id.ivPreview) ImageView ivPreview;
    @BindView(R.id.tvNewsTitle) TextView tvNewsTitle;
    @BindView(R.id.tvCreatedAt) TextView tvCreatedAt;
    @BindView(R.id.textureView) ScalableTextureView textureView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.ivPlay) ImageView ivPlay;

    private Surface surface;

    private NewsEntity newsEntity;

    private MediaPlayerManager mediaPlayerManager;


    public NewsItemView(Context context) {
        super(context);
        init();
    }

    @Override public void bindModel(NewsEntity newsEntity) {
        this.newsEntity = newsEntity;

        // 初始视图状态
        tvNewsTitle.setVisibility(View.VISIBLE);
        ivPreview.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        ivPlay.setVisibility(View.VISIBLE);

        // 下拉刷新后，需要停止视频播放
        if (newsEntity.getObjectId().equals(mediaPlayerManager.getVideoId())) {
            mediaPlayerManager.stopPlayer();
        }

        // 设置标题，创建时间和预览图
        tvNewsTitle.setText(newsEntity.getNewsTitle());
        tvCreatedAt.setText(CommonUtils.format(newsEntity.getCreatedAt()));
        Picasso.with(getContext()).load(CommonUtils.encodeUrl(newsEntity.getPreviewUrl())).into(ivPreview);
    }

    @OnClick(R.id.tvCreatedAt)
    public void navigateToComments() {
        CommentsActivity.open(getContext(), newsEntity);
    }

    @OnClick(R.id.ivPreview)
    public void startPlay() {
        if (surface == null) {
            Timber.e("Surface is not available yet.");
            return;
        }

        // 开始准备播放视频
        String path = CommonUtils.encodeUrl(newsEntity.getVideoUrl());
        MediaPlayerManager.getInstance(getContext()).startPlayer(surface, path, newsEntity.getObjectId());
    }

    @OnClick(R.id.textureView)
    public void stopPlay() {
        MediaPlayerManager.getInstance(getContext()).stopPlayer();
    }

    @Override public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        surface = new Surface(surfaceTexture);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
    }


    @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        surface.release();
        surface = null;

        // 如果当前播放的是此视图上的视频，则停止播放
        if (newsEntity.getObjectId().equals(mediaPlayerManager.getVideoId())) {
            mediaPlayerManager.stopPlayer();
        }
        return false;
    }

    @Override public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    @Override public void onStartPlay(String videoId) {
        if (isCurrentVideo(videoId)) {
            // 开始播放，显示TextureView
            ivPreview.setVisibility(View.INVISIBLE);
            tvNewsTitle.setVisibility(View.INVISIBLE);
            ivPlay.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override public void onSizeMeasured(String videoId, int width, int height) {
        if (isCurrentVideo(videoId)) {
            // 获取到视频尺寸，调整TextureView大小
            textureView.setContentWidth(width);
            textureView.setContentHeight(height);
            textureView.updateTextureViewSize();
        }
    }

    @Override public void onStartBuffering(String videoId) {
        if (isCurrentVideo(videoId)) {
            // 开始缓冲，显示进度条
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override public void onStopBuffering(String videoId) {
        if (isCurrentVideo(videoId)) {
            // 结束缓冲，隐藏进度条
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    @Override public void onStopPlay(String videoId) {
        if (isCurrentVideo(videoId)) {
            // 停止播放，显示标题和预览图
            ivPreview.setVisibility(View.VISIBLE);
            tvNewsTitle.setVisibility(View.VISIBLE);
            ivPlay.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    // 视图初始化，只在构造方法中调用一次
    private void init() {
        // 视图填充和绑定
        LayoutInflater.from(getContext()).inflate(R.layout.item_news, this, true);
        ButterKnife.bind(this);

        // MediaPlayerManager初始化
        mediaPlayerManager = MediaPlayerManager.getInstance(getContext());
        mediaPlayerManager.addPlaybackListener(this);

        // TextureView初始化
        textureView.setSurfaceTextureListener(this);
        textureView.setScaleType(ScalableTextureView.ScaleType.CENTER_CROP);

    }

    private boolean isCurrentVideo(String videoId) {
        //noinspection SimplifiableIfStatement
        if (videoId == null || newsEntity == null) return false;

        return newsEntity.getObjectId().equals(videoId);
    }
}
