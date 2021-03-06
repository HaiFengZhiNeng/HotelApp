package com.fanfan.hotel.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.fanfan.hotel.model.NavigationBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "NAVIGATION_BEAN".
*/
public class NavigationBeanDao extends AbstractDao<NavigationBean, Long> {

    public static final String TABLENAME = "NAVIGATION_BEAN";

    /**
     * Properties of entity NavigationBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property LocalType = new Property(1, int.class, "localType", false, "localType");
        public final static Property SaveTime = new Property(2, long.class, "saveTime", false, "saveTime");
        public final static Property ShowTitle = new Property(3, String.class, "showTitle", false, "showTitle");
        public final static Property ImgUrl = new Property(4, String.class, "imgUrl", false, "imgUrl");
        public final static Property Navigation = new Property(5, String.class, "navigation", false, "navigation");
        public final static Property NavigationData = new Property(6, String.class, "navigationData", false, "navigationData");
    }


    public NavigationBeanDao(DaoConfig config) {
        super(config);
    }
    
    public NavigationBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"NAVIGATION_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"localType\" INTEGER NOT NULL ," + // 1: localType
                "\"saveTime\" INTEGER NOT NULL ," + // 2: saveTime
                "\"showTitle\" TEXT," + // 3: showTitle
                "\"imgUrl\" TEXT," + // 4: imgUrl
                "\"navigation\" TEXT," + // 5: navigation
                "\"navigationData\" TEXT);"); // 6: navigationData
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NAVIGATION_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, NavigationBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getLocalType());
        stmt.bindLong(3, entity.getSaveTime());
 
        String showTitle = entity.getShowTitle();
        if (showTitle != null) {
            stmt.bindString(4, showTitle);
        }
 
        String imgUrl = entity.getImgUrl();
        if (imgUrl != null) {
            stmt.bindString(5, imgUrl);
        }
 
        String navigation = entity.getNavigation();
        if (navigation != null) {
            stmt.bindString(6, navigation);
        }
 
        String navigationData = entity.getNavigationData();
        if (navigationData != null) {
            stmt.bindString(7, navigationData);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, NavigationBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getLocalType());
        stmt.bindLong(3, entity.getSaveTime());
 
        String showTitle = entity.getShowTitle();
        if (showTitle != null) {
            stmt.bindString(4, showTitle);
        }
 
        String imgUrl = entity.getImgUrl();
        if (imgUrl != null) {
            stmt.bindString(5, imgUrl);
        }
 
        String navigation = entity.getNavigation();
        if (navigation != null) {
            stmt.bindString(6, navigation);
        }
 
        String navigationData = entity.getNavigationData();
        if (navigationData != null) {
            stmt.bindString(7, navigationData);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public NavigationBean readEntity(Cursor cursor, int offset) {
        NavigationBean entity = new NavigationBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // localType
            cursor.getLong(offset + 2), // saveTime
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // showTitle
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // imgUrl
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // navigation
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // navigationData
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, NavigationBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setLocalType(cursor.getInt(offset + 1));
        entity.setSaveTime(cursor.getLong(offset + 2));
        entity.setShowTitle(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setImgUrl(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setNavigation(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setNavigationData(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(NavigationBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(NavigationBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(NavigationBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
