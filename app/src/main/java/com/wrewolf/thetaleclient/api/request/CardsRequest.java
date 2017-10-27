package com.wrewolf.thetaleclient.api.request;

import com.wrewolf.thetaleclient.api.AbstractApiRequest;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.HttpMethod;
import com.wrewolf.thetaleclient.api.response.CardsResponse;
import com.wrewolf.thetaleclient.api.response.CommonResponse;
import com.wrewolf.thetaleclient.api.response.GameInfoResponse;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wrewolf on 27.10.17.
 */

public class CardsRequest extends AbstractApiRequest<CardsResponse> {


    public CardsRequest() {
        super(HttpMethod.POST, "game/cards/api/get-cards", "2.0", false);
    }

    public void execute(final int accountId, final ApiResponseCallback<CardsResponse> callback) {
        final Map<String, String> getParams = new HashMap<>(1);
        getParams.put("account", String.valueOf(accountId));
        execute(getParams, null, callback, false);
    }

    public void execute(final ApiResponseCallback<CardsResponse> callback) {
        execute(null, null, callback, false);
    }

    @Override
    protected CardsResponse getResponse(String response) throws JSONException {
        return null;
    }

    @Override
    protected long getStaleTime() {
        return 0;
    }
}
