package cn.fuyoushuo.fqbb.domain.entity;

import java.io.Serializable;

/**
 * Created by QA on 2016/11/11.
 */
public class DuihuanItem implements Serializable {


   private Integer orderStatus;

   private String dateTimeString;

   private String mobilePhone;

   private String orderDetail;


    public String getDateTimeString() {
        return dateTimeString;
    }

    public void setDateTimeString(String dateTimeString) {
        this.dateTimeString = dateTimeString;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(String orderDetail) {
        this.orderDetail = orderDetail;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }
}
