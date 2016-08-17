package feicuiedu.com.videonews.bombapi.model.result;

/**
 * 用户登录或注册的结果
 * <p/>
 * 用户注册是POST方法，登录是GET方法，注册和登录返回的响应体不完全一样。这里只取了共有的必要属性。
 */
@SuppressWarnings("unused")
public class UserResult {

    private String objectId;

    // 用来认证更新或删除用户的请求
    private String sessionToken;

    public String getSessionToken() {
        return sessionToken;
    }

    public String getObjectId() {
        return objectId;
    }
}
