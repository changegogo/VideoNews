package feicuiedu.com.videonews.bombapi.bombcall;

import java.io.IOException;

import feicuiedu.com.videonews.bombapi.bombcall.exception.BombHttpException;
import retrofit2.Response;

/**
 * 自定义接口，用来替代{@link retrofit2.Call}
 */
@SuppressWarnings("unused")
public interface BombCall<T> {
    void cancel();

    void enqueue(BombCallback<T> callback);

    BombCall<T> clone();

    Response<T> execute() throws BombHttpException, IOException;
}
