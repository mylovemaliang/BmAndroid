package cn.fuyoushuo.fqbb.view.view.login;

/**
 * Created by QA on 2016/10/28.
 */
public interface RegisterOneView {

    void  onErrorRecieveVerifiCode(String respMsg);

    void onSuccessRecieveVerifiCode(String phoneNum);
}
