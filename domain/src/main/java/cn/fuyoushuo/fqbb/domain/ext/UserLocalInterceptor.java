package cn.fuyoushuo.fqbb.domain.ext;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.fuyoushuo.fqbb.commonlib.utils.LoginInfoStore;
import cn.fuyoushuo.fqbb.commonlib.utils.MD5;
import cn.fuyoushuo.fqbb.commonlib.utils.UserInfoStore;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 对本地登录应用进行数据方面的验证
 * Created by QA on 2016/11/1.
 */
public class UserLocalInterceptor implements Interceptor {

    public static final String LOGGIN_URL_PATH = "/user/mlogin.htm";

    //不需要额外处理的请求集合
    public static final List<String>  NO_HANDLER_URLS = Arrays.asList(
            "/vcc/sc.htm",
            "/user/validePhone.htm",
            "/user/valideEmail.htm",
            "/user/doFindPwd.htm",
            "/point/getSkus.htm"
    );


    @Override
    public Response intercept(Chain chain) throws IOException {
        String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Request request = chain.request();
        //回复体
        Response response = null;
        String mappingPath = request.url().encodedPath();
        //根据请求路径处理request请求
        if(!LOGGIN_URL_PATH.equals(mappingPath) && !NO_HANDLER_URLS.contains(mappingPath)){
            UserInfoStore userInfoStore = LoginInfoStore.getIntance().getUserInfoStore();
            if(userInfoStore != null){
              Request.Builder newRequestBuilder = request.newBuilder();
              HttpUrl url = request.url();
              HttpUrl.Builder newUrlBuilder = url.newBuilder();
              if(!TextUtils.isEmpty(userInfoStore.getSessionId()) && !TextUtils.isEmpty(userInfoStore.getUserId()) && !TextUtils.isEmpty(userInfoStore.getToken())){
                  newUrlBuilder.addEncodedQueryParameter("sessionid",userInfoStore.getSessionId());
                  newUrlBuilder.addEncodedQueryParameter("t",currentTime);
                  String originText = userInfoStore.getSessionId()+userInfoStore.getUserId()+userInfoStore.getToken()+currentTime;
                  String vt = MD5.MD5Encode(originText);
                  newUrlBuilder.addEncodedQueryParameter("vt",vt);
                  //更改请求后进行请求
                  Request newRequest = newRequestBuilder.url(newUrlBuilder.build()).build();
                  response = chain.proceed(newRequest);
              }
            }else{
                 response = chain.proceed(request);
            }
        }else{
            //开始请求
            response = chain.proceed(request);
        }

        if(LOGGIN_URL_PATH.equals(mappingPath)){
           return loginAfterHander(request,response);
        }else{
            return response;
        }
    }

    //登录后处理
    private Response loginAfterHander(Request request,Response response){
        okhttp3.MediaType mediaType = response.body().contentType();
        String bodyString = "";
        try{
          if(!response.isSuccessful()) return response;
          ResponseBody body = response.body();
          bodyString = body.string();
          if(!TextUtils.isEmpty(bodyString)){
              JSONObject httpResp = JSONObject.parseObject(bodyString);
              if(httpResp != null && httpResp.getIntValue("s") == 1){
              JSONObject detail = httpResp.getJSONObject("r");
              UserInfoStore userInfoStore = new UserInfoStore();
              userInfoStore.setSessionId(detail.getString("sessionId"));
              userInfoStore.setUserId(detail.getString("userId"));
              userInfoStore.setToken(detail.getString("token"));
              //获取登录的账号
              String account = request.url().queryParameter("loginid");
              //写入登录的
              LoginInfoStore.getIntance().writeUserInfo(userInfoStore);
              //写入登录的账号
              LoginInfoStore.getIntance().writeUserAccount(account);
              }
           }
            return response.newBuilder().body(ResponseBody.create(mediaType,bodyString)).build();
         }catch (Exception e){
            Log.d("login_after","UserLocalInterceptor loginAfterHander,m="+e.getMessage());
            return response.newBuilder().body(ResponseBody.create(mediaType,bodyString)).build();
        }
    }

}
