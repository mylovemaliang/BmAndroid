package cn.fuyoushuo.fqbb.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;

import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.EventIdConstants;

public class AppstartActivity extends BaseActivity {

    private RelativeLayout appstartLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.appstart);

        appstartLayout = (RelativeLayout) findViewById(R.id.appstartLayout);
        Animation am = AnimationUtils.loadAnimation(this, R.anim.app_start_anim);
        appstartLayout.startAnimation(am);
        am.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                MobclickAgent.onEvent(MyApplication.getContext(), EventIdConstants.APP_START);
                startActivity(new Intent(AppstartActivity.this, MainActivity.class));
                AppstartActivity.this.finish();
            }
        });
    }
}
