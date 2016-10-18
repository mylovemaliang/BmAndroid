package cn.fuyoushuo.fqbb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.domain.entity.CateItem;
import rx.functions.Action1;

/**
 * Created by QA on 2016/6/27.
 */
public class SearchLeftRviewAdapter extends BaseListAdapter<SearchLeftRviewAdapter.RowItem>{

    //淘宝搜索
    public static List<RowItem> rowItemsForTaobaoSearch;
    //普通搜索
    public static List<RowItem> rowItemsForCommonfanSearch;
    //超级返搜索
    public static List<RowItem> rowItemsForSuperfanSearch;

    //执行初始化内容
    static{
        //初始化淘宝搜索内容
        rowItemsForTaobaoSearch = new ArrayList<>();
        rowItemsForTaobaoSearch.add(new RowItem("","综合排序"));
        rowItemsForTaobaoSearch.add(new RowItem("bid","价格从低到高"));
        rowItemsForTaobaoSearch.add(new RowItem("_bid","价格从高到低"));
        rowItemsForTaobaoSearch.add(new RowItem("_ratesum","信用排序"));
        rowItemsForTaobaoSearch.add(new RowItem("_sale","销量从高到低"));
        //初始化普通返利搜索内容
        rowItemsForCommonfanSearch = new ArrayList<>();
        rowItemsForCommonfanSearch.add(new RowItem("","综合排序"));
        rowItemsForCommonfanSearch.add(new RowItem("1","返利比例从高到低"));
        rowItemsForCommonfanSearch.add(new RowItem("4","价格从低到高"));
        rowItemsForCommonfanSearch.add(new RowItem("3","价格从高到低"));
        rowItemsForCommonfanSearch.add(new RowItem("9","销量从高到低"));
        //初始化超级返利搜索内容
        rowItemsForSuperfanSearch = new ArrayList<>();
        rowItemsForSuperfanSearch.add(new RowItem("","综合排序"));
        rowItemsForSuperfanSearch.add(new RowItem("1","返利比例从高到低"));
        rowItemsForSuperfanSearch.add(new RowItem("4","价格从低到高"));
        rowItemsForSuperfanSearch.add(new RowItem("3","价格从高到低"));
        rowItemsForSuperfanSearch.add(new RowItem("9","销量从高到低"));
    }

    private OnRowClick onRowClick;

    public void setOnRowClick(OnRowClick onRowClick){
        this.onRowClick = onRowClick;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flagment_search_left_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder,position);
        final ItemViewHolder currentHolder = (ItemViewHolder) holder;
        final RowItem item = getItem(position);
        RxView.clicks(currentHolder.itemView).throttleFirst(1000,TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                onRowClick.onClick(currentHolder.itemView,item);
            }
        });
        currentHolder.leftItemText.setText(item.getRowDesc());
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.flagmentSearchLeftItemText) TextView leftItemText;

        public ItemViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(ItemViewHolder.this,itemView);
        }
    }

    public interface OnRowClick {
        void onClick(View view,RowItem rowItem);
    }

    public static class RowItem{

        private String rowDesc;

        private String sortCode;

        public RowItem(String sortCode, String rowDesc) {
            this.sortCode = sortCode;
            this.rowDesc = rowDesc;
        }

        public String getRowDesc() {
            return rowDesc;
        }

        public void setRowDesc(String rowDesc) {
            this.rowDesc = rowDesc;
        }

        public String getSortCode() {
            return sortCode;
        }

        public void setSortCode(String sortCode) {
            this.sortCode = sortCode;
        }
    }

}
