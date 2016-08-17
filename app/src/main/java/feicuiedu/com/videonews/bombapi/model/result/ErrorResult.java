package feicuiedu.com.videonews.bombapi.model.result;

/**
 * 统一的Bomb错误响应体，注意当响应码为401或500时，{@link #code}不存在
 */
@SuppressWarnings("unused")
public class ErrorResult {

    private int code;

    private String error;

    public String getError() {
        return error;
    }

    public int getCode() {
        return code;
    }

    @Override public String toString() {
        return "ErrorResult{" +
                "code=" + code +
                ", error='" + error + '\'' +
                '}';
    }
}
