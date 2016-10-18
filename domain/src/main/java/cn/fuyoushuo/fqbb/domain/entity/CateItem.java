package cn.fuyoushuo.fqbb.domain.entity;

import java.io.Serializable;

/**
 * Created by QA on 2016/6/27.
 */
public class CateItem implements Serializable{

    private String cateName;

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public CateItem(String cateName) {
        this.cateName = cateName;
    }
}
