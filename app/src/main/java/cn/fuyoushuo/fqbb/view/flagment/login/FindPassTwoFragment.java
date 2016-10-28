package cn.fuyoushuo.fqbb.view.flagment.login;

import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.view.flagment.BaseFragment;

/**
 * 用于管理登录页面
 * Created by QA on 2016/10/28.
 */
public class FindPassTwoFragment extends BaseFragment {

    public static final String TAG_NAME = "find_pass_two_fragment";

    @Override
    protected int getRootLayoutId() {
        return R.layout.fragment_find_pass_2;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }


    public static FindPassTwoFragment newInstance() {
        FindPassTwoFragment fragment = new FindPassTwoFragment();
        return fragment;
    }

}
