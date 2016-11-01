package cn.fuyoushuo.fqbb.presenter.impl.login;

import android.text.TextUtils;

import java.lang.ref.WeakReference;

import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.commonlib.utils.MD5;
import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbLocalHttpService;
import cn.fuyoushuo.fqbb.presenter.impl.BasePresenter;
import cn.fuyoushuo.fqbb.view.view.login.LoginOriginView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/11/1.
 */
public class LoginOriginPresenter extends BasePresenter{

     private WeakReference<LoginOriginView> loginOriginView;

     public LoginOriginPresenter(LoginOriginView loginOriginView) {
         this.loginOriginView = new WeakReference<LoginOriginView>(loginOriginView);
     }

     //获取 VIEW 实例
     private LoginOriginView getMyView(){
        return loginOriginView.get();
    }

     public void userLogin(final String account, String password){
           if(TextUtils.isEmpty(account) || TextUtils.isEmpty(password)){
                return;
           }
           String passwordMD5 = MD5.MD5Encode(password+"user_pwd160909!@#");
           mSubscriptions.add(
                   ServiceManager.createService(FqbbLocalHttpService.class)
                   .userLogin(account,passwordMD5)
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new Subscriber<HttpResp>() {
                       @Override
                       public void onCompleted() {

                       }

                       @Override
                       public void onError(Throwable e) {
                           if(getMyView() != null){
                               getMyView().onLoginFail(account,"登录出现问题,请稍后再试");
                           }
                       }

                       @Override
                       public void onNext(HttpResp httpResp) {
                           if(httpResp == null){
                               if(getMyView() != null){
                                   getMyView().onLoginFail(account,"登录没响应,请稍后再试");
                               }
                           }else{
                               Integer status = httpResp.getS();
                               if(status == 0){
                                   if(getMyView() != null){
                                       getMyView().onLoginFail(account,httpResp.getM());
                                   }
                               }else{
                                   if(getMyView() != null){
                                       getMyView().onLoginSuccess(account);
                                   }
                               }
                           }
                       }
                   })
           );
     }
}
