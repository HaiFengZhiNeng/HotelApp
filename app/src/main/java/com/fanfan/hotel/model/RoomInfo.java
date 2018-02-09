package com.fanfan.hotel.model;

import java.io.Serializable;

/**
 * Created by android on 2017/12/19.
 */

public class RoomInfo implements Serializable {

    private int id;
    private String title;//标题
    private String imageUrl;//图片
    private int price;//价格
    private String surplus;//剩余
    private int bedNum;//2张床
    private double bedWid;
    private String bedType;//单人床，双人床
    private int peopleNum;//标准人数
    private double acreage;//面积
    private boolean hasCasement;//是否有窗
    private String manyDetails;
    private String address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSurplus() {
        return surplus;
    }

    public void setSurplus(String surplus) {
        this.surplus = surplus;
    }

    public int getBedNum() {
        return bedNum;
    }

    public void setBedNum(int bedNum) {
        this.bedNum = bedNum;
    }

    public double getBedWid() {
        return bedWid;
    }

    public void setBedWid(double bedWid) {
        this.bedWid = bedWid;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public int getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(int peopleNum) {
        this.peopleNum = peopleNum;
    }

    public double getAcreage() {
        return acreage;
    }

    public void setAcreage(double acreage) {
        this.acreage = acreage;
    }

    public boolean isHasCasement() {
        return hasCasement;
    }

    public void setHasCasement(boolean hasCasement) {
        this.hasCasement = hasCasement;
    }

    public String getManyDetails() {
        return manyDetails;
    }

    public void setManyDetails(String manyDetails) {
        this.manyDetails = manyDetails;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
