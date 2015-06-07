package com.sky.demo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sky.android.Request;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sky on 15-5-30.
 */
public class JsonRequest extends Request {

    private List<NameValuePair> mParamsList;

    public JsonRequest(String url) {
        super(url);
        addHeader("Content-Type", "application/x-www-form-urlencoded");
    }

    public JSONObject getJsonObject() throws JSONException {
        return new JSONObject(getResponseResult());
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

    // Get the parameters which will be appended to the requested url
    public Request addParam(String key, String value) {
        if (mParamsList == null) {
            mParamsList = new ArrayList<NameValuePair>();
        }

        BasicNameValuePair param = new BasicNameValuePair(key, value);
        if (!mParamsList.contains(param)) {
            mParamsList.add(param);
        }
        return this;
    }

    @Override
    protected String getPostParams() {
        return URLEncodedUtils.format(mParamsList, HTTP.UTF_8);
    }

    // Clear all parameters
    public void clearParams() {
        if (mParamsList != null) {
            mParamsList.clear();
        }
    }
}
