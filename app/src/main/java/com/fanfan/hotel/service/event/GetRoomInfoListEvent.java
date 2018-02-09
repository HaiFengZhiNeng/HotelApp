package com.fanfan.hotel.service.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanfan.hotel.model.RoomInfo;
import com.fanfan.youtu.api.base.event.BaseEvent;

import java.util.List;

/**
 * Created by android on 2017/12/19.
 */

public class GetRoomInfoListEvent extends BaseEvent<List<RoomInfo>> {

    public GetRoomInfoListEvent(@Nullable String uuid) {
        super(uuid);
    }

    public GetRoomInfoListEvent(@Nullable String uuid, @NonNull Integer code, @Nullable List<RoomInfo> roomInfos) {
        super(uuid, code, roomInfos);
    }
}
