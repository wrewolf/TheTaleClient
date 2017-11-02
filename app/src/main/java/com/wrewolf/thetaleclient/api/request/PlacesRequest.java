package com.wrewolf.thetaleclient.api.request;

import com.wrewolf.thetaleclient.api.AbstractApiRequest;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.HttpMethod;
import com.wrewolf.thetaleclient.api.response.PlacesResponse;

import org.json.JSONException;

/**
 * @author Hamster
 * @since 04.05.2015
 */
public class PlacesRequest extends AbstractApiRequest<PlacesResponse> {

    public PlacesRequest() {
        super(HttpMethod.GET, "game/places/api/list", "1.1", true);
    }

    public void execute(final ApiResponseCallback<PlacesResponse> callback) {
        execute(null, null, callback);
    }

    @Override
    protected PlacesResponse getResponse(String response) throws JSONException {
        return new PlacesResponse(response);
    }

    @Override
    protected long getStaleTime() {
        return 2 * 60 * 60 * 1000; // 2 hours
    }

}
