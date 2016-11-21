package cn.fuyoushuo.fqbb.view.flagment.searchpromt;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.view.adapter.AutoCompleteSearchItemAdapter;
import cn.fuyoushuo.fqbb.view.flagment.BaseFragment;
import cn.fuyoushuo.fqbb.view.flagment.SearchPromptFragment;

/**
 * Created by QA on 2016/10/31.
 */
public class SearchAutoCompleteFragment extends BaseFragment{


    @Bind(R.id.auto_complete_his_rview)
    RecyclerView hisRview;

    AutoCompleteSearchItemAdapter hisAdapter;

    @Bind(R.id.auto_complete_hot_rview)
    RecyclerView hotRview;

    AutoCompleteSearchItemAdapter  hotAdapter;


    @Override
    protected String getPageName() {
        return "searchTip_autoComplete";
    }

    @Override
    protected int getRootLayoutId() {
        return R.layout.auto_complete_search_view;
    }

    @Override
    protected void initView() {
        //定义hisrview
        hisRview.setHasFixedSize(true);
        hisAdapter = new AutoCompleteSearchItemAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        hisRview.setLayoutManager(linearLayoutManager);
        hisAdapter.setOnItemClickListener(new AutoCompleteSearchItemAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, String item) {
              ((SearchPromptFragment)getParentFragment()).clickHisItem(item);
            }
        });
        hisRview.setAdapter(hisAdapter);

        //定义hotrview
        hotRview.setHasFixedSize(true);
        hotAdapter = new AutoCompleteSearchItemAdapter();
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        hotRview.setLayoutManager(linearLayoutManager2);
        hotAdapter.setOnItemClickListener(new AutoCompleteSearchItemAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, String item) {
                ((SearchPromptFragment)getParentFragment()).clickHotItem(item);
            }
        });
        hotRview.setAdapter(hotAdapter);
    }

    @Override
    protected void initData() {

    }

    public static SearchAutoCompleteFragment newInstance() {
        SearchAutoCompleteFragment fragment = new SearchAutoCompleteFragment();
        return fragment;
    }

   //-----------------------------------------与上层flagment通信接口-------------------------------------

   public void refreshHisData(List<String> items){
       if(hisAdapter != null){
         hisAdapter.setData(items);
         hisAdapter.notifyDataSetChanged();
       }
   }

   public void refreshHotData(List<String> items){
        if(hotAdapter != null){
          hotAdapter.setData(items);
          hotAdapter.notifyDataSetChanged();
        }
   }

}
