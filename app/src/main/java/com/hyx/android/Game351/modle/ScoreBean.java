package com.hyx.android.Game351.modle;

import java.io.Serializable;

public class ScoreBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6576940336092080072L;

    private int num;
    private String user_name;
    private int score;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


}
