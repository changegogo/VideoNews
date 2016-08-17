package feicuiedu.com.videonews.ui.base;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.mugen.Mugen;
import com.mugen.MugenCallbacks;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import feicuiedu.com.videonews.R;
import feicuiedu.com.videonews.bombapi.BombClient;
import feicuiedu.com.videonews.bombapi.NewsApi;
import feicuiedu.com.videonews.bombapi.bombcall.BombCall;
import feicuiedu.com.videonews.bombapi.bombcall.BombCallback;
import feicuiedu.com.videonews.bombapi.bombcall.exception.BombHttpException;
import feicuiedu.com.videonews.bombapi.model.result.QueryResult;
import feicuiedu.com.videonews.commons.ToastUtils;
import retrofit2.Response;
import timber.log.Timber;

/**
 * 带下拉刷新和分页加载功能的自定义视图
 * <p/>
 * 下拉刷新使用{@link SwipeRefreshLayout}实现
 * <p/>
 * 分页加载使用{@link Mugen} + ProgressBar实现。
 */
public abstract class PagerResourceView<Model, ItemView extends PagerItemView<Model>> extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener,
        MugenCallbacks {

    @BindView(R.id.recyclerView) protected RecyclerView recyclerView;
    @BindView(R.id.refreshLayout) protected SwipeRefreshLayout refreshLayout;
    @BindView(R.id.progressBar) protected ProgressBar progressBar;

    // 是否已加载全部数据
    private boolean loadAll = false;

    // 加载数据时要跳过多少条，用于分页
    private int skip = 0;

    protected NewsApi newsApi;

    protected PagerResourceAdapter<Model, ItemView> adapter;

    public PagerResourceView(Context context) {
        this(context, null);
    }

    public PagerResourceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerResourceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 自动刷新，常用于首次进入页面
     */
    public final void autoRefresh() {
        refreshLayout.setRefreshing(true);
        onRefresh();
    }


    /**
     * 继承自{@link SwipeRefreshLayout.OnRefreshListener}。
     * <p/>
     * 当用户下拉到足够距离，松手后自动调用。
     * <p/>
     * 在{@link #autoRefresh()}中主动调用。
     */
    @Override public final void onRefresh() {
        BombCall<QueryResult<Model>> call = queryData(getLimit(), 0);

        // 返回null说明查询条件未满足，例如未登陆情况下查询收藏列表
        if (call == null) {
            refreshLayout.setRefreshing(false);
            return;
        }

        call.enqueue(new BombCallback<QueryResult<Model>>() {
            @Override public void success(Response<QueryResult<Model>> response) {
                refreshLayout.setRefreshing(false);
                List<Model> data = response.body().getResults();

                // 如果数据项数小于limit参数，说明全部数据都已加载
                loadAll = data.size() < getLimit();
                skip = data.size();

                adapter.clear();
                adapter.addData(response.body().getResults());
            }

            // 业务异常
            @Override public void businessError(BombHttpException e) {
                Timber.e(e, "onRefresh businessError");
                refreshLayout.setRefreshing(false);
                ToastUtils.showShort(e.getErrorResult().getError());
            }

            // 网络异常
            @Override public void networkError(IOException e) {
                Timber.e(e, "onRefresh networkError");
                refreshLayout.setRefreshing(false);
                ToastUtils.showShort(R.string.error_network);
            }

            // 未知异常
            @Override public void unexpectedError(Throwable t) {
                Timber.e(t, "onRefresh unexpectedError");
                refreshLayout.setRefreshing(false);
                ToastUtils.showShort(t.getMessage());
            }
        });
    }

    /**
     * 继承自{@link MugenCallbacks}，用于判断是否正在加载。
     * <p/>
     * 当此方法返回true时，{@link #onLoadMore()}不会被触发。
     */
    @Override public final boolean isLoading() {
        return progressBar.getVisibility() == View.VISIBLE;
    }

    /**
     * 继承自{@link MugenCallbacks}，用于判断是否已加载全部数据。
     * <p/>
     * 当此方法返回true时，{@link #onLoadMore()}不会被触发。
     */
    @Override public final boolean hasLoadedAllItems() {
        return loadAll;
    }

    /**
     * 继承自{@link MugenCallbacks}，当RecyclerView快滚动到底部时，此方法会自动触发，来加载下一页数据。
     * <p/>
     * 注意如果列表中数据太少，无法滚动时，此方法无法被处罚。
     */
    @Override public final void onLoadMore() {
        BombCall<QueryResult<Model>> call = queryData(getLimit(), skip);
        if (call == null) {
            return;
        }


        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new BombCallback<QueryResult<Model>>() {
            @Override public void success(Response<QueryResult<Model>> response) {
                progressBar.setVisibility(View.GONE);
                List<Model> data = response.body().getResults();

                // 如果数据项数小于limit参数，说明全部数据都已加载
                loadAll = data.size() < getLimit();
                skip += data.size();

                adapter.addData(response.body().getResults());
            }

            // 业务异常
            @Override public void businessError(BombHttpException e) {
                Timber.e(e, "onLoadMore businessError");
                progressBar.setVisibility(View.GONE);
                ToastUtils.showShort(e.getErrorResult().getError());
            }

            // 网络异常
            @Override public void networkError(IOException e) {
                Timber.e(e, "onLoadMore networkError");
                progressBar.setVisibility(View.GONE);
                ToastUtils.showShort(R.string.error_network);
            }

            // 未知异常
            @Override public void unexpectedError(Throwable t) {
                Timber.e(t, "onLoadMore unexpectedError");
                progressBar.setVisibility(View.GONE);
                ToastUtils.showShort(t.getMessage());
            }
        });
    }

    /**
     * @return 新的单项视图
     */
    protected abstract ItemView createItemView();

    /**
     * @return 每一页有多少条，子类需要返回固定的值
     */
    protected abstract int getLimit();

    /**
     * 从服务器查询数据
     *
     * @param limit 要查询多少条
     * @param skip  要跳过多少条
     * @return {@link BombCall}
     */
    protected abstract BombCall<QueryResult<Model>> queryData(int limit, int skip);


    // 视图的初始化，只在构造方法中使用一次
    private void init() {
        newsApi = BombClient.getInstance().getNewsApi();

        // 填充和绑定视图
        LayoutInflater.from(getContext()).inflate(R.layout.partial_pager_resource, this, true);
        ButterKnife.bind(this);

        // 初始化RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PagerResourceAdapter<Model, ItemView>() {
            @Override public ItemView onCreateItemView() {
                return createItemView();
            }
        };
        recyclerView.setAdapter(adapter);

        // 配置下拉刷新
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);

        // 配置分页加载
        Mugen.with(recyclerView, this).start();
    }
}
