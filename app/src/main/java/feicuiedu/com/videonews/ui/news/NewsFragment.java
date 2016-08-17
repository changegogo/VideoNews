package feicuiedu.com.videonews.ui.news;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import feicuiedu.com.videonews.R;
import feicuiedu.com.videoplayer.list.MediaPlayerManager;

/**
 * 新闻列表页面
 */
public class NewsFragment extends Fragment{


    @BindView(R.id.newsListView) NewsListView newsListView;

    private View view;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 重用视图
        if (view == null){
            view = inflater.inflate(R.layout.fragment_news, container, false);
            ButterKnife.bind(this, view);
            // 首次进入时自动刷新一次数据
            newsListView.post(new Runnable() {
                @Override public void run() {
                    newsListView.autoRefresh();
                }
            });
        }
        return view;
    }

    @Override public void onResume() {
        super.onResume();
        // 初始化MediaPlayer
        MediaPlayerManager.getInstance(getContext()).onResume();
    }

    @Override public void onPause() {
        super.onPause();
        // 释放MediaPlayer
        MediaPlayerManager.getInstance(getContext()).onPause();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup)view.getParent()).removeView(view);
    }

    @Override public void onDestroy() {
        super.onDestroy();
        // 释放所有监听器，避免内存溢出
        MediaPlayerManager.getInstance(getContext()).removeAllListeners();
    }
}
