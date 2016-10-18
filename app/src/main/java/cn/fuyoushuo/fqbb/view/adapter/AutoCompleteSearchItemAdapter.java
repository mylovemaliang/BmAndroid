package cn.fuyoushuo.fqbb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.R;
import rx.functions.Action1;

/**
 * @Project CommonProj
 * @Packate com.micky.commonproj.ui.adapter
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2016-01-08 14:13
 * @Version 1.0
 */
public class AutoCompleteSearchItemAdapter extends BaseListAdapter<String> {

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.auto_complete_search_item,parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        String searchText = getItem(position);
        itemViewHolder.searchText = searchText;
        itemViewHolder.itemText.setText(searchText);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.auto_complete_search_item_text) TextView itemText;

        String searchText;

        public ItemViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            RxView.clicks(itemView).throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
                @Override
                public void call(Void aVoid) {
                    mOnItemClickListener.onClick(itemView,searchText);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onClick(View view, String item);
    }
}
