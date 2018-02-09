package com.fanfan.hotel.service.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;

import com.fanfan.hotel.model.SerialBean;
import com.fanfan.youtu.api.base.event.BaseEvent;

/**
 * Created by android on 2017/12/26.
 */

public class ActivityToServiceEvent extends BaseEvent<SerialBean> {

    public ActivityToServiceEvent(@Nullable String uuid) {
        super(uuid);
    }

    public ActivityToServiceEvent(@Nullable String uuid, @NonNull Integer code, @Nullable SerialBean serialBean) {
        super(uuid, code, serialBean);
    }
}
