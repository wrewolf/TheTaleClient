package com.wrewolf.thetaleclient.api.request;

import com.wrewolf.thetaleclient.api.AbstractApiRequest;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.CommonRequest;
import com.wrewolf.thetaleclient.api.CommonResponseCallback;
import com.wrewolf.thetaleclient.api.HttpMethod;
import com.wrewolf.thetaleclient.api.response.MapResponse;
import com.wrewolf.thetaleclient.util.RequestUtils;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hamster
 * @since 07.10.2014
 * todo undocumented request
 */
public class MapRequest extends AbstractApiRequest<MapResponse>
{
  //  private static final String URL = "http://the-tale.org/dcont/map/region-%s.js";
  //private static final String URL = "http://the-tale.org/game/map/api/region?api_version=0.1&api_client=the_tale-v0.3.24.6&v=%s";

  private final String mapVersion;

  public MapRequest(final String mapVersion)
  {
    super(HttpMethod.GET, "game/map/api/region", "0.1", true);
    this.mapVersion = mapVersion;
  }

  public void execute(final ApiResponseCallback<MapResponse> callback)
  {
    final Map<String, String> getParams = new HashMap<>(1);
    getParams.put("v", mapVersion);
    execute(getParams, null, callback, false);
  }

  protected MapResponse getResponse(String response) throws JSONException {
    return new MapResponse(response);
  }

  @Override
  protected long getStaleTime()
  {
    return 2 * 60 * 60 * 1000; // 2 hours
  }

}