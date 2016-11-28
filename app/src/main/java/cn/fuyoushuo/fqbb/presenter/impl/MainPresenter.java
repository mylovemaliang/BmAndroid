package cn.fuyoushuo.fqbb.presenter.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.ServiceManager;

import cn.fuyoushuo.fqbb.domain.entity.FCateItem;
import cn.fuyoushuo.fqbb.domain.entity.FGoodItem;
import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbHttpService;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbLocalHttpService;
import cn.fuyoushuo.fqbb.view.view.MainView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/6/27.
 */
public class MainPresenter extends BasePresenter{

    private static final String shared_pre_key = "main_tip_info";

    private MainView mMainView;

    private SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(shared_pre_key,Context.MODE_PRIVATE);

    public MainPresenter(MainView mainView) {
        mMainView = mainView;
    }

    public void getFcates(){
        mSubscriptions.add(ServiceManager.createService(FqbbHttpService.class).getCates()
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
                            mMainView.setupFcatesView(cateItems);
                        }
                    }
                }));
    }

    /**
     * 获取商品信息
     * @param cateId
     * @param page
     */
    public void getFGoods(final Long cateId, final Integer page,final boolean isRefresh){
        mSubscriptions.add(ServiceManager.createService(FqbbHttpService.class).getGoodItems(cateId,20,page)
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
                        mMainView.setupFgoodsView(1,cateId,new ArrayList<FGoodItem>(),isRefresh);
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
                            mMainView.setupFgoodsView(page,cateId,goodItems,isRefresh);
                        }else{
                            Toast.makeText(MyApplication.getContext(),httpResp.getM(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }


    /**
     * 获取是否需要提示
     * @param flag
     */
    public boolean isNeedTip(int flag){
      boolean result = false;
      if(flag == 1){
         result = sharedPreferences.getBoolean("tipForTaobao",false);
         if(!result){
             result = sharedPreferences.getBoolean("short_time_tipForTaobao",false);
         }
      }
      if(flag == 2){
         result = sharedPreferences.getBoolean("tipForJd",false);
          if(!result){
              result = sharedPreferences.getBoolean("short_time_tipForJd",false);
          }
      }
      if(result){
          return false;
      }else{
          return true;
      }
    }

    /**
     * 更新提示状态
     * @param flag
     * @param isTip
     */
    public void setTipState(int flag,boolean isTip){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        if(!isTip){
            if(flag == 1){
                 edit.putBoolean("short_time_tipForTaobao",true);
            }
            if(flag == 2){
                edit.putBoolean("short_time_tipForJd",true);
            }
        }else{
            if(flag == 1){
                  edit.putBoolean("tipForTaobao",true);
            }
            if(flag == 2){
                  edit.putBoolean("tipForJd",true);
            }
        }
        edit.commit();
    }
}
