package cn.fuyoushuo.fqbb.domain.ext;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by QA on 2016/7/7.
 */
public class SearchCondition implements Serializable{

    public static String search_cate_taobao = "taobao";

    public static String search_cate_superfan = "superfan";

    public static String search_cate_commonfan = "commonfan";

    private SearchCondition() {}

    public static Map<String,String> searchTypeMap = new HashMap<String,String>();

    static {
        searchTypeMap.put(search_cate_taobao,"淘宝");
        searchTypeMap.put(search_cate_superfan,"超级返");
        searchTypeMap.put(search_cate_commonfan,"返利搜索");
    }

    //另外用途
     public static int search_type_extra= 0;
    //排序
     public static int search_type_sort = 1;
     //筛选
     public static int search_type_choose = 2;
     //搜索框
     public static int search_type_input = 3;

     private String currentSearchCate;

     //搜索条件集合
     private LinkedHashMap<String,SearchItem> searchItems;

     //获得搜索实例
     public static SearchCondition newInstance(String cateName){
         if(TextUtils.isEmpty(cateName)) return null;
         SearchCondition searchCondition = new SearchCondition();
         if(search_cate_taobao.equals(cateName)){
             searchCondition.setCurrentSearchCate(search_cate_taobao);
             searchCondition.generateSearchItemsForTaobao();
             return searchCondition;
         }
        else if(search_cate_superfan.equals(cateName)){
             searchCondition.setCurrentSearchCate(search_cate_superfan);
             searchCondition.generateSearchItemsForChaojifan();
             return searchCondition;
        }
        else if(search_cate_commonfan.equals(cateName)){
             searchCondition.setCurrentSearchCate(search_cate_commonfan);
             searchCondition.generateSearchItemsForFanli();
             return searchCondition;
        }
        return null;
     }

    public static SearchCondition newInstanceWithQ(String cateName,String q){
        SearchCondition searchCondition = newInstance(cateName);
        searchCondition.updateSearchKeyValue("q",q);
        return searchCondition;
    }

    //为淘宝搜索设置查询初始化参数
    private void generateSearchItemsForTaobao(){
        if(searchItems == null){
            searchItems = new LinkedHashMap<String,SearchItem>();
        }else{
            searchItems.clear();
        }
        searchItems.put("search",new SearchItem("search",search_type_extra,"%E6%8F%90%E4%BA%A4"));
        searchItems.put("q",new SearchItem("q",search_type_input,""));
        searchItems.put("tab",new SearchItem("tab",search_type_extra,"all"));
        searchItems.put("n",new SearchItem("n",search_type_extra,20));
        searchItems.put("buying",new SearchItem("buying",search_type_extra,"buyitnow"));
        searchItems.put("m",new SearchItem("m",search_type_extra,"api4h5"));
        searchItems.put("style",new SearchItem("style",search_type_extra,"list"));
        searchItems.put("sort",new SearchItem("sort",search_type_sort,""));
        searchItems.put("start_price",new SearchItem("start_price",search_type_choose,""));
        searchItems.put("end_price",new SearchItem("end_price",search_type_choose,""));
        searchItems.put("filter",new SearchItem("filter",search_type_choose,""));
        searchItems.put("page",new SearchItem("page",search_type_extra,1));
    }

    //为超级返搜索设置查询初始化参数
    private void generateSearchItemsForChaojifan(){
        if(searchItems == null){
            searchItems = new LinkedHashMap<String,SearchItem>();
        }else{
            searchItems.clear();
        }
        searchItems.put("q",new SearchItem("q",search_type_input,""));
        searchItems.put("channel",new SearchItem("channel",search_type_extra,"qqhd"));
        searchItems.put("toPage",new SearchItem("n",search_type_extra,1));
        searchItems.put("perPageSize",new SearchItem("perPageSize",search_type_extra,40));
        searchItems.put("userType",new SearchItem("userType",search_type_extra,""));
        searchItems.put("b2c",new SearchItem("b2c",search_type_extra,""));
        searchItems.put("shopTag",new SearchItem("shopTag",search_type_extra,""));
        searchItems.put("jpmj",new SearchItem("jpmj",search_type_extra,""));
        searchItems.put("startTkRate",new SearchItem("startTkRate",search_type_input,""));
        searchItems.put("endTkRate",new SearchItem("endTkRate",search_type_input,""));
        searchItems.put("startPrice",new SearchItem("startPrice",search_type_input,""));
        searchItems.put("endPrice",new SearchItem("endPrice",search_type_input,""));
        searchItems.put("sortType",new SearchItem("sortType",search_type_sort,""));
    }

