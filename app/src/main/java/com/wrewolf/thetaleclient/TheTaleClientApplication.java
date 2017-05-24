package com.wrewolf.thetaleclient;

import android.app.Application;
import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.wrewolf.thetaleclient.api.cache.RequestCacheManager;
import com.wrewolf.thetaleclient.util.NotificationManager;
import com.wrewolf.thetaleclient.util.PreferencesManager;
import com.wrewolf.thetaleclient.util.map.MapUtils;
import com.wrewolf.thetaleclient.util.onscreen.OnscreenStateWatcher;

/**
 * @author Hamster
 * @since 08.10.2014
 */
public class TheTaleClientApplication extends Application
{

  private static Context context;
  private static OnscreenStateWatcher onscreenStateWatcher;
  private static NotificationManager notificationManager;
  private static PreferencesManager preferencesManager;
  private static FirebaseAnalytics analytics;

  public static FirebaseAnalytics getAnalytics()
  {
    return analytics;
  }

  public static PreferencesManager getPreferencesManager()
  {
    return preferencesManager;
  }

  @Override
  public void onCreate()
  {
    super.onCreate();

    context = getApplicationContext();
    analytics = FirebaseAnalytics.getInstance(context);
    preferencesManager = new PreferencesManager(context);
    onscreenStateWatcher = new OnscreenStateWatcher();
    notificationManager = new NotificationManager(context);
  }

  @Override
  public void onLowMemory()
  {
    super.onLowMemory();

    RequestCacheManager.invalidate();
    MapUtils.cleanup();

    System.gc();
  }

  public static Context getContext()
  {
    return context;
  }

  public static long getFreeMemory()
  {
    return Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
  }

  public static OnscreenStateWatcher getOnscreenStateWatcher()
  {
    return onscreenStateWatcher;
  }

  public static NotificationManager getNotificationManager()
  {
    return notificationManager;
  }

}
