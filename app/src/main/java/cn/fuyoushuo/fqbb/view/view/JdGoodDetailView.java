package cn.fuyoushuo.fqbb.view.view;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by QA on 2016/11/3.
 */
public interface JdGoodDetailView {

    void onGetJdFanliFail();

    void onGetJdFanliSucc(JSONObject result,String loadUrl);

    void onGetCpsSucc(String cpsUrl);

    void onGetCpsFail();

}
