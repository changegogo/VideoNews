package feicuiedu.com.videonews.bombapi.model.entity;


import feicuiedu.com.videonews.bombapi.BombConst;
import feicuiedu.com.videonews.bombapi.model.other.Pointer;
import feicuiedu.com.videonews.bombapi.model.other.UserPointer;

/**
 * 对应服务器端的Comments表
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class CommentsEntity extends BaseEntity{

    // 评论内容
    private String content;

    // 评论作者
    private UserPointer author;

    // 评论针对的新闻
    private Pointer news;

    public CommentsEntity() {
    }

    public CommentsEntity(String content, String userId, String newsId) {
        this.content = content;

        this.author = new UserPointer(userId);
        this.news = new Pointer(BombConst.TABLE_NEWS, newsId);
    }

    public String getContent() {
        return content;
    }

    public UserPointer getAuthor() {
        return author;
    }

    public Pointer getNews() {
        return news;
    }
}
