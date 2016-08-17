package feicuiedu.com.videonews.bombapi.bombcall;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;

import feicuiedu.com.videonews.bombapi.bombcall.exception.BombHttpException;
import feicuiedu.com.videonews.bombapi.model.result.ErrorResult;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 自定义 {@link retrofit2.CallAdapter.Factory}, 将内置的 {@link Call} 适配成自定义版本的{@link BombCall}，
 * 提供更多的回调方法，统一处理异常和错误。
 *
 * @see <a href="http://docs.bmob.cn/data/Restful/g_errorcode/doc/index.html#RESTAPI错误码列表">
 * RESTAPI错误码列表</a>
 */
public class BombCallAdapterFactory extends CallAdapter.Factory {
    @Override public CallAdapter<BombCall<?>> get(Type returnType, Annotation[] annotations,
                                                  final Retrofit retrofit) {

        // 判断returnType的原始类型是否是BombClass
        // 例如List<String>的原始类型是List.class
        if (getRawType(returnType) != BombCall.class) {
            return null;
        }

        // BombClass必须定义了泛型参数
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalStateException(
                    "BombCall must have generic type (e.g., BombCall<ResponseBody>)");
        }

        // 获取泛型参数的上限
        // 例如List<? extends Runnable>会返回Runnable类型。
        final Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);

        // 如果在非Android环境下运行，此方法会返回null
        // 这就是为什么在Android下运行，Retrofit的回调发生在主线程；
        // 而在非Android环境下运行，回调发生在执行网络操作的后台线程。
        final Executor callbackExecutor = retrofit.callbackExecutor();

        return new CallAdapter<BombCall<?>>() {

            // BombCall<?> 中，问号的具体类型
            @Override public Type responseType() {
                return responseType;
            }

            // 将原生的Call适配成我们需要的BombCall
            @Override public <R> BombCall<R> adapt(Call<R> call) {
                return new BombCallAdapter<>(call, callbackExecutor, retrofit);
            }
        };
    }

    /**
     * 将 {@link Call} 适配成 {@link BombCall}.
     */
    static class BombCallAdapter<T> implements BombCall<T> {
        private final Call<T> call;
        private final Executor callbackExecutor;
        private final Retrofit retrofit;

        BombCallAdapter(Call<T> call, Executor callbackExecutor, Retrofit retrofit) {
            this.call = call;
            this.callbackExecutor = callbackExecutor;
            this.retrofit = retrofit;
        }

        @Override public void cancel() {
            call.cancel();
        }

        @Override public void enqueue(final BombCallback<T> callback) {
            call.enqueue(new Callback<T>() {
                @Override public void onResponse(final Call<T> call, final Response<T> response) {
                    if (callbackExecutor != null) {
                        callbackExecutor.execute(new Runnable() {
                            @Override public void run() {
                                handleResponse(call, response);
                            }
                        });
                    } else {
                        handleResponse(call, response);
                    }

                }

                @Override public void onFailure(Call<T> call, final Throwable t) {
                    if (callbackExecutor != null) {
                        callbackExecutor.execute(new Runnable() {
                            @Override public void run() {
                                handleFailure(t);
                            }
                        });
                    } else {
                        handleFailure(t);
                    }
                }


                // 按照响应码类型区分是成功、Bomb异常还是未知异常
                private void handleResponse(Call<T> call, Response<T> response) {
                    try {

                        int code = response.code();
                        BombHttpException.ErrorType errorType = BombHttpException.ErrorType.valueOf(code);

                        if (code >= 200 && code < 300) {
                            callback.success(response);
                        } else if (errorType != null) {
                            ErrorResult errorResult = toBombError(response);
                            callback.businessError(new BombHttpException(errorResult, errorType));
                        } else {
                            callback.unexpectedError(new RuntimeException("Unexpected response " + response.code()));
                        }
                    } catch (IOException e) {
                        onFailure(call, e);
                    }
                }

                // 按照异常类型区分是网络错误还是未知错误
                private void handleFailure(Throwable t) {
                    if (t instanceof IOException) {
                        callback.networkError((IOException) t);
                    } else {
                        callback.unexpectedError(t);
                    }
                }
            });
        }

        @SuppressWarnings("CloneDoesntCallSuperClone")
        @Override public BombCall<T> clone() {
            return new BombCallAdapter<>(call.clone(), callbackExecutor, retrofit);
        }

        @Override public Response<T> execute() throws BombHttpException, IOException {

            Response<T> response = call.execute();

            int code = response.code();
            BombHttpException.ErrorType errorType = BombHttpException.ErrorType.valueOf(code);

            if (code >= 200 && code < 300) {
                return response;
            } else if (errorType != null) {
                throw new BombHttpException(toBombError(response), errorType);
            } else {
                throw new RuntimeException("Unexpected response " + response.code());
            }
        }

        // 将响应体转换成错误实体类
        private ErrorResult toBombError(Response<T> response) throws IOException {
            Converter<ResponseBody, ErrorResult> errorConverter = retrofit.responseBodyConverter(ErrorResult.class, new Annotation[0]);
            ErrorResult errorResult = errorConverter.convert(response.errorBody());

            if (errorResult == null) {
                throw new RuntimeException("Fail to convert error body to ErrorResult!");
            }

            return errorResult;
        }
    }
}
