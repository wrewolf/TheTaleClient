package com.wrewolf.thetaleclient.api.response;

import com.wrewolf.thetaleclient.api.AbstractApiResponse;
import com.wrewolf.thetaleclient.api.model.MapPlaceInfo;
import com.wrewolf.thetaleclient.api.model.RoadInfo;
import com.wrewolf.thetaleclient.api.model.SpriteTileInfo;
import com.wrewolf.thetaleclient.util.ObjectUtils;
import com.wrewolf.thetaleclient.util.map.MapUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Hamster
 * @since 07.10.2014
 */
public class MapResponse extends AbstractApiResponse {

    public String formatVersion;
    public String mapVersion;
    public Map<Integer, MapPlaceInfo> places;
    public Map<Integer, RoadInfo> roads;
    public int width;
    public int height;
    public List<List<List<SpriteTileInfo>>> tiles;

    public MapResponse(String response) throws JSONException {
        super(response);
    }

    protected void parseData(final JSONObject data) throws JSONException {
        final JSONObject json = data.getJSONObject("region");

        formatVersion = json.getString("format_version");

        final JSONObject placesJson = json.getJSONObject("places");
        places = new HashMap<>(placesJson.length());
        for(final Iterator<String> placesIterator = placesJson.keys(); placesIterator.hasNext();) {
            final String key = placesIterator.next();
            places.put(Integer.decode(key), ObjectUtils.getModelFromJson(MapPlaceInfo.class, placesJson.getJSONObject(key)));
        }

        final JSONObject roadsJson = json.getJSONObject("roads");
        roads = new HashMap<>(roadsJson.length());
        for(final Iterator<String> roadsIterator = roadsJson.keys(); roadsIterator.hasNext();) {
            final String key = roadsIterator.next();
            roads.put(Integer.decode(key), ObjectUtils.getModelFromJson(RoadInfo.class, roadsJson.getJSONObject(key)));
        }

        final JSONArray drawJson = json.getJSONArray("draw_info");
        final int rowsCount = drawJson.length();
        tiles = new ArrayList<>(rowsCount);
        for(int i = 0; i < rowsCount; i++) {
            final JSONArray rowJson = drawJson.getJSONArray(i);
            final int columnsCount = rowJson.length();
            final List<List<SpriteTileInfo>> row = new ArrayList<>(columnsCount);
            for(int j = 0; j < columnsCount; j++) {
                final JSONArray cellJson = rowJson.getJSONArray(j);
                final int tilesCount = cellJson.length();
                final List<SpriteTileInfo> cell = new ArrayList<>(tilesCount);
                for(int k = 0; k < tilesCount; k++) {
                    final JSONArray tileJson = cellJson.getJSONArray(k);
                    cell.add(MapUtils.getSpriteTile(tileJson.getInt(0), tileJson.getInt(1)));
                }
                row.add(cell);
            }
            tiles.add(row);
        }

        width = json.getInt("width");
        height = json.getInt("height");
        mapVersion = json.getString("map_version");
    }
}