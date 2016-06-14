package com.hyx.android.Game351.modle;

import java.io.Serializable;

/**
 * Created by soli on 6/14/16.
 */
public class PositionBean implements Serializable {

    private int position;
    private boolean isShow = true;
    private String str;


    public PositionBean(int pos, String mStr) {
        position = pos;
        str = mStr;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
