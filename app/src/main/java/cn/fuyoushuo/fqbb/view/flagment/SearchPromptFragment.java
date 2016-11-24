package cn.fuyoushuo.fqbb.view.flagment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trello.rxlifecycle.FragmentEvent;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.CommonUtils;
import cn.fuyoushuo.fqbb.commonlib.utils.RxBus;
import cn.fuyoushuo.fqbb.commonlib.utils.SeartchPo;
import cn.fuyoushuo.fqbb.domain.ext.SearchCondition;
import cn.fuyoushuo.fqbb.presenter.impl.SearchPromptPresenter;
import cn.fuyoushuo.fqbb.view.Layout.AutoCompleteWindow;
import cn.fuyoushuo.fqbb.view.Layout.SearchTypeMenu;
import cn.fuyoushuo.fqbb.view.adapter.SearchMenuAdapter;
import cn.fuyoushuo.fqbb.view.flagment.searchpromt.SearchAutoCompleteFragment;
import cn.fuyoushuo.fqbb.view.flagment.searchpromt.SearchPromtOriginFragment;
import cn.fuyoushuo.fqbb.view.view.SearchPromptView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by QA on 2016/7/8.
 */
public class SearchPromptFragment extends BaseFragment implements SearchPromptView{

    public static String TAG_NAME = "search_prompt_flagment";

    public static final int MAIN_FLAGMENT = 0;

    public static final int SEARCH_FLAGMENT = 1;

    public static final String SEARCH_HISTORY = "search_history";

    public static final String SEARCH_KEY = "search_key";

    private boolean isViewBuild = false;

    //默认从首页跳转过来
    private int fromFlagment = MAIN_FLAGMENT;

    @Bind(R.id.search_prompt_flagment_toolbar)
    RelativeLayout toolbar;

    @Bind(R.id.serach_prompt_flagment_searchText)
    EditText searchText;

    @Bind(R.id.search_prompt_flagment_cancel_area)
    View cancelView;

    //当前搜索的存储信息
    SeartchPo seartchPo = new SeartchPo();

    SearchTypeMenu searchTypeMenu;

    //自动提示功能
    //AutoCompleteWindow autoCompleteWindow;

    private SharedPreferences mSharePreference;

    private SearchPromptPresenter searchPromptPresenter;

    LayoutInflater layoutInflater;

    InputMethodManager inputMethodManager;

    //是否需要刷新
    private boolean isReflash = false;

    SearchPromtOriginFragment searchPromtOriginFragment;

    SearchAutoCompleteFragment searchAutoCompleteFragment;

    private Fragment mContent;


    public SearchPromptFragment() {
        seartchPo.setQ("");
        seartchPo.setSearchType(SearchCondition.search_cate_superfan);
    }

    @Override
    protected String getPageName() {
        return "searchTip";
    }

