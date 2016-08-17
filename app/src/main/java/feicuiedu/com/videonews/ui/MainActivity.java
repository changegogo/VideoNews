package feicuiedu.com.videonews.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import feicuiedu.com.videonews.R;
import feicuiedu.com.videonews.ui.likes.LikesFragment;
import feicuiedu.com.videonews.ui.local.LocalVideoFragment;
import feicuiedu.com.videonews.ui.news.NewsFragment;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.btnNews) Button btnNews;
    @BindView(R.id.btnLocal) Button btnLocal;
    @BindView(R.id.btnLikes) Button btnLikes;

    private final FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new NewsFragment();
                case 1:
                    return new LocalVideoFragment();
                case 2:
                    return new LikesFragment();
                default:
                    throw new RuntimeException();
            }
        }

        @Override public int getCount() {
            return 3;
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override public void onContentChanged() {
        super.onContentChanged();

        ButterKnife.bind(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        btnNews.setSelected(true);
    }

    // 下方按钮的点击事件
    @OnClick({R.id.btnNews, R.id.btnLocal, R.id.btnLikes})
    public void chooseFragment(View view){
        // 参数false代表瞬间切换，而不是平滑过渡
        switch (view.getId()){
            case R.id.btnNews:
                viewPager.setCurrentItem(0, false);
                return;
            case R.id.btnLocal:
                viewPager.setCurrentItem(1, false);
                return;
            case R.id.btnLikes:
                viewPager.setCurrentItem(2, false);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override public void onPageSelected(int position) {
        // ViewPager页面变化时设置下方按钮的选中状态
        btnNews.setSelected(position == 0);
        btnLocal.setSelected(position == 1);
        btnLikes.setSelected(position == 2);
    }

    @Override public void onPageScrollStateChanged(int state) {
    }
}
