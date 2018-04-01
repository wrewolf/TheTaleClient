package com.wrewolf.thetaleclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.wrewolf.thetaleclient.fragment.NavigationDrawerFragment;
import com.wrewolf.thetaleclient.fragment.Refreshable;
import com.wrewolf.thetaleclient.fragment.WrapperFragment;
import com.wrewolf.thetaleclient.fragment.dialog.ChoiceDialog;
import com.wrewolf.thetaleclient.util.DialogUtils;
import com.wrewolf.thetaleclient.util.HistoryStack;
import com.wrewolf.thetaleclient.util.PreferencesManager;
import com.wrewolf.thetaleclient.util.TextToSpeechUtils;
import com.wrewolf.thetaleclient.util.UiUtils;
import com.wrewolf.thetaleclient.util.WebsiteUtils;
import com.wrewolf.thetaleclient.util.onscreen.OnscreenPart;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String KEY_GAME_TAB_INDEX = "KEY_GAME_TAB_INDEX";
    public static final String KEY_SHOULD_RESET_WATCHING_ACCOUNT = "KEY_SHOULD_RESET_WATCHING_ACCOUNT";

    private static final String KEY_DRAWER_TAB_INDEX = "KEY_DRAWER_TAB_INDEX";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private DrawerItem currentItem;
    private HistoryStack<DrawerItem> history;
    private boolean isPaused;

    private Menu menu;

    private TextView accountNameTextView;
    private TextView timeTextView;
    private View drawerItemInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // set up the drawer
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        accountNameTextView = (TextView) findViewById(R.id.drawer_account_name);
        timeTextView = (TextView) findViewById(R.id.drawer_time);
        drawerItemInfoView = findViewById(DrawerItem.PROFILE.getViewResId());

        history = new HistoryStack<>(DrawerItem.values().length);
        int tabIndex = DrawerItem.GAME.ordinal();
        if(savedInstanceState != null) {
            tabIndex = savedInstanceState.getInt(KEY_DRAWER_TAB_INDEX, tabIndex);
        }
        onNavigationDrawerItemSelected(DrawerItem.values()[tabIndex]);

