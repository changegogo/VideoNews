package feicuiedu.com.videonews.bombapi;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import feicuiedu.com.videonews.bombapi.bombcall.exception.BombHttpException;
import feicuiedu.com.videonews.bombapi.model.entity.UserEntity;
import feicuiedu.com.videonews.bombapi.model.result.UserResult;
import retrofit2.Response;
import timber.log.Timber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * 用户接口单元测试
 */
public class UserApiUnitTest {

    @BeforeClass
    public static void initTimber(){
        Timber.plant(new Timber.Tree() {
            @Override protected void log(int priority, String tag, String message, Throwable t) {
                System.out.println(tag + " : " + message);
                if (t != null) {
                    t.printStackTrace();
                }
            }
        });
    }

    private UserApi userApi;

    @Before
    public void init(){
        userApi = BombClient.getInstance().getUserApi();
    }

    @Test
    public void register() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setPassword("123456");
        userEntity.setUsername("YuanC");
        try {
            userApi.register(userEntity).execute();
        } catch (BombHttpException e) {
            assertEquals(e.getErrorType(), BombHttpException.ErrorType.INVALID_OPERATION);
            assertEquals(e.getErrorResult().getCode(), 202);
        }
    }

    @Test
    public void login() throws Exception {
        userApi.login("YuanC", "123456").enqueue(new TestBombCallback<UserResult>() {
            @Override public void success(Response<UserResult> response) {
                assertNotNull(response.body().getObjectId());
            }
        });
        Thread.sleep(1000);
    }
}
