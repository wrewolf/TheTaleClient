package com.wrewolf.thetaleclient.api.response;

import com.wrewolf.thetaleclient.api.AbstractApiResponse;
import com.wrewolf.thetaleclient.api.dictionary.GameState;
import com.wrewolf.thetaleclient.api.dictionary.HeroMode;
import com.wrewolf.thetaleclient.api.model.AccountInfo;
import com.wrewolf.thetaleclient.api.model.CardInfo;
import com.wrewolf.thetaleclient.api.model.CardsInfo;
import com.wrewolf.thetaleclient.api.model.TurnInfo;
import com.wrewolf.thetaleclient.util.ObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wrewolf on 27.10.17.
 */

public class CardsResponse extends AbstractApiResponse {
    public CardsInfo cards;

    public CardsResponse(final String response) throws JSONException {
        super(response);
    }

    @Override
    protected void parseData(JSONObject data) throws JSONException {
        cards = ObjectUtils.getModelFromJson(CardsInfo.class, data);
    }
}
