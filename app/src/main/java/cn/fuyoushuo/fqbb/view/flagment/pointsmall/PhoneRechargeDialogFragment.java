package cn.fuyoushuo.fqbb.view.flagment.pointsmall;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.zhy.view.flowlayout.TagFlowLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.DateUtils;
import cn.fuyoushuo.fqbb.presenter.impl.LocalLoginPresent;
import cn.fuyoushuo.fqbb.presenter.impl.pointsmall.PhoneRechargePresent;
import cn.fuyoushuo.fqbb.view.view.pointsmall.PhoneRechargeView;

/**
 * Created by QA on 2016/11/7.
 */
public class PhoneRechargeDialogFragment extends DialogFragment implements PhoneRechargeView{


    @Bind(R.id.phone_recharge_account)
    TextView accountView;

    @Bind(R.id.userinfo_currentpoints_value)
    TextView currentPoints;

    @Bind(R.id.userinfo_freezepoints_value)
    TextView freezePoints;

    @Bind(R.id.userinfo_useablepoints_value)
    TextView useablePoints;

    @Bind(R.id.phone_recharge_backArea)
    RelativeLayout backArea;

    //输入的手机号码
    @Bind(R.id.phone_recharge_input_phoneNum)
    EditText inputPhoneNum;

    //获取的可兑换商品的充值券
    @Bind(R.id.phone_recharge_good_info)
    TagFlowLayout myTagFlowLayout;

    @Bind(R.id.phone_recharge_need_points)
    TextView rechargeNeedPoints;

    @Bind(R.id.quick_duihuan)
    Button duihuanCommitButton;

    private LocalLoginPresent localLoginPresent;

    private PhoneRechargePresent phoneRechargePresent;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.fullScreenDialog);
        localLoginPresent = new LocalLoginPresent();
        phoneRechargePresent = new PhoneRechargePresent(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_pointsmall_phonerecharge_dialog, container);
        ButterKnife.bind(this,inflate);



        return inflate;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //初始化用户信息
    private void initUserInfo(JSONObject result){
        if(result == null || result.isEmpty()) return;
        Float validPoint = 0f;
        Float orderFreezePoint = 0f;
        Float convertFreezePoint = 0f;
        String account = "";
        if(result.containsKey("validPoint")){
            validPoint = DateUtils.getFormatFloat(result.getFloatValue("validPoint"));
        }
        if(result.containsKey("orderFreezePoint")){
            orderFreezePoint = DateUtils.getFormatFloat(result.getFloatValue("orderFreezePoint"));
        }
        if(result.containsKey("convertFreezePoint")){
            convertFreezePoint = DateUtils.getFormatFloat(result.getFloatValue("convertFreezePoint"));
        }
        if(result.containsKey("account")){
            account = result.getString("account");
        }
        currentPoints.setText(String.valueOf(validPoint+orderFreezePoint+convertFreezePoint));
        freezePoints.setText(String.valueOf(orderFreezePoint+convertFreezePoint));
        useablePoints.setText(String.valueOf(validPoint));
        accountView.setText(account);
    }


    public static PhoneRechargeDialogFragment newInstance() {
        PhoneRechargeDialogFragment fragment = new PhoneRechargeDialogFragment();
        return fragment;
    }


}
