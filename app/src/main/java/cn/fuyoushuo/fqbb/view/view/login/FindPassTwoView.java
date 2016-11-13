package cn.fuyoushuo.fqbb.view.view.login;

/**
 * Created by QA on 2016/10/28.
 */
public interface FindPassTwoView {

    void onFindSuccess(String phoneNum);

    void onFindFail(String phoneNum, String msg);

}
