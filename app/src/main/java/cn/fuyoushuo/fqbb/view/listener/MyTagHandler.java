package cn.fuyoushuo.fqbb.view.listener;

import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import org.xml.sax.XMLReader;

public class MyTagHandler implements Html.TagHandler {

    private int startIndex = 0;
    private int stopIndex = 0;

    private String tagName;

    private OnClickTag onClickTag;

    public MyTagHandler(String tagName,OnClickTag onClickTag) {
        this.tagName = tagName;
        this.onClickTag = onClickTag;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output,
            XMLReader xmlReader) {
        if (tag.equals(tagName)) {
            if (opening) {  
                startGame(tag, output, xmlReader);  
            } else {  
                endGame(tag, output, xmlReader);  
            }  
        }   

    }  
    public void startGame(String tag, Editable output, XMLReader xmlReader) {  
        startIndex = output.length();  
    }  

    public void endGame(String tag, Editable output, XMLReader xmlReader) {  
        stopIndex = output.length();  
        output.setSpan(new clickSpan(), startIndex, stopIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }  

    private class clickSpan extends ClickableSpan{

        @Override
        public void onClick(View v) {
              if(onClickTag != null){
                  onClickTag.onClick();
              }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
              ds.clearShadowLayer();

        }
    }

    public interface OnClickTag{

         void onClick();
    }
}