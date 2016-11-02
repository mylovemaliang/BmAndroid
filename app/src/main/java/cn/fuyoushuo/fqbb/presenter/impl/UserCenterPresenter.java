package cn.fuyoushuo.fqbb.presenter.impl;

import com.alibaba.fastjson.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Map;

import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.commonlib.utils.LoginInfoStore;
import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbLocalHttpService;
import cn.fuyoushuo.fqbb.view.view.UserCenterView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/11/1.
 */
public class UserCenterPresenter extends BasePresenter{


    //view 引用
    private WeakReference<UserCenterView> userCenterView;

    public UserCenterPresenter(UserCenterView userCenterView) {
        this.userCenterView = new WeakReference<UserCenterView>(userCenterView);
    }

    //获取 VIEW 实例
    private UserCenterView getMyView(){
        return userCenterView.get();
    }

    //获取当前用户信息
    public void getUserInfo(){
        mSubscriptions.add(ServiceManager.createService(FqbbLocalHttpService.class)
               .getUserInfo()
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Subscriber<HttpResp>() {
                   @Override
                   public void onCompleted() {

                   }

                   @Override
                   public void onError(Throwable e) {
                       if(getMyView() != null){
                           getMyView().onUserInfoGetError();
                       }
                   }

                   @Override
                   public void onNext(HttpResp httpResp) {
                       if(httpResp == null || httpResp.getS() != 1){
                           if(getMyView() != null){
                               getMyView().onLoginFail();
                           }
                       }else {
                           if(getMyView() != null){
                               JSONObject result = new JSONObject(((Map)(httpResp.getR())));
                               result.put("account", LoginInfoStore.getIntance().getUserAccount());
                               getMyView().onUserInfoGetSucc(result);
                           }
                       }
                   }
               })
        );



    }

}
