package cn.fuyoushuo.fqbb.view.view;

import java.util.List;

import cn.fuyoushuo.fqbb.domain.entity.FCateItem;
import cn.fuyoushuo.fqbb.domain.entity.FGoodItem;

/**
 * Created by QA on 2016/6/23.
 */
public interface MyOrderView {

     void setupFcatesView(List<FCateItem> cateItems);

     void setupFgoodsView(Integer page, Long cateId, List<FGoodItem> goodItems,boolean isRefresh);

}
