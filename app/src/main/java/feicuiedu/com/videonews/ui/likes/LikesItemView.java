package feicuiedu.com.videonews.ui.likes;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
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

/**
 * 收藏列表的单项视图
 */
public class LikesItemView extends PagerItemView<NewsEntity>{

    @BindView(R.id.ivPreview) ImageView ivPreview;
    @BindView(R.id.tvNewsTitle) TextView tvNewsTitle;
    @BindView(R.id.tvCreatedAt) TextView tvCreatedAt;

    private NewsEntity newsEntity;

    public LikesItemView(Context context) {
        super(context);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.item_likes, this, true);
        ButterKnife.bind(this);
    }

    @Override public void bindModel(NewsEntity newsEntity) {
        this.newsEntity = newsEntity;
        tvNewsTitle.setText(newsEntity.getNewsTitle());
        tvCreatedAt.setText(CommonUtils.format(newsEntity.getCreatedAt()));
        Picasso.with(getContext()).load(CommonUtils.encodeUrl(newsEntity.getPreviewUrl())).into(ivPreview);
    }

    @OnClick
    public void navigateToComments() {
        CommentsActivity.open(getContext(), newsEntity);
    }
}
