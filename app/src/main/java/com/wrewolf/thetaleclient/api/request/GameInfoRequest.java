package com.wrewolf.thetaleclient.api.request;

import com.wrewolf.thetaleclient.R;
import com.wrewolf.thetaleclient.TheTaleClientApplication;
import com.wrewolf.thetaleclient.api.AbstractApiRequest;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.ApiResponseStatus;
import com.wrewolf.thetaleclient.api.HttpMethod;
import com.wrewolf.thetaleclient.api.response.GameInfoResponse;
import com.wrewolf.thetaleclient.util.PreferencesManager;
import com.wrewolf.thetaleclient.util.RequestUtils;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hamster
 * @since 01.10.2014
 */
public class GameInfoRequest extends AbstractApiRequest<GameInfoResponse> {

    private final boolean needAuthorization;

    public GameInfoRequest(final boolean needAuthorization) {
        super(HttpMethod.GET, "game/api/info", "1.8", true);
        this.needAuthorization = needAuthorization;
    }

    public void execute(final int accountId, final ApiResponseCallback<GameInfoResponse> callback,
                        final boolean useCache) {
        final Map<String, String> getParams = new HashMap<>(1);
        getParams.put("account", String.valueOf(accountId));
        execute(getParams, null, callback, useCache);
    }

    public void execute(final ApiResponseCallback<GameInfoResponse> callback, final boolean useCache) {
        execute(null, null, callback, useCache);
    }

    protected GameInfoResponse getResponse(final String response) throws JSONException {
        final GameInfoResponse gameInfoResponse = new GameInfoResponse(response);

        if(gameInfoResponse.account == null) {
            PreferencesManager.setAccountId(0);
            PreferencesManager.setAccountName(null);
        } else if(gameInfoResponse.account.isOwnInfo) {
            PreferencesManager.setAccountId(gameInfoResponse.account.accountId);
        }
        PreferencesManager.setMapVersion(gameInfoResponse.mapVersion);

        if((gameInfoResponse.status == ApiResponseStatus.OK) && (gameInfoResponse.account == null) && needAuthorization) {
            return new GameInfoResponse(RequestUtils.getGenericErrorResponse(
                    TheTaleClientApplication.getContext().getString(R.string.game_not_authorized)));
        } else {
            return gameInfoResponse;
        }
    }

    @Override
    protected boolean isFinished(final GameInfoResponse response) {
        return super.isFinished(response) && ((response == null) ||
                ((response.account == null) || !response.account.isObsoleteInfo) &&
                ((response.enemy == null) || !response.enemy.isObsoleteInfo));
    }

    @Override
    protected long getStaleTime() {
        return 10000; // 10 seconds
    }

}
