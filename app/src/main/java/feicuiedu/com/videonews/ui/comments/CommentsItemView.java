package feicuiedu.com.videonews.ui.comments;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import feicuiedu.com.videonews.R;
import feicuiedu.com.videonews.bombapi.model.entity.CommentsEntity;
import feicuiedu.com.videonews.commons.CommonUtils;
import feicuiedu.com.videonews.ui.base.PagerItemView;

/**
 * 评论的单项视图
 */
public class CommentsItemView extends PagerItemView<CommentsEntity>{

    @BindView(R.id.tvContent) TextView tvContent; // 评论内容
    @BindView(R.id.tvAuthor) TextView tvAuthor; // 评论作者
    @BindView(R.id.tvCreatedAt) TextView tvCreatedAt; // 评论时间

    public CommentsItemView(Context context) {
        super(context);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.item_comments, this, true);
        ButterKnife.bind(this);
    }

    @Override public void bindModel(CommentsEntity commentsEntity) {
        tvContent.setText(commentsEntity.getContent());
        tvAuthor.setText(commentsEntity.getAuthor().getUsername());
        tvCreatedAt.setText(CommonUtils.format(commentsEntity.getCreatedAt()));
    }
}
