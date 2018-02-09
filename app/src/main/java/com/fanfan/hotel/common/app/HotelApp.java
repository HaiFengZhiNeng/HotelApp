package com.fanfan.hotel.common.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;

import com.fanfan.hotel.BuildConfig;
import com.fanfan.hotel.R;
import com.fanfan.hotel.common.Constants;
import com.fanfan.hotel.common.UserManage;
import com.fanfan.hotel.common.lifecycle.Foreground;
import com.fanfan.hotel.db.base.BaseManager;
import com.fanfan.hotel.service.cache.Config;
import com.fanfan.hotel.service.cache.UserInfoCache;
import com.fanfan.youtu.Youtucode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.seabreeze.log.Print;
import com.seabreeze.log.inner.ConsoleTree;
import com.seabreeze.log.inner.FileTree;
import com.seabreeze.log.inner.LogcatTree;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by android on 2017/12/18.
 */

public class HotelApp extends MultiDexApplication {

    private static HotelApp instance;

    public static HotelApp getInstance() {
        return instance;
    }

    private String mEngineType;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

//        if (initLeak()) return;
//        CrashHandler.getInstance().init(this);

        Foreground.init(this);

        BaseManager.initOpenHelper(this);
        initXf();

        initLogger(this);
        Config.init(this);

        Youtucode.init(this);

        UserInfoCache.getUser(this);

        UserManage.getInstance().setControlName("hotel003");
    }


    private boolean initLeak() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return true;
        }
        LeakCanary.install(this);
        return false;
    }


    private void initLogger(@NonNull Context context) {
        if (BuildConfig.DEBUG) {
            Print.getLogConfig().configAllowLog(true).configShowBorders(false);
            Print.plant(new FileTree(this, Constants.PRINT_LOG_PATH));
            Print.plant(new ConsoleTree());
            Print.plant(new LogcatTree());
        }
    }

    private void initXf() {
        StringBuffer param = new StringBuffer();
        param.append("appid=" + getString(R.string.app_id));
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(this, param.toString());
    }

    public String getEngineType() {
        return mEngineType;
    }

    public void setEngineType(String engineType) {
        this.mEngineType = engineType;
    }

}
