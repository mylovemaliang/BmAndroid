package cn.fuyoushuo.fqbb.domain.entity;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by QA on 2016/6/27.
 */
public class GoodItem implements Serializable{

    private String itemName;

    private String imageUrl;

    private Bitmap bitmap;

    public GoodItem() {
    }

    public GoodItem(String itemName, String imageUrl) {
        this.itemName = itemName;
        this.imageUrl = imageUrl;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
