package com.fanfan.hotel.common;

import com.fanfan.hotel.common.app.HotelApp;
import com.fanfan.hotel.model.PersonInfo;
import com.fanfan.hotel.utils.FileUtil;

import java.io.File;

/**
 * Created by android on 2017/12/18.
 */

public class Constants {

    public static int displayWidth;
    public static int displayHeight;

    public static int IMSDK_APPID = 1400043768;

    public static int IMSDK_ACCOUNT_TYPE = 17967;

    // exit
    public static final String EXIT_APP = "EXIT_APP";
    public static final String NET_LOONGGG_EXITAPP = "net.loonggg.exitapp";

    public final static String API_YOUTU_BASE = "http://api.youtu.qq.com/youtu/";

    private static final String M_SDROOT_CACHE_PATH = FileUtil.getCacheDir(HotelApp.getInstance().getApplicationContext()) + File.separator;
    private static final String M_SDROOT_FILE_PATH = FileUtil.getExternalFileDir(HotelApp.getInstance().getApplicationContext()) + File.separator;
    public static final String PROJECT_PATH = M_SDROOT_FILE_PATH + "fHotel" + File.separator;

    public static final String PRINT_LOG_PATH = M_SDROOT_CACHE_PATH + "print";
    public static final String CRASH_PATH = M_SDROOT_CACHE_PATH + "crash" + File.separator;
    public static final String GRM_PATH = PROJECT_PATH + "msc";

    public static final String IAT_CLOUD_BUILD = "iat_cloud_build";
    public static final String IAT_LOCAL_BUILD = "iat_local_build";

    public static final String IAT_CLOUD_UPDATELEXICON = "iat_cloud_updatelexicon";
    public static final String IAT_LOCAL_UPDATELEXICON = "iat_local_updatelexicon";


    public static final String IAT_LANGUAGE_PREFERENCE = "iat_language_preference";
    public static final String IAT_LINE_LANGUAGE_TALKER = "iat_line_language_talker";
    public static final String IAT_LOCAL_LANGUAGE_TALKER = "iat_local_language_talker";

    //udp
    public static String IP;
    public static int PORT;
    public static String CONNECT_IP = null;
    public static int CONNECT_PORT = 0;

    //视频
    public static boolean isCalling;

}
