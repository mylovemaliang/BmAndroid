package cn.fuyoushuo.fqbb.view.view;

import android.content.Context;
import android.view.View;

import java.util.List;

import cn.fuyoushuo.fqbb.domain.entity.TaoBaoItemVo;

/**
 * Created by QA on 2016/7/11.
 */
public interface SearchView {

    void updateFanliInfo(View fanliView,TaoBaoItemVo vo,boolean isError);

    void setGoodsList(List<TaoBaoItemVo> goodsList,boolean isRerlash);

    void setAlertDialogIfNull();

    Context getMyContext();
}
