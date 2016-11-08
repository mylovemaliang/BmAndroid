package cn.fuyoushuo.fqbb.presenter.impl;

import android.text.TextUtils;

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


    //获取register验证码
    public void getVerifiCode(final String phoneNum,final String flag,final VerifiCodeGetCallBack verifiCodeGetCallBack){
        if(TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(flag)) return;
        mSubscriptions.add(ServiceManager.createService(FqbbLocalHttpService.class)
                .getVerifiCode(flag,phoneNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResp>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        verifiCodeGetCallBack.onVerifiCodeGetError("获取验证码错误,请稍后再试");
                    }

                    @Override
                    public void onNext(HttpResp httpResp) {
                        if(httpResp == null || httpResp.getS() != 1){
                            verifiCodeGetCallBack.onVerifiCodeGetError("获取验证码错误,请稍后再试");
                        }else{
                            verifiCodeGetCallBack.onVerifiCodeGetSucc(phoneNum);
                        }
                    }
                })
        );
    }

    public interface LoginCallBack{

        void localLoginSuccess();

        void localLoginFail();

    }

    public interface UserInfoCallBack{

        void onUserInfoGetSucc(JSONObject jsonObject);

        void onUserInfoGetError();
    }

    //获取验证码验证
    public interface VerifiCodeGetCallBack{

        void onVerifiCodeGetSucc(String phoneNum);

        void onVerifiCodeGetError(String msg);
    }




}
