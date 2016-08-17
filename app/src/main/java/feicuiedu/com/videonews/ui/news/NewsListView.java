package feicuiedu.com.videonews.ui.news;


import android.content.Context;
import android.util.AttributeSet;

import feicuiedu.com.videonews.bombapi.bombcall.BombCall;
import feicuiedu.com.videonews.bombapi.model.entity.NewsEntity;
import feicuiedu.com.videonews.bombapi.model.result.QueryResult;
import feicuiedu.com.videonews.ui.base.PagerResourceView;

/**
 * 新闻列表视图，使用{@link PagerResourceView}的下拉刷新和分页加载功能。
 */
public class NewsListView extends PagerResourceView<NewsEntity, NewsItemView> {

    public NewsListView(Context context) {
        super(context);
    }

    public NewsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override public NewsItemView createItemView() {
        return new NewsItemView(getContext());
    }

    @Override public int getLimit() {
        return 5;
    }

    @Override public BombCall<QueryResult<NewsEntity>> queryData(int limit, int skip) {
        return newsApi.getNewsList(limit, skip);
    }
}
