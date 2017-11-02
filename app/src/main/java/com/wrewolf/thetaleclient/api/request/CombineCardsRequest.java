package com.wrewolf.thetaleclient.api.request;

import com.wrewolf.thetaleclient.api.AbstractApiRequest;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.HttpMethod;
import com.wrewolf.thetaleclient.api.response.CombineCardsResponse;
import com.wrewolf.thetaleclient.api.response.CommonResponse;
import com.wrewolf.thetaleclient.util.RequestUtils;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hamster
 * @since 21.03.2015
 */
public class CombineCardsRequest extends AbstractApiRequest<CombineCardsResponse> {

    private final List<String> cardIds;

    public CombineCardsRequest(final List<String> cardIds) {
        super(HttpMethod.POST, "game/cards/api/combine", "2.0", true);
        this.cardIds = cardIds;
    }

    public void execute(final ApiResponseCallback<CombineCardsResponse> callback) {
        final StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for(final String cardId : cardIds) {
            if(first) {
                first = false;
            } else {
                stringBuilder.append(",");
            }
            stringBuilder.append(cardId);
        }

        final Map<String, String> postParams = new HashMap<>(1);
        postParams.put("card", stringBuilder.toString());
        execute(null, postParams, callback);
    }

    protected CombineCardsResponse getResponse(final String response) throws JSONException {
        return new CombineCardsResponse(response);
    }

    @Override
    protected void retry(final Map<String, String> getParams, final Map<String, String> postParams,
                         final CombineCardsResponse response, final ApiResponseCallback<CombineCardsResponse> callback) {
        new PostponedTaskRequest(response.statusUrl).execute(new ApiResponseCallback<CommonResponse>() {
            @Override
            public void processResponse(CommonResponse response) {
                CombineCardsResponse combineCardsResponse;
                try {
                    combineCardsResponse = new CombineCardsResponse(response.rawResponse);
                    callback.processResponse(combineCardsResponse);
                } catch (JSONException e) {
                    try {
                        combineCardsResponse = new CombineCardsResponse(RequestUtils.getGenericErrorResponse(e.getMessage()));
                    } catch (JSONException ignored) {
                        combineCardsResponse = null;
                    }
                    callback.processError(combineCardsResponse);
                }
            }

            @Override
            public void processError(CommonResponse response) {
                CombineCardsResponse combineCardsResponse;
                try {
                    combineCardsResponse = new CombineCardsResponse(response.rawResponse);
                } catch (JSONException e) {
                    try {
                        combineCardsResponse = new CombineCardsResponse(RequestUtils.getGenericErrorResponse(e.getMessage()));
                    } catch (JSONException ignored) {
                        combineCardsResponse = null;
                    }
                }
                callback.processError(combineCardsResponse);
            }
        });
    }

    @Override
    protected long getStaleTime() {
        return 0;
    }

}
