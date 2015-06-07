package com.sky.demo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sky.android.Request;

import java.util.List;
import java.util.Map;

/**
 * Created by sky on 15-5-30.
 */
public class StringRequest extends Request {

    public StringRequest(String url) {
        super(url);
        setHttpMethod(Request.METHOD_GET);
    }

    @Override
    public String getUserAgent() {
        return null;
    }

    @Override
    protected void putCookies(Map<String, List<String>> map) {
    }

    @Override
    protected String getCookies() {
        return null;
    }

    @Override
    protected boolean isNetWorkConnected() {
        ConnectivityManager connManager = (ConnectivityManager) App.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }


}
