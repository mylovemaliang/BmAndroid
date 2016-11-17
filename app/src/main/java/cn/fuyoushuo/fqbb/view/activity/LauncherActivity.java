package cn.fuyoushuo.fqbb.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import cn.fuyoushuo.fqbb.MyApplication;


public class LauncherActivity extends BaseActivity {


    private SharedPreferences sharedPreferences;

    private SharedPreferences tipSharedPreferences;

    public final static String USER_GUIDE = "user_guide";

    public final static String TIP_INFO = "main_tip_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = MyApplication.getContext().getSharedPreferences(USER_GUIDE,Activity.MODE_PRIVATE);
        tipSharedPreferences = MyApplication.getContext().getSharedPreferences(TIP_INFO, Context.MODE_PRIVATE);

        SharedPreferences.Editor tipEditor = tipSharedPreferences.edit();
        tipEditor.putBoolean("short_time_tipForTaobao",false);
        tipEditor.putBoolean("short_time_tipForJd",false);
        tipEditor.commit();

        if(sharedPreferences.getBoolean("isUserGuided",false)){
            Intent intent1 = new Intent(this,AppstartActivity.class);
            startActivity(intent1);
        }else{
            Intent intent2 = new Intent(this,UserguideActivity.class);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("isUserGuided",true);
            edit.commit();
            startActivity(intent2);
        }
        finish();
    }
}
