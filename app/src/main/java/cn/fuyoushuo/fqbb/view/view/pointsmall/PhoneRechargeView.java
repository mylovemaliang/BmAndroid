package cn.fuyoushuo.fqbb.view.view.pointsmall;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by QA on 2016/11/7.
 */
public interface PhoneRechargeView {

    void onPhoneRechargeSkuGetSucc(List<JSONObject> results);

    void onPhoneRechargeSkuGetFail();


}
