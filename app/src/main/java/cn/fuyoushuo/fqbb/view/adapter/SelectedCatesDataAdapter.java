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
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.domain.entity.FCateItem;
import cn.fuyoushuo.fqbb.domain.entity.TbCateVo;
import rx.functions.Action1;

/**
 * Created by QA on 2016/6/27.
 */
public class SelectedCatesDataAdapter extends BaseListAdapter<TbCateVo>{

    private OnCateClick onCateClick;

    //当前所在的位置
    private int currentPosition = 0;

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setOnCateClick(OnCateClick onCateClick){
        this.onCateClick = onCateClick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_recycle_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder,position);
        final ItemViewHolder currentHolder = (ItemViewHolder) holder;
        final TbCateVo item = getItem(position);
        RxView.clicks(currentHolder.itemView).throttleFirst(1000,TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                onCateClick.onClick(currentHolder.itemView, item, currentPosition);
                currentPosition = position;
            }
        });
        currentHolder.cateName.setText(item.getCateName());
        if(item.isRed()){
            currentHolder.cateName.setTextColor(MyApplication.getContext().getResources().getColor(R.color.module_11));
        }else{
            currentHolder.cateName.setTextColor(MyApplication.getContext().getResources().getColor(R.color.darkBackground));
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.top_recycle_item_text) public TextView cateName;

        public ItemViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(ItemViewHolder.this,itemView);
        }
    }

    public interface OnCateClick {
        void onClick(View view, TbCateVo cateItem, int lastPosition);
    }

}
