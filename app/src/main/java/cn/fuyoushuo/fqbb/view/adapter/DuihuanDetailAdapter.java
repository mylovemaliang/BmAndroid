package cn.fuyoushuo.fqbb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        //head
        Long bizTime = item.getBizTime();
        if(bizTime != null){
            String dateTime = String.valueOf(bizTime);
            try {
                Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(dateTime);
                String dateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                currentHolder.dateString.setText(dateTimeString);
            } catch (ParseException e) {
                currentHolder.dateString.setText("");
            }
        }
        //middle
        String pfSource = item.getPfSource();
        if(!TextUtils.isEmpty(pfSource)){
            currentHolder.orderDetail.setText(pfSource);
        }else{
            currentHolder.orderDetail.setText("");
        }
        //bottom
        String pointTypeStr = item.getPointTypeStr();
        if(!TextUtils.isEmpty(pointTypeStr)){
            currentHolder.orderType.setText(pointTypeStr);
        }else{
            currentHolder.orderType.setText("");
        }

        String increaseStr = item.getIncreaseStr();
        Integer increase = item.getIncrease();
        if(!TextUtils.isEmpty(increaseStr)){
            currentHolder.pointsChangedText.setText(increaseStr);
            if (increase == 1){
              currentHolder.pointsChangedText.setTextColor(MyApplication.getContext().getResources().getColor(R.color.module_7));
            }else{
              currentHolder.pointsChangedText.setTextColor(MyApplication.getContext().getResources().getColor(R.color.module_11));
            }
        }else{
            currentHolder.orderType.setText("");
        }

        Long prePoint = item.getPrePoint();
        Long afterPoint = item.getAfterPoint();
        if(prePoint!=null && afterPoint !=null){
            currentHolder.pointsChangedDetail.setText(String.valueOf(prePoint)+"-->"+String.valueOf(afterPoint));
        }else{
            currentHolder.pointsChangedDetail.setText("");
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.dh_detail_item_date_string)
        TextView dateString;

        @Bind(R.id.dh_detail_order_detail)
        TextView orderDetail;

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
