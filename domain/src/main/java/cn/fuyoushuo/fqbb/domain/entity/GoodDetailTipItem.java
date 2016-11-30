package cn.fuyoushuo.fqbb.domain.entity;

import java.io.Serializable;

/**
 * Created by QA on 2016/11/29.
 */

public class GoodDetailTipItem implements Serializable{

    private String tipTitle;

    private String tipContent;

    public String getTipContent() {
        return tipContent;
    }

    public void setTipContent(String tipContent) {
        this.tipContent = tipContent;
    }

    public String getTipTitle() {
        return tipTitle;
    }

    public void setTipTitle(String tipTitle) {
        this.tipTitle = tipTitle;
    }
}
