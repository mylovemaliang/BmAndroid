package cn.fuyoushuo.fqbb.presenter.impl;

import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbLocalHttpService;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
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

    public interface LoginCallBack{

        void localLoginSuccess();

        void localLoginFail();

    }




}
