package feicuiedu.com.videonews.bombapi;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import feicuiedu.com.videonews.bombapi.bombcall.BombCallAdapterFactory;
import feicuiedu.com.videonews.commons.ContextUtils;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class BombClient {

    private static final String BOMB_CACHE = "bomb-cache";

    private static BombClient sInstance;

    public static synchronized BombClient getInstance() {
        if (sInstance == null) {
            sInstance = new BombClient();
        }

        return sInstance;
    }

    private final Retrofit retrofit;

    private UserApi userApi;

    private NewsApi newsApi;

    private BombClient() {

        // 日志拦截器
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override public void log(String message) {
                Timber.i(message);
            }
        });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 设置缓存
        File cacheFile = ContextUtils.getCacheFile(BOMB_CACHE);
        Timber.d("cacheFile: %s", cacheFile.getAbsolutePath());
        Cache cache =  new Cache(cacheFile, 1024 * 1024 * 100); // 100Mb

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(new CacheInterceptor()) // 处理缓存的拦截器
                .addInterceptor(new BombInterceptor()) // 统一处理Bomb必要的头字段的拦截器
                .addInterceptor(httpLoggingInterceptor) // 打印日志的拦截器
                .addNetworkInterceptor(new CacheInterceptor()) // 网络拦截器是为了在有网的情况下也能使用缓存
                .build();

        // 让Gson能将Bomb返回的时间戳自动转成Date对象
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))  // Gson转换器
                .addCallAdapterFactory(new BombCallAdapterFactory()) // Call适配器
                .baseUrl(BombConst.BASE_URL)
                .build();

    }


    public UserApi getUserApi() {
        if (userApi == null) {
            userApi = retrofit.create(UserApi.class);
        }

        return userApi;
    }

    public NewsApi getNewsApi() {
        if (newsApi == null) {
            newsApi = retrofit.create(NewsApi.class);
        }

        return newsApi;
    }


}
