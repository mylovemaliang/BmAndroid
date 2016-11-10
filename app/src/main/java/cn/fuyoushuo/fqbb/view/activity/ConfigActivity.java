package cn.fuyoushuo.fqbb.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.view.flagment.AboutFragment;
import cn.fuyoushuo.fqbb.view.flagment.ConfigFragment;

public class ConfigActivity extends BaseActivity {

    private FragmentManager fragmentManager;

    private int lastShowFragmentIndex;

    ConfigFragment configFragment;

    AboutFragment aboutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_config);

        configFragment = new ConfigFragment();
        aboutFragment = new AboutFragment();

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.config_fragment_area, configFragment).show(configFragment);
        fragmentTransaction.add(R.id.config_fragment_area, aboutFragment).hide(aboutFragment);
        fragmentTransaction.commit();
    }

    //0  配置界面    1 关于返钱宝宝界面
    public void showFragment(int index){
        if(index!=lastShowFragmentIndex){
            if(index==0){
                switchContent(aboutFragment, configFragment);
                lastShowFragmentIndex = index;
            }else if(index==1){
                switchContent(configFragment, aboutFragment);
                lastShowFragmentIndex = index;
            }
        }
    }

    //转换flagment
    public void switchContent(Fragment from, Fragment to){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!to.isAdded()) {    // 先判断是否被add过
            transaction.hide(from).add(R.id.config_fragment_area, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
        }
    }

    @Override
    public void onBackPressed() {
        if(lastShowFragmentIndex==0){
            configFragment.goBack();
        }else if(lastShowFragmentIndex == 1){
            aboutFragment.goBack();
        }
    }
}
