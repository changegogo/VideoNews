package feicuiedu.com.videonews.bombapi.model.entity;

/**
 * 对应服务器端的_User表
 */
@SuppressWarnings("unused")
public class UserEntity extends BaseEntity {

    private String username; // 用户名

    private String password; // 密码

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
