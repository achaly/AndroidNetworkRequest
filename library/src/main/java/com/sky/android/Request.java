package com.sky.android;

import android.text.TextUtils;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sky on 15-5-30.
 */
public abstract class Request {
    private static final String TAG = "Request";
    /**
     * The following variables started with STATUS_ are request response status
     * code to indicate callers what happens.
     */
    // Status OK
    public static final int STATUS_OK = 0;
    // NOT MODIFIED
    public static final int STATUS_NOT_MODIFIED = 1;
    // NETWORK UNAVAILABLE.
    public static final int STATUS_NETWORK_UNAVAILABLE = 3;
    // Unknow error.
    public static final int STATUS_UNKNOW_ERROR = -1;

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    public static final String ETAG = "ETag";
    public static final String LAST_MODIFIED = "Last-Modified";

    // Timeout (in ms) we specify for each http request
    protected static final int HTTP_REQUEST_TIMEOUT_MS = 30 * 1000; // 30s.

    // The responsed etag and json object.
    private String mResponseResult;
    private String mLastModified;
    private String mETag;

    // request url;
    private String mRequestUrl;
    private String mRequestMethod = METHOD_POST;
    private HashMap<String, String> mRequestHeaders;

    public Request(String url) {
        mRequestUrl = url;
    }

    // add headers.
    public Request addHeader(String name, String value) {
        if (mRequestHeaders == null) {
            mRequestHeaders = new HashMap<String, String>();
        }
        mRequestHeaders.put(name, value);
        return this;
    }

    // set request method
    public Request setHttpMethod(String method) {
        mRequestMethod = method;
        return this;
    }

    // initialize http request.
    protected HttpURLConnection getHttpURLConnection() throws IOException {
        final URL req = new URL(getRequestUrl());
        final HttpURLConnection conn = (HttpURLConnection) req.openConnection();
        conn.setReadTimeout(HTTP_REQUEST_TIMEOUT_MS);
        conn.setConnectTimeout(HTTP_REQUEST_TIMEOUT_MS);
        conn.setRequestMethod(mRequestMethod);
        if (TextUtils.equals(mRequestMethod, HttpPost.METHOD_NAME)) {
            conn.setDoOutput(true);
            conn.setUseCaches(false);
        }

        // cookies.
        String cookie = getCookies();
        if (!TextUtils.isEmpty(cookie)) {
            conn.setRequestProperty("Cookie", getCookies());
        }

        // user agent.
        String userAgent = getUserAgent();
        if (!TextUtils.isEmpty(userAgent)) {
            conn.setRequestProperty("User-Agent", userAgent);
        }

        // other headers.
        if (mRequestHeaders != null && mRequestHeaders.size() > 0) {
            for (Map.Entry<String, String> entry : mRequestHeaders.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        return conn;
    }

    // get request status
    public int getStatus() {
        if (!isNetWorkConnected()) {
            return STATUS_NETWORK_UNAVAILABLE;
        }

        HttpURLConnection conn = null;
        BufferedReader br = null;
        int statusCode = STATUS_UNKNOW_ERROR;

        try {
            conn = getHttpURLConnection();
            // Connect to the server
            // here don't need explicit invoke.
//            conn.connect();

            // write post data
            if (TextUtils.equals(mRequestMethod, METHOD_POST)) {
                String params = getPostParams();
                if (!TextUtils.isEmpty(params)) {
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeBytes(getPostParams());
                    out.flush();
                }
            }

            // Check the response code
            int responseCode = conn.getResponseCode();
            Log.i(TAG, "http response code: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // read e-tag and last-modified.
                mETag = conn.getHeaderField(ETAG);
                mLastModified = conn.getHeaderField(LAST_MODIFIED);

                // Read cookie
                putCookies(conn.getHeaderFields());

                // Read response from the connection to after posting info
                InputStream in = conn.getInputStream();
                br = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                mResponseResult = sb.toString();
                Log.d(TAG, "response result: " + mResponseResult);

                statusCode = STATUS_OK;
            } else if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
                statusCode = STATUS_NOT_MODIFIED;
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            if (!isNetWorkConnected()) {
                statusCode = STATUS_NETWORK_UNAVAILABLE;
            }
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // here don't need explicit invoke.
//            if (conn != null) {
//                conn.disconnect();
//            }
        }
        return statusCode;
    }

    // get request ret etag.
    public final String getEtag() {
        return mETag;
    }

    // get request ret last-modified.
    public final String getLastModified() {
        return mLastModified;
    }

    // get response result.
    public final String getResponseResult() {
        return mResponseResult;
    }

    // get params.
    protected String getPostParams() {
        return null;
    }

    // The constructed url to requested
    public final String getRequestUrl() {
        return mRequestUrl;
    }

    public abstract String getUserAgent();

    protected abstract void putCookies(Map<String, List<String>> map);

    protected abstract String getCookies();

    protected abstract boolean isNetWorkConnected();
}
