package cn.fuyoushuo.fqbb.presenter.impl;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.domain.entity.FCateItem;
import cn.fuyoushuo.fqbb.domain.entity.FGoodItem;
import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbHttpService;
import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.view.view.MyOrderView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/6/27.
 */
public class MyOrderPresenter extends BasePresenter{

    private MyOrderView myOrderView;

    public MyOrderPresenter(MyOrderView myOrderView) {
        this.myOrderView = myOrderView;
    }


    public void getFcates(){
        ServiceManager.createService(FqbbHttpService.class).getCates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResp>() {
                    @Override
                    public void onCompleted() {
                        return;
                    }
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MyApplication.getContext(),"网速稍慢,请等待",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onNext(HttpResp httpResp) {
                        if(httpResp.getS() == 1){
                            Object result = httpResp.getR();
                            JSONArray jsonArray = new JSONArray((List)result);
                            List<FCateItem> cateItems = new ArrayList<FCateItem>();
                            for(Object item : jsonArray){
                                JSONObject jobject = new JSONObject((Map<String, Object>) item);
                                cateItems.add(jobject.toJavaObject(FCateItem.class));
                            }
                            myOrderView.setupFcatesView(cateItems);
                        }
                    }
                });
    }

    /**
     * 获取商品信息
     * @param cateId
     * @param page
     */
    public void getFGoods(final ProgressBar progressBar, final Long cateId, final Integer page, final boolean isProgressShow, final boolean isRefresh){
        if(isProgressShow){
          progressBar.setVisibility(View.VISIBLE);
        }
        ServiceManager.createService(FqbbHttpService.class).getGoodItems(cateId,20,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResp>() {
                    @Override
                    public void onCompleted() {
                        if(isProgressShow){
                           progressBar.setVisibility(View.GONE);
                        }
                        return;
                    }
                    @Override
                    public void onError(Throwable e) {
                       if(isProgressShow){
                         progressBar.setVisibility(View.GONE);
                       }
                        Toast.makeText(MyApplication.getContext(),"网速稍慢,请等待",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onNext(HttpResp httpResp) {
                        if(httpResp.getS() == 1){
                            Object result = httpResp.getR();
                            JSONArray jsonArray = new JSONArray((List<Object>) result);
                            List<FGoodItem> goodItems = new ArrayList<FGoodItem>();
                            for(Object item : jsonArray){
                                JSONObject jobject = new JSONObject((Map<String, Object>) item);
                                goodItems.add(jobject.toJavaObject(FGoodItem.class));
                            }
                            myOrderView.setupFgoodsView(page,cateId,goodItems,isRefresh);
                        }else{
                            Toast.makeText(MyApplication.getContext(),httpResp.getM(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
