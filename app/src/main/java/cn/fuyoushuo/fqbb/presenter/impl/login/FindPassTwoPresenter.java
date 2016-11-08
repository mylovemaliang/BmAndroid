package cn.fuyoushuo.fqbb.presenter.impl.login;

import android.text.TextUtils;

import java.lang.ref.WeakReference;

import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbLocalHttpService;
import cn.fuyoushuo.fqbb.presenter.impl.BasePresenter;
import cn.fuyoushuo.fqbb.view.view.login.FindPassTwoView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/11/8.
 */
public class FindPassTwoPresenter extends BasePresenter {

    private WeakReference<FindPassTwoView> findPassTwoView;

    public FindPassTwoPresenter(FindPassTwoView findPassTwoView) {
        this.findPassTwoView = new WeakReference<FindPassTwoView>(findPassTwoView);
    }

    //获取 VIEW 实例
    private FindPassTwoView getMyView(){
        return findPassTwoView.get();
    }


    public void doFindPass(final String account, String verifiCode, String newpass){
        if(TextUtils.isEmpty(account) || TextUtils.isEmpty(verifiCode) || TextUtils.isEmpty(newpass)){
            return;
        }
        mSubscriptions.add(ServiceManager.createService(FqbbLocalHttpService.class)
                .findUserPass(account,verifiCode,newpass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResp>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                      if(getMyView() != null){
                          getMyView().onFindFail(account,"找回密码错误");
                      }
                    }

                    @Override
                    public void onNext(HttpResp httpResp) {
                       if(httpResp == null || httpResp.getS() != 1){
                           if(getMyView() != null){
                               getMyView().onFindFail(account,"找回密码错误");
                           }
                       }else{
                           if(getMyView() != null){
                               getMyView().onFindSuccess(account);
                           }
                       }
                    }
                })
        );
    }


}
