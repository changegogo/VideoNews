package feicuiedu.com.videonews.ui.likes;


import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import feicuiedu.com.videonews.R;
import feicuiedu.com.videonews.bombapi.BombClient;
import feicuiedu.com.videonews.bombapi.bombcall.BombCallback;
import feicuiedu.com.videonews.bombapi.bombcall.exception.BombHttpException;
import feicuiedu.com.videonews.bombapi.model.result.UserResult;
import feicuiedu.com.videonews.commons.ToastUtils;
import feicuiedu.com.videonews.ui.base.BaseDialogFragment;
import retrofit2.Response;

/**
 * 登陆对话框。
 * <p>
 * 使用{@link OnLoginSuccessListener}和其它组件通信。
 * <p>
 * 使用前必须调用{@link #setListener(OnLoginSuccessListener)}方法。
 */
public class LoginFragment extends BaseDialogFragment {

    @BindView(R.id.etUsername) EditText etUsername;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.btnLogin) Button btnLogin;

    private OnLoginSuccessListener listener;
    private String username;

    private final BombCallback<UserResult> loginCallback = new BombCallback<UserResult>() {
        @Override public void success(Response<UserResult> response) {
            btnLogin.setVisibility(View.VISIBLE);
            ToastUtils.showShort(R.string.login_success);
            listener.loginSuccess(username, response.body().getObjectId());
        }

        @Override public void businessError(BombHttpException e) {
            btnLogin.setVisibility(View.VISIBLE);
            ToastUtils.showShort(e.getErrorResult().getError());
        }

        @Override public void networkError(IOException e) {
            btnLogin.setVisibility(View.VISIBLE);
            ToastUtils.showShort(R.string.error_network);
        }

        @Override public void unexpectedError(Throwable t) {
            btnLogin.setVisibility(View.VISIBLE);
            ToastUtils.showShort(t.getMessage());
        }
    };

    @Override protected int getLayoutId() {
        return R.layout.dialog_login;
    }

    @OnClick(R.id.btnLogin)
    public void login() {
        username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        // 用户名或密码不能为空
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            ToastUtils.showShort(R.string.username_or_password_can_not_be_null);
            return;
        }

        // 隐藏按钮，避免按钮重复点击，此时显示的是按钮下方的进度条。
        btnLogin.setVisibility(View.GONE);

        BombClient.getInstance().getUserApi().login(username, password).enqueue(loginCallback);
    }

    public void setListener(@NonNull OnLoginSuccessListener listener) {
        this.listener = listener;
    }

    public interface OnLoginSuccessListener {
        void loginSuccess(String username, String objectId);
    }
}
