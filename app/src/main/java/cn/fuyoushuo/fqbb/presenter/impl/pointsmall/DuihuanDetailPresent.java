package cn.fuyoushuo.fqbb.presenter.impl.pointsmall;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.commonlib.utils.LoginInfoStore;
import cn.fuyoushuo.fqbb.domain.entity.DuihuanDetail;
import cn.fuyoushuo.fqbb.domain.entity.DuihuanItem;
import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbLocalHttpService;
import cn.fuyoushuo.fqbb.presenter.impl.BasePresenter;
import cn.fuyoushuo.fqbb.view.Layout.ImageCycleView;
import cn.fuyoushuo.fqbb.view.view.pointsmall.DuihuanDetailView;
import cn.fuyoushuo.fqbb.view.view.pointsmall.DuihuanjiluView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/11/11.
 */
public class DuihuanDetailPresent extends BasePresenter {

    private WeakReference<DuihuanDetailView> duihuanDetailView;

    public DuihuanDetailPresent(DuihuanDetailView duihuanDetailView) {
        this.duihuanDetailView = new WeakReference<DuihuanDetailView>(duihuanDetailView);
    }

    private DuihuanDetailView getMyView(){
        return duihuanDetailView.get();
    }


    public void getDhDetails(final int pageNum, final boolean isRefresh){
         if(pageNum == 0){
             if(getMyView() != null){
                 getMyView().onLoadDataFail("页数错误");
             }
         }

        mSubscriptions.add(ServiceManager.createService(FqbbLocalHttpService.class)
                .getDhDetails(pageNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResp>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                       if(getMyView() != null){
                           getMyView().onLoadDataFail("");
                       }
                    }

                    @Override
                    public void onNext(HttpResp httpResp) {
                       if(httpResp == null || httpResp.getS() != 1){
                           if(getMyView() != null){
                               getMyView().onLoadDataFail("");
                           }
                       }else{
                           List<DuihuanDetail> details = new ArrayList<DuihuanDetail>();
                           JSONObject result = new JSONObject(((Map)(httpResp.getR())));
                           parseDuihuanDetail(result,details);
                           if(getMyView() != null){
                               getMyView().onLoadDataSucc(pageNum,isRefresh,details);
                           }
                       }
                    }
                })
        );
    }

    private void parseDuihuanDetail(JSONObject result,List<DuihuanDetail> details){
        if(result == null || result.isEmpty()) return;
        JSONArray listObjs = result.getJSONArray("listObjs");
        if(listObjs == null || listObjs.isEmpty()) return;
        for(int i=0;i<listObjs.size();i++){
            JSONObject item = listObjs.getJSONObject(i);
            DuihuanDetail duihuanDetail = item.toJavaObject(DuihuanDetail.class);
            details.add(duihuanDetail);
        }
    }
}
