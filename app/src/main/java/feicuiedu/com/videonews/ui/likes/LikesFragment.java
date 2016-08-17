package feicuiedu.com.videonews.ui.likes;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import feicuiedu.com.videonews.R;
import feicuiedu.com.videonews.ui.UserManager;

/**
 * 我的收藏页面
 */
public class LikesFragment extends Fragment implements
        LoginFragment.OnLoginSuccessListener, RegisterFragment.OnRegisterSuccessListener{

    private View view;

    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.btnRegister) Button btnRegister;
    @BindView(R.id.btnLogout) Button btnLogout;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.divider) View divider;
    @BindView(R.id.likesListView) LikesListView likesListView;

    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view =  inflater.inflate(R.layout.fragment_likes, container, false);
            ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup)view.getParent()).removeView(view);
    }

    // 显示登陆对话框
    @OnClick(R.id.btnLogin)
    public void showLoginDialog(){
        if (loginFragment == null) {
            loginFragment = new LoginFragment();
            loginFragment.setListener(this);
        }
        loginFragment.show(getChildFragmentManager(), "Login Dialog");
    }

    // 显示注册对话框
    @OnClick(R.id.btnRegister)
    public void showRegisterDialog(){
        if (registerFragment == null) {
            registerFragment = new RegisterFragment();
            registerFragment.setListener(this);
        }
        registerFragment.show(getChildFragmentManager(), "Register Dialog");
    }

    // 登出
    @OnClick(R.id.btnLogout)
    public void logout(){
        userOffline();
    }

    // 登陆成功的回调
    @Override public void loginSuccess(String username, String objectId) {
        loginFragment.dismiss();
        userOnline(username, objectId);
    }

    // 注册成功的回调
    @Override public void registerSuccess(String username, String objectId) {
        registerFragment.dismiss();
        userOnline(username, objectId);
    }

    // 用户上线
    private void userOnline(String username, String objectId){
        // 存储用户信息
        UserManager.getInstance().setUsername(username);
        UserManager.getInstance().setObjectId(objectId);

        // 更改UI状态
        btnLogout.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.INVISIBLE);
        btnRegister.setVisibility(View.INVISIBLE);
        divider.setVisibility(View.INVISIBLE);
        tvUsername.setText(username);

        // 刷新收藏列表
        likesListView.setUserId(objectId);
        likesListView.autoRefresh();
    }

    // 用户离线
    private void userOffline(){
        // 清除用户信息
        UserManager.getInstance().clear();

        // 更改UI状态
        btnLogout.setVisibility(View.INVISIBLE);
        btnLogin.setVisibility(View.VISIBLE);
        btnRegister.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);
        tvUsername.setText(R.string.tourist);

        // 清空收藏列表
        likesListView.clear();
    }
}
