package cn.fuyoushuo.fqbb.presenter.impl.login;


import android.text.TextUtils;

import java.lang.ref.WeakReference;

import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbLocalHttpService;
import cn.fuyoushuo.fqbb.presenter.impl.BasePresenter;
import cn.fuyoushuo.fqbb.view.view.login.RegisterOneView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/10/28.
 */
public class RegisterOnePresenter extends BasePresenter {


    private WeakReference<RegisterOneView> registerOneView;

    public RegisterOnePresenter(RegisterOneView registerOneView) {
        this.registerOneView = new WeakReference<RegisterOneView>(registerOneView);
    }

    //获取 VIEW 实例
    private RegisterOneView getMyView(){
        return registerOneView.get();
    }

    //获取register验证码
    public void getVerifiCode(final String phoneNum){
        if(TextUtils.isEmpty(phoneNum)) return;
        mSubscriptions.add(ServiceManager.createService(FqbbLocalHttpService.class)
         .getVerifiCode("phone_register",phoneNum)
         .subscribeOn(Schedulers.io())
         .observeOn(AndroidSchedulers.mainThread())
         .subscribe(new Subscriber<HttpResp>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                         if(getMyView() != null){
                             getMyView().onErrorRecieveVerifiCode("获取验证码错误,请稍后再试");
                         }
                    }

                    @Override
                    public void onNext(HttpResp httpResp) {
                        if(httpResp == null){
                            if(getMyView() != null){
                                getMyView().onErrorRecieveVerifiCode("获取验证码错误,请稍后再试");
                            }
                        }
                        if(getMyView() != null){
                            if(httpResp.getS() != null && httpResp.getS() == 1){
                               getMyView().onSuccessRecieveVerifiCode(phoneNum);
                            }else{
                                getMyView().onErrorRecieveVerifiCode(httpResp.getM());
                            }
                        }
                    }
                })
        );
    }




}
