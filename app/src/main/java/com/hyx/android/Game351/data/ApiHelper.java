package com.hyx.android.Game351.data;

import android.app.Activity;
import android.content.Context;

import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.Constants;
import com.hyx.android.Game351.base.MyApplication;
import com.hyx.android.Game351.util.MLog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.Iterator;
import java.util.Map.Entry;

public class ApiHelper {

    private static final String TAG = "ApiHelper";

    /**
     * @param ctx
     * @param params
     * @param callBack
     */
    public static void get(final Context ctx, final ApiParams params, final ApiCallBack callBack) {

        MyApplication app = (MyApplication) ctx.getApplicationContext();
        if (!app.isNetworkAvailable()) {
            Result result = new Result(ResultCode.RESULT_FAILED, ctx.getString(R.string.error_network_Unavailable));
            if (callBack != null) {
                callBack.receive(result);
            }
        } else {

            String paramsStr = "";
            if (params != null) {
                // 解析组合参数
                StringBuffer bufferParams = new StringBuffer();

                bufferParams.append("?");

                Iterator<Entry<String, Object>> iter = params.entrySet().iterator();
                while (iter.hasNext()) {
                    Entry<String, Object> entry = iter.next();
                    Object key = entry.getKey();
                    Object val = entry.getValue();

                    MLog.e(TAG, "参数" + key + ":" + val);

                    bufferParams.append(key).append("=").append(val).append("&");
                }

                // 删除最后一个&
                bufferParams.deleteCharAt(bufferParams.length() - 1);
                paramsStr = bufferParams.toString();

            }

            final String absUrl = Constants.ServerDataAddress + paramsStr;
            absUrl.trim();

            MLog.i(TAG, "调用接口 " + absUrl);

            HttpUtil.post(absUrl, new AsyncHttpResponseHandler() {

                private Result result;// 返回结果

                @Override
                public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

                    String content = new String(arg2);
                    MLog.e(TAG, "#返回:\r\n" + content);

                    result = new Result(ResultCode.RESULT_OK, content);
                }

                @Override
                public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {

                    result = new Result(ResultCode.RESULT_FAILED, ctx.getString(R.string.FAILED));

                }

                @Override
                public void onProgress(int bytesWritten, int totalSize) {
                }

                @Override
                public void onFinish() {
                    super.onFinish();

                    if (callBack != null && result != null) {
                        callBack.receive(result);
                    }

                }

            });
        }
    }

    /**
     * @param ctx
     * @param params
     * @param callBack
     */
    // 有返回这样的情况 {"code":"10000"}接口需要修改
    public static void post(final Context ctx, final ApiParams params, final ApiCallBack callBack) {

        MyApplication app = (MyApplication) ((Activity) ctx).getApplication();
        if (!app.isNetworkAvailable()) {
            Result result = new Result(ResultCode.RESULT_FAILED, ctx.getString(R.string.error_network_Unavailable));
            if (callBack != null) {
                callBack.receive(result);
            }
        } else {

            String paramsStr = "";

            RequestParams reqParmas = null;
            if (params != null) {

                reqParmas = new RequestParams();

                // 解析组合参数
                StringBuffer bufferParams = new StringBuffer();

                bufferParams.append("?");

                Iterator<Entry<String, Object>> iter = params.entrySet().iterator();
                while (iter.hasNext()) {
                    Entry<String, Object> entry = iter.next();
                    Object key = entry.getKey();
                    Object val = entry.getValue();

                    MLog.e(TAG, "参数" + key + ":" + val);

                    reqParmas.put(key.toString(), val);

                    bufferParams.append(key).append("=").append(val).append("&");
                }

                // 删除最后一个&
                bufferParams.deleteCharAt(bufferParams.length() - 1);
                paramsStr = bufferParams.toString();

            }

            String absUrl = Constants.ServerTopidAddress + paramsStr;
            absUrl.trim();

            MLog.i(TAG, "# 全路径:\r\n" + absUrl);

            HttpUtil.post(Constants.ServerTopidAddress, reqParmas, new AsyncHttpResponseHandler() {

                private Result result;// 返回结果

                @Override
                public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

                    String content = new String(arg2);
                    MLog.e(TAG, "#返回:\r\n" + content);

                    result = new Result(ResultCode.RESULT_OK, content);
                }

                @Override
                public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {

                    result = new Result(ResultCode.RESULT_FAILED, ctx.getString(R.string.FAILED));
                }

                @Override
                public void onProgress(int bytesWritten, int totalSize) {
                }

                @Override
                public void onFinish() {
                    super.onFinish();

                    if (callBack != null && result != null) {
                        callBack.receive(result);
                    }
                }

            });
        }
    }

    /**
     * 上传文件到服务器
     *
     * @param ctx
     * @param url
     * @param params
     * @param callBack
     */
    public static void uploadFile(final Context ctx, final RequestParams params, final ApiCallBack callBack) {
        MyApplication app = (MyApplication) ((Activity) ctx).getApplication();
        if (!app.isNetworkAvailable()) {
            Result result = new Result(ResultCode.RESULT_FAILED, ctx.getString(R.string.error_network_Unavailable));
            if (callBack != null) {
                callBack.receive(result);
            }
        } else {
            MLog.e(TAG, "全路径:" + params.toString());

            HttpUtil.post(Constants.ServerTopidAddress, params, new AsyncHttpResponseHandler() {

                private Result result;// 返回结果

                @Override
                public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

                    String content = new String(arg2);
                    MLog.e(TAG, "#返回:" + content);
                    result = new Result(ResultCode.RESULT_OK, content);
                }

                @Override
                public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                    MLog.e(TAG, "返回:" + arg3.getMessage());
                    result = new Result(ResultCode.RESULT_FAILED, ctx.getString(R.string.FAILED));
                }

                @Override
                public void onProgress(int bytesWritten, int totalSize) {
                    if (totalSize != 1) {
                        MLog.e("上传进度", String.valueOf(Math.round((bytesWritten * 1.0 / totalSize * 1.0) * 100)) + "%");
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();

                    if (callBack != null && result != null) {
                        callBack.receive(result);
                    }
                }

            });
        }
    }
}
