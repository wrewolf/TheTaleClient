package com.wrewolf.thetaleclient.api.request;

import com.wrewolf.thetaleclient.api.AbstractApiRequest;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.HttpMethod;
import com.wrewolf.thetaleclient.api.dictionary.CardTargetType;
import com.wrewolf.thetaleclient.api.response.CommonResponse;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hamster
 * @since 05.05.2015
 */
public class UseCardRequest extends AbstractApiRequest<CommonResponse> {

    public UseCardRequest() {
        super(HttpMethod.POST, "game/cards/api/use", "2.0", true);
    }

    public void execute(final String cardId, final ApiResponseCallback<CommonResponse> callback) {
        final Map<String, String> getParams = new HashMap<>(1);
        getParams.put("card", String.valueOf(cardId));
        execute(getParams, null, callback);
    }

    public void execute(final String cardId, final String value,
                        final ApiResponseCallback<CommonResponse> callback) {
        final Map<String, String> getParams = new HashMap<>(1);
        getParams.put("card", String.valueOf(cardId));

        final Map<String, String> postParams = new HashMap<>();
        if (value != null) {
            postParams.put("value", value);
        }

        execute(getParams, postParams, callback);
    }

    public void execute(final String cardId, final CardTargetType targetType, final int targetId,
                        final ApiResponseCallback<CommonResponse> callback) {
        final Map<String, String> getParams = new HashMap<>(1);
        getParams.put("card", String.valueOf(cardId));

        final Map<String, String> postParams = new HashMap<>();
        switch(targetType) {
            case PERSON:
                postParams.put("person", String.valueOf(targetId));
                break;

            case PLACE:
                postParams.put("place", String.valueOf(targetId));
                break;

            case BUILDING:
                postParams.put("building", String.valueOf(targetId));
                break;
        }

        execute(getParams, postParams, callback);
    }

    @Override
    protected CommonResponse getResponse(String response) throws JSONException {
        return null;
    }

    @Override
    protected long getStaleTime() {
        return 0;
    }

}
