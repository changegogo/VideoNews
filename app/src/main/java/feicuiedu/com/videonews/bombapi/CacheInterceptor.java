package feicuiedu.com.videonews.bombapi;


import java.io.IOException;

import feicuiedu.com.videonews.commons.ContextUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

class CacheInterceptor implements Interceptor{


    @Override public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        boolean noStore = request.cacheControl().noStore();

        Timber.w("No store %s", noStore);

        // 如果已经在请求中配置了"Cache-Control: no-store"，则该请求不使用缓存
        if (noStore) return chain.proceed(request);

        if (ContextUtils.hasNetwork()) {
            // 如果有网，可重用60秒内的缓存
            request = request.newBuilder().header("Cache-Control", "public, max-age=" + 60).build();
        } else {
            // 如果没网，可重用7天之内的缓存
            // only-if-cached : 如果有缓存，不请求服务器，直接使用缓存数据
            request = request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build();
        }

        Response response =  chain.proceed(request);

        // Bomb服务器没有设置Cache-Control头字段，我们需要自己设置
        // 实际开发中，服务器会返回合适的Cache-Control的值，不需要做这一步
        response = response.newBuilder().header("Cache-Control", "public, max-age=" + 60).build();

        if (response.cacheResponse() != null && response.networkResponse() == null) {
            Timber.w("Use cached response!");
        } else if (response.cacheResponse() != null && response.networkResponse() != null){
            Timber.w("Cache will not be used!");
        } else {
            Timber.w("No Cache available!");
        }

        return response;
    }
}
