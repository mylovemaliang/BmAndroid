package cn.fuyoushuo.fqbb.view.flagment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.FragmentEvent;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.EventIdConstants;
import cn.fuyoushuo.fqbb.commonlib.utils.RxBus;
import cn.fuyoushuo.fqbb.commonlib.utils.SeartchPo;
import cn.fuyoushuo.fqbb.domain.ext.SearchCondition;
import cn.fuyoushuo.fqbb.view.Layout.SearchTypeMenu;
import rx.functions.Action1;

/**
 *  SearchFlagment
 */
public class SearchFlagment extends BaseFragment{

    public static String TAG_NAME = "search_flagment";

    @Bind(R.id.search_flagment_toolbar)
    RelativeLayout toolbar;

    @Bind(R.id.serach_flagment_searchText)
    TextView searchText;

    @Bind(R.id.search_flagment_cancel_area)
    View cancelView;

    @Bind(R.id.line1)
    View line1;

    @Bind(R.id.search_type_tabTitle)
    TabLayout tabLayout;

    @Bind(R.id.search_result_page)
    ViewPager viewPager;

    //搜索菜单处理
    SearchTypeMenu searchTypeMenu;

    private static final String ARG_PARAM1 = "q";

    //搜索词
    private String q = "";

    private Map<String,Fragment> fragmentMap = new LinkedHashMap<String, Fragment>();

    public Map<String, Fragment> getFragmentMap() {
        return fragmentMap;
    }

    public void setFragmentMap(Map<String, Fragment> fragmentMap) {
        this.fragmentMap = fragmentMap;
    }

    @Override
    protected String getPageName() {
        return "searchResult";
    }

    @Override
    protected int getRootLayoutId() {
        return R.layout.flagment_search_result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
        if (getArguments() != null) {
            q = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destoryPopupWindow();
        fragmentMap.clear();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        RxView.clicks(cancelView).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                    RxBus.getInstance().send(new toMainFlagmentEvent());
            }
        });

        RxView.clicks(searchText).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                SeartchPo seartchPo = new SeartchPo();
                seartchPo.setQ(q);
                RxBus.getInstance().send(new toSearchPromptFragmentEvent(seartchPo));
            }
        });
    }

    @Override
    public void initView(){
        FragmentManager childFragmentManager = getChildFragmentManager();
        MyPageAdapter myPageAdapter = new MyPageAdapter(childFragmentManager);
        for(Map.Entry<String,Fragment> entry : this.getFragmentMap().entrySet()){
            myPageAdapter.addFragment(entry.getValue(),entry.getKey());
        }
        viewPager.setAdapter(myPageAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                 if(position == 0){
                     MobclickAgent.onEvent(MyApplication.getContext(),EventIdConstants.SEARCH_TYPE_FANLI);
                 }
                 else if(position == 1){
                     MobclickAgent.onEvent(MyApplication.getContext(),EventIdConstants.SEARCH_TYPE_TB);
                 }
                 else if(position == 2){
                     MobclickAgent.onEvent(MyApplication.getContext(),EventIdConstants.SEARCH_TYPE_JD);
                 }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
        if(!isInit){
          if(!TextUtils.isEmpty(q)){
             searchText.setText(q);
          }
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFlagment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFlagment newInstance() {
        SearchFlagment fragment = new SearchFlagment();
        fragment.getFragmentMap().put("超级返利",TbSearchResFlagment.newInstance(SearchCondition.search_cate_superfan));
        fragment.getFragmentMap().put("淘宝返利",TbSearchResFlagment.newInstance(SearchCondition.search_cate_taobao));
        fragment.getFragmentMap().put("京东",new JdSearchResFlagment());
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param alpha
     */
    public void backgroundAlpha(float alpha) {
        Window window = getActivity().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = alpha; //0.0-1.0
        window.setAttributes(lp);
    }

    //--------------------------------组装搜索条件------------------------------------------------------
    //刷新flagment view
    public void refreshSearchView(SeartchPo po){
        String q = po.getQ();
        this.q = q;
        if(isInit){
            searchText.setText(q);
        }
        MobclickAgent.onEvent(MyApplication.getContext(), EventIdConstants.SEARCH_TYPE_FOR_ALL);
        dispatchWhenQChanged(this.q);
    }

    /**
     * 当搜索改变的时候，触发各fragment 执行
     * @param q
     */
    private void dispatchWhenQChanged(String q){
        Map<String, Fragment> fragmentMap = getFragmentMap();
        if(fragmentMap != null && fragmentMap.size() > 0){
            for(Map.Entry<String,Fragment> entry : fragmentMap.entrySet()){
                ((doUpdateWithQ)entry.getValue()).updateQ(q);
            }
        }
    }

    /**
     * 当需要初始化信息的时候，触发各fragment 执行
     */
    private void dispatchWhenToInit(){
        Map<String,Fragment> fragmentMap = getFragmentMap();
        if(fragmentMap != null && fragmentMap.size() > 0){
            for(Map.Entry<String,Fragment> entry : fragmentMap.entrySet()){
                ((doUpdateWithQ)entry.getValue()).initOrigin();
            }
        }
    }

    /**
     * 释放相关 popupwindow 的资源
     */
    private void destoryPopupWindow() {
        if(searchTypeMenu != null){
            searchTypeMenu.dismissWindow();
            searchTypeMenu = null;
        }
    }

    public void initToOrigin() {
        dispatchWhenToInit();
        if(viewPager != null){
            //回到初始位置
            viewPager.setCurrentItem(0);
        }
    }


    //--------------------------------------------------------------------------------------------------

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
            return mFragmentTitles.get(position);
        }

        public List<String> getmFragmentTitles() {
            return mFragmentTitles;
        }

        public List<Fragment> getmFragments() {
            return mFragments;
        }
    }

    //------------------------------与SearcjActivity 通信-----------------------------------------------

    public class toSearchPromptFragmentEvent extends RxBus.BusEvent{

        private  SeartchPo seartchPo;

        public toSearchPromptFragmentEvent(SeartchPo seartchPo) {
            this.seartchPo = seartchPo;
        }

        public SeartchPo getSeartchPo() {
            return seartchPo;
        }

        public void setSeartchPo(SeartchPo seartchPo) {
            this.seartchPo = seartchPo;
        }
    }

    public class toMainFlagmentEvent extends RxBus.BusEvent{}

    //-----------------------------------外部需要实现的接口----------------------------------------------

    /**
     *  当搜索词发生改变所触发动作
     */
    public interface doUpdateWithQ{
        /**
         * 触发动作
         * @param q
         */
        void updateQ(String q);

        void initOrigin();

    }
}
