package com.fanfan.hotel.utils;

import android.app.Activity;

import com.fanfan.hotel.R;
import com.fanfan.hotel.common.enums.SpecialType;

import java.util.Arrays;

/**
 * Created by android on 2017/12/19.
 */

public class SpecialUtils {

    public static String[] MusicArray = {"唱歌", "唱歌儿", "唱一首歌", "我想听音乐", "播放音乐", "来首歌曲", "播放歌曲", "唱首歌", "音乐",
            "音乐播放中..."};
    public static String[] StoryArray = {"故事", "讲故事", "讲一个故事", "换一个故事吧", "讲个故事", "段子", "讲个故事", "你会讲故事嘛",
            "换一个故事"};
    public static String[] JokeArray = {"笑话", "讲笑话", "讲一个笑话", "换一个笑话吧", "讲个段子", "讲个笑话", "段子"};
    public static String[] CheckinArray = {"入住"};
    public static String[] CheckOutArray = {"退房"};
    public static String[] ServceArray = {"服务"};
    public static String[] BackArray = {"返回"};
    public static String[] ForwardArray = {"前进", "向前", "向前走"};
    public static String[] BackoffArray = {"后退", "向后", "向后走"};
    public static String[] TurnleftArray = {"左转", "向左转"};
    public static String[] TurnrightArray = {"右转", "向右转"};
    public static String[] MapArray = {"打开地图"};
    public static String[] LogoutArray = {"退出登录"};
    public static String[] StopListenerArray = {"停止监听"};

    public static SpecialType doesExist(Activity activity, String speakTxt) {
        if (txtInArray(speakTxt, MusicArray) || Arrays.asList(resFoFinal(activity, R.array.other_misic)).contains(speakTxt)) {
            return SpecialType.Music;
        } else if (txtInArray(speakTxt, StoryArray) || Arrays.asList(resFoFinal(activity, R.array.other_story)).contains(speakTxt)) {
            return SpecialType.Story;
        } else if (txtInArray(speakTxt, JokeArray) || Arrays.asList(resFoFinal(activity, R.array.other_joke)).contains(speakTxt)) {
            return SpecialType.Joke;
        } else if (txtInArray(speakTxt, CheckinArray)) {
            return SpecialType.CheckIn;
        } else if (txtInArray(speakTxt, CheckOutArray)) {
            return SpecialType.CheckOut;
        } else if (txtInArray(speakTxt, ServceArray)) {
            return SpecialType.Servce;
        } else if (txtInArray(speakTxt, ForwardArray)) {
            return SpecialType.Forward;
        } else if (txtInArray(speakTxt, BackoffArray)) {
            return SpecialType.Backoff;
        } else if (txtInArray(speakTxt, TurnleftArray)) {
            return SpecialType.Turnleft;
        } else if (txtInArray(speakTxt, TurnrightArray)) {
            return SpecialType.Turnright;
        } else if (txtInArray(speakTxt, LogoutArray)) {
            return SpecialType.Logout;
        } else if (txtInArray(speakTxt, MapArray)) {
            return SpecialType.Map;
        } else if (txtInArray(speakTxt, StopListenerArray)) {
            return SpecialType.StopListener;
        }

        return SpecialType.NoSpecial;
    }

    private static boolean txtInArray(String speakTxt, String[] speakArray) {
        if (speakTxt.endsWith("。")) {
            speakTxt = speakTxt.substring(0, speakTxt.length() - 1);
        }
        return Arrays.asList(speakArray).contains(speakTxt);
    }

    private static String[] resFoFinal(Activity activity, int id) {
        String[] res = activity.getResources().getStringArray(id);
        return res;
    }

    public static SpecialType doesExistLocal(String speakTxt) {
        if (txtInArray(speakTxt, ForwardArray)) {
            return SpecialType.Forward;
        } else if (txtInArray(speakTxt, BackoffArray)) {
            return SpecialType.Backoff;
        } else if (txtInArray(speakTxt, TurnleftArray)) {
            return SpecialType.Turnleft;
        } else if (txtInArray(speakTxt, TurnrightArray)) {
            return SpecialType.Turnright;
        } else if (txtInArray(speakTxt, MapArray)) {
            return SpecialType.Map;
        } else if (txtInArray(speakTxt, StopListenerArray)) {
            return SpecialType.StopListener;
        } else if(txtInArray(speakTxt, BackArray)){
            return SpecialType.Back;
        }
        return SpecialType.NoSpecial;
    }
}
