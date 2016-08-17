package feicuiedu.com.videonews.ui.base;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;

/**
 * 列表中单项的视图
 *
 * @param <Model> 该视图对应的实体类型
 */
public abstract class PagerItemView<Model> extends FrameLayout {


    public PagerItemView(Context context) {
        super(context);
    }

    /**
     * 将实体数据绑定到视图上
     *
     * @see PagerResourceAdapter#onBindViewHolder(RecyclerView.ViewHolder, int)
     */
    public abstract void bindModel(Model model);
}
