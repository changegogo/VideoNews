package feicuiedu.com.videonews.bombapi.model.result;

import java.util.Date;

/**
 * 更新数据的结果，常对应{@link retrofit2.http.PUT}方法
 * <p/>
 * PUT和POST的区别：多次发送同样的请求，PUT会得到同样的响应，POST会得到不同的响应，这叫做幂等性(idempotent)。
 *
 * @see CreateResult
 */
@SuppressWarnings("unused")
public class UpdateResult {

    private Date updateAt;

    public Date getUpdateAt() {
        return updateAt;
    }
}
