package cn.fuyoushuo.fqbb.view.flagment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.view.flagment.order.LocalOrderFragment;
import cn.fuyoushuo.fqbb.view.flagment.order.TbOrderFragment;

/**
 * Created by QA on 2016/11/4.
 */
public class MyOrderFragment extends BaseFragment{


    @Bind(R.id.myorder_type_title)
    TabLayout myTablayout;

    @Bind(R.id.order_result_detail)
    ViewPager orderViewPage;

    private Map<String,Fragment> fragmentMap = new LinkedHashMap<String, Fragment>();

    public Map<String, Fragment> getFragmentMap() {
        return fragmentMap;
    }

    public void setFragmentMap(Map<String, Fragment> fragmentMap) {
        this.fragmentMap = fragmentMap;
    }

    @Override
    protected String getPageName() {
        return "myOrder";
    }

    @Override
    protected int getRootLayoutId() {
        return R.layout.flagment_myorder;
    }

    @Override
    protected void initView() {
        FragmentManager childFragmentManager = getChildFragmentManager();
        MyPageAdapter myPageAdapter = new MyPageAdapter(childFragmentManager);
        for(Map.Entry<String,Fragment> entry : this.getFragmentMap().entrySet()){
            myPageAdapter.addFragment(entry.getValue(),entry.getKey());
        }
        orderViewPage.setAdapter(myPageAdapter);
        myTablayout.setupWithViewPager(orderViewPage);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        orderViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                 return;
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                    ((LocalOrderFragment)(fragmentMap.get("localOrder"))).loadWebview();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                 return;
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void initData() {

    }

    public static MyOrderFragment newInstance() {
        MyOrderFragment fragment = new MyOrderFragment();
        fragment.getFragmentMap().put("tbOrder",new TbOrderFragment());
        fragment.getFragmentMap().put("localOrder",LocalOrderFragment.newInstance());
        return fragment;
    }

    public void reflashLocalOrder(){
        ((LocalOrderFragment)(fragmentMap.get("localOrder"))).loadWebview();
    }

    static class MyPageAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }
        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if("tbOrder".equals(mFragmentTitles.get(position))){
                 return "现金订单(淘宝天猫)";
            }
            else if("localOrder".equals(mFragmentTitles.get(position))){
                return "积分订单";
            }
            return mFragmentTitles.get(position);
        }

        public List<String> getmFragmentTitles() {
            return mFragmentTitles;
        }

        public List<Fragment> getmFragments() {
            return mFragments;
        }
    }

}
