package com.fanfan.hotel.model;

import com.fanfan.hotel.common.ChatConst;
import com.fanfan.hotel.ui.recyclerview.tree.base.BaseItemData;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by android on 2017/12/20.
 */
@Entity
public class NavigationBean extends BaseItemData {

    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "localType")
    @ChatConst.LocalType
    private int localType;
    @Property(nameInDb = "saveTime")
    private long saveTime;
    @Property(nameInDb = "showTitle")
    private String showTitle;
    @Property(nameInDb = "imgUrl")
    private String imgUrl;
    @Property(nameInDb = "navigation")
    private String navigation;
    @Property(nameInDb = "navigationData")
    private String navigationData;
    @Generated(hash = 994678742)
    public NavigationBean(Long id, int localType, long saveTime, String showTitle,
            String imgUrl, String navigation, String navigationData) {
        this.id = id;
        this.localType = localType;
        this.saveTime = saveTime;
        this.showTitle = showTitle;
        this.imgUrl = imgUrl;
        this.navigation = navigation;
        this.navigationData = navigationData;
    }
    @Generated(hash = 270470942)
    public NavigationBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getNavigation() {
        return this.navigation;
    }

    public void setNavigation(String navigation) {
        this.navigation = navigation;
    }

    public String getNavigationData() {
        return this.navigationData;
    }

    public void setNavigationData(String navigationData) {
        this.navigationData = navigationData;
    }
    public int getLocalType() {
        return this.localType;
    }
    public void setLocalType(int localType) {
        this.localType = localType;
    }
    public long getSaveTime() {
        return this.saveTime;
    }
    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }
    public String getShowTitle() {
        return this.showTitle;
    }
    public void setShowTitle(String showTitle) {
        this.showTitle = showTitle;
    }

}
