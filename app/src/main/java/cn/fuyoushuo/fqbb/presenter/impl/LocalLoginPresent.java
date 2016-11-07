package cn.fuyoushuo.fqbb.presenter.impl;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.commonlib.utils.LoginInfoStore;
import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbLocalHttpService;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 本地登录信息的管理
 * Created by QA on 2016/11/2.
 */
public class LocalLoginPresent extends BasePresenter{


    public void isFqbbLocalLogin(final LoginCallBack loginCallBack){

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
                        if(loginCallBack != null){
                            loginCallBack.localLoginFail();
                        }
                    }

                    @Override
                    public void onNext(HttpResp httpResp) {
                        if(httpResp == null || httpResp.getS() != 1){
                            if(loginCallBack != null){
                                loginCallBack.localLoginFail();
                            }
                        }else{
                            if(loginCallBack != null){
                                loginCallBack.localLoginSuccess();
                            }
                        }
                    }
                })
        );
    }


    //获取用户的登录信息
    public void getUserInfo(final UserInfoCallBack userInfoCallBack){

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
                        userInfoCallBack.onUserInfoGetError();
                    }

                    @Override
                    public void onNext(HttpResp httpResp) {
                        if(httpResp == null || httpResp.getS() != 1){
                            userInfoCallBack.onUserInfoGetError();
                        }else {
                               JSONObject result = new JSONObject(((Map)(httpResp.getR())));
                                result.put("account", LoginInfoStore.getIntance().getUserAccount());
                                userInfoCallBack.onUserInfoGetSucc(result);
                        }
                    }
                })
        );}

    public interface LoginCallBack{

        void localLoginSuccess();

        void localLoginFail();

    }

    public interface UserInfoCallBack{

        void onUserInfoGetSucc(JSONObject jsonObject);

        void onUserInfoGetError();
    }




}
