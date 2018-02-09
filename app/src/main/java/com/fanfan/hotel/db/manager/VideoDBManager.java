package com.fanfan.hotel.db.manager;

import com.fanfan.hotel.db.VideoBeanDao;
import com.fanfan.hotel.db.VoiceBeanDao;
import com.fanfan.hotel.db.base.BaseManager;
import com.fanfan.hotel.model.VideoBean;
import com.fanfan.hotel.model.VoiceBean;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by android on 2017/12/20.
 */

public class VideoDBManager extends BaseManager<VideoBean, Long> {
    @Override
    public AbstractDao<VideoBean, Long> getAbstractDao() {
        return daoSession.getVideoBeanDao();
    }

    public List<VideoBean> queryVideoByQuestion(String question) {
        Query<VideoBean> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(VideoBeanDao.Properties.ShowTitle.eq(question))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }

    public List<VideoBean> queryLikeVideoByQuestion(String question) {
        Query<VideoBean> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(VideoBeanDao.Properties.ShowTitle.like("%" + question + "%"))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }

}
