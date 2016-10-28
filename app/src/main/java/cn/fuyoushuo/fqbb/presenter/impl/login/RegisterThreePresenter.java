package cn.fuyoushuo.fqbb.presenter.impl.login;


import android.text.TextUtils;

import java.lang.ref.WeakReference;

import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbLocalHttpService;
import cn.fuyoushuo.fqbb.presenter.impl.BasePresenter;
import cn.fuyoushuo.fqbb.view.view.login.RegisterThreeView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/10/28.
 */
public class RegisterThreePresenter extends BasePresenter {


    private WeakReference<RegisterThreeView> registerThreeView;

    public RegisterThreePresenter(RegisterThreeView registerThreeView) {
        this.registerThreeView = new WeakReference<RegisterThreeView>(registerThreeView);
    }

    //获取 VIEW 实例
    private RegisterThreeView getMyView(){
        return registerThreeView.get();
    }


    public void registUser(final String phoneNum, String password, String verifiCode){
        if(TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(password) || TextUtils.isEmpty(verifiCode)){
            return;
        }
        mSubscriptions.add(ServiceManager.createService(FqbbLocalHttpService.class)
           .registerUser(phoneNum,password,verifiCode)
           .subscribeOn(Schedulers.io())
           .observeOn(AndroidSchedulers.mainThread())
           .subscribe(new Subscriber<HttpResp>() {
               @Override
               public void onCompleted() {

               }

               @Override
               public void onError(Throwable e) {
                   if(getMyView() != null){
                       getMyView().onRegistFail(phoneNum,"注册发生错误,请重试");
                   }
               }

               @Override
               public void onNext(HttpResp httpResp) {
                   if(httpResp != null){
                       if(httpResp.getS() != null && httpResp.getS() == 1){
                           if(getMyView() != null){
                             getMyView().onRegistSuccess(phoneNum);
                           }
                       }else{
                           if(getMyView() != null){
                             getMyView().onRegistFail(phoneNum,httpResp.getM());
                           }
                       }
                   }
               }
           })
        );


    }




}
