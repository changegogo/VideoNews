package feicuiedu.com.videonews.bombapi.model.result;

import java.util.Date;

/**
 * 创建数据的结果，常对应{@link retrofit2.http.POST}方法
 * <p/>
 * PUT和POST的区别：多次发送同样的请求，PUT会得到同样的响应，POST会得到不同的响应，这叫做幂等性(idempotent)。
 *
 * @see UpdateResult
 */
@SuppressWarnings("unused")
public class CreateResult {

    private Date createdAt;

    private String objectId;

    public String getObjectId() {
        return objectId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
