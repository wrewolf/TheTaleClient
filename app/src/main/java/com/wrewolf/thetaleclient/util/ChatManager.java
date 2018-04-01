package com.wrewolf.thetaleclient.util;

import android.os.Handler;
import android.os.Looper;

import com.wrewolf.thetaleclient.api.CommonRequest;
import com.wrewolf.thetaleclient.api.CommonResponseCallback;
import com.wrewolf.thetaleclient.api.HttpMethod;
import com.wrewolf.thetaleclient.api.model.ChatMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Hamster
 * @since 24.10.2014
 * todo refactor to use object, not class with static fields/methods
 */
public class ChatManager
{

  private static final String URL_BASE = "https://embed.tlk.io/";
  private static final String URL_MAIN = URL_BASE + "the-tale";
  private static final String URL_PARTICIPANT = URL_BASE + "api/participant";
  private static final String URL_MESSAGE = URL_BASE + "api/chats/141060/messages";
  private static final String HEADER_CSRF_TOKEN = "X-CSRF-Token";
  private static final String COOKIE_SESSION = "_tlkio_session";

  private static final Handler handler = new Handler(Looper.getMainLooper());

  private static String csrfToken = null;
  private static String session = null;

  private static int lastId = 0;

  public static void init(final String nickname, final ChatCallback callback)
  {
    csrfToken = null;
    session = null;
    final CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
    cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    CookieJar cookieJar = new JavaNetCookieJar(cookieManager);
    final OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
    client.newCall(new Request.Builder().url(URL_MAIN).build()).enqueue(new Callback()
    {
      @Override
      public void onFailure(Call call, IOException e)
      {
        callCallbackError(callback);
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException
      {
        final Pattern pattern = Pattern.compile("<meta\\s+content=\"([^\"]*)\"\\s+name=\"csrf-token\"");
        final Matcher matcher = pattern.matcher(response.body().string());

        if (matcher.find())
        {
          csrfToken = matcher.group(1);
        }
        else
        {
          callCallbackError(callback);
          return;
        }
        response.header("Set-Cookie");
        session = getSession(cookieManager);

        // post nickname
        Request.Builder requestBuilder = new Request.Builder().url(URL_PARTICIPANT);
        setParams(requestBuilder, session, csrfToken);
        FormBody.Builder formBodyBuilder = new FormBody.Builder();

        formBodyBuilder.add("nickname", nickname);
        requestBuilder.post(formBodyBuilder.build());

        client.newCall(requestBuilder.build()).enqueue(new Callback()
        {
          @Override
          public void onFailure(Call call, IOException e)
          {
            callCallbackError(callback);
          }

          @Override
          public void onResponse(Call call, Response response) throws IOException
          {

          }
        });

        session = getSession(cookieManager);

        callCallbackSuccess(callback);
      }
    });

  }

  public static void post(final String message, final ChatCallback callback)
  {
    if ((csrfToken == null) || (session == null))
    {
      callCallbackError(callback);
      return;
    }
    final CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
    cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    CookieJar cookieJar = new JavaNetCookieJar(cookieManager);
    final OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
    Request.Builder requestBuilder = new Request.Builder().url(URL_MESSAGE);
    setParams(requestBuilder, session, csrfToken);
    FormBody.Builder formBodyBuilder = new FormBody.Builder();
    formBodyBuilder.add("body", message);
    requestBuilder.post(formBodyBuilder.build());

    client.newCall(requestBuilder.build()).enqueue(new Callback()
    {
      @Override
      public void onFailure(Call call, IOException e)
      {
        callCallbackError(callback);
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException
      {
        final JSONObject json;
        try
        {
          json = new JSONObject(response.body().string());
          if (json.has("error"))
          {
            callCallbackError(callback);
          }
        } catch (JSONException e)
        {
          e.printStackTrace();
          callCallbackError(callback);
        }
      }
    });
    session = getSession(cookieManager);

    callCallbackSuccess(callback);
  }

  public static void getMessages(final ChatMessagesCallback callback)
  {
    new CommonRequest()
    {
      protected long getStaleTime()
      {
        return 0;
      }
    }.execute(URL_MESSAGE, HttpMethod.GET, null, null, new CommonResponseCallback<String, Throwable>()
    {
      @Override
      public void processResponse(String response)
      {
        try
        {
          final JSONArray messagesJson = new JSONArray(response);
          final int count = messagesJson.length();
          final List<ChatMessage> messages = new ArrayList<>(count);
          for (int i = 0; i < count; i++)
          {
            final ChatMessage message = ObjectUtils.getModelFromJson(ChatMessage.class, messagesJson.getJSONObject(i));
            messages.add(message);
            if (message.id > lastId)
            {
              lastId = message.id;
            }
          }
          callCallbackSuccess(callback, messages);
        } catch (JSONException e)
        {
          callCallbackError(callback);
        }
      }

      @Override
      public void processError(Throwable error)
      {
        callCallbackError(callback);
      }
    });
  }

  public static void getNewMessages(final ChatMessagesCallback callback)
  {
    final int lastId = ChatManager.lastId;
    getMessages(new ChatMessagesCallback()
    {
      @Override
      public void onSuccess(List<ChatMessage> messages)
      {
        final int count = messages.size();
        if (count > 0)
        {
          int position = 0;
          for (; position < count; position++)
          {
            if (messages.get(position).id > lastId)
            {
              break;
            }
          }
          if (position < count)
          {
            callCallbackSuccess(callback, messages.subList(position, count));
          }
          else
          {
            callCallbackSuccess(callback, new ArrayList<ChatMessage>(0));
          }
        }
        else
        {
          callCallbackSuccess(callback, new ArrayList<ChatMessage>(0));
        }
      }

      @Override
      public void onError()
      {
        callCallbackError(callback);
      }
    });
  }

  // TODO: rewrite
  private static void setParams(final Request.Builder httpRequest,
                                final String cookieSession, final String csrfToken)
  {
    httpRequest.addHeader("Cookie", COOKIE_SESSION + "=" + cookieSession);
    httpRequest.addHeader(HEADER_CSRF_TOKEN, csrfToken);
  }

  // TODO: rewrite
  private static String getSession(CookieManager cookieManager)
  {

    for (final HttpCookie cookie : cookieManager.getCookieStore().getCookies())
    {
      if (cookie.getName().equals(COOKIE_SESSION))
      {
        return cookie.getValue();
      }
    }
    return null;
  }

  public interface ChatCallback
  {
    void onSuccess();

    void onError();
  }

  public interface ChatMessagesCallback
  {
    void onSuccess(List<ChatMessage> messages);

    void onError();
  }

  private static void callCallbackSuccess(final ChatCallback callback)
  {
    handler.post(new Runnable()
    {
      @Override
      public void run()
      {
        callback.onSuccess();
      }
    });
  }

  private static void callCallbackSuccess(final ChatMessagesCallback callback, final List<ChatMessage> messages)
  {
    handler.post(new Runnable()
    {
      @Override
      public void run()
      {
        callback.onSuccess(messages);
      }
    });
  }

  private static void callCallbackError(final ChatCallback callback)
  {
    handler.post(new Runnable()
    {
      @Override
      public void run()
      {
        callback.onError();
      }
    });
  }

  private static void callCallbackError(final ChatMessagesCallback callback)
  {
    handler.post(new Runnable()
    {
      @Override
      public void run()
      {
        callback.onError();
      }
    });
  }

}
