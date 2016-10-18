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
import cn.fuyoushuo.fqbb.domain.entity.CateItem;
import cn.fuyoushuo.fqbb.domain.entity.FCateItem;
import rx.functions.Action1;

/**
 * Created by QA on 2016/6/27.
 */
public class FcatesDataAdapter extends BaseListAdapter<FCateItem>{

    private OnCateClick onCateClick;

    public void setOnCateClick(OnCateClick onCateClick){
        this.onCateClick = onCateClick;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myorder_item_cate, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder,position);
        final ItemViewHolder currentHolder = (ItemViewHolder) holder;
        final FCateItem item = getItem(position);
        RxView.clicks(currentHolder.itemView).throttleFirst(1000,TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                onCateClick.onClick(currentHolder.itemView,item);
            }
        });
        currentHolder.cateName.setText(item.getName());
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.myorder_item_cate_text) TextView cateName;

        private FCateItem cateItem;

        public FCateItem getCateItem() {
            return cateItem;
        }

        public void setCateItem(FCateItem cateItem) {
            this.cateItem = cateItem;
        }

        public ItemViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(ItemViewHolder.this,itemView);
        }
    }

    public interface OnCateClick {
        void onClick(View view, FCateItem cateItem);
    }

}
