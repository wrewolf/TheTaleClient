package com.wrewolf.thetaleclient.api.model;

import com.wrewolf.thetaleclient.api.dictionary.PlaceSpecialization;
import com.wrewolf.thetaleclient.util.ObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Hamster
 * @since 04.05.2015
 */
public class Place
{
    //   "id": <целое число>,        // идентификатор города
    //   "name": "строка",           // название города
    //   "frontier": true|false,     // является ли город фронтиром
    //   "new_for": <timestamp>,     // время, до которого город считается новым
    //   "updated_at": <timestamp>,  // время последнего обновления информации
    //   "description": "строка",    // описание города

    //   "position": {"x": <целое число>, "y": <целое число>},   // координаты города

    //   "politic_power": <politic_power>,     // политическое влияние города
    //   "persons": <persons_info>,            // Мастера
    //   "attributes": <attributes_info>,      // все параметры города
    //   "demographics": <demographics_info>,  // расовый состав
    //   "bills": <bills_info>,                // действующие записи в книге судеб
    //   "habits": <habits_info>,              // черты города
    //   "chronicle": <chronicle_info>,        // последние записи в летописи
    //   "job": <job_info>,                    // информация о проекте
    //   "accounts": <accounts_info>,          // краткая дополнительная информация об игроках, связанных с городом
    //   "clans": <clans_info>                 // краткая дополнительная информация о кланах, связанных с городом

    public final int id;
    public final String name;
    public final int x;
    public final int y;
    public final boolean isFrontier;

    public Place(final JSONObject json) throws JSONException {
        id = json.getInt("id");
        name = json.getString("name");
        x = json.getJSONObject("position").getInt("x");
        y = json.getJSONObject("position").getInt("y");
        isFrontier = json.getBoolean("frontier");
    }

}
