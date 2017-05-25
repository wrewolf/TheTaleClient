package com.wrewolf.thetaleclient.api.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Hamster
 * @since 02.10.2014
 */
public class DiaryEntry {

    public final String message;
    public final String position;
    public final String game_time;
    public final String game_date;
    public final String type;
    public final String variables;
    public final int timestamp;

    public DiaryEntry(final JSONObject json) throws JSONException {
        message = json.getString("message");
        position = json.getString("position");
        game_time = json.getString("game_time");
        game_date = json.getString("game_date");
        type = json.getString("type");
        variables =json.getString("variables");
        timestamp = json.getInt("timestamp");
    }

}
