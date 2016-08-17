package feicuiedu.com.videonews.bombapi;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

import feicuiedu.com.videonews.bombapi.model.other.InQuery;
import feicuiedu.com.videonews.bombapi.model.other.LikesOperation;
import feicuiedu.com.videonews.bombapi.model.entity.CommentsEntity;
import feicuiedu.com.videonews.bombapi.model.entity.NewsEntity;
import feicuiedu.com.videonews.bombapi.model.result.CreateResult;
import feicuiedu.com.videonews.bombapi.model.result.QueryResult;
import feicuiedu.com.videonews.bombapi.model.other.RelationOperation;
import timber.log.Timber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 新闻相关接口单元测试
 */
public class NewsApiUnitTest {

    private NewsApi newsApi;

    @BeforeClass
    public static void initTimber(){
        Timber.plant(new Timber.Tree() {
            @Override protected void log(int priority, String tag, String message, Throwable t) {
                System.out.println(tag + " : " + message);
                if (t != null) {
                    t.printStackTrace();
                }
            }
        });
    }

    @Before
    public void init(){
        newsApi = BombClient.getInstance().getNewsApi();
    }

    // 测试获取新闻列表
    @Test
    public void getNewsList() throws Exception {
        QueryResult<NewsEntity> result = newsApi.getNewsList(1, 0).execute().body();
        assertEquals(result.getResults().size(), 1);
    }

    // 测试收藏和取消收藏
    @Test
    public void changeLikes() throws Exception {
        QueryResult<NewsEntity> result = newsApi.getNewsList(1, 0).execute().body();

        newsApi.changeLikes(result.getResults().get(0).getObjectId(), new LikesOperation("81b5fd2c64", RelationOperation.Operation.AddRelation))
                .execute();
        newsApi.changeLikes(result.getResults().get(0).getObjectId(), new LikesOperation("81b5fd2c64", RelationOperation.Operation.RemoveRelation))
                .execute();
    }

    // 测试获取收藏列表
    @Test
    public void getLikedNewsList() throws Exception {
        QueryResult<NewsEntity> result = newsApi.getNewsList(1, 0).execute().body();

        // 获取第一条新闻
        NewsEntity news = result.getResults().get(0);

        // 添加收藏
        newsApi.changeLikes(news.getObjectId(), new LikesOperation("81b5fd2c64", RelationOperation.Operation.AddRelation))
                .execute();

        // 获取用户收藏的新闻
        InQuery inQuery = new InQuery(BombConst.FIELD_LIKES, "81b5fd2c64", BombConst.TABLE_USER);
        QueryResult<NewsEntity> likedList = newsApi.getLikedNewsList(100, 0, inQuery).execute().body();

        assertTrue(likedList.getResults().contains(news));
    }

    // 测试发表评论
    @Test
    public void postComments() throws Exception {

        String content = "官方测试员出没：" + new Date(System.currentTimeMillis()).toString();
        CreateResult result = newsApi.postComments(new CommentsEntity(content, "81b5fd2c64", "q3pE999a")).execute().body();
        assertNotNull(result.getCreatedAt());
    }

    // 测试获取评论
    @Test
    public void getComments() throws Exception {
        InQuery inQuery = new InQuery(BombConst.FIELD_NEWS, "q3pE999a", BombConst.TABLE_NEWS);
        newsApi.getComments(100, 0, inQuery).execute();
    }


}
