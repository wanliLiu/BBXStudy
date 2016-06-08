package com.hyx.android.Game351.modle;

import java.io.Serializable;
import java.util.List;

public class HistoryBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1269903516734427466L;
    private String title;
    private String FistId;
    private String secodeId;
    private List<SubjectBean> data;
    private MenuDataBean AllSubjects;
    private int currentPosition;
    private String recordTime;
    private SubjectBean favorite;
    private String sort_id;

    public SubjectBean getFavorite() {
        return favorite;
    }

    public void setFavorite(SubjectBean favorite) {
        this.favorite = favorite;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getSort_id() {
        return sort_id;
    }

    public void setSort_id(String sort_id) {
        this.sort_id = sort_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFistId() {
        return FistId;
    }

    public void setFistId(String fistId) {
        FistId = fistId;
    }

    public String getSecodeId() {
        return secodeId;
    }

    public void setSecodeId(String secodeId) {
        this.secodeId = secodeId;
    }

    public List<SubjectBean> getData() {
        return data;
    }

    public void setData(List<SubjectBean> data) {
        this.data = data;
    }

    public MenuDataBean getAllSubjects() {
        return AllSubjects;
    }

    public void setAllSubjects(MenuDataBean allSubjects) {
        AllSubjects = allSubjects;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }


}
