package cn.fuyoushuo.fqbb.presenter.impl.pointsmall;

import java.lang.ref.WeakReference;

import cn.fuyoushuo.fqbb.presenter.impl.BasePresenter;
import cn.fuyoushuo.fqbb.view.view.pointsmall.PhoneRechargeView;

/**
 * Created by QA on 2016/11/7.
 */
public class PhoneRechargePresent extends BasePresenter{


    private WeakReference<PhoneRechargeView> phoneRechargeView;

    public PhoneRechargePresent(PhoneRechargeView phoneRechargeView) {
        this.phoneRechargeView = new WeakReference<PhoneRechargeView>(phoneRechargeView);
    }

    //可能为null,后续操作全部需要加上非NULL判断
    private PhoneRechargeView getMyView(){
        return phoneRechargeView.get();
    }


}
