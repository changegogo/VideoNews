package feicuiedu.com.videonews.bombapi;

import feicuiedu.com.videonews.bombapi.bombcall.BombCall;
import feicuiedu.com.videonews.bombapi.model.entity.UserEntity;
import feicuiedu.com.videonews.bombapi.model.result.UserResult;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Restful API，用户相关
 */
public interface UserApi {

    /**
     * 用户注册
     */
    @POST("1/users") BombCall<UserResult> register(@Body UserEntity userEntity);

    /**
     * 用户登录
     */
    @GET("1/login") BombCall<UserResult> login(@Query("username") String username, @Query("password") String password);
}
