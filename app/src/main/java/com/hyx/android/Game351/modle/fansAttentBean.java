package com.hyx.android.Game351.modle;

import java.io.Serializable;

public class fansAttentBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2018173629286900713L;

    private String user_id;
    private String user_name;
    private String avatar;
    private String user_nick_name;
    private String area;
    private boolean isAttetnion;


    public boolean isAttetnion() {
        return isAttetnion;
    }

    public void setAttetnion(boolean isAttetnion) {
        this.isAttetnion = isAttetnion;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUser_nick_name() {
        return user_nick_name;
    }

    public void setUser_nick_name(String user_nick_name) {
        this.user_nick_name = user_nick_name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

}
