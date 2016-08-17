package feicuiedu.com.videonews.ui.base;


import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 简单封装{@link RecyclerView.Adapter}，将视图和数据实体一一对应
 *
 * @param <Model> 列表中每一项对应的数据实体
 * @param <ItemView> 列表中每一项对应的视图
 */
public abstract class PagerResourceAdapter<Model, ItemView extends PagerItemView<Model>> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    // 适配器使用的数据集合
    private final LinkedList<Model> dataSet = new LinkedList<>();

    @Override public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemView itemView = onCreateItemView();
        itemView.setLayoutParams(new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        ));
        return new RecyclerView.ViewHolder(itemView) {};
    }

    @Override public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        @SuppressWarnings("unchecked") ItemView itemView = (ItemView) holder.itemView;
        itemView.bindModel(dataSet.get(position));
    }

    @Override public final int getItemCount() {
        return dataSet.size();
    }

    /**
     * 由子类负责实现，创建具体的单项视图
     */
    public abstract ItemView onCreateItemView();

    public final void clear(){
        dataSet.clear();
        notifyDataSetChanged();
    }

    public final void addData(Collection<Model> data){
        dataSet.addAll(data);
        notifyDataSetChanged();
    }
}
