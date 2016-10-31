package cn.fuyoushuo.fqbb.view.flagment.searchpromt;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.view.flagment.BaseFragment;
import cn.fuyoushuo.fqbb.view.flagment.SearchPromptFragment;

/**
 * Created by QA on 2016/10/31.
 */
public class SearchPromtOriginFragment extends BaseFragment{

    @Bind(R.id.searchHisRview)
    TagFlowLayout searchHisRview;

    List<String> hisWords;

    @Bind(R.id.searchHotRview)
    TagFlowLayout searchHotRview;

    List<String> hotWords;

    LayoutInflater layoutInflater;


    @Override
    protected int getRootLayoutId() {
        return R.layout.origin_promt_search_view;
    }

    @Override
    protected void initView() {
        hisWords = new ArrayList<String>();
        searchHisRview.setAdapter(new TagAdapter<String>(hisWords) {
            @Override
            public View getView(FlowLayout parent, int position, String o) {
                RelativeLayout view = (RelativeLayout)layoutInflater.inflate(R.layout.search_prompt_item, searchHisRview, false);
                TextView textView = (TextView) view.findViewById(R.id.search_prompt_item_text);
                textView.setText(o);
                return view;
            }
        });

        hotWords = new ArrayList<String>();
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
        // 初始化默认 flagment
        ((SearchPromptFragment)getParentFragment()).initPromtOrigin();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        layoutInflater = LayoutInflater.from(getActivity());
    }

    @Override
    protected void initData() {

    }

    public static SearchPromtOriginFragment newInstance() {
        SearchPromtOriginFragment fragment = new SearchPromtOriginFragment();
        return fragment;
    }

   //---------------------------用于和上层FLAGMENT通信----------------------------------------------------

   public void refreshHisData(List<String> items){
       hisWords.clear();
       hisWords.addAll(items);
       searchHisRview.getAdapter().notifyDataChanged();
   }

   public void refreshHotData(List<String> items){
       hotWords.clear();
       hotWords.addAll(items);
       searchHotRview.getAdapter().notifyDataChanged();
   }

}
