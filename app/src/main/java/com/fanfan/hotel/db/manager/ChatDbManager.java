package com.fanfan.hotel.db.manager;


import com.fanfan.hotel.db.base.BaseManager;
import com.fanfan.hotel.model.ChatMessageBean;

import org.greenrobot.greendao.AbstractDao;

/**
 * Created by Mao Jiqing on 2016/10/15.
 */

public class ChatDbManager extends BaseManager<ChatMessageBean, Long> {
    @Override
    public AbstractDao<ChatMessageBean, Long> getAbstractDao() {
        return daoSession.getChatMessageBeanDao();
    }
}
