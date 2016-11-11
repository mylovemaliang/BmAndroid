package cn.fuyoushuo.fqbb.presenter.impl.pointsmall;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.domain.entity.DuihuanItem;
import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbLocalHttpService;
import cn.fuyoushuo.fqbb.presenter.impl.BasePresenter;
import cn.fuyoushuo.fqbb.view.view.pointsmall.DuihuanjiluView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/11/11.
 */
public class DuihuanjiluPresent extends BasePresenter {

    private WeakReference<DuihuanjiluView> duihuanjiluView;

    public DuihuanjiluPresent(DuihuanjiluView duihuanjiluView) {
        this.duihuanjiluView = new WeakReference<DuihuanjiluView>(duihuanjiluView);
    }

    private DuihuanjiluView getMyView(){
        return duihuanjiluView.get();
    }

    public void getDhOrders(final Integer queryStatus, final int pageNum, final boolean isRefresh){
         if(pageNum == 0){
             if(getMyView() != null){
                 getMyView().onLoadDataFail("页数错误");
             }
         }

        mSubscriptions.add(ServiceManager.createService(FqbbLocalHttpService.class)
                .getDhOrders(pageNum,queryStatus)
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
                           List<DuihuanItem> orders = new ArrayList<DuihuanItem>();
                           JSONObject result = new JSONObject(((Map)(httpResp.getR())));
                           parseDuihuanItem(result,orders);
                           if(getMyView() != null){
                               getMyView().onLoadDataSuccess(queryStatus,pageNum,isRefresh,orders);
                           }
                       }
                    }
                })
        );
    }

    private void parseDuihuanItem(JSONObject result,List<DuihuanItem> orders){
        if(result == null || result.isEmpty()) return;
        JSONArray listObjs = result.getJSONArray("listObjs");
        if(listObjs == null || listObjs.isEmpty()) return;
        for(int i=0;i<listObjs.size();i++){
            JSONObject item = listObjs.getJSONObject(i);
            DuihuanItem duihuanItem = new DuihuanItem();
            duihuanItem.setDateTimeString(item.getString("gmtCreateStr"));
            duihuanItem.setMobilePhone(item.getString("mobilePhone"));
            duihuanItem.setOrderDetail(item.getString("orderItemDesc"));
            duihuanItem.setOrderStatus(item.getInteger("status"));
            orders.add(duihuanItem);
        }
    }
}
