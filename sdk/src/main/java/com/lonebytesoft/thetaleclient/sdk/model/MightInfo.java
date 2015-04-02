package com.lonebytesoft.thetaleclient.sdk.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Hamster
 * @since 18.03.2015
 */
public class MightInfo {

    public final double value;
    public final double helpCriticalChance;
    public final double pvpEffectivenessBonus;

    public MightInfo(final JSONObject json) throws JSONException {
        value = json.getDouble("value");
        helpCriticalChance = json.getDouble("crit_chance");
        pvpEffectivenessBonus = json.getDouble("pvp_effectiveness_bonus");
    }

}
