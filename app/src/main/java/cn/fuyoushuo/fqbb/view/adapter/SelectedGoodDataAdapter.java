package cn.fuyoushuo.fqbb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.CommonUtils;
import cn.fuyoushuo.fqbb.commonlib.utils.DateUtils;
import cn.fuyoushuo.fqbb.domain.entity.FGoodItem;
import cn.fuyoushuo.fqbb.domain.entity.TaoBaoItemVo;
import rx.functions.Action1;

/**
 * Created by QA on 2016/6/27.
 */
public class SelectedGoodDataAdapter extends BaseListAdapter<TaoBaoItemVo>{

    private int currentPage = 1;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    private OnLoad onLoad;

    public void setOnLoad(OnLoad onLoad){
        this.onLoad = onLoad;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_item_good, parent, false);
        return new ItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final ItemViewHolder currentHolder = (ItemViewHolder) holder;
        final TaoBaoItemVo item = getItem(position);

        String realTitle = CommonUtils.getShortTitle(item.getTitle());
        currentHolder.titleView.setText(realTitle);
        currentHolder.orginPriceView.setText("￥"+item.getPrice());
        currentHolder.sellOutView.setText("已售"+item.getSold()+"件");
        onLoad.onLoadImage(currentHolder.imageView,item);

        if(null != item.getJfbCount()){
                if(item.getJfbCount() == 0){
                    currentHolder.discountView.setText("无返利信息");
                }else{
                    currentHolder.discountView.setText("返集分宝 "+item.getJfbCount());
                }
                currentHolder.pricesaveView.setVisibility(View.GONE);
            }else if(null != item.getTkRate()) {
                currentHolder.discountView.setText("返" + DateUtils.getFormatFloat(item.getTkRate()) + "%");
                currentHolder.pricesaveView.setVisibility(View.VISIBLE);
                currentHolder.pricesaveView.setText("约" + DateUtils.getFormatFloat(item.getTkCommFee()) + "元");
            }else{
                currentHolder.discountView.setText("无返利信息");
                currentHolder.pricesaveView.setVisibility(View.GONE);
        }


        RxView.clicks(currentHolder.itemView).throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                onLoad.onItemClick(currentHolder.itemView,item);
            }
        });

    }




    public class ItemViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.myorder_item_good_image) SimpleDraweeView imageView;

        @Bind(R.id.myorder_item_good_titletext) TextView titleView;

        @Bind(R.id.myorder_item_good_originprice) TextView orginPriceView;

        @Bind(R.id.myorder_item_good_sellcount) TextView sellOutView;

        @Bind(R.id.myorder_item_fanli_info) View fanliInfo;

        @Bind(R.id.myorder_item_good_discount) TextView discountView;

        @Bind(R.id.myorder_item_good_pricesaved) TextView pricesaveView;

        public ItemViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnLoad{

        void onLoadImage(SimpleDraweeView view,TaoBaoItemVo goodItem);

        void onItemClick(View view,TaoBaoItemVo goodItem);

    }
}
