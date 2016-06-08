package com.hyx.android.Game351.data;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 开源库官网
 * http://loopj.com/android-async-http/
 *
 * @author milanoouser
 */
public class HttpUtil {

    private static AsyncHttpClient client = new AsyncHttpClient(); // 实例话对象

    static {
        client.setTimeout(20000); // 设置链接超时，如果不设置，默认为10s
    }

    /**
     * 使用get方法,用一个完整url获取一个string对象
     *
     * @param urlString       url绝对路径
     * @param responseHandler 结果
     */
    public static void get(String urlString,
                           AsyncHttpResponseHandler responseHandler) {
        client.get(urlString, responseHandler);

    }

    /**
     * 使用get方法,url里面带参数,获取一个string对象
     *
     * @param urlString       url绝对路径
     * @param params          参数
     * @param responseHandler 结果
     */
    public static void get(String urlString, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {

        client.get(urlString, params, responseHandler);

    }

    /**
     * 使用post方法,用一个完整url获取一个string对象
     *
     * @param urlString       url绝对路径
     * @param responseHandler 结果
     */
    public static void post(String urlString,
                            AsyncHttpResponseHandler responseHandler) {
        client.post(urlString, responseHandler);
    }

    /**
     * 使用post方法,url里面带参数,获取一个string对象
     *
     * @param urlString       url绝对路径
     * @param params          参数
     * @param responseHandler 结果
     */
    public static void post(String urlString, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        client.post(urlString, params, responseHandler);
    }

    // // 不带参数，获取json对象或者数组
    // public static void get(String urlString, JsonHttpResponseHandler
    // responseHandler) {
    // client.get(urlString, responseHandler);
    //
    // }
    //
    // // 带参数，获取json对象或者数组
    // public static void get(String urlString, RequestParams params,
    // JsonHttpResponseHandler responseHandler) {
    //
    // client.get(urlString, params, responseHandler);
    //
    // }
    //
    // 下载数据使用，会返回byte数据
    public static void get(String uString, BinaryHttpResponseHandler bHandler) {

        client.get(uString, bHandler);
    }

    public static AsyncHttpClient getClient() {

        return client;

    }

}
