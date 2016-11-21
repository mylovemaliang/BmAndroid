package cn.fuyoushuo.fqbb.view.flagment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.trello.rxlifecycle.components.support.RxFragment;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.view.activity.BaseActivity;

/**
 * Fragment 的基本抽象
 */
public abstract class BaseFragment extends RxFragment {

    protected BaseActivity mactivity;

    private boolean mAutoBindView;

    protected boolean isInit = false;

    public BaseFragment() {
        // Required empty public constructor
    }

    /**
     * 获取当前布局的ID
     * @return
     */
    protected abstract String getPageName();

    protected abstract int getRootLayoutId();

    protected void initView(){}

    /**
     * 初始化组件数据
     */
    protected abstract void initData();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mactivity = (BaseActivity) getActivity();
        mAutoBindView = true;
        initData();
        setRetainInstance(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflateView = inflater.inflate(getRootLayoutId(), container, false);
        setupUI(inflateView,getActivity());
        if(mAutoBindView){
            ButterKnife.bind(this,inflateView);
        }
        initView();
        initView(inflateView);
        isInit = true;
        return inflateView;
    }


    protected  void initView(View inflateView){};

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAutoBindView){
            ButterKnife.unbind(this);
        }
    }

    //当点击edittext 以外的地方,隐藏键盘
    protected void setupUI(View view,final Activity context) {
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

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getPageName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getPageName());
    }
}
