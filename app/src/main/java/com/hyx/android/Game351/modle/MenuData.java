package com.hyx.android.Game351.modle;

import java.io.Serializable;

public class MenuData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -419683119036001213L;

    private String sort_id;
    private String sort_name;

    public String getSort_id() {
        return sort_id;
    }

    public void setSort_id(String sort_id) {
        this.sort_id = sort_id;
    }

    public String getSort_name() {
        return sort_name;
    }

    public void setSort_name(String sort_name) {
        this.sort_name = sort_name;
    }

}
