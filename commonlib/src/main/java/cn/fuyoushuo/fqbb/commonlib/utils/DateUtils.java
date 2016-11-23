package cn.fuyoushuo.fqbb.commonlib.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Project CommonProj
 * @Packate com.micky.commonlib.utils
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2016-01-08 10:30
 * @Version 1.0
 */
public class DateUtils {
    private static final String DATE_SHORT_FORMAT = "yyyy-MM-dd";
    private static final String DATE_WEEK_FORMAT = "yyyy-MM-dd EEEE";

    public static String getWeekDay(String dateStr) {
        String result = "";
        try {
           Date date = new SimpleDateFormat(DATE_SHORT_FORMAT).parse(dateStr);
            result = getWeekDate(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getWeekDate(Date date) {
        SimpleDateFormat dateFm = new SimpleDateFormat(DATE_WEEK_FORMAT);
        return dateFm.format(date);
    }

    public static Float getFormatFloat(Float f){
        if(f == null){
            return 0.00f;
        }
        BigDecimal b = new BigDecimal(f);
        float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return f1;
    }

    public static Integer floatToInt(Float f){
        if(f == null){
            return 0;
        }
        BigDecimal b = new BigDecimal(f);
        int value = b.intValue();
        return value;
    }
}
