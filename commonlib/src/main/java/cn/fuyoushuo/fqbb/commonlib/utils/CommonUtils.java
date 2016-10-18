package cn.fuyoushuo.fqbb.commonlib.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by QA on 2016/7/11.
 */
public class CommonUtils {


    public static String getShortTitle(String title){
        if(TextUtils.isEmpty(title)){
            return "";
        }
        if(title.length() <= 10){
            return title;
        }
        return title.substring(0,10);
    }

    /**
     * 字符串格式化为字符串数组
     * @param itemsstring
     * @return List<String>
     */
    public static List<String> toStringList(String itemsstring){
        List<String> resultList = new ArrayList<String>();
        if(TextUtils.isEmpty(itemsstring)) return resultList;
        if(itemsstring.startsWith(",") && itemsstring.endsWith(",")){
            itemsstring = itemsstring.substring(1,itemsstring.length()-1);
        }
        String[] split = itemsstring.split(",");
        return Arrays.asList(split);
    }

    /**
     * 得到整百数
     * @param origin
     * @return
     */
    public static int getIntHundred(int origin){
        int hundreds = origin / 100;
        if(hundreds%100 != 0){
            hundreds += 1;
        }
        return hundreds*100;
    }

    /**
     * 得到浮点数的整数部分
     * @param origin
     * @return
     */
    public static int getIntHundred(float origin){
       return (int)origin;
    }
}
