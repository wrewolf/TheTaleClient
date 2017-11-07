package com.wrewolf.thetaleclient.api;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * @author Hamster
 * @since 30.09.2014
 */
public enum HttpMethod
{

  GET
      {
        public com.wrewolf.thetaleclient.api.cache.Request getHttpRequest(final String url, final Map<String, String> getParams, final Map<String, String> postParams, final Map<String, ArrayList<String>> multiParams)
        {
          return new com.wrewolf.thetaleclient.api.cache.Request(url, new Request.Builder().url(appendGetParams(url, getParams)).build(),
                                                                 getParams,
                                                                 postParams);
        }
          public com.wrewolf.thetaleclient.api.cache.Request getHttpRequest(final String url, final Map<String, String> getParams, final Map<String, String> postParams)
          {
              return getHttpRequest(url, getParams, postParams, null);
          }
      },
  POST
      {
        public com.wrewolf.thetaleclient.api.cache.Request getHttpRequest(final String url, final Map<String, String> getParams, final Map<String, String> postParams, final Map<String, ArrayList<String>> multiParams)
        {
          Request.Builder httpPostBuilder = new Request.Builder().url(appendGetParams(url, getParams));
          if (postParams == null)
          {
            return new com.wrewolf.thetaleclient.api.cache.Request(url, httpPostBuilder.build(), getParams, postParams);
          }

          FormBody.Builder formBodyBuilder = new FormBody.Builder();

          for (final Map.Entry<String, String> postParam : postParams.entrySet())
          {
            formBodyBuilder.add(postParam.getKey(), postParam.getValue());
          }
            if (multiParams != null) {
                for (final Map.Entry<String, ArrayList<String>> multiParam : multiParams.entrySet()) {
                    for (final String value : multiParam.getValue()) {
                        formBodyBuilder.add(multiParam.getKey(), value);
                    }
                }
            }
          return new com.wrewolf.thetaleclient.api.cache.Request(url, httpPostBuilder.post(formBodyBuilder.build()).build(), getParams, postParams);
        }

          public com.wrewolf.thetaleclient.api.cache.Request getHttpRequest(final String url, final Map<String, String> getParams, final Map<String, String> postParams)
          {
              return getHttpRequest(url, getParams, postParams, null);
          }
      };

  public abstract com.wrewolf.thetaleclient.api.cache.Request getHttpRequest(final String url, final Map<String, String> getParams, final Map<String, String> postParams);
  public abstract com.wrewolf.thetaleclient.api.cache.Request getHttpRequest(final String url, final Map<String, String> getParams, final Map<String, String> postParams, final Map<String, ArrayList<String>> multiParams);

  private static String appendGetParams(final String url, final Map<String, String> getParams)
  {
    if (getParams == null)
    {
      return url;
    }

    final Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
    for (final Map.Entry<String, String> getParam : getParams.entrySet())
    {
      uriBuilder.appendQueryParameter(getParam.getKey(), getParam.getValue());
    }
    return uriBuilder.build().toString();
  }

}
