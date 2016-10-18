package cn.fuyoushuo.fqbb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.domain.entity.FGoodItem;
import cn.fuyoushuo.fqbb.domain.entity.GoodItem;

/**
 * Created by QA on 2016/6/27.
 */
public class FgoodDataAdapter extends BaseListAdapter<FGoodItem>{

    private int currentPage = 1;

    private Long cateId;

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

    private OnLoadImage onLoadImage;

    public void setOnLoadImage(OnLoadImage onLoadImage){
        this.onLoadImage = onLoadImage;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myorder_item_good, parent, false);
        return new ItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemViewHolder currentHolder = (ItemViewHolder) holder;
        FGoodItem item = getItem(position);
        currentHolder.setGoodItem(item);
        currentHolder.textView.setText(item.getWebSmallTitle());
        onLoadImage.onLoad(currentHolder.imageView,item);
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder{

        private FGoodItem goodItem;

        public FGoodItem getGoodItem() {
            return goodItem;
        }

        public void setGoodItem(FGoodItem goodItem) {
            this.goodItem = goodItem;
        }

        @Bind(R.id.myorder_item_good_image) SimpleDraweeView imageView;

        @Bind(R.id.myorder_item_good_titletext) TextView textView;

        public ItemViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnLoadImage{
         void onLoad(SimpleDraweeView view, FGoodItem goodItem);
    }
}
