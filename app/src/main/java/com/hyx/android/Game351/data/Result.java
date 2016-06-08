package com.hyx.android.Game351.data;

/**
 * @author star_Yang
 * @version V1.0
 * @Title: AIResult.java
 * @Package com.palmlink.weedu.model
 * @Description: TODO(请求返回结果)
 * @date 2014-1-5 下午9:49:36
 */
public class Result {

    /**
     * 成功失败
     */
    private boolean success;

    private Object obj;

    public Result(boolean success, Object obj) {
        this.success = success;
        this.obj = obj;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

}
