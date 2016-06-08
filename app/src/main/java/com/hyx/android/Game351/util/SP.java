package com.hyx.android.Game351.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 本地存储
 *
 * @author wst
 */
public class SP {

    public static final String UserName = "UserName";
    public static final String UserPassword = "UserPassword";
    public static final String logindate = "logindate";
    public static final String UserID = "UserID";
    public static final String UserAvatar = "UserAvatar";
    public static final String UserPhone = "UserPhone";

    public static final String isPlayCouninue = "isPlayCouninue";
    public static final String isPlayTime = "isPlayTime";

    public static final String SUBJECTFONT = "SUBJECTFONT";

    public static final String IsFistTime = "IsFistTime";

    public static final String isAutoDisplay = "isAutoDisplay";
    public static final String SHOWTIME = "showtime";

    public static final String CopySwitchAnswer = "CopySwitchAnswer";
    public static final String CopySwitchZh = "CopySwitchZh";
    public static final String CopySwitchpic = "CopySwitchpic";

    public static final String IsVIPuser = "IsVIPuser";

    /**
     * 设备唯一ID
     */
    public static final String DEVICE_ID = "DEVICE_ID";

    public static SharedPreferences getSp(Context ctx) {
        return ctx.getSharedPreferences("setting", Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getEdit(Context ctx) {
        return ctx.getSharedPreferences("setting", Context.MODE_PRIVATE).edit();
    }

}
