package cn.fuyoushuo.fqbb.domain.entity;

import java.io.Serializable;

/**
 * Created by QA on 2016/7/11.
 */
public class TaoBaoItemVo implements Serializable{

    private String item_id;

    private String title;

    private String pic_path;

    private String price;

    private String sold;

    private String url;

    //返钱比例
    private Float tkRate;
    //返钱数
    private Float tkCommFee;

    //返还的集分宝
    private Integer jfbCount;

    private boolean isFanliSearched = false;

    //是否已经load 基本信息
    private boolean isLoaded = false;

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getPic_path() {
        return pic_path;
    }

    public void setPic_path(String pic_path) {
        this.pic_path = pic_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSold() {
        return sold;
    }

    public void setSold(String sold) {
        this.sold = sold;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Float getTkCommFee() {
        return tkCommFee;
    }

    public void setTkCommFee(Float tkCommFee) {
        this.tkCommFee = tkCommFee;
    }

    public Float getTkRate() {
        return tkRate;
    }

    public void setTkRate(Float tkRate) {
        this.tkRate = tkRate;
    }

    public boolean isFanliSearched() {
        return isFanliSearched;
    }

    public void setFanliSearched(boolean fanliSearched) {
        isFanliSearched = fanliSearched;
    }

    public Integer getJfbCount() {
        return jfbCount;
    }

    public void setJfbCount(Integer jfbCount) {
        this.jfbCount = jfbCount;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }
}
