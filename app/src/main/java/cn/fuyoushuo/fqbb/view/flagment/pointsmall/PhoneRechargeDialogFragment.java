package cn.fuyoushuo.fqbb.view.flagment.pointsmall;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxDialogFragment;
import com.umeng.analytics.MobclickAgent;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.DateUtils;
import cn.fuyoushuo.fqbb.presenter.impl.LocalLoginPresent;
import cn.fuyoushuo.fqbb.presenter.impl.pointsmall.PhoneRechargePresent;
import cn.fuyoushuo.fqbb.view.view.pointsmall.PhoneRechargeView;
import rx.functions.Action1;

/**
 * Created by QA on 2016/11/7.
 */
public class PhoneRechargeDialogFragment extends RxDialogFragment implements PhoneRechargeView{


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

    private List<JSONObject> phoneRecharges;

    //手机充值账号
    private String inputPhoneNumValue = "";
    //可用积分
    private float useAblePoints = 0f;
    //需要使用的积分
    private float toUsePoints = 0f;

    //当前选中的skuId
    private Long skuId = null;

    LayoutInflater layoutInflater;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.fullScreenDialog);
        localLoginPresent = new LocalLoginPresent();
        phoneRechargePresent = new PhoneRechargePresent(this);
        phoneRecharges = new ArrayList<JSONObject>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_pointsmall_phonerecharge_dialog, container);
        ButterKnife.bind(this,inflate);
        setupUI(inflate,getActivity());
        myTagFlowLayout.setAdapter(new TagAdapter(phoneRecharges) {
            @Override
            public View getView(FlowLayout parent, int position, Object o) {
                RelativeLayout view = (RelativeLayout) layoutInflater.inflate(R.layout.phone_recharge_item,myTagFlowLayout,false);
                TextView textView = (TextView) view.findViewById(R.id.phone_recharge_item_text);
                JSONObject item = new JSONObject((Map)o);
                if(item != null && !item.isEmpty()){
                    String priceYuan = item.getString("originalPriceYuan");
                    textView.setText(priceYuan+"元");
                }
                return view;
            }
        });

        return inflate;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myTagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                JSONObject result = phoneRecharges.get(position);
                if(result != null && !result.isEmpty()){
                    String needPoints = result.getInteger("price").toString();
                    skuId = result.getLong("skuId");
                    if(!TextUtils.isEmpty(needPoints)){
                      toUsePoints = Float.valueOf(needPoints);
                      rechargeNeedPoints.setText("所需积分:"+needPoints);
                    }
                }
                return true;
            }
        });

        RxTextView.textChanges(inputPhoneNum).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                          inputPhoneNumValue = charSequence.toString();
                    }
                });

        RxView.clicks(backArea).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        dismissAllowingStateLoss();
                    }
                });

        RxView.clicks(duihuanCommitButton).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000,TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // TODO: 2016/11/7
                        if(skuId != null && useAblePoints > 0 && useAblePoints >= toUsePoints && !TextUtils.isEmpty(inputPhoneNumValue)){
                            phoneRechargePresent.createPhoneRechargeOrder(skuId,inputPhoneNumValue);
                        }
                    }
                });
        //编辑框类型
        inputPhoneNum.setInputType(InputType.TYPE_CLASS_PHONE);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        layoutInflater = LayoutInflater.from(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localLoginPresent.onDestroy();
        phoneRechargePresent.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        localLoginPresent.getUserInfo(new LocalLoginPresent.UserInfoCallBack() {
            @Override
            public void onUserInfoGetSucc(JSONObject jsonObject) {
                initUserInfo(jsonObject);
            }

            @Override
            public void onUserInfoGetError() {
                currentPoints.setText("--");
                freezePoints.setText("--");
                useablePoints.setText("--");
                accountView.setText("--");
                Toast.makeText(MyApplication.getContext(),"获取用户信息失败",Toast.LENGTH_SHORT).show();
            }
        });

        phoneRechargePresent.getPhoneRechargeSkus();
        rechargeNeedPoints.setText("所需积分:0");
        inputPhoneNum.setText("");
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
            useAblePoints = validPoint;
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

   //---------------------------------------VIEW接口的实现-------------------------------------------------

    @Override
    public void onPhoneRechargeSkuGetSucc(List<JSONObject> results) {
        if(phoneRecharges != null){
            phoneRecharges.clear();
            phoneRecharges.addAll(results);
        }
        if(myTagFlowLayout.getAdapter() != null){
            myTagFlowLayout.getAdapter().notifyDataChanged();
        }
    }

    @Override
    public void onPhoneRechargeSkuGetFail() {
        Toast.makeText(MyApplication.getContext(),"手机兑换券获取失败,请稍后重试",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPhoneRechargeSucc() {
        Toast.makeText(MyApplication.getContext(),"手机充值成功",Toast.LENGTH_SHORT).show();
        localLoginPresent.getUserInfo(new LocalLoginPresent.UserInfoCallBack() {
            @Override
            public void onUserInfoGetSucc(JSONObject jsonObject) {
                initUserInfo(jsonObject);
            }

            @Override
            public void onUserInfoGetError() {
                currentPoints.setText("--");
                freezePoints.setText("--");
                useablePoints.setText("--");
                accountView.setText("--");
                Toast.makeText(MyApplication.getContext(),"获取用户信息失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPhoneRechargeFail(String msg) {
        Toast.makeText(MyApplication.getContext(),"手机充值失败",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("pointsMall-phoneRecharge");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("pointsMall-phoneRecharge");
    }

    //----------------------------------处理编辑框-------------------------------------------------------
    //当点击edittext 以外的地方,隐藏键盘
    private void setupUI(View view,final Activity context) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                    return false;
                }
            });

        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView,context);
            }
        }
    }
}
