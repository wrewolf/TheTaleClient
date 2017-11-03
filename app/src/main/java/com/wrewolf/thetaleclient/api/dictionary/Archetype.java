package com.wrewolf.thetaleclient.api.dictionary;

/**
 * @author Hamster
 * @since 25.01.2015
 */
public enum Archetype {

    MAGE("маг", "ARCHETYPE.MAGICAL"),
    ADVENTURER("авантюрист", "ARCHETYPE.NEUTRAL"),
    WARRIOR("воин", "ARCHETYPE.PHYSICAL"),
    ;

    private final String code;
    private final String value;

    Archetype(final String code, final String value) {
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
