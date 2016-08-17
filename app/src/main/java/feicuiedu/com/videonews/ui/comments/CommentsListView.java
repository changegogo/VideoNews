package feicuiedu.com.videonews.ui.comments;


import android.content.Context;
import android.util.AttributeSet;

import feicuiedu.com.videonews.bombapi.BombConst;
import feicuiedu.com.videonews.bombapi.bombcall.BombCall;
import feicuiedu.com.videonews.bombapi.model.entity.CommentsEntity;
import feicuiedu.com.videonews.bombapi.model.other.InQuery;
import feicuiedu.com.videonews.bombapi.model.result.QueryResult;
import feicuiedu.com.videonews.ui.base.PagerResourceView;

/**
 * 评论列表视图。
 * <p/>
 * 必须首先调用{@link #setNewsId(String)}方法，设置该评论列表针对的新闻。
 */
public class CommentsListView extends PagerResourceView<CommentsEntity, CommentsItemView> {

    private InQuery inQuery;

    public CommentsListView(Context context) {
        super(context);
    }

    public CommentsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override public CommentsItemView createItemView() {
        return new CommentsItemView(getContext());
    }

    @Override public int getLimit() {
        return 10;
    }

    @Override public BombCall<QueryResult<CommentsEntity>> queryData(int limit, int skip) {
        if (inQuery == null) throw new RuntimeException("CommentsListView: newsId is missing!");

        return newsApi.getComments(limit, skip, inQuery);
    }


    public void setNewsId(String newsId) {
        inQuery = new InQuery(BombConst.FIELD_NEWS, newsId, BombConst.TABLE_NEWS);
    }
}
