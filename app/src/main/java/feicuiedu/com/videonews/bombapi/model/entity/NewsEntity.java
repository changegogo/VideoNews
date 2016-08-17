package feicuiedu.com.videonews.bombapi.model.entity;

/**
 * 对应服务器端的News表 (不包含likes字段)
 */
@SuppressWarnings("unused")
public class NewsEntity extends BaseEntity {

    private String newsTitle; // 新闻标题

    private String videoUrl; // 视频地址

    private String previewUrl; // 视频预览图地址

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override public boolean equals(Object obj) {
        if (obj == this) return true;

        if ((!(obj instanceof NewsEntity))) return false;

        return getObjectId().equals(((NewsEntity) obj).getObjectId());

    }

    @Override public int hashCode() {
        return getObjectId().hashCode();
    }

}
