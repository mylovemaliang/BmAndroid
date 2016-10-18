package cn.fuyoushuo.fqbb.presenter.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.HttpCookie;
import java.net.URI;


import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.commonlib.utils.okhttp.OkHttpStack;
import cn.fuyoushuo.fqbb.commonlib.utils.okhttp.PersistentCookieStore;

public class TaobaoInterPresenter {

    //https://login.taobao.com/member/login.jhtml?style=common&from=alimama&redirectURL=http%3A%2F%2Flogin.taobao.com%2Fmember%2Ftaobaoke%2Flogin.htm%3Fis_login%3d1&full_redirect=true&disableQuickLogin=true&qq-pf-to=pcqq.discussion
    //https://login.m.taobao.com/login.htm?redirectURL=http://login.taobao.com/member/taobaoke/login.htm?is_login=1&loginFrom=wap_alimama
    public static  final String TAOBAOKE_LOGINURL = "http://login.taobao.com/member/login.jhtml?style=common&from=alimama&redirectURL=http%3A%2F%2Flogin.taobao.com%2Fmember%2Ftaobaoke%2Flogin.htm%3Fis_login%3d1&full_redirect=true&disableQuickLogin=true&qq-pf-to=pcqq.discussion";

    //用户登录后的信息缓存(用户昵称、媒体ID、渠道ID、广告位ID)
    public static SharedPreferences loginUserInfoCache;

    private static final String tbcacheFile = "tbcache";

    private static RequestQueue volleyRq;

    private static long lastLoginTime = 0;

    static{
        loginUserInfoCache = MyApplication.getContext().getSharedPreferences(tbcacheFile, Context.MODE_PRIVATE);
    }

    public static void clearRq(){
        synchronized (TaobaoInterPresenter.class){
            volleyRq = null;
        }
    }

    public static RequestQueue getVolleyRequestQueue(){
        if(volleyRq==null){
            synchronized (TaobaoInterPresenter.class){
                if(volleyRq==null){
                    volleyRq = Volley.newRequestQueue(MyApplication.getContext(), new OkHttpStack(MyApplication.getContext()));
                }
            }
        }
        return volleyRq;
    }

    public static boolean isTaobaokeLoginUrl(String url){
        /*String replaceUrl = url.replace("https://", "").replace("http://", "");
        if(replaceUrl.startsWith("login.taobao.com/member/login.jhtml") && replaceUrl.contains("alimama")){
            return true;
        }*/
        String replaceUrl = url.replace("https://", "").replace("http://", "");
        if(replaceUrl.equals("login.m.taobao.com/login.htm?redirectURL=http%3A%2F%2Flogin.taobao.com%2Fmember%2Ftaobaoke%2Flogin.htm%3Fis_login%3D1%26loginFrom%3Dwap_alimama")
                || replaceUrl.equals("login.m.taobao.com/login.htm?redirectURL=http://login.taobao.com/member/taobaoke/login.htm?is_login=1&loginFrom=wap_alimama")){//当前页面是我们的阿里妈妈登录页(比如未登录访问了订单页面，后面提现页面登陆过了，订单页面还是显示着登录页)
            return true;
        }

        return false;
    }

    public static boolean hasCacheLoginInfo(){
        String mmNickFromCache = loginUserInfoCache.getString("mmNick", null);
        if(mmNickFromCache!=null && !"".equals(mmNickFromCache.trim())){
            return true;
        }else{
            return false;
        }
    }

    public static void saveLoginCookie(String url){
        CookieManager cookieManager = CookieManager.getInstance();
        String alimamaCookies = cookieManager.getCookie(url);
        Log.d("cookie login alimama", alimamaCookies);

        PersistentCookieStore cs = new PersistentCookieStore(MyApplication.getContext());
        URI uri = URI.create(url);
        URI uri2 = URI.create("http://pub.alimama.com");

        String[] cookieValues = alimamaCookies.split(";");
        HttpCookie cookie;
        for (int i = 0; i < cookieValues.length; i++) {
            String[] split = cookieValues[i].split("=");
            if (split.length == 2)
                cookie = new HttpCookie(split[0].trim(), split[1].trim());
            else
                cookie = new HttpCookie(split[0].trim(), null);

            cookie.setDomain(".alimama.com");
            cookie.setPath("/");
            cs.add(uri, cookie);
            cs.add(uri2, cookie);
        }

        clearRq();//已登录保存cookie,同时清理Volley的RQ,下次取Volley时会重新创建,否则,一直用老的,即便登录了,那么最后登录判断时也是未登录

        lastLoginTime = System.currentTimeMillis();
    }

    public static void judgeAlimamaLogin(final LoginCallback loginCallback,String tagName){
        String isLoginUrl = "http://pub.alimama.com/common/getUnionPubContextInfo.json";

        URI uri = URI.create(isLoginUrl);
        PersistentCookieStore s = new PersistentCookieStore(MyApplication.getContext());
        Log.d("cookie isLogin:"+isLoginUrl, s.get(uri).toString());

        //RequestQueue volleyRq = Volley.newRequestQueue(MyApplication.getContext(), new OkHttpStack(MyApplication.getContext()));
        volleyRq = getVolleyRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, isLoginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObj =null;
                        try{
                            jsonObj = JSONObject.parseObject(response.trim());
                        }catch(Exception e){//获取登录信息异常
                            loginCallback.judgeErrorCallback();
                            return;
                        }

                        JSONObject dataJsonObj = jsonObj.getJSONObject("data");
                        if(dataJsonObj!=null){
                            Log.d("isLogin Callback Data", dataJsonObj.toString());
                            Boolean nologin = dataJsonObj.getBoolean("noLogin");
                            if(nologin!=null && nologin){//未登录
                                Log.d("isLoginCallback", "nologin1");

                                SharedPreferences.Editor prefsWriter = loginUserInfoCache.edit();
                                prefsWriter.remove("memberid");
                                prefsWriter.remove("mmNick");
                                prefsWriter.remove("siteId");
                                prefsWriter.remove("channelId");
                                prefsWriter.remove("adzoneId");
                                prefsWriter.commit();

                                loginCallback.nologinCallback();
                            }else{
                                String mmNick = dataJsonObj.getString("mmNick");
                                if(mmNick!=null && !"".equals(mmNick.trim())){
                                    Log.d("isLoginCallback", "haslogin");
                                    Long memberid = dataJsonObj.getLong("memberid");

                                    SharedPreferences.Editor prefsWriter = loginUserInfoCache.edit();
                                    prefsWriter.putLong("memberid", memberid);
                                    prefsWriter.putString("mmNick", mmNick);
                                    prefsWriter.commit();

                                    loginCallback.hasLoginCallback();
                                }else{
                                    loginCallback.nologinCallback();
                                }
                            }
                        }else{
                            loginCallback.judgeErrorCallback();
                        }
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loginCallback.judgeErrorCallback();
                    }
                });

        // 把这个请求加入请求队列
        if(!TextUtils.isEmpty(tagName)){
            stringRequest.setTag(tagName);
        }
        volleyRq.add(stringRequest);
    }

    /**
     * 取消对应 tag 的请求
     * @param tagName
     */
    public static void cancelTagedRuquests(String tagName){
        if(volleyRq != null && !TextUtils.isEmpty(tagName)){
            volleyRq.cancelAll(tagName);
        }
    }

    public interface LoginCallback {

        public void hasLoginCallback();

        public void nologinCallback();

        public void judgeErrorCallback();

    }


}
