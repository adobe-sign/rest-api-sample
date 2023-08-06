/*************************************************************************
 * ADOBE SYSTEMS INCORPORATED
 * Copyright 2020 Adobe Systems Incorporated
 * All Rights Reserved.
 * 
 * NOTICE: Adobe permits you to use, modify, and distribute this file in accordance with the
 * terms of the Adobe license agreement accompanying it. If you have received this file from a
 * source other than Adobe, then your use, modification, or distribution of it requires the prior
 * written permission of Adobe.
 **************************************************************************/

package adobesign.api.rest.sample.util;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * Encapsulates calls to REST end points related to users groups.
 */
public class RestApiUserGroups {
  private static final String userGroups = "/users/me/groups";

  /**
   * Fetches the list of all active groups memberships of a user.
   * 
   * @param accessToken access token of the user whose groups are to be fetched.
   * @return JSON response containing the list of all the groups the user is a member of.
   * @throws IOException
   */
  public static JSONObject getUserGroups(String accessToken) throws Exception {
    // URL for library documents end point.
    String url = RestApiUtils.getBaseURIForAPI(accessToken) + userGroups;

    // Create header list.
    HashMap<String, String> headers = new HashMap<String, String>();
    headers.put(RestApiUtils.HttpHeaderField.CONTENT_TYPE.toString(), RestApiUtils.MimeType.JSON.toString());
    headers.put(RestApiUtils.HttpHeaderField.AUTHORIZATION.toString(), accessToken);

    // Invoke API and get JSON response.
    JSONObject responseJSON = null;
    responseJSON = (JSONObject) RestApiUtils.makeApiCall(url, RestApiUtils.HttpRequestMethod.GET, headers);
    
    return responseJSON;
  }

}
