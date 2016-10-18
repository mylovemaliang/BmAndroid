package cn.fuyoushuo.fqbb.presenter.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.view.activity.MainActivity;

public class AutoFanliPresenter {

    //是否启动自动返利
    public static Boolean isAutoFanli = null;

    public static SharedPreferences autoFanliCache;

    public static final String autoFanliFile = "config_autofanli";

    private MainActivity ma;

    private AutoFanliPresenter(MainActivity ma){
        this.ma = ma;
    }

    public static boolean isAutoFanli(){
        if(isAutoFanli==null){
            if(autoFanliCache==null){
                autoFanliCache = MyApplication.getContext().getSharedPreferences(autoFanliFile, Context.MODE_PRIVATE);
            }

            isAutoFanli = autoFanliCache.getBoolean("autoFanli", true);
        }

        return isAutoFanli;
    }

    public static boolean initAutoFanli(){
        if(isAutoFanli==null){
            if(autoFanliCache==null){
                autoFanliCache = MyApplication.getContext().getSharedPreferences(autoFanliFile, Context.MODE_PRIVATE);
            }

            isAutoFanli = autoFanliCache.getBoolean("autoFanli", true);
        }

        return isAutoFanli;
    }

    /*
    * 返回true表示设置成功
    * 返回false表示设置有异常,设置失败
    * */
    public static boolean setAutoFanli(boolean autoFanli){
        try{
            if(autoFanliCache==null){
                autoFanliCache = MyApplication.getContext().getSharedPreferences(autoFanliFile, Context.MODE_PRIVATE);
            }

            SharedPreferences.Editor prefsWriter = autoFanliCache.edit();
            prefsWriter.putBoolean("autoFanli", autoFanli);
            prefsWriter.commit();

            Log.i("autofanli", "set succ,value:"+autoFanli);

            isAutoFanli = autoFanli;

            return true;
        }catch(Exception e){
            return false;
        }
    }

}
