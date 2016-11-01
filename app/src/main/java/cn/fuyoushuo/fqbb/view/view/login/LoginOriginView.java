package cn.fuyoushuo.fqbb.view.view.login;

/**
 * Created by QA on 2016/11/1.
 */
public interface LoginOriginView {

    /**
     * 登录成功回调
     * @param account
     */
    void onLoginSuccess(String account);

    /**
     * 登录失败回调
     * @param account
     * @param msg
     */
    void onLoginFail(String account,String msg);

}
