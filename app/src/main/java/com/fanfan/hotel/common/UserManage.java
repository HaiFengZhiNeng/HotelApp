package com.fanfan.hotel.common;

/**
 * Created by android on 2017/12/26.
 */

public class UserManage {

    public static final String roomAVId = "@TGS#2GOCMM6EN";

    private volatile static UserManage userManage;

    private UserManage() {
    }

    public static UserManage getInstance() {
        if (null == userManage) {
            synchronized (UserManage.class) {
                if (null == userManage) {
                    userManage = new UserManage();
                }
            }
        }
        return userManage;
    }

    private String controlName;

    public String getControlName() {
        return controlName;
    }

    public void setControlName(String controlName) {
        this.controlName = controlName;
    }
}
