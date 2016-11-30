package cn.fuyoushuo.fqbb.presenter.impl;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.BooleanCodec;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.commonlib.utils.Constants;
import cn.fuyoushuo.fqbb.commonlib.utils.DataCheckUtils;
import cn.fuyoushuo.fqbb.commonlib.utils.LoginInfoStore;
import cn.fuyoushuo.fqbb.commonlib.utils.MD5;
import cn.fuyoushuo.fqbb.commonlib.utils.UserInfoStore;
import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbLocalHttpService;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 本地登录信息的管理
 * Created by QA on 2016/11/2.
 */
public class LocalLoginPresent extends BasePresenter{


    public void isFqbbLocalLogin(final LoginCallBack loginCallBack){

        mSubscriptions.add(createLoginInfoObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
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
                    public void onNext(Boolean resp) {
                        if(!resp){
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
    public void getVerifiCode(final String account,final String flag,final VerifiCodeGetCallBack verifiCodeGetCallBack){
        if(TextUtils.isEmpty(account) || TextUtils.isEmpty(flag)) return;
        mSubscriptions.add(ServiceManager.createService(FqbbLocalHttpService.class)
                .getVerifiCode(flag,account)
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
                            verifiCodeGetCallBack.onVerifiCodeGetSucc(account);
                        }
                    }
                })
        );
    }


    //绑定邮箱
    public void bindMail(final String email, String verifiCode, final BindEmailCallBack bindEmailCallBack){
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(verifiCode)) return;
        mSubscriptions.add(ServiceManager.createService(FqbbLocalHttpService.class)
                .bindEmail(email,verifiCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResp>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        bindEmailCallBack.onBindEmailFail("绑定邮箱出错,请重试");
                    }

                    @Override
                    public void onNext(HttpResp httpResp) {
                        if(httpResp == null || httpResp.getS() != 1){
                            bindEmailCallBack.onBindEmailFail("绑定邮箱出错,请重试");
                        }else{
                            bindEmailCallBack.onBindEmailSuccess(email);
                        }
                    }
                })
        );
    }


    //绑定邮箱
    public void unBindMail(final String account, String verifiCode, final UnBindEmailCallBack unbindEmailCallBack){
        if(TextUtils.isEmpty(account) || TextUtils.isEmpty(verifiCode)) return;
        mSubscriptions.add(ServiceManager.createService(FqbbLocalHttpService.class)
                .unbindEmail(account,verifiCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResp>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        unbindEmailCallBack.onUnBindEmailFail("绑定邮箱出错,请重试");
                    }

                    @Override
                    public void onNext(HttpResp httpResp) {
                        if(httpResp == null || httpResp.getS() != 1){
                            unbindEmailCallBack.onUnBindEmailFail("绑定邮箱出错,请重试");
                        }else{
                            unbindEmailCallBack.onUnBindEmailSuccess(account);
                        }
                    }
                })
        );
    }

    /**
     *
     * @param account
     * @param frag 1: 手机号码 2: 邮箱
     * @param dataValidataCallBack
     */
    public void validateData(final String account, int frag, final DataValidataCallBack dataValidataCallBack){
          if(TextUtils.isEmpty(account)){
               return;
          }
          if(frag == 1 && !DataCheckUtils.isPhoneLegal(account)){
              dataValidataCallBack.onValidataFail("数据格式有误");
              return;
          }
          if(frag == 2 && !DataCheckUtils.checkEmail(account)){
              dataValidataCallBack.onValidataFail("数据格式有误");
              return;
          }
          mSubscriptions.add(Observable.just(frag)
              .flatMap(new Func1<Integer, Observable<HttpResp>>() {
                  @Override
                  public Observable<HttpResp> call(Integer integer) {
                      if(integer == 1){
                          return ServiceManager.createService(FqbbLocalHttpService.class).validePhone(account);
                      }else{
                          return ServiceManager.createService(FqbbLocalHttpService.class).valideEmail(account);
                      }
                  }
              })
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new Subscriber<HttpResp>() {
                  @Override
                  public void onCompleted() {

                  }

                  @Override
                  public void onError(Throwable e) {
                      dataValidataCallBack.onValidataFail("数据异常");
                  }

                  @Override
                  public void onNext(HttpResp httpResp) {
                      if(httpResp == null){
                          dataValidataCallBack.onValidataFail("数据异常");
                      }else{
                          if(httpResp.getS() == 1){
                              dataValidataCallBack.onValidataSucc(2);
                          }else if(httpResp.getS() == 0){
                              dataValidataCallBack.onValidataSucc(1);
                          }
                      }
                  }
              })
          );

    }

    //更新密码
    public void updatePassword(String originPassword, String newPassword, final UpdatePasswordCallBack updatePasswordCallBack){

       if(TextUtils.isEmpty(originPassword) || TextUtils.isEmpty(newPassword)) return;

       mSubscriptions.add(ServiceManager.createService(FqbbLocalHttpService.class)
            .updatePassword(originPassword,newPassword)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<HttpResp>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    updatePasswordCallBack.onUpdatePasswordFail();
                }

                @Override
                public void onNext(HttpResp httpResp) {
                    if(httpResp == null || httpResp.getS() != 1){
                        updatePasswordCallBack.onUpdatePasswordFail();
                    }else{
                        updatePasswordCallBack.onUpdatePasswordSucc();
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

        void onVerifiCodeGetSucc(String account);

        void onVerifiCodeGetError(String msg);
    }

    //绑定EMAIL回调
    public interface BindEmailCallBack{

        void onBindEmailSuccess(String account);

        void onBindEmailFail(String msg);
    }

    //解绑EMAIL回调
    public interface UnBindEmailCallBack{

        void onUnBindEmailSuccess(String account);

        void onUnBindEmailFail(String msg);
    }


    public interface DataValidataCallBack{

        /**
         * 验证成功
         * @param flag 1：已使用  2:未使用
         */
        void onValidataSucc(int flag);

        void onValidataFail(String msg);

    }

    public interface UpdatePasswordCallBack{

        void onUpdatePasswordSucc();

        void onUpdatePasswordFail();

    }

    //-----------------------------------------创建观察者----------------------------------------------------------

    private Observable<Boolean> createLoginInfoObservable(){
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                UserInfoStore userInfoStore = LoginInfoStore.getIntance().getUserInfoStore();
                if(userInfoStore == null){
                    subscriber.onNext(false);
                    return;
                }
                if(!TextUtils.isEmpty(userInfoStore.getSessionId()) && !TextUtils.isEmpty(userInfoStore.getUserId()) && !TextUtils.isEmpty(userInfoStore.getToken())){
                    subscriber.onNext(true);
                    return;
                }
                subscriber.onNext(false);
            }
        });
    }






}
