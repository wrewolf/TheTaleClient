package com.wrewolf.thetaleclient.api.response;

import com.wrewolf.thetaleclient.api.AbstractApiResponse;
import com.wrewolf.thetaleclient.api.model.CouncilMemberInfo;
import com.wrewolf.thetaleclient.api.model.MapCellTerrainInfo;
import com.wrewolf.thetaleclient.api.model.Place;
import com.wrewolf.thetaleclient.api.model.PlaceInfo;
import com.wrewolf.thetaleclient.util.ObjectUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Алексей on 26.05.2017.
 */

public class PlaceInfoResponse extends AbstractApiResponse
{

  public final Place place;

  public PlaceInfoResponse(final String response) throws JSONException
  {
    super(response);
    final JSONObject json = new JSONObject(response);
    this.place = ObjectUtils.getModelFromJson(Place.class, json);
  }

  @Override
  protected void parseData(JSONObject data) throws JSONException
  {

  }
}
