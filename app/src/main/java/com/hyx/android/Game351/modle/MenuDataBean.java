package com.hyx.android.Game351.modle;

import java.io.Serializable;
import java.util.ArrayList;

public class MenuDataBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4065703005320738761L;

    private boolean success;
    private String info;
    private int flag;

    private ArrayList<MenuData> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public ArrayList<MenuData> getData() {
        return data;
    }

    public void setData(ArrayList<MenuData> data) {
        this.data = data;
    }

}
