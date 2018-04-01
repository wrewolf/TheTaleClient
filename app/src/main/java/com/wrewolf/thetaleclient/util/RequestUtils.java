package com.wrewolf.thetaleclient.util;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.wrewolf.thetaleclient.api.AbstractApiResponse;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.ApiResponseStatus;
import com.wrewolf.thetaleclient.api.CommonResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;

/**
 * @author Hamster
 * @since 29.10.2014
 */
public class RequestUtils {

    public static final String URL_BASE = "https://the-tale.org";

    public static final String COOKIE_SESSION_ID = "sessionid";

    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void setSession(final String session) {
        if(CookieHandler.getDefault() == null) {
            CookieHandler.setDefault(new CookieManager());
        }

        final String domain = "the-tale.org";
        final String path = "/";
        final HttpCookie httpCookie = new HttpCookie(COOKIE_SESSION_ID, session);
        httpCookie.setDomain(domain);
        httpCookie.setPath(path);
        ((CookieManager) CookieHandler.getDefault()).getCookieStore().add(
                URI.create(domain + path), httpCookie);
    }

    public static void setSession() {
        final String session = PreferencesManager.getSession();
        if(!TextUtils.isEmpty(session)) {
            setSession(session);
        }
    }

    public static String getGenericErrorResponse(final String error) {
        try {
            final JSONObject json = new JSONObject();
            json.put("status", ApiResponseStatus.GENERIC.getCode());
            json.put("code", ApiResponseStatus.GENERIC.getCode());
            json.put("error", error);
            return json.toString();
        } catch(JSONException e) {
            return "";
        }
    }

    public static <T, E> void processResultInMainThread(final CommonResponseCallback<T, E> callback, final boolean isError,
                                                        final T responseResult, final E errorResult) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(isError) {
                    callback.processError(errorResult);
                } else {
                    callback.processResponse(responseResult);
                }
            }
        });
    }

    public static <T, E> CommonResponseCallback<T, E> wrapCallback(final CommonResponseCallback<T, E> callback, final Fragment fragment) {
        return new CommonResponseCallback<T, E>() {
            @Override
            public void processResponse(T response) {
                if(fragment.isAdded()) {
                    callback.processResponse(response);
                }
            }

            @Override
            public void processError(E error) {
                if(fragment.isAdded()) {
                    callback.processError(error);
                }
            }
        };
    }

    public static <T extends AbstractApiResponse> ApiResponseCallback<T> wrapCallback(final ApiResponseCallback<T> callback, final Fragment fragment) {
        return new ApiResponseCallback<T>() {
            @Override
            public void processResponse(T response) {
                if(fragment.isAdded()) {
                    callback.processResponse(response);
                }
            }

            @Override
            public void processError(T response) {
                if(fragment.isAdded()) {
                    callback.processError(response);
                }
            }
        };
    }

}
