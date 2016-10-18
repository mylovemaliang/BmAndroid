package cn.fuyoushuo.fqbb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.domain.entity.FGoodItem;
import cn.fuyoushuo.fqbb.domain.entity.GoodItem;
import rx.functions.Action1;

/**
 * Created by QA on 2016/6/27.
 */
public class GoodDataAdapter extends BaseListAdapter<FGoodItem>{

    public static int ITEM_VIEW_TYPE_HEADER = 1;

    public static int ITEM_VIEW_TYPE_DATA = 2;

    private int currentPage = 1;

    private Long cateId;

    private final View headView;

    public Long getCateId() {
        return cateId;
    }

    public void setCateId(Long cateId) {
        this.cateId = cateId;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    private OnLoad onLoad;

    public void setOnLoadImage(OnLoad onLoad){
        this.onLoad = onLoad;
    }

    public GoodDataAdapter(View headView) {
        if(headView == null){
            throw new IllegalArgumentException("header may not be null");
        }
        this.headView = headView;
    }

    public boolean isHeader(int position){
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ITEM_VIEW_TYPE_HEADER){
            return new ItemViewHolder(headView,ITEM_VIEW_TYPE_HEADER);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_recycle_item, parent, false);
        return new ItemViewHolder(view,ITEM_VIEW_TYPE_DATA);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(isHeader(position)){
            return;
        }
        int realPosition = position-1;
        super.onBindViewHolder(holder, realPosition);
        final ItemViewHolder currentHolder = (ItemViewHolder) holder;
        final FGoodItem item = getItem(realPosition);
        currentHolder.titleView.setText(item.getWebSmallTitle());
        currentHolder.originPrice.setText("￥"+item.getPriceYuan());
        currentHolder.sellCount.setText("已售"+item.getSoldCount());
        currentHolder.disCount.setText("返"+item.getFanliPercent());
        currentHolder.priceSaved.setText("约"+item.getFanliYuan()+"元");
        onLoad.onLoadImage(currentHolder.imageView,item);
        RxView.clicks(currentHolder.itemView).throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                    onLoad.onGoodItemClick(currentHolder.itemView,item);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_VIEW_TYPE_HEADER;
        }
        return ITEM_VIEW_TYPE_DATA;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount()+1;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.bottom_item_good_image) SimpleDraweeView imageView;

        @Bind(R.id.bottom_item_good_titletext) TextView titleView;

        @Bind(R.id.bottom_item_good_originprice) TextView originPrice;

        @Bind(R.id.bottom_item_good_sellcount) TextView sellCount;

        @Bind(R.id.bottom_item_good_discount) TextView disCount;

        @Bind(R.id.bottom_item_good_pricesaved) TextView priceSaved;

        public ItemViewHolder(final View itemView, int itemType){
            super(itemView);
            if(ITEM_VIEW_TYPE_DATA == itemType){
              ButterKnife.bind(this,itemView);
            }
        }
    }

    public interface OnLoad{
        void onLoadImage(SimpleDraweeView view, FGoodItem goodItem);

        void onGoodItemClick(View clickView,FGoodItem goodItem);
    }
}
