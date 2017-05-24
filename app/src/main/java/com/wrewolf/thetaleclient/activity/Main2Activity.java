package com.wrewolf.thetaleclient.activity;


import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.wrewolf.thetaleclient.DataViewMode;
import com.wrewolf.thetaleclient.DrawerItem;
import com.wrewolf.thetaleclient.R;
import com.wrewolf.thetaleclient.TheTaleClientApplication;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.cache.RequestCacheManager;
import com.wrewolf.thetaleclient.api.cache.prerequisite.InfoPrerequisiteRequest;
import com.wrewolf.thetaleclient.api.cache.prerequisite.PrerequisiteRequest;
import com.wrewolf.thetaleclient.api.request.GameInfoRequest;
import com.wrewolf.thetaleclient.api.request.LogoutRequest;
import com.wrewolf.thetaleclient.api.response.CommonResponse;
import com.wrewolf.thetaleclient.api.response.GameInfoResponse;
import com.wrewolf.thetaleclient.api.response.InfoResponse;
import com.wrewolf.thetaleclient.fragment.GameFragment;
import com.wrewolf.thetaleclient.fragment.WrapperFragment;
import com.wrewolf.thetaleclient.fragment.dialog.ChoiceDialog;
import com.wrewolf.thetaleclient.util.DialogUtils;
import com.wrewolf.thetaleclient.util.HistoryStack;
import com.wrewolf.thetaleclient.util.PreferencesManager;
import com.wrewolf.thetaleclient.util.TextToSpeechUtils;
import com.wrewolf.thetaleclient.util.UiUtils;
import com.wrewolf.thetaleclient.util.WebsiteUtils;
import com.wrewolf.thetaleclient.util.onscreen.OnscreenPart;

import java.util.Locale;

public class Main2Activity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener
{
  public static final String KEY_GAME_TAB_INDEX = "KEY_GAME_TAB_INDEX";
  public static final String KEY_SHOULD_RESET_WATCHING_ACCOUNT = "KEY_SHOULD_RESET_WATCHING_ACCOUNT";

  private static final String KEY_DRAWER_TAB_INDEX = "KEY_DRAWER_TAB_INDEX";

  private CharSequence mTitle;
  private DrawerItem currentItem;
  private HistoryStack<DrawerItem> history;
  private boolean isPaused;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main2);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

