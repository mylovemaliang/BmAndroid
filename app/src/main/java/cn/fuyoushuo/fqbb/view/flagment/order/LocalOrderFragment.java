package cn.fuyoushuo.fqbb.view.flagment.order;

import android.webkit.WebView;
import android.widget.TextView;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.view.flagment.BaseFragment;

/**
 * Created by QA on 2016/11/4.
 */
public class LocalOrderFragment extends BaseFragment{

    @Bind(R.id.local_order_all)
    TextView allOrderText;

    @Bind(R.id.local_order_allstate)
    TextView allOrderStateText;

    @Bind(R.id.local_order_last30Day)
    TextView last30DayText;

    private WebView orderWebview;

    @Override
    protected int getRootLayoutId() {
        return R.layout.fragment_local_myorder;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }


    public static LocalOrderFragment newInstance() {
        LocalOrderFragment fragment = new LocalOrderFragment();
        return fragment;
    }

}
