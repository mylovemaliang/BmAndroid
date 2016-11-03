package cn.fuyoushuo.fqbb.presenter.impl;

import com.alibaba.fastjson.JSONArray;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbLocalHttpService;
import cn.fuyoushuo.fqbb.view.view.JdGoodDetailView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/11/1.
 */
public class JdGoodDetailPresenter extends BasePresenter{

    public static final String VOLLEY_TAG_NAME = "JdGoodDetailPresenter";

    //view 引用
    private WeakReference<JdGoodDetailView> jdGoodDetailView;

    public JdGoodDetailPresenter(JdGoodDetailView jdGoodDetailView) {
        this.jdGoodDetailView = new WeakReference<JdGoodDetailView>(jdGoodDetailView);
    }

    //获取 VIEW 实例
    private JdGoodDetailView getMyView(){
        return jdGoodDetailView.get();
    }


    //获取京东的返利信息
    public void getJdFanliInfo(String itemId){

       mSubscriptions.add(ServiceManager.createService(FqbbLocalHttpService.class)
               .getJdFanliInfo(itemId)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Subscriber<HttpResp>() {
                   @Override
                   public void onCompleted() {

                   }

                   @Override
                   public void onError(Throwable e) {
                       if(getMyView() != null){
                           getMyView().onGetJdFanliFail();
                       }
                   }

                   @Override
                   public void onNext(HttpResp httpResp) {
                       if(httpResp == null || httpResp.getS() != 1){
                           if(getMyView() != null){
                               getMyView().onGetJdFanliFail();
                           }
                       }else{
                           if(getMyView() != null){
                               Object result = httpResp.getR();
                               JSONArray resultArray = new JSONArray((List<Object>) result);
                               if(!resultArray.isEmpty()){
                                   if(getMyView() != null){
                                       getMyView().onGetJdFanliSucc(resultArray.getJSONObject(0));
                                   }
                               }
                           }
                       }
                   }
               })
       );
    }




}
