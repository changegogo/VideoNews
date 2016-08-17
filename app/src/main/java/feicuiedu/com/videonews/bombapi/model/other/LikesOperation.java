package feicuiedu.com.videonews.bombapi.model.other;

import feicuiedu.com.videonews.bombapi.BombConst;

/**
 * 收藏新闻 / 取消收藏的请求体
 */
@SuppressWarnings("unused")
public class LikesOperation {

    private RelationOperation likes;

    public LikesOperation(String userId, RelationOperation.Operation operation) {

        Pointer pointer = new Pointer(BombConst.TABLE_USER, userId);

        likes = new RelationOperation(operation, pointer);
    }

    public RelationOperation getLikes() {
        return likes;
    }
}
