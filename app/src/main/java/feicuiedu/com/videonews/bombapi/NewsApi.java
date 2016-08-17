package feicuiedu.com.videonews.bombapi;

import feicuiedu.com.videonews.bombapi.bombcall.BombCall;
import feicuiedu.com.videonews.bombapi.model.entity.CommentsEntity;
import feicuiedu.com.videonews.bombapi.model.entity.NewsEntity;
import feicuiedu.com.videonews.bombapi.model.other.InQuery;
import feicuiedu.com.videonews.bombapi.model.other.LikesOperation;
import feicuiedu.com.videonews.bombapi.model.result.CreateResult;
import feicuiedu.com.videonews.bombapi.model.result.QueryResult;
import feicuiedu.com.videonews.bombapi.model.result.UpdateResult;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 新闻相关操作的Restful接口，包括获取新闻，收藏新闻，发表评论等
 */
public interface NewsApi {

    /**
     * 获取所有的新闻列表，按时间由新到旧排序
     */
    @GET("1/classes/News?order=-createdAt")
    BombCall<QueryResult<NewsEntity>> getNewsList(@Query("limit") int limit, @Query("skip") int skip);


    /**
     * 收藏新闻和取消收藏
     *
     * @param newsId    新闻Id
     * @param operation 收藏新闻还是取消收藏，其中包含用户Id
     * @return @see {@link BombCall}
     */
    @PUT("1/classes/News/{objectId}")
    BombCall<UpdateResult> changeLikes(@Path("objectId") String newsId, @Body LikesOperation operation);


    /**
     * 获取用户收藏的新闻列表，按时间由新到旧排序
     * <p/>
     * 在请求头中设置不使用缓存(<b>Cache-Control: no-store</b>)，是为了能立刻刷新出刚收藏的新闻。
     */
    @Headers({
            "Cache-Control: no-store"
    })
    @GET("1/classes/News?order=-createdAt")
    BombCall<QueryResult<NewsEntity>> getLikedNewsList(@Query("limit") int limit, @Query("skip") int skip, @Query("where") InQuery where);

    /**
     * 发表评论
     */
    @POST("1/classes/Comments")
    BombCall<CreateResult> postComments(@Body CommentsEntity commentsEntity);

    /**
     * 获取评论，按时间由新到旧排序
     * <p/>
     * 在请求头中设置不使用缓存(<b>Cache-Control: no-store</b>)，是为了能立刻刷新出刚发表的评论。
     */
    @Headers({
            "Cache-Control: no-store"
    })
    @GET("1/classes/Comments?include=author&order=-createdAt")
    BombCall<QueryResult<CommentsEntity>> getComments(@Query("limit") int limit, @Query("skip") int skip, @Query("where") InQuery where);


}
