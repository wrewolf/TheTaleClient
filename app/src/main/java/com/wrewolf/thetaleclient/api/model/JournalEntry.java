package com.wrewolf.thetaleclient.api.model;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;

import com.wrewolf.thetaleclient.util.PreferencesManager;
import com.wrewolf.thetaleclient.util.UiUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Hamster
 * @since 02.10.2014
 */
public class JournalEntry {

    public final int timestamp;
    public final String time;
    public final String text;
    public final Spannable spannableText;

    public JournalEntry(final JSONObject json) throws JSONException {
        timestamp = json.getInt("timestamp");
        time = json.getString("time");
        text = json.getString("text");

        String artText = json.getString("text");
        JSONObject dictionary = json.getJSONObject("dictionary");

        if (PreferencesManager.getAppearanceJournalType() == 2) {
            dictionary = new JSONObject();
        }

        if (PreferencesManager.getAppearanceJournalType() == 3) {
            artText = null;
        }

        if (dictionary.has("damage") && dictionary.has("defender")) {
            final String coloredText = "-" + dictionary.getString("damage") + "♥";
            final String technicalText = dictionary.getString("defender").substring(0, 1).toUpperCase() + dictionary.getString("defender").substring(1) + " " + coloredText;
            spannableText = UiUtils.buildSpannableText(artText, technicalText, coloredText.length(), Color.parseColor("#ff0000"));
        } else if (dictionary.has("coins") && dictionary.has("hero")) {
            final String coloredText;
            if (dictionary.has("companion")) {
                // TODO: Нужно как-то различать потратил деньги спутник или заработал. Информации об этом в json нет
                coloredText = dictionary.getString("coins") + "☉";
            } else {
                coloredText = "+" + dictionary.getString("coins") + "☉";
            }
            final String technicalText = dictionary.getString("hero").substring(0, 1).toUpperCase() + dictionary.getString("hero").substring(1) + " " + coloredText;
            spannableText = UiUtils.buildSpannableText(artText, technicalText, coloredText.length(), Color.parseColor("#ff6600"));
        } else if (dictionary.has("health") && dictionary.has("hero")) {
            final String coloredText = "+" + dictionary.getString("health") + "♥";
            final String technicalText;
            if (dictionary.has("companion")) {
                technicalText = dictionary.getString("companion").substring(0, 1).toUpperCase() + dictionary.getString("companion").substring(1) + " " + coloredText;
            } else if (dictionary.has("actor")) {
                technicalText = dictionary.getString("actor").substring(0, 1).toUpperCase() + dictionary.getString("actor").substring(1) + " " + coloredText;
            } else {
                technicalText = dictionary.getString("hero").substring(0, 1).toUpperCase() + dictionary.getString("hero").substring(1) + " " + coloredText;
            }
            spannableText = UiUtils.buildSpannableText(artText, technicalText, coloredText.length(), Color.parseColor("#008000"));
        } else if (dictionary.has("energy") && dictionary.has("hero")) {
            final String coloredText = "+" + dictionary.getString("energy") + "⚡";
            final String technicalText = dictionary.getString("hero").substring(0, 1).toUpperCase() + dictionary.getString("hero").substring(1) + " " + coloredText;
            spannableText = UiUtils.buildSpannableText(artText, technicalText, coloredText.length(), Color.parseColor("#0000ff"));
        } else if (dictionary.has("experience") && dictionary.has("hero")) {
            final String coloredText = "+" + dictionary.getString("experience") + "★";
            final String technicalText = dictionary.getString("hero").substring(0, 1).toUpperCase() + dictionary.getString("hero").substring(1) + " " + coloredText;
            spannableText = UiUtils.buildSpannableText(artText, technicalText, coloredText.length(), Color.parseColor("#6600ff"));
        } else {
            spannableText = new SpannableString(text);
        }
    }

}
