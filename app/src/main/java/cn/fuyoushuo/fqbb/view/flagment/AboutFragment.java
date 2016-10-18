package cn.fuyoushuo.fqbb.view.flagment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.view.activity.ConfigActivity;
import cn.fuyoushuo.fqbb.view.activity.MainActivity;

public class AboutFragment extends Fragment {

    ConfigActivity parentActivity;

    LinearLayout aboutBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flagment_about, container, false);
        aboutBack = (LinearLayout) view.findViewById(R.id.aboutBack);

        parentActivity = (ConfigActivity) this.getActivity();

        aboutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        return view;
    }

    public void goBack(){
        parentActivity.showFragment(0);
    }

}
