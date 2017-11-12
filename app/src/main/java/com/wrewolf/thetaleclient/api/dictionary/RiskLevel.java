package com.wrewolf.thetaleclient.api.dictionary;

/**
 * Created by Alexew on 12.11.2017.
 */

public enum RiskLevel {
    VERY_HIGH("очень высокий", "RISK_LEVEL.VERY_HIGH"),
    HIGH("высокий", "RISK_LEVEL.HIGH"),
    NORMAL("обычный", "RISK_LEVEL.NORMAL"),
    LOW("низкий", "RISK_LEVEL.LOW"),
    VERY_LOW("очень низкий", "RISK_LEVEL.VERY_LOW"),
    ;

    private final String code;
    private final String value;

    RiskLevel(final String code, final String value) {
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
