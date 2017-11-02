package com.wrewolf.thetaleclient.api.dictionary;

/**
 * Created by Alexew on 02.11.2017.
 */

public enum EnergyRegeneration {
    PRAY("молитва", "ENERGY_REGENERATION.PRAY"),
    SACRIFICE("жертвоприношение", "ENERGY_REGENERATION.SACRIFICE"),
    INCENSE("благовония", "ENERGY_REGENERATION.INCENSE"),
    SYMBOLS("символы", "ENERGY_REGENERATION.SYMBOLS"),
    MEDITATION("медитация", "ENERGY_REGENERATION.MEDITATION"),
    ;

    private final String code;
    private final String value;

    EnergyRegeneration(final String code, final String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
