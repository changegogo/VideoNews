package feicuiedu.com.videonews.bombapi.bombcall.exception;


import android.support.annotation.NonNull;

import feicuiedu.com.videonews.bombapi.model.result.ErrorResult;

/**
 * Bomb服务器已知的异常，文档见：
 * <p/>
 * <a href="http://docs.bmob.cn/data/Restful/g_errorcode/doc/index.html">RESTAPI错误码列表</a>
 */
public class BombHttpException extends Exception {

    private final ErrorType errorType;

    private final ErrorResult errorResult;

    public BombHttpException(@NonNull ErrorResult errorResult,
                             @NonNull ErrorType errorType) {
        this.errorType = errorType;
        this.errorResult = errorResult;
    }

    public @NonNull ErrorType getErrorType() {
        return errorType;
    }

    public @NonNull ErrorResult getErrorResult() {
        return errorResult;
    }


    /**
     * Bomb服务器定义的错误类型
     */
    public enum ErrorType {

        UNAUTHENTICATED, // 未授权(401)

        SERVER_IS_BUSY, // 服务器忙(500)

        OBJECT_NOT_FOUND, // 对象未找到(400)

        INVALID_OPERATION;// 无效的操作(404)

        /**
         * @param value HTTP响应码
         * @return 错误类型
         */
        public static ErrorType valueOf(int value) {
            switch (value) {
                case 401:
                    return UNAUTHENTICATED;
                case 500:
                    return SERVER_IS_BUSY;
                case 400:
                    return OBJECT_NOT_FOUND;
                case 404:
                    return INVALID_OPERATION;
                default:
                    return null;
            }
        }
    }
}
