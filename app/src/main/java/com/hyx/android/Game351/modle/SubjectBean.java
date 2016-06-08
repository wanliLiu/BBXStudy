package com.hyx.android.Game351.modle;

import java.io.Serializable;

public class SubjectBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2700818819689269048L;

    private String question;
    private String mp3_addr;
    private String pic_addr;
    private String subject_sort_id;
    private String answer;
    //1---听力
    //2---看
    //3---复述过关
    //4---口语过关‍
    private int is_select;
    private String id;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getMp3_addr() {
        return mp3_addr;
    }

    public void setMp3_addr(String mp3_addr) {
        this.mp3_addr = mp3_addr;
    }

    public String getPic_addr() {
        return pic_addr;
    }

    public void setPic_addr(String pic_addr) {
        this.pic_addr = pic_addr;
    }

    public String getSubject_sort_id() {
        return subject_sort_id;
    }

    public void setSubject_sort_id(String subject_sort_id) {
        this.subject_sort_id = subject_sort_id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getIs_select() {
        return is_select;
    }

    public void setIs_select(int is_select) {
        this.is_select = is_select;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnglishStr() {
        String en = "";
        String[] test = getAnswer().split("\\|");
        if (getIs_select() == 3 || getIs_select() == 4) {
            en = getAnswer();
        } else {
            for (int i = 0; i < test.length; i++) {

                if (i % 2 == 0) {
                    en += test[i] + " ";
                }
            }
        }

        return en;
    }

}
