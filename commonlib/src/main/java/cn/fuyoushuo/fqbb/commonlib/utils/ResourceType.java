package cn.fuyoushuo.fqbb.commonlib.utils;

/**
 * Created by QA on 2016/6/27.
 */
public enum ResourceType {

    Image(1),

    ;

    private int TypeCode;

    ResourceType(int typeCode) {
        TypeCode = typeCode;
    }
}
