package cn.fuyoushuo.fqbb.domain.entity;

import java.io.Serializable;

/**
 * Created by QA on 2016/11/10.
 */
public class TbCateVo implements Serializable{

    //分类名
    private String CateName;

    //类目ID
    private String CatId;

    //类目层级
    private int level;

    private boolean isRed = false;

    public boolean isRed() {
        return isRed;
    }

    public void setRed(boolean red) {
        isRed = red;
    }

    public TbCateVo() {
    }

    public TbCateVo(String catId, int level, String cateName) {
        CatId = catId;
        this.level = level;
        CateName = cateName;
    }

    public String getCateName() {
        return CateName;
    }

    public void setCateName(String cateName) {
        CateName = cateName;
    }

    public String getCatId() {
        return CatId;
    }

    public void setCatId(String catId) {
        CatId = catId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
