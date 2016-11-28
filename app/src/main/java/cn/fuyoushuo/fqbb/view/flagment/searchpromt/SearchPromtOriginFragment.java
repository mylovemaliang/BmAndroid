package cn.fuyoushuo.fqbb.view.flagment.searchpromt;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.FragmentEvent;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.RxBus;
import cn.fuyoushuo.fqbb.view.flagment.BaseFragment;
import cn.fuyoushuo.fqbb.view.flagment.SearchPromptFragment;
import rx.functions.Action1;

/**
 * Created by QA on 2016/10/31.
 */
public class SearchPromtOriginFragment extends BaseFragment{

    @Bind(R.id.searchHisRview)
    TagFlowLayout searchHisRview;

    List<String> hisWords;

    @Bind(R.id.searchHotRview)
    TagFlowLayout searchHotRview;

    @Bind(R.id.search_promt_deleteHis_area)
    RelativeLayout deleteArea;

    @Bind(R.id.search_promt_deleteHis_text)
    TextView deleteText;

    List<String> hotWords;

    LayoutInflater layoutInflater;


    @Override
    protected String getPageName() {
        return "searchTip_origin";
    }

    @Override
    protected int getRootLayoutId() {
        return R.layout.origin_promt_search_view;
    }

    @Override
    protected void initView() {
        searchHisRview.setAdapter(new TagAdapter<String>(hisWords) {
            @Override
            public View getView(FlowLayout parent, int position, String o) {
                RelativeLayout view = (RelativeLayout)layoutInflater.inflate(R.layout.search_prompt_item, searchHisRview, false);
                TextView textView = (TextView) view.findViewById(R.id.search_prompt_item_text);
                textView.setText(o);
                return view;
            }
        });

        searchHotRview.setAdapter(new TagAdapter<String>(hotWords) {
            @Override
            public View getView(FlowLayout parent, int position, String o) {
                RelativeLayout view = (RelativeLayout)layoutInflater.inflate(R.layout.search_prompt_item, searchHotRview, false);
                TextView textView = (TextView) view.findViewById(R.id.search_prompt_item_text);
                textView.setText(o);
                return view;
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        searchHisRview.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                String target = hisWords.get(position);
                ((SearchPromptFragment)getParentFragment()).clickHisItem(target);
                return true;
            }
        });

        //
        searchHotRview.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                String target = hotWords.get(position);
                ((SearchPromptFragment)getParentFragment()).clickHotItem(target);
                return true;
            }
        });

        RxView.clicks(deleteArea).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if(hisWords == null || hisWords.isEmpty()){
                            Toast.makeText(MyApplication.getContext(),"历史搜索记录已清空",Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            RxBus.getInstance().send(new ClearHisItemsEvent());
                            Toast.makeText(MyApplication.getContext(),"历史搜索记录清空成功",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        initIconFront();
        //Log.d("lifecycleTest","SearchPromptOriginFragment onViewCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        //Log.d("lifecycleTest","SearchPromptOriginFragment onstart");
        RxBus.getInstance().send(new RefreshSearchPromtOriginEvent());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        layoutInflater = LayoutInflater.from(getActivity());
    }

    @Override
    protected void initData() {
        hisWords = new ArrayList<String>();
        hotWords = new ArrayList<String>();
        Log.d("lifecycleTest","SearchPromptOriginFragment onCreate");

    }

    public static SearchPromtOriginFragment newInstance() {
        SearchPromtOriginFragment fragment = new SearchPromtOriginFragment();
        return fragment;
    }

    //初始化字体图标
    private void initIconFront() {
        Typeface iconfont = Typeface.createFromAsset(getActivity().getAssets(), "iconfront/iconfont_delete.ttf");
        deleteText.setTypeface(iconfont);
    }

   //---------------------------用于和上层FLAGMENT通信----------------------------------------------------

   public void refreshHisData(List<String> items){
       hisWords.clear();
       hisWords.addAll(items);
       if(searchHisRview != null && searchHisRview.getAdapter() != null){
          searchHisRview.getAdapter().notifyDataChanged();
       }
   }

   public void refreshHotData(List<String> items){
       hotWords.clear();
       hotWords.addAll(items);
       if(searchHotRview != null && searchHotRview.getAdapter() != null) {
           searchHotRview.getAdapter().notifyDataChanged();
       }
   }


  //---------------------------Event定义-------------------------------------------------------

  public class RefreshSearchPromtOriginEvent extends RxBus.BusEvent{}

  public class ClearHisItemsEvent extends RxBus.BusEvent{}

}
