package cn.fuyoushuo.fqbb.domain.ext;

import java.io.Serializable;

/**
 * Created by QA on 2016/6/30.
 * 接受http返回的数据
 */
public class HttpResp implements Serializable {


    private String m;

    private Object r;

    private Integer s;

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public Object getR() {
        return r;
    }

    public void setR(Object r) {
        this.r = r;
    }

    public Integer getS() {
        return s;
    }

    public void setS(Integer s) {
        this.s = s;
    }
}
