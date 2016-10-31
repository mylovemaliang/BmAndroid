package cn.fuyoushuo.fqbb.commonlib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * 用于单例保存用户信息
 * Created by QA on 2016/10/31.
 */
public class LoginInfoStore {


    private static class LoginInfoStoreHolder{
         private static LoginInfoStore INTANCE = new LoginInfoStore();
    }

    public static LoginInfoStore getIntance(){
         return LoginInfoStoreHolder.INTANCE;
    }

    public void init(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(share_userinfo_key,Context.MODE_PRIVATE);
    }

    private Context context;

    private SharedPreferences sharedPreferences;

    private String share_userinfo_key = "user_info";

    private void writeUserInfo(UserInfoStore userInfoStore){
         if(userInfoStore == null) return;
         SharedPreferences.Editor edit = sharedPreferences.edit();
         if(!TextUtils.isEmpty(userInfoStore.getSessionId())){
             edit.putString("sessionId",userInfoStore.getSessionId());
         }
         else if(!TextUtils.isEmpty(userInfoStore.getToken())){
             edit.putString("token",userInfoStore.getToken());
         }
         else if(!TextUtils.isEmpty(userInfoStore.getUserId())){
             edit.putString("userId",userInfoStore.getUserId());
        }
        edit.commit();
    }

    private UserInfoStore getUserInfoStore(){
        String sessionId = sharedPreferences.getString("sessionId","");
        String token = sharedPreferences.getString("token","");
        String userId = sharedPreferences.getString("userId","");
        if(TextUtils.isEmpty(sessionId) || TextUtils.isEmpty(token) || TextUtils.isEmpty(userId)){
            return null;
        }
        UserInfoStore userInfoStore = new UserInfoStore();
        userInfoStore.setSessionId(sessionId);
        userInfoStore.setToken(token);
        userInfoStore.setUserId(userId);
        return userInfoStore;
    }

    private void clearUserInfo(){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        if(sharedPreferences.contains("sessionId")){
            edit.remove("sessionId");
        }
        if(sharedPreferences.contains("token")){
            edit.remove("token");
        }
        if(sharedPreferences.contains("userId")){
            edit.remove("userId");
        }
        edit.commit();
    }



}
