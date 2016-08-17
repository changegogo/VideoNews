package feicuiedu.com.videonews.ui.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 对话框的基类
 */
public abstract class BaseDialogFragment extends DialogFragment {

    private Unbinder unbinder;

    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 设置对话框无标题栏
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // View和Fragment的绑定
        unbinder = ButterKnife.bind(this, view);
    }

    @Override public final void onDestroyView() {
        super.onDestroyView();
        // View和Fragment解绑定
        unbinder.unbind();
    }

    /**
     * @return 此Fragment使用的布局文件Id
     */
    @LayoutRes
    protected abstract int getLayoutId();
}
