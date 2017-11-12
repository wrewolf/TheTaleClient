package com.wrewolf.thetaleclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.wrewolf.thetaleclient.BuildConfig;
import com.wrewolf.thetaleclient.DataViewMode;
import com.wrewolf.thetaleclient.R;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.ApiResponseStatus;
import com.wrewolf.thetaleclient.api.cache.RequestCacheManager;
import com.wrewolf.thetaleclient.api.request.AuthRequest;
import com.wrewolf.thetaleclient.api.request.GameInfoRequest;
import com.wrewolf.thetaleclient.api.request.InfoRequest;
import com.wrewolf.thetaleclient.api.request.ThirdPartyAuthRequest;
import com.wrewolf.thetaleclient.api.request.ThirdPartyAuthStateRequest;
import com.wrewolf.thetaleclient.api.response.AuthResponse;
import com.wrewolf.thetaleclient.api.response.GameInfoResponse;
import com.wrewolf.thetaleclient.api.response.InfoResponse;
import com.wrewolf.thetaleclient.api.response.ThirdPartyAuthResponse;
import com.wrewolf.thetaleclient.api.response.ThirdPartyAuthStateResponse;
import com.wrewolf.thetaleclient.service.WatcherService;
import com.wrewolf.thetaleclient.service.widget.AppWidgetHelper;
import com.wrewolf.thetaleclient.util.DialogUtils;
import com.wrewolf.thetaleclient.util.PreferencesManager;
import com.wrewolf.thetaleclient.util.RequestUtils;
import com.wrewolf.thetaleclient.util.UiUtils;

import io.fabric.sdk.android.Fabric;

/**
 * @author Hamster
 * @since 05.10.2014
 */
public class LoginActivity extends FragmentActivity {

    private static final String URL_HOME = "http://the-tale.org/?action=the-tale-client";
    private static final String URL_REGISTRATION = "http://the-tale.org/accounts/registration/fast?action=the-tale-client";
    private static final String URL_PASSWORD_REMIND = "http://the-tale.org/accounts/profile/reset-password?action=the-tale-client";
    private static final long THIRD_PARTY_AUTH_STATE_TIMEOUT = 10000;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private TextView textLogin;
    private TextView textPassword;
    private TextView textError;
    private TextView textErrorLogin;
    private TextView textErrorPassword;

    private View dataView;
    private View loadingView;
    private View errorView;

    private View contentStart;
    private View contentLoginPassword;
    private View actionRetry;
    private TextView textErrorGlobal;

    private boolean wasError = false;
    private boolean isStartLoginContainerVisible = true;
    private boolean isThirdPartyAuthInProgress = false;

