package com.wrewolf.thetaleclient.api.request;

import com.wrewolf.thetaleclient.api.AbstractApiRequest;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.HttpMethod;
import com.wrewolf.thetaleclient.api.response.PlaceInfoResponse;
import com.wrewolf.thetaleclient.api.response.PlacesResponse;

import org.json.JSONException;

/**
 * @author Hamster
 * @since 04.05.2015
 */
public class PlaceInfoRequest extends AbstractApiRequest<PlaceInfoResponse> {

    public PlaceInfoRequest(String place) {
        super(HttpMethod.GET, String.format("/game/places/%s/api/show",place), "2.0", true);
    }

    public void execute(final ApiResponseCallback<PlaceInfoResponse> callback) {
        execute(null, null, callback);
    }

    @Override
    protected PlaceInfoResponse getResponse(String response) throws JSONException {
        return new PlaceInfoResponse(response);
    }

    @Override
    protected long getStaleTime() {
        return 2 * 60 * 60 * 1000; // 2 hours
    }

}
