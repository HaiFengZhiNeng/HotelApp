package com.fanfan.hotel.db.manager;

import com.fanfan.hotel.db.VoiceBeanDao;
import com.fanfan.hotel.db.base.BaseManager;
import com.fanfan.hotel.model.VoiceBean;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;

import java.util.List;

import javax.xml.validation.Schema;

/**
 * Created by android on 2017/12/20.
 */

public class VoiceDBManager extends BaseManager<VoiceBean, Long> {
    @Override
    public AbstractDao<VoiceBean, Long> getAbstractDao() {
        return daoSession.getVoiceBeanDao();
    }

    public List<VoiceBean> queryVoiceByQuestion(String question) {
        Query<VoiceBean> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(VoiceBeanDao.Properties.ShowTitle.eq(question))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }

    public List<VoiceBean> queryLikeVoiceByQuestion(String question) {
        Query<VoiceBean> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(VoiceBeanDao.Properties.ShowTitle.like("%" + question + "%"))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }

}
