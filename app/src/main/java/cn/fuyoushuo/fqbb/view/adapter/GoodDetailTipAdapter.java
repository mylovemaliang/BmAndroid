package cn.fuyoushuo.fqbb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.domain.entity.GoodDetailTipItem;

/**
 * Created by QA on 2016/6/27.
 */
public class GoodDetailTipAdapter extends BaseListAdapter<GoodDetailTipItem>{

    public static LinkedList<GoodDetailTipItem> detailTips = new LinkedList<GoodDetailTipItem>();

    static{
        GoodDetailTipItem goodDetailTipItem1 = new GoodDetailTipItem();
        goodDetailTipItem1.setTipTitle("使用购物券、优惠券可以返利吗?");
        goodDetailTipItem1.setTipContent("<font color=\"#ff0000\">不建议使用</font>。部分优惠券会导致高比例的商品返利比例降低，若使用优惠券出现问题，请退款重新购买");

        GoodDetailTipItem goodDetailTipItem2 = new GoodDetailTipItem();
        goodDetailTipItem2.setTipTitle("使用红包可以返利吗?");
        goodDetailTipItem2.setTipContent("<font color=\"#ff0000\">无返利</font>。红包会导致返利全部或部分被发红包的家伙拿走");

        GoodDetailTipItem goodDetailTipItem3 = new GoodDetailTipItem();
        goodDetailTipItem3.setTipTitle("使用集分宝、淘金币、积分可以返利吗?");
        goodDetailTipItem3.setTipContent("<font color=\"#ff0000\">正常返利</font>。被抵扣的部分无返利，实际返利金额为 实际付款*返利比例");

        GoodDetailTipItem goodDetailTipItem4 = new GoodDetailTipItem();
        goodDetailTipItem4.setTipTitle("使用购物车可以返利吗?");
        goodDetailTipItem4.setTipContent("<font color=\"#ff0000\">不建议使用</font>。加入购物车可能会导致高比例商品比例降低。若加入购物车必须先进入返钱模式,加入购物车后立即付款");

        GoodDetailTipItem goodDetailTipItem5 = new GoodDetailTipItem();
        goodDetailTipItem5.setTipTitle("订单部分退款、换货有返利吗?");
        goodDetailTipItem5.setTipContent("<font color=\"#ff0000\">有返利</font>。退款部分无返利，换货有返利");

        detailTips.add(goodDetailTipItem1);
        detailTips.add(goodDetailTipItem2);
        detailTips.add(goodDetailTipItem3);
        detailTips.add(goodDetailTipItem4);
        detailTips.add(goodDetailTipItem5);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_good_detail_tip_item, parent, false);
        return new ItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final ItemViewHolder currentHolder = (ItemViewHolder) holder;
        final GoodDetailTipItem item = getItem(position);

        currentHolder.titleView.setText(item.getTipTitle());
        currentHolder.contentView.setText(Html.fromHtml(item.getTipContent()));
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.tip_title)
        TextView titleView;

        @Bind(R.id.tip_content)
        TextView contentView;

        public ItemViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
