package cn.fuyoushuo.fqbb.presenter.impl;

import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.fuyoushuo.fqbb.commonlib.utils.Constants;
import cn.fuyoushuo.fqbb.commonlib.utils.LoginInfoStore;
import cn.fuyoushuo.fqbb.commonlib.utils.MD5;
import cn.fuyoushuo.fqbb.commonlib.utils.UserInfoStore;
import cn.fuyoushuo.fqbb.view.view.LocalOrderView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/11/8.
 */
public class LocalOrderPresenter extends BasePresenter{

   private WeakReference<LocalOrderView> localOrderView;

   public LocalOrderPresenter(LocalOrderView localOrderView) {
        this.localOrderView = new WeakReference<LocalOrderView>(localOrderView);
   }

   private LocalOrderView getMyView(){
        return localOrderView.get();
   }

   public void getloadUrl(boolean isALL,String status,boolean isLast30Day){
       mSubscriptions.add(createLoadUrlObservable(isALL,status,isLast30Day)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<String>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    if(getMyView() != null){
                        getMyView().onGetLoadUrlFail();
                    }
                }

                @Override
                public void onNext(String s) {
                    if(TextUtils.isEmpty(s)){
                        getMyView().onGetLoadUrlFail();
                    }else{
                        getMyView().onGetLoadUrlSucc(s);
                    }
                }
            })
       );



   }


    private Observable<String> createLoadUrlObservable(final boolean isALL, final String status, final boolean isLast30Day){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                StringBuilder loadUrl = new StringBuilder();
                loadUrl.append(Constants.ENDPOINT_FQBB_LOCAL+"/point/mshopOrder.htm?");
                if(!isALL){
                   if(isLast30Day){
                       loadUrl.append("days=30&");
                   }
                    if(!TextUtils.isEmpty(status)){
                        loadUrl.append("status="+status+"&");
                    }
                }
                 UserInfoStore userInfoStore = LoginInfoStore.getIntance().getUserInfoStore();
                  if(userInfoStore == null){
                     subscriber.onNext("");
                  }
                  if(!TextUtils.isEmpty(userInfoStore.getSessionId()) && !TextUtils.isEmpty(userInfoStore.getUserId()) && !TextUtils.isEmpty(userInfoStore.getToken())){
                    loadUrl.append("sessionid="+userInfoStore.getSessionId()+"&");
                    loadUrl.append("t="+currentTime+"&");
                    String originText = userInfoStore.getSessionId()+userInfoStore.getUserId()+userInfoStore.getToken()+currentTime;
                    String vt = MD5.MD5Encode(originText);
                    loadUrl.append("vt="+vt);
                    subscriber.onNext(loadUrl.toString());
                }
            }
        });
    }


}
