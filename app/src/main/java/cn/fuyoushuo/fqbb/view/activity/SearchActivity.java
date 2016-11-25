package cn.fuyoushuo.fqbb.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.RxBus;
import cn.fuyoushuo.fqbb.commonlib.utils.SeartchPo;
import cn.fuyoushuo.fqbb.domain.ext.SearchCondition;
import cn.fuyoushuo.fqbb.view.flagment.JdWebviewDialogFragment;
import cn.fuyoushuo.fqbb.view.flagment.SearchFlagment;
import cn.fuyoushuo.fqbb.view.flagment.SearchPromptFragment;
import cn.fuyoushuo.fqbb.view.flagment.TbSearchResFlagment;
import cn.fuyoushuo.fqbb.view.flagment.searchpromt.SearchPromtOriginFragment;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by QA on 2016/7/8.
 */
public class SearchActivity extends BaseActivity {


    private static final int FLAG_SEARCHPROMT = 1;

    private static final int FLAG_SEARCH = 2;


    SearchFlagment searchFlagment;

    FragmentManager fragmentManager;

    SearchPromptFragment searchPromptFragment;

    private CompositeSubscription mSubscriptions;

    private Fragment mContent;

    //保存当前呈现的flagment类型
    private int currentFlag;

    public int getCurrentFlag() {
        return currentFlag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        mSubscriptions = new CompositeSubscription();
        searchFlagment = SearchFlagment.newInstance();
        searchPromptFragment = SearchPromptFragment.newInstance();
        fragmentManager = getSupportFragmentManager();
        initFragments();
        initBusEventListen();
    }

    //初始化fragment
    private void initFragments() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.search_flagment_container, searchPromptFragment, SearchPromptFragment.TAG_NAME);
         //fragmentTransaction.add(R.id.search_flagment_container, searchFlagment,SearchFlagment.TAG_NAME);
         //处理crash
         //int resultcode = processCrash();
         //Log.i("testmy","c0"+resultcode);
         //fragmentTransaction.hide(searchFlagment);
         fragmentTransaction.show(searchPromptFragment);
         mContent = searchPromptFragment;
         currentFlag = FLAG_SEARCHPROMT;

        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if(processIntent()){
            //initFragmentToOrigin();
        }
    }

    //判断是否重新重置页面
    private boolean processIntent(){
        Intent intent = getIntent();
        boolean intentFromMain = intent.getBooleanExtra("intentFromMain",true);
        String bizCallBack = intent.getStringExtra("bizCallBack");
        if("SearchToJdWv".equals(bizCallBack)){
            Fragment jdWebviewDialogFragment = getSupportFragmentManager().findFragmentByTag("JdWebviewDialogFragment");
            if(jdWebviewDialogFragment != null) {
                ((JdWebviewDialogFragment)jdWebviewDialogFragment).reloadGoodPage();
            }
        }
        //重置
        if(intentFromMain){
            return true;
        }
        return false;
    }

    private void initFragmentToOrigin(){
        searchPromptFragment.initToOrigin();
        searchFlagment.initToOrigin();
        switchContent(searchFlagment,searchPromptFragment);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //防止fragment 重叠
    }

    //处理crash恢复
    private int processCrash(){
        Intent intent = getIntent();
        boolean isCrash =  intent.getBooleanExtra("isCrash",false);
        int currentFlag = intent.getIntExtra("currentFlag",FLAG_SEARCHPROMT);
        if(!isCrash){
            return 0;
        }
        if(currentFlag == FLAG_SEARCH){
            String lastestHistory = searchPromptFragment.getLastestHistory();
            SeartchPo po = new SeartchPo();
            po.setSearchType(SearchCondition.search_cate_superfan);
            po.setQ(lastestHistory);
            searchFlagment.refreshSearchView(po);
            return FLAG_SEARCH;
        }
        return FLAG_SEARCHPROMT;
    }

    //转换flagment
    public void switchContent(Fragment from, Fragment to) {
        if (mContent != to) {
            mContent = to;
            if(to != null && to instanceof SearchPromptFragment){
                currentFlag = FLAG_SEARCHPROMT;
            }
            else if(to != null && to instanceof SearchFlagment){
                currentFlag = FLAG_SEARCH;
            }
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.search_flagment_container, to).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    //初始化事件总线
    private void initBusEventListen(){
        mSubscriptions.add(RxBus.getInstance().toObserverable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<RxBus.BusEvent>() {
            @Override
            public void call(RxBus.BusEvent busEvent) {
                if (busEvent instanceof SearchFlagment.toSearchPromptFragmentEvent) {
                    SearchFlagment.toSearchPromptFragmentEvent event = (SearchFlagment.toSearchPromptFragmentEvent) busEvent;
                    searchPromptFragment.bindFromFlagment(SearchPromptFragment.SEARCH_FLAGMENT);
                    switchContent(mContent, searchPromptFragment);
                    searchPromptFragment.reflashView(event.getSeartchPo());
                }
                if (busEvent instanceof SearchPromptFragment.ToSearchFlagmentEvent) {
                    SearchPromptFragment.ToSearchFlagmentEvent event = (SearchPromptFragment.ToSearchFlagmentEvent) busEvent;
                    switchContent(mContent, searchFlagment);
                    searchFlagment.refreshSearchView(event.getSeartchPo());
                }
                if (busEvent instanceof SearchPromptFragment.BacktoMainFlagEvent) {
                    goBack();
                }
                if (busEvent instanceof SearchPromptFragment.BacktoSearchFlagEvent) {
                    switchContent(mContent, searchFlagment);
                }
                if (busEvent instanceof SearchFlagment.toMainFlagmentEvent) {
                    goBack();
                }
                if(busEvent instanceof TbSearchResFlagment.toGoodInfoEvent){
                    TbSearchResFlagment.toGoodInfoEvent event = (TbSearchResFlagment.toGoodInfoEvent) busEvent;
                    /*Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                    intent.putExtra("goodUrl",event.getGoodUrl());*/
                    Intent intent = new Intent(SearchActivity.this, WebviewActivity.class);
                    intent.putExtra("loadUrl",event.getGoodUrl());
                    intent.putExtra("forSearchGoodInfo",true);
                    intent.putExtra("bizString","tbGoodDetail");
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
                if(busEvent instanceof SearchPromtOriginFragment.RefreshSearchPromtOriginEvent){
                    searchPromptFragment.initPromtOrigin();
                }
            }
        }));
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack(){
        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSubscriptions.hasSubscriptions()){
            mSubscriptions.unsubscribe();
        }
    }

    //-----------------------------------------------通信接口--------------------------------------

    public class ReflashSearchPromtEvent extends RxBus.BusEvent{

        private SeartchPo seartchPo;

        public ReflashSearchPromtEvent(SeartchPo seartchPo) {
            this.seartchPo = seartchPo;
        }

        public SeartchPo getSeartchPo() {
            return seartchPo;
        }

        public void setSeartchPo(SeartchPo seartchPo) {
            this.seartchPo = seartchPo;
        }
    }

   //---------------------------------------通信接口-------------------------------------------------

}