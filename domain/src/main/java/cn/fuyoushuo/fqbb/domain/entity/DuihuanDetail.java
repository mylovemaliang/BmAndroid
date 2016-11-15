package cn.fuyoushuo.fqbb.domain.entity;

import java.io.Serializable;

/**
 * Created by QA on 2016/11/15.
 */
public class DuihuanDetail implements Serializable {


     private Long userId;

     private Long point;

     private Long prePoint;

     private Long afterPoint;

     private Integer pointType;

     private Integer increase;

     private String orderId;

     private Long bizTime;

     private Integer bizType;

     private Integer bizShop;

     private String orderDesc;

     private String userName;


    public Long getAfterPoint() {
        return afterPoint;
    }

    public void setAfterPoint(Long afterPoint) {
        this.afterPoint = afterPoint;
    }

    public Integer getBizShop() {
        return bizShop;
    }

    public void setBizShop(Integer bizShop) {
        this.bizShop = bizShop;
    }

    public Long getBizTime() {
        return bizTime;
    }

    public void setBizTime(Long bizTime) {
        this.bizTime = bizTime;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public Integer getIncrease() {
        return increase;
    }

    public void setIncrease(Integer increase) {
        this.increase = increase;
    }

    public String getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = orderDesc;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getPoint() {
        return point;
    }

    public void setPoint(Long point) {
        this.point = point;
    }

    public Integer getPointType() {
        return pointType;
    }

    public void setPointType(Integer pointType) {
        this.pointType = pointType;
    }

    public Long getPrePoint() {
        return prePoint;
    }

    public void setPrePoint(Long prePoint) {
        this.prePoint = prePoint;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
