package com.wrewolf.thetaleclient.api.request;

import android.util.Log;

import com.wrewolf.thetaleclient.api.CommonRequest;
import com.wrewolf.thetaleclient.api.CommonResponseCallback;
import com.wrewolf.thetaleclient.api.HttpMethod;
import com.wrewolf.thetaleclient.api.response.MapCellResponse;
import com.wrewolf.thetaleclient.util.RequestUtils;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hamster
 * @since 15.10.2014
 */
public class MapCellRequest extends CommonRequest
{

  private static final String URL = "http://the-tale.org/game/map/cell-info";

  public void execute(final int cellX, final int cellY, final CommonResponseCallback<MapCellResponse, String> callback)
  {
    final Map<String, String> getParams = new HashMap<>(2);
    getParams.put("x", String.valueOf(cellX));
    getParams.put("y", String.valueOf(cellY));
    execute(URL, HttpMethod.GET, getParams, null, new CommonResponseCallback<String, Throwable>()
    {
      @Override
      public void processResponse(String response)
      {
        try
        {
          RequestUtils.processResultInMainThread(callback, false, new MapCellResponse(response), null);
        } catch (JSONException e)
        {
          Log.d("MapCell", response);
          if (e != null)
          {
            RequestUtils.processResultInMainThread(callback, true, null, e.getLocalizedMessage());
          }
          RequestUtils.processResultInMainThread(callback, true, null, null);
        }
      }

      @Override
      public void processError(Throwable error)
      {
        if (error != null)
        {
          RequestUtils.processResultInMainThread(callback, true, null, error.getLocalizedMessage());
        }
        RequestUtils.processResultInMainThread(callback, true, null, null);
      }
    });
  }

  @Override
  protected long getStaleTime()
  {
    return 60 * 60 * 1000; // 1 hour
  }

}
