package cn.fuyoushuo.fqbb.view.view;

import java.util.List;

/**
 * Created by QA on 2016/7/11.
 */
public interface SearchPromptView {

     public void updateHotWords(List<String> items);

     public void updateAutoCompHisWords(List<String> words);

     public void updateAutoCompHotWords(List<String> words);

}
