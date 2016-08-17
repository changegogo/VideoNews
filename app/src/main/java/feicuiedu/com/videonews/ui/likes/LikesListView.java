package feicuiedu.com.videonews.ui.likes;


import android.content.Context;
import android.util.AttributeSet;

import feicuiedu.com.videonews.bombapi.BombConst;
import feicuiedu.com.videonews.bombapi.bombcall.BombCall;
import feicuiedu.com.videonews.bombapi.model.entity.NewsEntity;
import feicuiedu.com.videonews.bombapi.model.other.InQuery;
import feicuiedu.com.videonews.bombapi.model.result.QueryResult;
import feicuiedu.com.videonews.ui.base.PagerResourceView;

/**
 * 收藏列表视图
 */
public class LikesListView extends PagerResourceView<NewsEntity, LikesItemView>{

    private InQuery inQuery;

    public LikesListView(Context context) {
        super(context);
    }

    public LikesListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LikesListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override protected LikesItemView createItemView() {
        return new LikesItemView(getContext());
    }

    @Override protected int getLimit() {
        return 10;
    }

    @Override protected BombCall<QueryResult<NewsEntity>> queryData(int limit, int skip) {
        if (inQuery == null){
            return null;
        }

        return newsApi.getLikedNewsList(limit, skip, inQuery);
    }

    public void setUserId(String userId){
        inQuery = new InQuery(BombConst.FIELD_LIKES, userId, BombConst.TABLE_USER);
    }

    public void clear(){
        inQuery = null;
        adapter.clear();
    }
}
