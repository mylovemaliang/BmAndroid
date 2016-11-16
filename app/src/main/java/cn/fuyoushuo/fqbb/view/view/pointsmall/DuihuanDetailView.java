package cn.fuyoushuo.fqbb.view.view.pointsmall;

import java.util.List;

import cn.fuyoushuo.fqbb.domain.entity.DuihuanDetail;

/**
 * Created by QA on 2016/11/11.
 */
public interface DuihuanDetailView {

     void onLoadDataFail(String msg);

     void onLoadDataSucc(int pageNum, boolean isRefresh, List<DuihuanDetail> details);
}

