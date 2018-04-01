package com.wrewolf.thetaleclient.api.request;

import com.wrewolf.thetaleclient.api.CommonRequest;
import com.wrewolf.thetaleclient.api.CommonResponseCallback;
import com.wrewolf.thetaleclient.api.HttpMethod;
import com.wrewolf.thetaleclient.api.response.MapTerrainResponse;
import com.wrewolf.thetaleclient.util.RequestUtils;

import org.json.JSONException;

/**
 * @author Hamster
 * @since 18.10.2014
 */
public class MapTerrainRequest extends CommonRequest {

    public static final String URL_BASE = "https://the-tale.org/";
    private static final String URL = URL_BASE + "game/map/api/region?api_version=0.1&api_client=the_tale-v0.3.24.6";

    public void execute(final CommonResponseCallback<MapTerrainResponse, String> callback) {
        execute(URL, HttpMethod.GET, null, null, new CommonResponseCallback<String, Throwable>() {
            @Override
            public void processResponse(String response) {
                try {
                    RequestUtils.processResultInMainThread(callback, false, new MapTerrainResponse(response), null);
                } catch (JSONException e) {
                    RequestUtils.processResultInMainThread(callback, true, null, e.getLocalizedMessage());
                }
            }

            @Override
            public void processError(Throwable error) {
                RequestUtils.processResultInMainThread(callback, true, null, error.getLocalizedMessage());
            }
        });
    }

    @Override
    protected long getStaleTime() {
        return 60000;
    }

}
