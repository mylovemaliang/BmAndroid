package cn.fuyoushuo.fqbb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import rx.functions.Action1;

/**
 * Created by QA on 2016/6/27.
 */
public class SearchMenuAdapter extends BaseListAdapter<SearchMenuAdapter.RowItem>{

    public static List<RowItem> SearchRowItems;

    public static List<RowItem> pointsOrderSearchItems;


    //执行初始化内容
    static{
        SearchRowItems = new ArrayList<>();
        SearchRowItems.add(new RowItem("superfan","超级返"));
        SearchRowItems.add(new RowItem("commonfan","返利搜索"));
        SearchRowItems.add(new RowItem("taobao","淘宝"));

        pointsOrderSearchItems = new ArrayList<>();
        pointsOrderSearchItems.add(new RowItem("","全部状态"));
        pointsOrderSearchItems.add(new RowItem("1","有效"));
        pointsOrderSearchItems.add(new RowItem("2","失效"));
        pointsOrderSearchItems.add(new RowItem("3","已完成"));
        pointsOrderSearchItems.add(new RowItem("4","已结算佣金"));
    }

    private OnRowClick onRowClick;

    public void setOnRowClick(OnRowClick onRowClick){
        this.onRowClick = onRowClick;
    }

    public List<RowItem> getNcRowItems(String currentRow){
        List<RowItem> rows = new ArrayList<RowItem>();
        for(RowItem item : SearchRowItems){
            if(!currentRow.equals(item.getSortCode())){
                rows.add(item);
            }
        }
        rows.add(new RowItem("cancel","取消"));
        return rows;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_type_menu_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder,position);
        final ItemViewHolder currentHolder = (ItemViewHolder) holder;
        final RowItem item = getItem(position);

        RxView.touches(currentHolder.itemView).subscribe(new Action1<MotionEvent>() {
            @Override
            public void call(MotionEvent motionEvent) {
                 if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    currentHolder.itemView.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.grayBackground));
                 }
                 else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                     currentHolder.itemView.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.white));
                     onRowClick.onClick(currentHolder.itemView,item);
                 }
            }
        });
        currentHolder.leftItemText.setText(item.getRowDesc());
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.searchMenuItemText) TextView leftItemText;

        public ItemViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(ItemViewHolder.this,itemView);
        }
    }

    public interface OnRowClick {
        void onClick(View view, RowItem rowItem);
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