//    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//    fab.setOnClickListener(new View.OnClickListener()
//    {
//      @Override
//      public void onClick(View view)
//      {
//        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//            .setAction("Action", null).show();
//      }
//    });
    history = new HistoryStack<>(DrawerItem.values().length);
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    final View header = navigationView.getHeaderView(0);
    int tabIndex = DrawerItem.GAME.ordinal();
    if(savedInstanceState != null) {
      tabIndex = savedInstanceState.getInt(KEY_DRAWER_TAB_INDEX, tabIndex);
    }
    onNavigationDrawerItemSelected(DrawerItem.values()[tabIndex]);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
    {
      @Override
      public void onDrawerOpened(View drawerView)
      {
        super.onDrawerOpened(drawerView);
        drawerView.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            DialogUtils.showChoiceDialog(getSupportFragmentManager(), getString(R.string.drawer_title_site),
                                         new String[]{
                                             getString(R.string.drawer_dialog_profile_item_keeper),
                                             getString(R.string.drawer_dialog_profile_item_hero)
                                         },
                                         new ChoiceDialog.ItemChooseListener()
                                         {
                                           @Override
                                           public void onItemSelected(final int position)
                                           {
                                             new InfoPrerequisiteRequest(new Runnable()
                                             {
                                               @Override
                                               public void run()
                                               {
                                                 final int accountId = PreferencesManager.getAccountId();
                                                 if (accountId == 0)
                                                 {
                                                   if (!isPaused())
                                                   {
                                                     DialogUtils.showCommonErrorDialog(getSupportFragmentManager(), Main2Activity.this);
                                                   }
                                                 }
                                                 else
                                                 {
                                                   switch (position)
                                                   {
                                                     case 0:
                                                       startActivity(UiUtils.getOpenLinkIntent(String.format(Locale.getDefault(), WebsiteUtils
                                                           .URL_PROFILE_KEEPER, accountId)));
                                                       break;

                                                     case 1:
                                                       startActivity(UiUtils.getOpenLinkIntent(String.format(Locale.getDefault(),
                                                                                                             WebsiteUtils.URL_PROFILE_HERO,
                                                                                                             accountId)));
                                                       break;

                                                     default:
                                                       if (!isPaused())
                                                       {
                                                         DialogUtils.showCommonErrorDialog(getSupportFragmentManager(), Main2Activity.this);
                                                       }
                                                       break;
                                                   }
                                                 }
                                               }
                                             }, new PrerequisiteRequest.ErrorCallback<InfoResponse>()
                                             {
                                               @Override
                                               public void processError(InfoResponse response)
                                               {
                                                 if (!isPaused())
                                                 {
                                                   DialogUtils.showCommonErrorDialog(getSupportFragmentManager(), Main2Activity.this);
                                                 }
                                               }
                                             }, null).execute();
                                           }
                                         });

          }
        });
        // -----------------------------------
        final TextView accountNameTextView = (TextView) header.findViewById(R.id.accountName);
        final TextView timeTextView = (TextView) header.findViewById(R.id.time);
        UiUtils.setText(accountNameTextView, PreferencesManager.getAccountName());
        new GameInfoRequest(false).execute(new ApiResponseCallback<GameInfoResponse>()
        {
          @Override
          public void processResponse(GameInfoResponse response)
          {
            UiUtils.setText(timeTextView, String.format("%s %s", response.turnInfo.verboseDate, response.turnInfo.verboseTime));
          }

          @Override
          public void processError(GameInfoResponse response)
          {
            UiUtils.setText(timeTextView, null);
          }
        }, true);
        // -----------------------------------
        invalidateOptionsMenu();
      }

    };

    drawer.addDrawerListener(toggle);

    toggle.syncState();


  }

  @Override
  public void onBackPressed()
  {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START))
    {
      drawer.closeDrawer(GravityCompat.START);
    }
    else
    {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main2, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item)
  {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    switch (id)
    {
      case R.id.nav_game:
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtras(getIntent());

        startActivity(intent);
        finish();
        break;
      case R.id.nav_map:
        onNavigationDrawerItemSelected(DrawerItem.MAP);
        break;
      case R.id.nav_chat:
        onNavigationDrawerItemSelected(DrawerItem.CHAT);
        break;
      case R.id.nav_cite:
        onNavigationDrawerItemSelected(DrawerItem.SITE);
        break;
      case R.id.nav_find:
        onNavigationDrawerItemSelected(DrawerItem.FIND_PLAYER);
        break;
      case R.id.nav_setting:
        onNavigationDrawerItemSelected(DrawerItem.SETTINGS);
        break;
      case R.id.nav_logout:
        PreferencesManager.setSession("");

//        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(currentItem.getFragmentTag());
//        if(fragment instanceof WrapperFragment) {
//          ((WrapperFragment) fragment).setMode(DataViewMode.LOADING);
//        }

        new LogoutRequest().execute(new ApiResponseCallback<CommonResponse>()
        {
          @Override
          public void processResponse(CommonResponse response)
          {
            startActivity(new Intent(Main2Activity.this, LoginActivity.class));
            finish();
          }

          @Override
          public void processError(CommonResponse response)
          {
//            if(fragment instanceof WrapperFragment) {
//              ((WrapperFragment) fragment).setError(response.errorMessage);
//            }
          }
        });
        break;
      case R.id.nav_about:
        DialogUtils.showAboutDialog(getSupportFragmentManager());
        break;
    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  public void onNavigationDrawerItemSelected(DrawerItem item)
  {
    if (item != currentItem)
    {

      FragmentManager fragmentManager = getSupportFragmentManager();
      Fragment oldFragment = fragmentManager.findFragmentByTag(item.getFragmentTag());
      if (oldFragment == null)
      {
        oldFragment = item.getFragment();
        fragmentManager.beginTransaction()
            .replace(R.id.container, oldFragment, item.getFragmentTag())
            .commit();
      }
      else if (oldFragment.isDetached())
      {
        fragmentManager.beginTransaction()
            .attach(oldFragment)
            .commit();
      }

      if (currentItem != null)
      {
        UiUtils.callOnscreenStateChange(getSupportFragmentManager().findFragmentByTag(currentItem.getFragmentTag()), false);
      }
      UiUtils.callOnscreenStateChange(oldFragment, true);

      currentItem = item;
      mTitle = getString(currentItem.getTitleResId());
      history.set(currentItem);
      supportInvalidateOptionsMenu();

    }
  }

  public boolean isPaused()
  {
    return isPaused;
  }

  @Override
  protected void onPause()
  {
    isPaused = true;

    TheTaleClientApplication.getOnscreenStateWatcher().onscreenStateChange(OnscreenPart.MAIN, false);
    TextToSpeechUtils.pause();
    RequestCacheManager.invalidate();
    UiUtils.callOnscreenStateChange(getSupportFragmentManager().findFragmentByTag(currentItem.getFragmentTag()), false);

    super.onPause();
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    isPaused = false;

    if (PreferencesManager.shouldExit())
    {
      PreferencesManager.setShouldExit(false);
      finish();
    }

    final Intent intent = getIntent();
    int tabIndex = -1;
    if (intent != null)
    {
      if (intent.hasExtra(KEY_GAME_TAB_INDEX))
      {
//        onNavigationDrawerItemSelected(DrawerItem.GAME);
        tabIndex = intent.getIntExtra(KEY_GAME_TAB_INDEX, GameFragment.GamePage.GAME_INFO.ordinal());
        intent.removeExtra(KEY_GAME_TAB_INDEX);
      }

      if (intent.getBooleanExtra(KEY_SHOULD_RESET_WATCHING_ACCOUNT, false))
      {
        PreferencesManager.setWatchingAccount(0, null);
        intent.removeExtra(KEY_SHOULD_RESET_WATCHING_ACCOUNT);
      }
    }

    final Fragment fragment = getSupportFragmentManager().findFragmentByTag(currentItem.getFragmentTag());
    if (tabIndex != -1)
    {
      final GameFragment.GamePage gamePage = GameFragment.GamePage.values()[tabIndex];
      if (fragment instanceof GameFragment)
      {
        ((GameFragment) fragment).setCurrentPage(gamePage);
      }
      else
      {
        PreferencesManager.setDesiredGamePage(gamePage);
      }
    }
    UiUtils.callOnscreenStateChange(fragment, true);

    TheTaleClientApplication.getOnscreenStateWatcher().onscreenStateChange(OnscreenPart.MAIN, true);
  }

}
