package cn.fuyoushuo.fqbb.view.flagment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.presenter.impl.AutoFanliPresenter;
import cn.fuyoushuo.fqbb.view.Layout.AppUpdateView;
import cn.fuyoushuo.fqbb.view.activity.ConfigActivity;
import cn.fuyoushuo.fqbb.view.activity.MainActivity;

public class ConfigFragment extends Fragment {

    ConfigActivity parentActivity;

    //CheckBox configFanliRadio;

    RelativeLayout configUpdateRl;

    RelativeLayout configAboutFqbbRl;

    LinearLayout configBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flagment_config, container, false);
        //configFanliRadio = (CheckBox) view.findViewById(R.id.configFanliRadio);
        configUpdateRl = (RelativeLayout) view.findViewById(R.id.configUpdateRl);
        configBack = (LinearLayout) view.findViewById(R.id.configBack);
        configAboutFqbbRl = (RelativeLayout) view.findViewById(R.id.configAboutFqbbRl);

        parentActivity = (ConfigActivity) this.getActivity();

        boolean isAutoFanli = AutoFanliPresenter.isAutoFanli();
        //configFanliRadio.setChecked(isAutoFanli);

        /*configFanliRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AutoFanliPresenter.setAutoFanli(isChecked);
            }
        });*/

        configBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        configUpdateRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(parentActivity, "点击APP版本更新", Toast.LENGTH_SHORT).show();
                getUpdateInfo(false);
            }
        });

        configAboutFqbbRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.showFragment(1);
            }
        });

        return view;
    }

    public void getUpdateInfo(boolean isAutoCheck){
        AppUpdateView updateView = new AppUpdateView(parentActivity);
        updateView.getUpdateInfo(isAutoCheck);
    }

    public void goBack(){
        Intent intent = new Intent(parentActivity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        parentActivity.finish();
    }

    /*
    * 返回true表示设置成功
    * 返回false表示设置有异常,设置失败
    * */
    public boolean setAutoFanli(boolean autoFanli){
        try{
            AutoFanliPresenter.setAutoFanli(autoFanli);
            return true;
        }catch(Exception e){
            return false;
        }
    }

}
