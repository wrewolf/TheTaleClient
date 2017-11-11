package com.wrewolf.thetaleclient.api.request;

import com.wrewolf.thetaleclient.api.CommonRequest;
import com.wrewolf.thetaleclient.api.CommonResponseCallback;
import com.wrewolf.thetaleclient.api.HttpMethod;
import com.wrewolf.thetaleclient.api.response.MapResponse;
import com.wrewolf.thetaleclient.util.RequestUtils;

import org.json.JSONException;

/**
 * @author Hamster
 * @since 07.10.2014
 * todo undocumented request
 */
public class MapRequest extends CommonRequest
{
  //  private static final String URL = "http://the-tale.org/dcont/map/region-%s.js";
  private static final String URL = "http://the-tale.org/game/map/api/region?api_version=0.1&api_client=the_tale-v0.3.24.6&v=%s";

  private final String mapVersion;

  public MapRequest(final String mapVersion)
  {
    this.mapVersion = mapVersion;
  }

  public void execute(final CommonResponseCallback<MapResponse, String> callback)
  {
    execute(String.format(URL, mapVersion), HttpMethod.GET, null, null, new CommonResponseCallback<String, Throwable>()
    {
      @Override
      public void processResponse(String response)
      {
        try
        {
          RequestUtils.processResultInMainThread(callback, false, new MapResponse(response), null);
        } catch (JSONException e)
        {
          RequestUtils.processResultInMainThread(callback, true, null, e.getLocalizedMessage());
        }
      }

      @Override
      public void processError(Throwable error)
      {
        if (error == null)
        {
          RequestUtils.processResultInMainThread(callback, true, null, null);
          return;
        }
        try
        {
          error.printStackTrace();
        } catch (Exception e)
        {
          e.printStackTrace();
        }
        RequestUtils.processResultInMainThread(callback, true, null, error.getLocalizedMessage());
      }
    });
  }

  @Override
  protected long getStaleTime()
  {
    return 2 * 60 * 60 * 1000; // 2 hours
  }

}