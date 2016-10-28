package cn.fuyoushuo.fqbb.view.flagment.login;

import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.view.flagment.BaseFragment;

/**
 * 用于管理登录页面
 * Created by QA on 2016/10/28.
 */
public class FindPassOneFragment extends BaseFragment {

    public static final String TAG_NAME = "find_pass_one_fragment";


    @Override
    protected int getRootLayoutId() {
        return R.layout.fragment_find_pass_1;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }


    public static FindPassOneFragment newInstance() {
        FindPassOneFragment fragment = new FindPassOneFragment();
        return fragment;
    }

}
