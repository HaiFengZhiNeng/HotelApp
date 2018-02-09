package com.fanfan.hotel.common;

import com.fanfan.hotel.model.PersonInfo;
import com.fanfan.hotel.model.RoomInfo;

/**
 * Created by android on 2018/1/5.
 */

public class InfoManage {

    private volatile static InfoManage instance;

    private InfoManage() {
    }

    public static InfoManage getInstance() {
        if (instance == null) {
            synchronized (InfoManage.class) {
                if (instance == null) {
                    instance = new InfoManage();
                }
            }
        }
        return instance;
    }

    private RoomInfo roomInfo;
    private PersonInfo personInfo;

    public RoomInfo getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(RoomInfo roomInfo) {
        this.roomInfo = roomInfo;
    }

    public PersonInfo getPersonInfo() {
        return personInfo;
    }

    public void setPersonInfo(PersonInfo personInfo) {
        this.personInfo = personInfo;
    }

    public void clear() {
        roomInfo = null;
        personInfo = null;
    }
}
