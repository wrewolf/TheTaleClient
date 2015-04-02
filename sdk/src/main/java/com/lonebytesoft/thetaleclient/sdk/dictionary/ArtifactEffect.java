package com.lonebytesoft.thetaleclient.sdk.dictionary;

/**
 * @author Hamster
 * @since 02.10.2014
 */
public enum ArtifactEffect {

    POCKETS(8, "карманы", "Немного увеличивает вместимость рюкзака героя"),
    STRONG_SPIRIT(1030, "крепость духа", "Черты героя уменьшаются медленнее"),
    EXCEPTIONAL_STRENGTH(1000, "небывалая мощь", "Сильно увеличивает физический урон"),
    EXCEPTIONAL_MAGIC(1001, "могучее колдовство", "Сильно увеличивает магический урон"),
    HEALTH(3, "здоровье", "Немного увеличивает максимальное здоровье героя"),
    HASTE(1033, "ускорение", "Герою становится доступна способность «Ускорение» максимального уровня"),
    EXCEPTIONAL_HEALTH(1003, "невероятное здоровье", "Сильно увеличивает максимальное здоровье героя"),
    EXCEPTIONAL_INTUITION(1004, "сверхинтуиция", "Сильно увеличивает получаемый героем опыт"),
    EXCEPTIONAL_CUNNING(1005, "особая хитрость", "Сильно увеличивает влияние героя"),
    NO_EFFECT(666, "нет эффекта", "нет эффекта"),
    EXCEPTIONAL_ASTRAL_VESSEL(1006, "большой астральный сосуд", "Сильно увеличивает максимум энергии Хранителя"),
    TIRELESS_WALKER(1007, "неутомимый скороход", "Сильно увеличивает скорость движения героя"),
    ASTRAL_BLOCK(1026, "астральная преграда", "Герою становится доступна способность «Горгулья» максимального уровня"),
    BIG_POCKETS(1008, "большие карманы", "Сильно увеличивает вместимость рюкзака героя"),
    ENDURANCE(1009, "выносливость", "Герой быстрее восстанавливает здоровье во время отдыха"),
    SURVIVABILITY(1010, "живучесть", "Герой быстрее восстаёт из мёртвых"),
    ACTIVENESS(1011, "деятельность", "Герой меньше бездельничает"),
    IDEOLOGICAL(1031, "идейность", "Черты героя растут быстрее"),
    PERSUASION(1012, "убеждение", "Уменьшение всех трат"),
    CHARM(1013, "очарование", "Увеличение цены продажи предметов"),
    FOGGY_MIND(1027, "затуманенный разум", "Разум героя затуманивается и тот начинает вести себя независимо от черт"),
    SPIRIT_LINK(1014, "духовная связь", "Все затраты энергии уменьшаются на 1 (но не меньше 1)"),
    PEACE_OF_MIND(1015, "душевное равновесие", "Хранитель иногда получает в два раза больше энергии от героя"),
    STRENGTH(0, "мощь", "Немного увеличивает физический урон"),
    SPECIAL_AURA(1016, "особая аура", "Физическая и магическая сила всех артефактов, получаемых героем, увеличивается на 1"),
    REGENERATION(1017, "регенерация", "Герою становится доступна способность «Регенерация» максимального уровня"),
    DURABILITY(1032, "нерушимость", "Экипировка героя медленнее ломается"),
    LAST_CHANCE(1018, "последний шанс", "Герою становится доступна способность «Последний шанс» максимального уровня"),
    ICE(1019, "лёд", "Герою становится доступна способность «Заморозка» максимального уровня"),
    ASTRAL_VESSEL(6, "астральный сосуд", "Немного увеличивает максимум энергии Хранителя"),
    FIRE(1020, "пламя", "Герою становится доступна способность «Огненный шар» максимального уровня"),
    POISON(1021, "яд", "Герою становится доступна способность «Ядовитое облако» максимального уровня"),
    MENTAL_ALERTNESS(1023, "живость ума", "Задержка смены предпочтений уменьшается до 1 дня"),
    VAMPIRE(1022, "вампиризм", "Герою становится доступна способность «Удар вампира» максимального уровня"),
    MAGIC(1, "колдовство", "Немного увеличивает магический урон"),
    EXCEPTIONAL_REACTION(1002, "превосходная реакция", "Сильно увеличивает инициативу героя в бою"),
    DREADFUL_LOOK(1024, "ужасный вид", "Герой выглядит настолько ужасно, что некоторые противники в ужасе убегают, не вступая в бой"),
    PRECISE_ATTACKS(1025, "точные атаки", "Герою становится доступна способность «Критический удар» максимального уровня"),
    WALKER(7, "скороход", "Немного увеличивает скорость движения героя"),
    INTUITION(4, "повышение интуиции", "Немного увеличивает получаемый героем опыт"),
    CUNNING(5, "хитрость", "Немного увеличивает влияние героя"),
    REACTION(2, "хорошая реакция", "Немного увеличивает инициативу героя в бою"),
    WANDERER_LUCK(1028, "удача странника", "Увеличивается шанс получения редких артефактов"),
    HERO_LUCK(1029, "удача героя", "Увеличивается шанс получения эпических артефактов"),
    CHILD_GIFT(100001, "детский подарок", "Это потерянный подарок ребёнка. Помогите герою, когда артефакт лежит в рюкзаке, и подарок вернётся к ребёнку."),
    ;

    public final int code;
    public final String name;
    public final String description;

    ArtifactEffect(final int code, final String name, final String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

}
