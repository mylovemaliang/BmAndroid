package cn.fuyoushuo.fqbb.domain.entity;

import java.io.Serializable;

/**
 * Created by QA on 2016/6/30.
 */
public class FGoodItem implements Serializable {

    private Long id;

    private Long cateId;

    private Long dataDate;

    private String dazhe;

    private Long fanli;

    private String fanliPercent;

    private Double fanliYuan;

    private String imageUrl;

    private Integer itemFrom;

    private String itemOutId;

    private Integer itemType;

    private String itemUrl;

    private Integer newTag;

    private String originalPriceYuan;

    private Long price;

    private String priceAloneFen;

    private String priceAloneYuan;

    private String priceYuan;

    private String smallTitle;

    private Integer soldCount;

    private String soldCountStr;

    private Integer status;

    private String title;

    private Integer top;

    private String webSmallTitle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDataDate() {
        return dataDate;
    }

    public void setDataDate(Long dataDate) {
        this.dataDate = dataDate;
    }

    public Long getFanli() {
        return fanli;
    }

    public void setFanli(Long fanli) {
        this.fanli = fanli;
    }

    public String getDazhe() {
        return dazhe;
    }

    public void setDazhe(String dazhe) {
        this.dazhe = dazhe;
    }

    public Long getCateId() {
        return cateId;
    }

    public void setCateId(Long cateId) {
        this.cateId = cateId;
    }

    public String getFanliPercent() {
        return fanliPercent;
    }

    public void setFanliPercent(String fanliPercent) {
        this.fanliPercent = fanliPercent;
    }

    public Double getFanliYuan() {
        return fanliYuan;
    }

    public void setFanliYuan(Double fanliYuan) {
        this.fanliYuan = fanliYuan;
    }

    public Integer getItemFrom() {
        return itemFrom;
    }

    public void setItemFrom(Integer itemFrom) {
        this.itemFrom = itemFrom;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getItemOutId() {
        return itemOutId;
    }

    public void setItemOutId(String itemOutId) {
        this.itemOutId = itemOutId;
    }

    public Integer getItemType() {
        return itemType;
    }

    public void setItemType(Integer itemType) {
        this.itemType = itemType;
    }

    public Integer getNewTag() {
        return newTag;
    }

    public void setNewTag(Integer newTag) {
        this.newTag = newTag;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public String getOriginalPriceYuan() {
        return originalPriceYuan;
    }

    public void setOriginalPriceYuan(String originalPriceYuan) {
        this.originalPriceYuan = originalPriceYuan;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getPriceAloneFen() {
        return priceAloneFen;
    }

    public void setPriceAloneFen(String priceAloneFen) {
        this.priceAloneFen = priceAloneFen;
    }

    public String getPriceAloneYuan() {
        return priceAloneYuan;
    }

    public void setPriceAloneYuan(String priceAloneYuan) {
        this.priceAloneYuan = priceAloneYuan;
    }

    public String getPriceYuan() {
        return priceYuan;
    }

    public void setPriceYuan(String priceYuan) {
        this.priceYuan = priceYuan;
    }

    public String getSmallTitle() {
        return smallTitle;
    }

    public void setSmallTitle(String smallTitle) {
        this.smallTitle = smallTitle;
    }

    public Integer getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(Integer soldCount) {
        this.soldCount = soldCount;
    }

    public String getSoldCountStr() {
        return soldCountStr;
    }

    public void setSoldCountStr(String soldCountStr) {
        this.soldCountStr = soldCountStr;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public String getWebSmallTitle() {
        if(webSmallTitle!=null && webSmallTitle.trim().length()>13)
            return webSmallTitle.trim().substring(0, 13)+"...";
        return webSmallTitle;
    }

    public void setWebSmallTitle(String webSmallTitle) {
        this.webSmallTitle = webSmallTitle;
    }
}
