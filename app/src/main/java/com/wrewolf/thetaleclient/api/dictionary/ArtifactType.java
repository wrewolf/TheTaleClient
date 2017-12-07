package com.wrewolf.thetaleclient.api.dictionary;

/**
 * @author Hamster
 * @since 02.10.2014
 */
public enum ArtifactType {

    JUNK(0, "хлам"),
    MAIN_HAND(1, "основная рука", "EQUIPMENT_SLOT.HAND_PRIMARY"),
    OFF_HAND(2, "вторая рука", "EQUIPMENT_SLOT.HAND_SECONDARY"),
    BODY(3, "доспех", "EQUIPMENT_SLOT.PLATE"),
    AMULET(4, "амулет", "EQUIPMENT_SLOT.AMULET"),
    HEAD(5, "шлем", "EQUIPMENT_SLOT.HELMET"),
    CLOAK(6, "плащ", "EQUIPMENT_SLOT.CLOAK"),
    SHOULDERS(7, "наплечники", "EQUIPMENT_SLOT.SHOULDERS"),
    GLOVES(8, "перчатки", "EQUIPMENT_SLOT.GLOVES"),
    TROUSERS(9, "штаны", "EQUIPMENT_SLOT.PANTS"),
    BOOTS(10, "обувь", "EQUIPMENT_SLOT.BOOTS"),
    RING(11, "кольцо", "EQUIPMENT_SLOT.RING"),
    ;

    private final int code;
    private final String name;
    private String value;

    private ArtifactType(final int code, final String name) {
        this.code = code;
        this.name = name;
    }

    ArtifactType(final int code, final String name, final String value) {
        this.code = code;
        this.name = name;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
