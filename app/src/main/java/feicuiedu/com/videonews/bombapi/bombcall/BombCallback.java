package feicuiedu.com.videonews.bombapi.bombcall;

import java.io.IOException;

import feicuiedu.com.videonews.bombapi.bombcall.exception.BombHttpException;
import retrofit2.Response;

/**
 * 针对Bomb服务器响应码的不同情况，提供细粒度的回调方法
 */
public interface BombCallback<T> {
    /**
     * [200, 300) 响应，成功.
     */
    void success(Response<T> response);

    /**
     * 业务异常
     */
    void businessError(BombHttpException e);

    /**
     * 网络异常
     */
    void networkError(IOException e);

    /**
     * 未知异常
     */
    void unexpectedError(Throwable t);
}
