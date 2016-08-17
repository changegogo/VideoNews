package feicuiedu.com.videonews.ui.comments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import feicuiedu.com.videonews.R;
import feicuiedu.com.videonews.bombapi.BombClient;
import feicuiedu.com.videonews.bombapi.NewsApi;
import feicuiedu.com.videonews.bombapi.bombcall.BombCallback;
import feicuiedu.com.videonews.bombapi.bombcall.exception.BombHttpException;
import feicuiedu.com.videonews.bombapi.model.entity.NewsEntity;
import feicuiedu.com.videonews.bombapi.model.other.LikesOperation;
import feicuiedu.com.videonews.bombapi.model.other.RelationOperation;
import feicuiedu.com.videonews.bombapi.model.result.UpdateResult;
import feicuiedu.com.videonews.commons.CommonUtils;
import feicuiedu.com.videonews.commons.ToastUtils;
import feicuiedu.com.videonews.ui.UserManager;
import feicuiedu.com.videoplayer.part.SimpleVideoPlayer;
import retrofit2.Response;

/**
 * 新闻评论页面，主要包括三个部分：
 * <ul>
 * <li/>上半部分是用MediaPlayer + SurfaceView实现的视频播放，详见{@link SimpleVideoPlayer}。
 * <li/>下半部分是继承自{@link feicuiedu.com.videonews.ui.base.PagerResourceView}的列表视图。
 * <li/>选项菜单中的评论和收藏功能。
 * </ul>
 */
public class CommentsActivity extends AppCompatActivity implements EditCommentFragment.OnCommentSuccessListener {

    private static final String KEY_NEWS = "key_news";

    /**
     * 开启此Activity，封装此方法是因为CommentsActivity必须要有一个NewsEntity作为参数
     */
    public static void open(Context context, NewsEntity newsEntity) {
        // 使用Json来序列化对象。
        // 不使用Parcelable是因为Parcelable的代码太复杂。
        // 不使用Serializable是因为，据说过多的Serializable会严重影响Android性能。
        Gson gson = new Gson();
        String news = gson.toJson(newsEntity);

        Intent intent = new Intent(context, CommentsActivity.class);
        intent.putExtra(KEY_NEWS, news);
        context.startActivity(intent);
    }


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.commentsListView) CommentsListView commentsListView;
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.simpleVideoPlayer) SimpleVideoPlayer simpleVideoPlayer;

    private NewsEntity newsEntity;
    private NewsApi newsApi;

    // 评论Fragment
    private EditCommentFragment editCommentFragment;

    // 用于收藏的BombCallback
    private final BombCallback<UpdateResult> likesCallback = new BombCallback<UpdateResult>() {
        @Override public void success(Response<UpdateResult> response) {
            ToastUtils.showShort(R.string.like_success);
        }

        @Override public void businessError(BombHttpException e) {
            ToastUtils.showShort(e.getErrorResult().getError());
        }

        @Override public void networkError(IOException e) {
            ToastUtils.showShort(R.string.error_network);
        }

        @Override public void unexpectedError(Throwable t) {
            ToastUtils.showShort(t.getMessage());
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Gson gson = new Gson();
        newsEntity = gson.fromJson(getIntent().getStringExtra(KEY_NEWS), NewsEntity.class);
        newsApi = BombClient.getInstance().getNewsApi();
        setContentView(R.layout.activity_comments);
    }

    @Override public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);

        // 设置Toolbar
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle.setText(newsEntity.getNewsTitle());

        // 告诉评论列表针对的是哪一个新闻，并触发自动刷新
        commentsListView.setNewsId(newsEntity.getObjectId());
        commentsListView.autoRefresh();

        // 设置预览图
        String previewPath = CommonUtils.encodeUrl(newsEntity.getPreviewUrl());
        Picasso.with(this).load(previewPath).into(simpleVideoPlayer.getPreviewView());

        // 设置视频地址
        String videoPath = CommonUtils.encodeUrl(newsEntity.getVideoUrl());
        simpleVideoPlayer.setVideoPath(videoPath);
    }

    @Override protected void onResume() {
        super.onResume();
        simpleVideoPlayer.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
        simpleVideoPlayer.onPause();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // 返回按钮
            finish();
            return true;
        }

        // 未登录情况下不能收藏或评论
        if (UserManager.getInstance().isOffline()) {
            ToastUtils.showShort(R.string.please_login_first);
            return true;
        }

        // TODO: 取消收藏
        if (item.getItemId() == R.id.menu_item_like) { // 收藏

            LikesOperation likesOperation = new LikesOperation(UserManager.getInstance().getObjectId(), RelationOperation.Operation.AddRelation);
            newsApi.changeLikes(newsEntity.getObjectId(), likesOperation).enqueue(likesCallback);
            return true;
        }

        if (item.getItemId() == R.id.menu_item_comment) { // 评论

            if (editCommentFragment == null) {
                editCommentFragment = EditCommentFragment.getInstance(newsEntity.getObjectId());
                editCommentFragment.setListener(this);
            }
            editCommentFragment.show(getSupportFragmentManager(), "Dialog Edit Comment");
            return true;
        }

        // 代码不应该走到此处
        throw new RuntimeException("Wrong branch!");
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.activity_comments, menu);
        return true;
    }

    /**
     * {@link CommentsActivity}和{@link EditCommentFragment}沟通的方法，在评论成功时调用。
     */
    @Override public void onCommentSuccess() {
        editCommentFragment.dismiss();
        // 刷新视图，获取最新评论
        commentsListView.autoRefresh();
    }
}
