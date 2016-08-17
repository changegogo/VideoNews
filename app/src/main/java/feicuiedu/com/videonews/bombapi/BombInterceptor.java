package feicuiedu.com.videonews.bombapi;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 处理Bomb需要的统一的头字段
 */
class BombInterceptor implements Interceptor{


    @Override public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Request.Builder builder = request.newBuilder();

        // 用于让Bomb区分是哪一个应用
        builder.header(BombConst.HEADER_APPLICATION_ID, BombConst.APPLICATION_ID);
        // 用于授权
        builder.header(BombConst.HEADER_REST_API_KEY, BombConst.REST_API_KEY);
        // Bomb的请求体和响应体都是统一的Json格式
        builder.header(BombConst.HEADER_CONTENT_TYPE, BombConst.CONTENT_TYPE_JSON);

        return chain.proceed(builder.build());
    }
}