    //绑定 flagment 的来源
    public void bindFromFlagment(int from){
        this.fromFlagment = from;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFlagment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchPromptFragment newInstance() {
        SearchPromptFragment fragment = new SearchPromptFragment();
        return fragment;
    }

    @Override
    protected int getRootLayoutId() {
        return R.layout.flagment_search_prompt;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
        searchPromptPresenter = new SearchPromptPresenter(this,mSharePreference);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        layoutInflater = LayoutInflater.from(getActivity());
        inputMethodManager = (InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        mSharePreference = MyApplication.getContext().getSharedPreferences(SEARCH_HISTORY, Activity.MODE_PRIVATE);

    }

    @Override
    public void onPause() {
        super.onPause();
        searchPromptPresenter.onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destoryPopupView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        RxTextView.textChanges(searchText)
                .debounce(150,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .flatMap(new Func1<CharSequence, Observable<String>>() {
                    @Override
                    public Observable<String> call(CharSequence charSequence) {
                        String q = charSequence.toString();
                        if(!TextUtils.isEmpty(q)){
                            switchContent(mContent,searchAutoCompleteFragment);
                        }
                        return Observable.just(q);
                    }
                })
                .subscribe(new Subscriber<String>(){
                    @Override
                    public void onCompleted() {
                       return;
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO: 2016/9/5  自定义异常事件
                        return;
                    }

                    @Override
                    public void onNext(String s) {
                        String q = s;
                        if (!TextUtils.isEmpty(q)) {
                            searchPromptPresenter.searchWordsByKey(q);
                        }else if(TextUtils.isEmpty(q)) {
                             switchContent(mContent,searchPromtOriginFragment);
                        }
                        seartchPo.setQ(q);
                    }
                });



        //
        searchTypeMenu.setOnItemClick(new SearchTypeMenu.OnItemClick() {
            @Override
            public void onclick(View view, SearchMenuAdapter.RowItem rowItem) {
                searchTypeMenu.dismissWindow();
                if (SearchCondition.search_cate_superfan.equals(rowItem.getSortCode())) {
                    seartchPo.setSearchType(SearchCondition.search_cate_superfan);
                } else if (SearchCondition.search_cate_commonfan.equals(rowItem.getSortCode())) {
                    seartchPo.setSearchType(SearchCondition.search_cate_commonfan);
                } else if (SearchCondition.search_cate_taobao.equals(rowItem.getSortCode())) {
                    seartchPo.setSearchType(SearchCondition.search_cate_taobao);
                }
            }
        });

        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    //保存当前搜索词
                    String q = seartchPo.getQ();
                    /*隐藏软键盘*/
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    if(!TextUtils.isEmpty(q)){
                       saveSearch(q);
                       RxBus.getInstance().send(new ToSearchFlagmentEvent(seartchPo));
                    }
                }
//                if(keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN){
//                    //解决键盘删除的BUG
//                    if(!TextUtils.isEmpty(seartchPo.getQ())){
//                        int index = searchText.getSelectionStart();
//                        Editable editable = searchText.getText();
//                        editable.delete(index-1, index);
//                    }
//                }
                return false;
            }
        });