    //为普通搜索搜索设置查询初始化参数
    private void generateSearchItemsForFanli(){
        if(searchItems == null){
            searchItems = new LinkedHashMap<String,SearchItem>();
        }else{
            searchItems.clear();
        }
        searchItems.put("q",new SearchItem("q",search_type_input,""));
        searchItems.put("toPage",new SearchItem("n",search_type_extra,1));
        searchItems.put("perPageSize",new SearchItem("perPageSize",search_type_extra,40));
        searchItems.put("userType",new SearchItem("userType",search_type_extra,""));
        searchItems.put("b2c",new SearchItem("b2c",search_type_extra,""));
        searchItems.put("shopTag",new SearchItem("shopTag",search_type_extra,""));
        searchItems.put("jpmj",new SearchItem("jpmj",search_type_extra,""));
        searchItems.put("startTkRate",new SearchItem("startTkRate",search_type_input,""));
        searchItems.put("endTkRate",new SearchItem("endTkRate",search_type_input,""));
        searchItems.put("startPrice",new SearchItem("startPrice",search_type_input,""));
        searchItems.put("endPrice",new SearchItem("endPrice",search_type_input,""));
        searchItems.put("sortType",new SearchItem("sortType",search_type_sort,""));
        searchItems.put("freeShipment",new SearchItem("freeShipment",search_type_choose,""));
    }





     //当前的搜索排序
    public class SearchItem{

         //搜索的Key值
         private String key;

         //当前搜索的type，是排序还是筛选
         private int type;

         //当前的搜索值
         private Object values;

         public SearchItem(String key,int type, Object values) {
             this.key = key;
             this.type = type;
             this.values = values;
         }

         public String getKey() {
             return key;
         }

         public void setKey(String key) {
             this.key = key;
         }

         public int getType() {
             return type;
         }

         public void setType(int type) {
             this.type = type;
         }

         public Object getValues() {
             return values;
         }

         public void setValues(Object values) {
             this.values = values;
         }
     }

    public String getCurrentSearchCate() {
        return currentSearchCate;
    }

    public void setCurrentSearchCate(String currentSearchCate) {
        this.currentSearchCate = currentSearchCate;
    }

    public LinkedHashMap<String, SearchItem> getSearchItems() {
        return searchItems;
    }

    public void setSearchItems(LinkedHashMap<String, SearchItem> searchItems) {
        this.searchItems = searchItems;
    }

    //获取当前排序的SearchMap
    public Map<String,String> getQueryMap(){
        Map<String,String> queryMap = new HashMap<String,String>();
        try{
        if(searchItems != null){
            for(Map.Entry<String,SearchItem> entry : searchItems.entrySet()){
                String tvalue = String.valueOf(entry.getValue().getValues());
                //tvalue = URLEncoder.encode(tvalue,"utf-8");
                queryMap.put(entry.getKey(),tvalue);
            }
        }
            return queryMap;
        }catch (Exception e){
            return new HashMap<String,String>();
        }
    }

    //更新搜索值
    public void updateSearchKeyValue(String key,Object values){
         if(!searchItems.containsKey(key)){
             return;
         }
        SearchItem searchItem = searchItems.get(key);
        searchItem.setValues(values);
    }

    public void deleteSortItem(){
        if(searchItems != null && !searchItems.isEmpty()){
            Set<Map.Entry<String, SearchItem>> entries = searchItems.entrySet();
            for(Map.Entry<String, SearchItem> entry : entries){
                if(search_type_sort == entry.getValue().getType()){
                     searchItems.remove(entry.getKey());
                     break;
                }
            }
        }
    }

    //获取搜索描述
    public static String getSearchTypeDesc(String searchType){
        if(TextUtils.isEmpty(searchType)){
            return "";
        }else{
            return null == searchTypeMap.get(searchType) ? "" : searchTypeMap.get(searchType);
        }
    }
}
