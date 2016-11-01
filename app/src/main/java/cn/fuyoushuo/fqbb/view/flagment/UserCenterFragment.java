package cn.fuyoushuo.fqbb.view.flagment;

import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.presenter.impl.UserCenterPresenter;
import cn.fuyoushuo.fqbb.view.view.UserCenterView;

/**
 * Created by QA on 2016/10/27.
 */
public class UserCenterFragment extends BaseFragment implements UserCenterView{


    private UserCenterPresenter userCenterPresenter;

    @Bind(R.id.user_center_account)
    TextView accountView;

    @Bind(R.id.userinfo_currentpoints_value)
    TextView currentPoints;

    @Bind(R.id.userinfo_freezepoints_value)
    TextView freezePoints;

    @Bind(R.id.userinfo_useablepoints_value)
    TextView useablePoints;

    @Bind(R.id.userinfo_month_20day_value)
    TextView thisMonth20Count;

    @Bind(R.id.userinfo_nextmonth_20day_value)
    TextView nextMonth20Count;

    @Bind(R.id.userinfo_useable_money_value)
    TextView useableCount;

    @Override
    protected int getRootLayoutId() {
        return R.layout.fragment_user_center;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
      userCenterPresenter = new UserCenterPresenter(this);
    }

    public static UserCenterFragment newInstance() {
        UserCenterFragment fragment = new UserCenterFragment();
        return fragment;
    }

    //----------------------------------用于外部调用--------------------------------------------------

    public void refreshUserInfo(){
        userCenterPresenter.getUserInfo();
    }


   //------------------------------------view 层回调--------------------------------------------------

    @Override
    public void onUserInfoGetError() {
        Toast.makeText(MyApplication.getContext(),"获取用户信息失败",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserInfoGetSucc(JSONObject result) {
         if(result == null || result.isEmpty()) return;
         Float validPoint = 0f;
         Float orderFreezePoint = 0f;
         Float convertFreezePoint = 0f;
         String account = "";
         if(result.containsKey("validPoint")){
             validPoint = result.getFloatValue("validPoint");
         }
         if(result.containsKey("orderFreezePoint")){
             orderFreezePoint = result.getFloatValue("orderFreezePoint");
         }
         if(result.containsKey("convertFreezePoint")){
             convertFreezePoint = result.getFloatValue("convertFreezePoint");
         }
         if(result.containsKey("account")){
             account = result.getString("account");
         }
         currentPoints.setText(String.valueOf(validPoint+orderFreezePoint+convertFreezePoint));
         freezePoints.setText(String.valueOf(orderFreezePoint+convertFreezePoint));
         useablePoints.setText(String.valueOf(validPoint));
         accountView.setText(account);
    }
}