        RxView.clicks(cancelView).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                //关闭键盘
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(searchText.getApplicationWindowToken(), 0);
                }

                if (fromFlagment == MAIN_FLAGMENT) {
                    RxBus.getInstance().send(new BacktoMainFlagEvent());
                } else if (fromFlagment == SEARCH_FLAGMENT) {
                    RxBus.getInstance().send(new BacktoSearchFlagEvent());
                }
            }
        });
    }

    @Override
    public void initView() {

        searchText.requestFocus();

        searchTypeMenu = new SearchTypeMenu(getActivity(), toolbar).init();

        initChildFragments();

        searchText.setFocusable(true);
        searchText.requestFocus();

        //更新数据
        searchText.setText(seartchPo.getQ());
        isViewBuild = true;
    }

    //初始化 子flagment
    public void initChildFragments(){
        searchPromtOriginFragment = SearchPromtOriginFragment.newInstance();
        searchAutoCompleteFragment = SearchAutoCompleteFragment.newInstance();
        FragmentManager childFragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.search_promt_flagment_area,searchPromtOriginFragment).show(searchPromtOriginFragment);
        //fragmentTransaction.add(R.id.search_promt_flagment_area,searchAutoCompleteFragment).hide(searchAutoCompleteFragment);
        mContent = searchPromtOriginFragment;
        fragmentTransaction.commitAllowingStateLoss();
    }

    //转换flagment
    public void switchContent(Fragment from,Fragment to){
        if (mContent != to) {
            mContent = to;
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.search_promt_flagment_area, to).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    //刷新当前历史搜索
    public void reflashSearchHisItems(){
        String searchHistory = getSearchHistory();
        List<String> items = CommonUtils.toStringList(searchHistory);
        searchPromtOriginFragment.refreshHisData(items);
    }


    //刷新当前view
    public void reflashView(SeartchPo po){
        String searchType = po.getSearchType();
        String q = po.getQ();
        seartchPo.setSearchType(searchType);
        seartchPo.setQ(q);
        if(!TextUtils.isEmpty(q)){
            switchContent(mContent,searchAutoCompleteFragment);
            searchPromptPresenter.searchWordsByKey(q);
            searchText.setText(q);
            searchText.setSelection(q.length());
            searchText.requestFocus();
            inputMethodManager.showSoftInput(searchText,0);
          }
    }

    /**
     *  释放 所有 的 popupwindow
     */
    private void destoryPopupView() {
        if(searchTypeMenu != null){
            searchTypeMenu.dismissWindow();
            searchTypeMenu = null;
        }
    }


    //--------------------------------------------关于历史记录的操作--------------------------------------------

    /**
     * 获取用户最后的搜索词
     * @return
     */
    public String getLastestHistory(){
        String searchHistory = getSearchHistory();
        if(!TextUtils.isEmpty(searchHistory)){
            List<String> strings = CommonUtils.toStringList(searchHistory);
            int length = strings.size();
            return strings.get(length-1);
        }
        return "";
    }



    //获取历史搜索
    public String getSearchHistory(){
        String hisTexts = mSharePreference.getString(SEARCH_KEY, null);
        return hisTexts == null ? "" : hisTexts;
    }

    //保存存储记录
    public void saveSearch(String text) {
        String newText = text;
        String oldTexts = getSearchHistory();
        String resultTexts = "";
        if(TextUtils.isEmpty(oldTexts)){
            resultTexts += ",";
        }
        if (!TextUtils.isEmpty(newText) && !oldTexts.contains(","+newText+",")) {
            List<String> strings = CommonUtils.toStringList(oldTexts);
            if(strings.size() == 30){
                int index = oldTexts.indexOf(",",1);
                oldTexts = oldTexts.substring(index+1,oldTexts.length());
            }
            resultTexts += (oldTexts+newText+",");
        }else{
            resultTexts += oldTexts;
        }
        SharedPreferences.Editor editor = mSharePreference.edit();
        editor.putString(SEARCH_KEY,resultTexts);
        editor.commit();
        //mArrAdapter.notifyDataSetChanged();
    }

    //删除历史操作
    private void cleanHistory() {
        SharedPreferences.Editor editor = mSharePreference.edit();
        editor.putString(SEARCH_KEY,"");
        editor.commit();
    }

    //-------------------------------------通信总线接口-----------------------------------------
    public class BacktoMainFlagEvent extends RxBus.BusEvent{}

    public class BacktoSearchFlagEvent extends RxBus.BusEvent{}

    public class ToSearchFlagmentEvent extends RxBus.BusEvent{

        public ToSearchFlagmentEvent() {
            seartchPo = new SeartchPo();
        }

        public ToSearchFlagmentEvent(SeartchPo seartchPo) {
            this.seartchPo = seartchPo;
        }

        SeartchPo seartchPo;

        public SeartchPo getSeartchPo() {
            return seartchPo;
        }

        public void setSeartchPo(SeartchPo seartchPo) {
            this.seartchPo = seartchPo;
        }
    }

    //-----------------------------------实现VIEW 接口------------------------------------------------
    @Override
    public void updateHotWords(List<String> items) {
          searchPromtOriginFragment.refreshHotData(items);
    }

    @Override
    public void updateAutoCompHisWords(List<String> words) {
         searchAutoCompleteFragment.refreshHisData(words);
    }

    @Override
    public void updateAutoCompHotWords(List<String> words) {
         searchAutoCompleteFragment.refreshHotData(words);
    }

    //-----------------------------------供外部调用---------------------------------------------------
    public void clickHisItem(String item){
        seartchPo.setQ(item);
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(searchText.getApplicationWindowToken(), 0);
        }
        RxBus.getInstance().send(new ToSearchFlagmentEvent(seartchPo));
    }

    public void clickHotItem(String item){
        seartchPo.setQ(item);
        saveSearch(item);
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(searchText.getApplicationWindowToken(), 0);
        }
        RxBus.getInstance().send(new ToSearchFlagmentEvent(seartchPo));
    }

    public void initPromtOrigin(){
        String searchHistory = getSearchHistory();
        List<String> items = CommonUtils.toStringList(searchHistory);
        searchPromtOriginFragment.refreshHisData(items);
        searchPromptPresenter.getHotSearchWords();
    }

    //初始化原始状态
    public void initToOrigin(){
        this.seartchPo = new SeartchPo();
        searchText.setText("");
        initPromtOrigin();
        switchContent(searchAutoCompleteFragment,searchPromtOriginFragment);
    }


}
