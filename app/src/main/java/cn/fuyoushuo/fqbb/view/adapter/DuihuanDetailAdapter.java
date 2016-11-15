package cn.fuyoushuo.fqbb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.domain.entity.DuihuanDetail;
import cn.fuyoushuo.fqbb.domain.entity.DuihuanItem;

/**
 * Created by QA on 2016/6/27.
 */
public class DuihuanDetailAdapter extends BaseListAdapter<DuihuanDetail>{

    private int currentPage = 1;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_jifen_detail_item, parent, false);
        return new ItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final ItemViewHolder currentHolder = (ItemViewHolder) holder;
        final DuihuanDetail item = getItem(position);

    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.dh_detail_item_date_string)
        TextView dateString;

        @Bind(R.id.dh_detail_item_time_string)
        TextView timeString;

        @Bind(R.id.dh_detail_order_state)
        TextView orderState;

        @Bind(R.id.dh_detail_order_num)
        TextView orderNum;

        @Bind(R.id.dh_detail_order_type)
        TextView orderType;

        @Bind(R.id.dh_detail_points_changed_text)
        TextView pointsChangedText;

        @Bind(R.id.dh_detail_points_changed_detail)
        TextView pointsChangedDetail;

        public ItemViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
