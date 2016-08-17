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
import feicuiedu.com.videonews.bombapi.model.entity.UserEntity;
import feicuiedu.com.videonews.bombapi.model.result.UserResult;
import feicuiedu.com.videonews.commons.ToastUtils;
import feicuiedu.com.videonews.ui.base.BaseDialogFragment;
import retrofit2.Response;

/**
 * 注册对话框。
 * <p>
 * 使用{@link OnRegisterSuccessListener}和其它组件通信。
 * <p>
 * 使用前必须调用{@link #setListener(OnRegisterSuccessListener)}方法。
 */
public class RegisterFragment extends BaseDialogFragment {

    @BindView(R.id.etUsername) EditText etUsername;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.btnRegister) Button btnRegister;

    private String username;

    private final BombCallback<UserResult> registerCallback = new BombCallback<UserResult>() {
        @Override public void success(Response<UserResult> response) {
            btnRegister.setVisibility(View.VISIBLE);
            ToastUtils.showShort(R.string.register_success);
            listener.registerSuccess(username, response.body().getObjectId());
        }

        @Override public void businessError(BombHttpException e) {
            btnRegister.setVisibility(View.VISIBLE);
            ToastUtils.showShort(e.getErrorResult().getError());
        }

        @Override public void networkError(IOException e) {
            btnRegister.setVisibility(View.VISIBLE);
            ToastUtils.showShort(R.string.error_network);
        }

        @Override public void unexpectedError(Throwable t) {
            btnRegister.setVisibility(View.VISIBLE);
            ToastUtils.showShort(t.getMessage());
        }
    };

    private OnRegisterSuccessListener listener;

    @Override protected int getLayoutId() {
        return R.layout.dialog_register;
    }

    @OnClick(R.id.btnRegister)
    public void register() {
        username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        // 用户名或密码不能为空
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            ToastUtils.showShort(R.string.username_or_password_can_not_be_null);
            return;
        }

        // 隐藏按钮，避免按钮重复点击，此时显示的是按钮下方的进度条。
        btnRegister.setVisibility(View.GONE);

        UserEntity userEntity = new UserEntity();
        userEntity.setPassword(password);
        userEntity.setUsername(username);
        BombClient.getInstance().getUserApi().register(userEntity).enqueue(registerCallback);
    }

    public void setListener(@NonNull OnRegisterSuccessListener listener) {
        this.listener = listener;
    }

    public interface OnRegisterSuccessListener {
        void registerSuccess(String username, String objectId);
    }
}
