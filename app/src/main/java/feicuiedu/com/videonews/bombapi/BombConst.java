package feicuiedu.com.videonews.bombapi;

/**
 * 网络连接模块用到的常量值
 */
@SuppressWarnings("unused")
public interface BombConst {

    // 服务器用户表表名
    String TABLE_USER = "_User";

    // 服务器新闻表表名
    String TABLE_NEWS = "News";

    // 服务器评论表表名
    String TABLE_COMMENTS = "Comments";

    // 新闻表中的likes字段，代表收藏此新闻的用户
    String FIELD_LIKES = "likes";

    // 评论表中的news字段，代表此评论针对的新闻
    String FIELD_NEWS = "news";

    String BASE_URL = "https://api.bmob.cn/";

    // 应用Id，让Bomb后端区分是哪一个应用
    String APPLICATION_ID = "623aaef127882aed89b9faa348451da3";

    // REST API的授权码
    String REST_API_KEY = "c00104962a9b67916e8cbcb9157255de";

    String HEADER_APPLICATION_ID = "X-Bmob-Application-Id";

    String HEADER_REST_API_KEY = "X-Bmob-REST-API-Key";

    String HEADER_CONTENT_TYPE = "Content-Type";

    String CONTENT_TYPE_JSON = "application/json";

    /**
     * 用于约束查询。
     */
    String LIKES_IN_QUERY = "{ \"%s\": { \"$inQuery\": {\"where\": {\"objectId\":\"%s\"}, \"className\": \"%s\"}}}";

}
