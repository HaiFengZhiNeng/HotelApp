package com.fanfan.hotel.service.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fanfan.hotel.model.RoomInfo;
import com.fanfan.hotel.service.event.GetRoomInfoListEvent;
import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.utils.UUIDGenerator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by android on 2017/12/19.
 */

public class OnlineData {

    private volatile static OnlineData mOnlineData;

    private OnlineData() {
    }


    public static OnlineData getSingleInstance() {
        if (null == mOnlineData) {
            synchronized (OnlineData.class) {
                if (null == mOnlineData) {
                    mOnlineData = new OnlineData();
                }
            }
        }
        return mOnlineData;
    }

    public static OnlineData init(@NonNull Context context, @NonNull final String client_id,
                                  @NonNull final String client_secret) {
        return getSingleInstance();
    }


    public String getRoomList(Integer node_id, int offset, int limit) {
        String uuid = UUIDGenerator.getUUID();
        GetInfoThread roomInfoThread = new GetInfoThread(new GetRoomInfoListEvent(uuid), offset, limit);
        new Thread(roomInfoThread).start();
        return uuid;
    }


    class GetInfoThread<T> implements Runnable {

        protected BaseEvent<T> event;

        private int offset;
        private int limit;

        public <Event extends BaseEvent<T>> GetInfoThread(@NonNull Event event, int offset, int limit) {
            this.event = event;
            this.offset = offset;
            this.limit = limit;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (offset > 50) {
                //没有数据
                EventBus.getDefault().post(event.setEvent(-1, null));
            }else {
                ArrayList<RoomInfo> roomInfos = new ArrayList<>();
                for (int i = offset; i < offset + limit; i++) {
                    RoomInfo roomInfo = new RoomInfo();
                    roomInfo.setId(i);
                    roomInfo.setTitle("房间" + i);
                    roomInfos.add(roomInfo);
                }
                T t = (T) roomInfos;
                EventBus.getDefault().post(event.setEvent(200, t));
            }
        }
    }

}
