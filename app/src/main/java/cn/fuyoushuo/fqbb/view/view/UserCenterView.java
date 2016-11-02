package cn.fuyoushuo.fqbb.view.view;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by QA on 2016/11/1.
 */
public interface UserCenterView {

    void onUserInfoGetError();

    void onUserInfoGetSucc(JSONObject result);

    void onLoginFail();

    void onAlimamaLoginFail();

    void onAlimamaLoginSuccess(JSONObject resule);

    void onAlimamaLoginError();
}
