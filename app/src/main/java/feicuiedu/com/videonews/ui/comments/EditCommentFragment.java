package feicuiedu.com.videonews.ui.comments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import feicuiedu.com.videonews.R;
import feicuiedu.com.videonews.bombapi.BombClient;
import feicuiedu.com.videonews.bombapi.bombcall.BombCallback;
import feicuiedu.com.videonews.bombapi.bombcall.exception.BombHttpException;
import feicuiedu.com.videonews.bombapi.model.entity.CommentsEntity;
import feicuiedu.com.videonews.bombapi.model.result.CreateResult;
import feicuiedu.com.videonews.commons.ToastUtils;
import feicuiedu.com.videonews.ui.UserManager;
import feicuiedu.com.videonews.ui.base.BaseDialogFragment;
import retrofit2.Response;

/**
 * 编辑评论的{@link DialogFragment}。
 * <p>
 * 此Fragment通过接口{@link OnCommentSuccessListener}和Activity通信。在使用前，必须先调用{@link #setListener}方法。
 */
public class EditCommentFragment extends BaseDialogFragment {

    private static final String KEY_NEWS_ID = "key_news_id";

    /**
     * @param newsId 要评论的新闻Id
     * @return 编辑评论的DialogFragment。
     */
    public static EditCommentFragment getInstance(String newsId) {
        EditCommentFragment fragment = new EditCommentFragment();
        Bundle args = new Bundle();
        args.putString(KEY_NEWS_ID, newsId);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.etComment) EditText etComment;
    @BindView(R.id.btnOK) Button btnOk;

    private OnCommentSuccessListener listener;

    // 发表评论的BombCallback
    private final BombCallback<CreateResult> commentCallback = new BombCallback<CreateResult>() {
        @Override public void success(Response<CreateResult> response) {
            btnOk.setVisibility(View.VISIBLE);
            listener.onCommentSuccess();
        }

        @Override public void businessError(BombHttpException e) {
            btnOk.setVisibility(View.VISIBLE);
            ToastUtils.showShort(e.getErrorResult().getError());
        }

        @Override public void networkError(IOException e) {
            btnOk.setVisibility(View.VISIBLE);
            ToastUtils.showShort(R.string.error_network);
        }

        @Override public void unexpectedError(Throwable t) {
            btnOk.setVisibility(View.VISIBLE);
            ToastUtils.showShort(t.getMessage());
        }
    };

    @Override protected int getLayoutId() {
        return R.layout.dialog_edit_comment;
    }

    /**
     * 点击确定按钮时，发表评论
     */
    @OnClick(R.id.btnOK)
    public void postComment() {

        String comment = etComment.getText().toString();
        // 评论内容不能为空
        if (TextUtils.isEmpty(comment)) {
            ToastUtils.showShort(R.string.please_edit_comment);
            return;
        }

        // 隐藏按钮，避免按钮重复点击，此时显示的是按钮下方的进度条。
        btnOk.setVisibility(View.INVISIBLE);

        String userId = UserManager.getInstance().getObjectId();
        String newsId = getArguments().getString(KEY_NEWS_ID);

        // 构建评论的实体，并发表评论
        CommentsEntity commentsEntity = new CommentsEntity(comment, userId, newsId);
        BombClient.getInstance().getNewsApi().postComments(commentsEntity).enqueue(commentCallback);
    }

    public void setListener(OnCommentSuccessListener listener) {
        this.listener = listener;
    }

    /**
     * 评论成功的监听器
     */
    public interface OnCommentSuccessListener {

        void onCommentSuccess();
    }
}
