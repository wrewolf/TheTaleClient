package com.wrewolf.thetaleclient.api.dictionary;

/**
 * Created by Alexew on 01.11.2017.
 */

public enum CardFullType {
    FRESH_LOOK_1("117-1", CardTargetType.PLACE, true),
    FRESH_LOOK_2("117-2", CardTargetType.PERSON, true),
    FRESH_LOOK_3("117-4", CardTargetType.ENERGY_REGENERATION, false),
    FRESH_LOOK_6("117-6", CardTargetType.RISK_LEVEL, false),
    FRESH_LOOK_7("117-7", CardTargetType.EQUIPMENT_SLOT, true),
    FRESH_LOOK_8("117-8", CardTargetType.ARCHETYPE, false),
    ;

    private final String code;
    private final CardTargetType targetType;
    private final Boolean canBeForgotten;

    CardFullType(final String code, final CardTargetType targetType, final Boolean canBeForgotten) {
        this.code = code;
        this.targetType = targetType;
        this.canBeForgotten = canBeForgotten;
    }

    public String getCode() {
        return code;
    }

    public CardTargetType getTargetType() {
        return targetType;
    }

    public Boolean getCanBeForgotten() {
        return canBeForgotten;
    }
}
