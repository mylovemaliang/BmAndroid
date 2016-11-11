package cn.fuyoushuo.fqbb.view.view.pointsmall;

import java.util.List;

import cn.fuyoushuo.fqbb.domain.entity.DuihuanItem;

/**
 * Created by QA on 2016/11/11.
 */
public interface DuihuanjiluView {

     void onLoadDataFail(String msg);

     void onLoadDataSuccess(Integer queryStatus, int page, boolean isRefresh, List<DuihuanItem> itemList);

}
