package com.wrewolf.thetaleclient.api.model;

import com.wrewolf.thetaleclient.util.ObjectUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Hamster
 * @since 16.02.2015
 */
public class CardsInfo {

    public final Collection<CardInfo> cards;
    public Integer cardHelpCurrent = 0;
    public Integer cardHelpBarrier = 0;

    public CardsInfo(final JSONObject json) throws JSONException {
        final JSONArray cardsJson = json.getJSONArray("cards");
        final int size = cardsJson.length();
        cards = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            final CardInfo card = ObjectUtils.getModelFromJson(CardInfo.class, cardsJson.getJSONObject(i));
            if (card != null) {
                cards.add(card);
            }
        }
    }
}