    private Runnable thirdPartyAuthStateRequester = new Runnable() {
        @Override
        public void run() {
            new ThirdPartyAuthStateRequest().execute(new ApiResponseCallback<ThirdPartyAuthStateResponse>() {
                @Override
                public void processResponse(ThirdPartyAuthStateResponse response) {
                    switch (response.authState) {
                        case NOT_REQUESTED:
                        case REJECT:
                            isThirdPartyAuthInProgress = false;
                            UiUtils.setText(textError, response.authState.getDescription());
                            setLoginContainersVisibility(true);
                            setMode(DataViewMode.DATA);
                            break;

                        case SUCCESS:
                            isThirdPartyAuthInProgress = false;
                            onSuccessfulLogin();
                            break;

                        default:
                            isThirdPartyAuthInProgress = true;
                            handler.postDelayed(thirdPartyAuthStateRequester, THIRD_PARTY_AUTH_STATE_TIMEOUT);
                    }
                }

                @Override
                public void processError(ThirdPartyAuthStateResponse response) {
                    isThirdPartyAuthInProgress = true;
                    handler.postDelayed(thirdPartyAuthStateRequester, THIRD_PARTY_AUTH_STATE_TIMEOUT);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);

        textLogin = (TextView) findViewById(R.id.login_text_login);
        textPassword = (TextView) findViewById(R.id.login_text_password);

        textError = (TextView) findViewById(R.id.login_error);
        textErrorLogin = (TextView) findViewById(R.id.login_error_login);
        textErrorPassword = (TextView) findViewById(R.id.login_error_password);

        dataView = findViewById(R.id.login_content);
        loadingView = findViewById(R.id.login_progressbar);
        errorView = findViewById(R.id.login_error_global);

        contentStart = findViewById(R.id.login_content_start);
        contentLoginPassword = findViewById(R.id.login_content_login_password);

        actionRetry = findViewById(R.id.login_error_global_retry);
        textErrorGlobal = (TextView) findViewById(R.id.login_error_global_text);

        findViewById(R.id.login_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(UiUtils.getOpenLinkIntent(URL_HOME));
            }
        });

        findViewById(R.id.login_action_authorization_site).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRequestAuthSite();
            }
        });

        findViewById(R.id.login_action_authorization_login_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearErrors();
                setLoginContainersVisibility(false);
                textLogin.requestFocus();
            }
        });

        findViewById(R.id.login_action_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode(DataViewMode.LOADING);
                clearErrors();
                authorize(textLogin.getText().toString(), textPassword.getText().toString());
            }
        });

        findViewById(R.id.login_action_registration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(UiUtils.getOpenLinkIntent(URL_REGISTRATION));
            }
        });
        findViewById(R.id.login_action_password_remind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(UiUtils.getOpenLinkIntent(URL_PASSWORD_REMIND));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferencesManager.setShouldExit(false);
        AppWidgetHelper.update(this, DataViewMode.LOADING, null);
        if (isThirdPartyAuthInProgress) {
            thirdPartyAuthStateRequester.run();
        } else {
            startRequestInit();
        }
    }

    private void startRequestInit() {
        setMode(DataViewMode.LOADING);
        new InfoRequest().execute(new ApiResponseCallback<InfoResponse>() {
            @Override
            public void processResponse(InfoResponse response) {
                RequestUtils.setSession();
                new GameInfoRequest(false).execute(new ApiResponseCallback<GameInfoResponse>() {
                    @Override
                    public void processResponse(GameInfoResponse response) {
                        if (response.account == null) {
                            setLoginContainersVisibility(true);
                            setMode(DataViewMode.DATA);
                            AppWidgetHelper.updateWithError(LoginActivity.this, getString(R.string.game_not_authorized));
                        } else {
                            onSuccessfulLogin();
                        }
                    }

                    @Override
                    public void processError(GameInfoResponse response) {
                        actionRetry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startRequestInit();
                            }
                        });
                        setError(response.errorMessage);
                    }
                }, false);
            }

            @Override
            public void processError(InfoResponse response) {
                actionRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startRequestInit();
                    }
                });
                if (response == null) {
                    setError("Ошибка NULL...");
                } else {
                    setError(response.errorMessage);
                }
            }
        });
    }

    private void startRequestAuthSite() {
        setMode(DataViewMode.LOADING);
        new ThirdPartyAuthRequest().execute(new ApiResponseCallback<ThirdPartyAuthResponse>() {
            @Override
            public void processResponse(final ThirdPartyAuthResponse response) {
                // TODO check for isPaused if there will be crashes
                DialogUtils.showMessageDialog(
                        getSupportFragmentManager(),
                        getString(R.string.common_dialog_attention_title),
                        getString(R.string.login_dialog_confirm_authorization),
                        new Runnable() {
                            @Override
                            public void run() {
                                isThirdPartyAuthInProgress = true;
                                thirdPartyAuthStateRequester.run();
                                startActivity(UiUtils.getOpenLinkIntent(RequestUtils.URL_BASE + response.nextUrl));
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                setLoginContainersVisibility(true);
                                setMode(DataViewMode.DATA);
                            }
                        });
            }

            @Override
            public void processError(ThirdPartyAuthResponse response) {
                actionRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startRequestAuthSite();
                    }
                });
                setError(response.errorMessage);
            }
        });
    }

    private void sendAuthRequest(final String login, final String password) {
        new AuthRequest().execute(login, password, true,
                new ApiResponseCallback<AuthResponse>() {
                    @Override
                    public void processResponse(AuthResponse response) {
                        if (response.status == ApiResponseStatus.OK) {
                            onSuccessfulLogin();
                        } else {
                            setMode(DataViewMode.LOADING);
                        }
                    }

                    @Override
                    public void processError(AuthResponse response) {
                        LoginActivity.this.processError(response.errorMessage, login, password);
                        if (response.errorsLogin != null) {
                            UiUtils.setText(textErrorLogin, TextUtils.join("\n", response.errorsLogin));
                            textLogin.requestFocus();
                        }
                        if (response.errorsPassword != null) {
                            UiUtils.setText(textErrorPassword, TextUtils.join("\n", response.errorsPassword));
                            if (response.errorsLogin == null) {
                                textPassword.requestFocus();
                            } else {
                                textLogin.requestFocus();
                            }
                        }
                    }
                });
    }

    private void authorize(final String login, final String password) {
        RequestCacheManager.invalidate();
        if (wasError) {
            new InfoRequest().execute(new ApiResponseCallback<InfoResponse>() {
                @Override
                public void processResponse(InfoResponse response) {
                    sendAuthRequest(login, password);
                }

                @Override
                public void processError(InfoResponse response) {
                    LoginActivity.this.processError(response.errorMessage, login, password);
                    textLogin.requestFocus();
                }
            });
        } else {
            sendAuthRequest(login, password);
        }
    }

    private void setMode(final DataViewMode mode) {
        dataView.setVisibility(mode == DataViewMode.DATA ? View.VISIBLE : View.GONE);
        loadingView.setVisibility(mode == DataViewMode.LOADING ? View.VISIBLE : View.GONE);
        errorView.setVisibility(mode == DataViewMode.ERROR ? View.VISIBLE : View.GONE);
    }

    private void setLoginContainersVisibility(final boolean isStartContainer) {
        isStartLoginContainerVisible = isStartContainer;
        contentStart.setVisibility(isStartContainer ? View.VISIBLE : View.GONE);
        contentLoginPassword.setVisibility(isStartContainer ? View.GONE : View.VISIBLE);
    }

    private void setError(final String error) {
        textErrorGlobal.setText(error);
        setMode(DataViewMode.ERROR);
        AppWidgetHelper.updateWithError(this, error);
    }

    private void processError(final String error, final String login, final String password) {
        wasError = true;
        setMode(DataViewMode.DATA);
        UiUtils.setText(textError, error);
        textLogin.setText(login);
        textPassword.setText(password);
    }

    private void clearErrors() {
        UiUtils.setText(textError, null);
        UiUtils.setText(textErrorLogin, null);
        UiUtils.setText(textErrorPassword, null);
    }

    private void onSuccessfulLogin() {
        startService(new Intent(this, WatcherService.class));
        startMainActivity();
    }

    private void startMainActivity() {
        AppWidgetHelper.updateWithRequest(this);

        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtras(getIntent());

        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(thirdPartyAuthStateRequester);
    }

    @Override
    public void onBackPressed() {
        if (isThirdPartyAuthInProgress) {
            isThirdPartyAuthInProgress = false;
            handler.removeCallbacks(thirdPartyAuthStateRequester);
            setLoginContainersVisibility(true);
            setMode(DataViewMode.DATA);
        } else {
            if (isStartLoginContainerVisible) {
                PreferencesManager.setShouldExit(true);
                finish();
            } else {
                setLoginContainersVisibility(true);
            }
        }
    }

}
