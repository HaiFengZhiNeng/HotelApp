package com.fanfan.hotel.db.manager;

import com.fanfan.hotel.db.NavigationBeanDao;
import com.fanfan.hotel.db.VideoBeanDao;
import com.fanfan.hotel.db.base.BaseManager;
import com.fanfan.hotel.model.NavigationBean;
import com.fanfan.hotel.model.VideoBean;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by android on 2017/12/20.
 */

public class NavigationDBManager extends BaseManager<NavigationBean, Long> {
    @Override
    public AbstractDao<NavigationBean, Long> getAbstractDao() {
        return daoSession.getNavigationBeanDao();
    }

    public List<NavigationBean> queryNavigationByQuestion(String question) {
        Query<NavigationBean> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(NavigationBeanDao.Properties.ShowTitle.eq(question))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }

    public List<NavigationBean> queryLikeNavigationByQuestion(String question) {
        Query<NavigationBean> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(NavigationBeanDao.Properties.ShowTitle.like("%" + question + "%"))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }

}
