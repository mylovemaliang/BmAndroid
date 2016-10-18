package cn.fuyoushuo.fqbb.presenter.impl;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.fuyoushuo.fqbb.commonlib.utils.CommonUtils;
import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.domain.httpservice.TaoBaoSuggestHttpService;
import cn.fuyoushuo.fqbb.view.flagment.SearchPromptFragment;
import cn.fuyoushuo.fqbb.view.view.SearchPromptView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/7/21.
 */
public class SearchPromptPresenter extends BasePresenter{

    private WeakReference<SearchPromptView> searchPromptView;

    private WeakReference<SharedPreferences> sharedPreferences;

    public SearchPromptPresenter(SearchPromptView searchPromptView,SharedPreferences sharedPreferences) {
        this.searchPromptView = new WeakReference<SearchPromptView>(searchPromptView);
        this.sharedPreferences = new WeakReference<SharedPreferences>(sharedPreferences);
    }

    public void getHotSearchWords(){
        final List<String> resultWords = new ArrayList<String>();
        mSubscriptions.add(ServiceManager.createService(TaoBaoSuggestHttpService.class).getTaobaoHotWords()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //解决回调异常问题
                        try{
                           if(searchPromptView.get() != null) {
                               searchPromptView.get().updateHotWords(resultWords);
                           }
                        }catch (Exception t){

                        }
                   }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                         if(jsonObject == null){
                             if(searchPromptView.get() != null) {
                                 searchPromptView.get().updateHotWords(resultWords);
                             }
                         }
                        JSONArray querys = jsonObject.getJSONArray("querys");
                        if(querys != null && !querys.isEmpty()){
                            Iterator<Object> queryIterator = querys.iterator();
                            while(queryIterator.hasNext()){
                                Object next = queryIterator.next();
                                String word = (String) next;
                                resultWords.add(word);
                            }
                        }
                        if(searchPromptView.get() != null) {
                            searchPromptView.get().updateHotWords(resultWords);
                        }
                    }
                })
        );
    }

    //请求数据
    public void searchWordsByKey(String key){
        if(TextUtils.isEmpty(key)) return;
        //结果视图
        final List<String> hotWords = new ArrayList<String>();
        Observable<List<String>> hisMatchedWordsObserver = getHisMatchedWords(key);
        Observable<JSONObject> taobaoSuggestWordsObserver = ServiceManager.createService(TaoBaoSuggestHttpService.class).getTaobaoSuggestWords(key);

        mSubscriptions.add(Observable.merge(hisMatchedWordsObserver,taobaoSuggestWordsObserver)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Subscriber<Object>() {
                          @Override
                          public void onCompleted() {

                          }

                          @Override
                          public void onError(Throwable e) {
                              if(searchPromptView.get() != null){
                                searchPromptView.get().updateAutoCompHotWords(hotWords);
                                searchPromptView.get().updateAutoCompHisWords(new ArrayList<String>());
                           }
                          }

                          @Override
                          public void onNext(Object o) {
                              if(o instanceof List){
                                  if(searchPromptView.get() != null){
                                    searchPromptView.get().updateAutoCompHisWords((List<String>) o);
                                  }
                              }else if(o instanceof JSONObject){
                                  if(o != null) {
                                      JSONObject jsonObject = (JSONObject) o;
                                      JSONArray result = jsonObject.getJSONArray("result");
                                      if(result != null){
                                          List<String> taobaoSuggWords = new ArrayList<String>();
                                          Iterator<Object> resultIterator = result.iterator();
                                          while(resultIterator.hasNext()){
                                              Object next = resultIterator.next();
                                              JSONArray nextArray = (JSONArray) next;
                                              taobaoSuggWords.add(nextArray.getString(0));
                                          }
                                          if(searchPromptView.get() != null){
                                            searchPromptView.get().updateAutoCompHotWords(taobaoSuggWords);
                                        }
                                      }
                                  }
                              }
                          }
                      })
        );
    }


    //产生观察者
    private Observable<List<String>> getHisMatchedWords(final String key){
        return Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                  List<String> resultList = new ArrayList<String>();
                  if(sharedPreferences.get() != null){
                    String resultString = sharedPreferences.get().getString(SearchPromptFragment.SEARCH_KEY, null);
                    List<String> strings = CommonUtils.toStringList(resultString);
                    if(!strings.isEmpty()){
                     Iterator<String> iterator = strings.iterator();
                     while (iterator.hasNext()){
                     String next = iterator.next();
                     if(next.startsWith(key)){
                         resultList.add(next);
                     }
                  }
                 }
               }
                subscriber.onNext(resultList);
            }
        });
    }
}