//        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();
//        Retrofit retrofit = new Retrofit.Builder()
//            .baseUrl("https://the-tale.org")
//            .client(okHttpClient)
//            .build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(PreferencesManager.isReadAloudConfirmed()) {
            TextToSpeechUtils.init(TheTaleClientApplication.getContext(), null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;

        if(PreferencesManager.shouldExit()) {
            PreferencesManager.setShouldExit(false);
            finish();
        }

        final Intent intent = getIntent();
        int tabIndex = -1;
        if(intent != null) {
            if(intent.hasExtra(KEY_GAME_TAB_INDEX)) {
                onNavigationDrawerItemSelected(DrawerItem.GAME);
                tabIndex = intent.getIntExtra(KEY_GAME_TAB_INDEX, GameFragment.GamePage.GAME_INFO.ordinal());
                intent.removeExtra(KEY_GAME_TAB_INDEX);
            }

            if(intent.getBooleanExtra(KEY_SHOULD_RESET_WATCHING_ACCOUNT, false)) {
                PreferencesManager.setWatchingAccount(0, null);
                intent.removeExtra(KEY_SHOULD_RESET_WATCHING_ACCOUNT);
            }
        }

        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(currentItem.getFragmentTag());
        if(tabIndex != -1) {
            final GameFragment.GamePage gamePage = GameFragment.GamePage.values()[tabIndex];
            if(fragment instanceof GameFragment) {
                ((GameFragment) fragment).setCurrentPage(gamePage);
            } else {
                PreferencesManager.setDesiredGamePage(gamePage);
            }
        }
        UiUtils.callOnscreenStateChange(fragment, true);

        TheTaleClientApplication.getOnscreenStateWatcher().onscreenStateChange(OnscreenPart.MAIN, true);
    }

    @Override
    protected void onPause() {
        isPaused = true;

        TheTaleClientApplication.getOnscreenStateWatcher().onscreenStateChange(OnscreenPart.MAIN, false);
        TextToSpeechUtils.pause();
        RequestCacheManager.invalidate();
        UiUtils.callOnscreenStateChange(getSupportFragmentManager().findFragmentByTag(currentItem.getFragmentTag()), false);

        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        isPaused = true;
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_DRAWER_TAB_INDEX, currentItem.ordinal());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        TextToSpeechUtils.destroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onNavigationDrawerItemSelected(DrawerItem item) {
        if(item != currentItem) {
            switch(item) {
                case PROFILE:
                    DialogUtils.showChoiceDialog(getSupportFragmentManager(), getString(R.string.drawer_title_site),
                            new String[]{
                                    getString(R.string.drawer_dialog_profile_item_keeper),
                                    getString(R.string.drawer_dialog_profile_item_hero)
                            },
                            new ChoiceDialog.ItemChooseListener() {
                                @Override
                                public void onItemSelected(final int position) {
                                    new InfoPrerequisiteRequest(new Runnable() {
                                        @Override
                                        public void run() {
                                            final int accountId = PreferencesManager.getAccountId();
                                            if(accountId == 0) {
                                                if(!isPaused()) {
                                                    DialogUtils.showCommonErrorDialog(getSupportFragmentManager(), MainActivity.this);
                                                }
                                            } else {
                                                switch(position) {
                                                    case 0:
                                                        startActivity(UiUtils.getOpenLinkIntent(String.format(WebsiteUtils.URL_PROFILE_KEEPER, accountId)));
                                                        break;

                                                    case 1:
                                                        startActivity(UiUtils.getOpenLinkIntent(String.format(WebsiteUtils.URL_PROFILE_HERO, accountId)));
                                                        break;

                                                    default:
                                                        if(!isPaused()) {
                                                            DialogUtils.showCommonErrorDialog(getSupportFragmentManager(), MainActivity.this);
                                                        }
                                                        break;
                                                }
                                            }
                                        }
                                    }, new PrerequisiteRequest.ErrorCallback<InfoResponse>() {
                                        @Override
                                        public void processError(InfoResponse response) {
                                            if(!isPaused()) {
                                                DialogUtils.showCommonErrorDialog(getSupportFragmentManager(), MainActivity.this);
                                            }
                                        }
                                    }, null).execute();
                                }
                            });
                    break;

                case SITE:
                    startActivity(UiUtils.getOpenLinkIntent(WebsiteUtils.URL_GAME));
                    break;

                case LOGOUT:
                    PreferencesManager.setSession("");

                    final Fragment fragment = getSupportFragmentManager().findFragmentByTag(currentItem.getFragmentTag());
                    if(fragment instanceof WrapperFragment) {
                        ((WrapperFragment) fragment).setMode(DataViewMode.LOADING);
                    }

                    new LogoutRequest().execute(new ApiResponseCallback<CommonResponse>() {
                        @Override
                        public void processResponse(CommonResponse response) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }

                        @Override
                        public void processError(CommonResponse response) {
                            if(fragment instanceof WrapperFragment) {
                                ((WrapperFragment) fragment).setError(response.errorMessage);
                            }
                        }
                    });
                    break;

                case ABOUT:
                    DialogUtils.showAboutDialog(getSupportFragmentManager());
                    break;

                default:
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Fragment oldFragment = fragmentManager.findFragmentByTag(item.getFragmentTag());
                    if(oldFragment == null) {
                        oldFragment = item.getFragment();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, oldFragment, item.getFragmentTag())
                                .commit();
                    } else if(oldFragment.isDetached()) {
                        fragmentManager.beginTransaction()
                                .attach(oldFragment)
                                .commit();
                    }

                    if(currentItem != null) {
                        UiUtils.callOnscreenStateChange(getSupportFragmentManager().findFragmentByTag(currentItem.getFragmentTag()), false);
                    }
                    UiUtils.callOnscreenStateChange(oldFragment, true);

                    currentItem = item;
                    mTitle = getString(currentItem.getTitleResId());
                    history.set(currentItem);
                    supportInvalidateOptionsMenu();

                    break;
            }
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen() && (currentItem.getMenuResId() != 0)) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            this.menu = menu;
            getMenuInflater().inflate(currentItem.getMenuResId(), menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void refresh() {
        onRefreshStarted();
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(currentItem.getFragmentTag());
        if(fragment instanceof Refreshable) {
            ((Refreshable) fragment).refresh(true);
        }
    }

    public void refreshGameAdjacentFragments() {
        if(currentItem == DrawerItem.GAME) {
            final Fragment fragment = getSupportFragmentManager().findFragmentByTag(currentItem.getFragmentTag());
            if(fragment instanceof GameFragment) {
                ((GameFragment) fragment).refreshAdjacentFragments();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onRefreshStarted() {
        if(menu != null) {
            final MenuItem itemRefresh = menu.findItem(R.id.action_refresh);
            if(itemRefresh != null) {
                MenuItemCompat.setActionView(itemRefresh, getLayoutInflater().inflate(R.layout.menu_progress, null));
            }
        }
    }

    public void onRefreshFinished() {
        if(menu != null) {
            final MenuItem itemRefresh = menu.findItem(R.id.action_refresh);
            if(itemRefresh != null) {
                MenuItemCompat.setActionView(itemRefresh, null);
            }
        }
    }

    public void onDataRefresh() {
        new InfoPrerequisiteRequest(new Runnable() {
            @Override
            public void run() {
                drawerItemInfoView.setVisibility(View.VISIBLE);
                UiUtils.setText(accountNameTextView, PreferencesManager.getAccountName());
                new GameInfoRequest(false).execute(new ApiResponseCallback<GameInfoResponse>() {
                    @Override
                    public void processResponse(GameInfoResponse response) {
                        UiUtils.setText(timeTextView, String.format("%s %s", response.turnInfo.verboseDate, response.turnInfo.verboseTime));
                    }

                    @Override
                    public void processError(GameInfoResponse response) {
                        UiUtils.setText(timeTextView, null);
                    }
                }, true);
            }
        }, new PrerequisiteRequest.ErrorCallback<InfoResponse>() {
            @Override
            public void processError(InfoResponse response) {
                drawerItemInfoView.setVisibility(View.GONE);
                UiUtils.setText(accountNameTextView, null);
                UiUtils.setText(timeTextView, null);
            }
        }, null).execute();
    }

    @Override
    public void onBackPressed() {
        if(mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        } else {
            final DrawerItem drawerItem = history.pop();
            if(drawerItem == null) {
                PreferencesManager.setShouldExit(true);
                finish();
            } else {
                onNavigationDrawerItemSelected(drawerItem);
            }
        }
    }

    public Menu getMenu() {
        return menu;
    }

    public boolean isPaused() {
        return isPaused;
    }

}
