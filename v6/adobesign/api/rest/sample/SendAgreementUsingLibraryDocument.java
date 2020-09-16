/*************************************************************************
 * ADOBE SYSTEMS INCORPORATED
 * Copyright 2018 Adobe Systems Incorporated
 * All Rights Reserved.
 * 
 * NOTICE: Adobe permits you to use, modify, and distribute this file in accordance with the
 * terms of the Adobe license agreement accompanying it. If you have received this file from a
 * source other than Adobe, then your use, modification, or distribution of it requires the prior
 * written permission of Adobe.
 **************************************************************************/

package adobesign.api.rest.sample;

import adobesign.api.rest.sample.util.RestApiAgreements;
import adobesign.api.rest.sample.util.RestApiLibraryDocuments;
import adobesign.api.rest.sample.util.RestApiOAuthTokens;
import adobesign.api.rest.sample.util.RestApiUserGroups;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * This sample client demonstrates how to send an agreement using a library document ID. The user needs to have have at
 * least one library template of document type in their account to run this sample successfully.
 * 
 * <p>
 * <b>IMPORTANT</b>: Before running this sample, check that you have modified the JSON files 'OAuthCredentials.json' and 'SendAgreement.json'
 * with appropriate values. Which values need to be specified is indicated in the files.
 * You can also provide OAuth access token in OAUTH_ACCESS_TOKEN variable in RestApiOAuthTokens class which will then be used as OAuth access token for making api calls.
 * You can also provide refresh token in OAUTH_REFRESH_TOKEN variable in RestApiOAuthTokens class to refresh OAuth access token.
 *
 * Note: The access token must have agreement_write scope to run this sample.
 * </p>
 */
public class SendAgreementUsingLibraryDocument {
  // File containing the request JSON for fetching access token.
  private static final String authRequestJSONFileName = "OAuthCredentials.json";

  // File containing the request JSON for sending an agreement.
  private static final String sendAgreementJSONFileName = "SendAgreement.json";

  /**
   * Entry point for this sample client program.
   */
  public static void main(String args[]) {
    try {
      SendAgreementUsingLibraryDocument client = new SendAgreementUsingLibraryDocument();
      client.run();
    }
    catch (Exception e) {
      System.err.println("Failure in sending the agreemnet using the library document ID specified.");
      e.printStackTrace();
    }
  }

  /**
   * Execution of this sample client program.
   */
  private void run() throws Exception {
    // Fetch oauth access token to make further API calls.
    String accessToken = RestApiOAuthTokens.getOauthAccessToken(authRequestJSONFileName);

    // get all active groups of the user
    JSONObject userGroups = RestApiUserGroups.getUserGroups(accessToken);
    JSONArray userGroupsList = (JSONArray) userGroups.get("groupInfoList");

    // select the first group the user can send from
    String groupId = null;
    for (Object eachUserGroup : userGroupsList) {
      JSONObject userGroup = (JSONObject) eachUserGroup;
      String groupStatus = (String) userGroup.get("status");
      JSONObject groupSettings = (JSONObject) userGroup.get("settings");
      JSONObject userCanSend = (JSONObject) groupSettings.get("userCanSend");
      Boolean userCanSendValue = (Boolean) userCanSend.get("value");
      if (groupStatus.equals("ACTIVE") && userCanSendValue) {
        groupId = userGroup.get("id").toString();
        break;
      }
    }

    // no group with send permissions was found
    if (groupId == null){
      System.err.println("No Group the user can send from");
      return;
    }

    // Fetch library documents of the user using access token from above.
    JSONObject libraryDocumentsResponse = RestApiLibraryDocuments.getLibraryDocuments(accessToken, groupId);

    // Retrieve library documents list for the user and fetch the ID of first library document.
    JSONArray libraryDocumentList = (JSONArray) libraryDocumentsResponse.get("libraryDocumentList");

    String libraryDocumentId = null;
    // Fetch the first personal or shared library document of the user.
    for (Object eachLibraryDocument : libraryDocumentList) {
      JSONObject libraryDocument = (JSONObject) eachLibraryDocument;
      if (libraryDocument.get("sharingMode").equals("ACCOUNT") || libraryDocument.get("sharingMode").equals("GROUP") || libraryDocument.get("sharingMode").equals("USER")) {
        libraryDocumentId = (String) libraryDocument.get("id");
        break;
      }
    }
    
    if (libraryDocumentId != null && !libraryDocumentId.isEmpty()) {
      // Send agreement using this library document ID retrieved from above.
      JSONObject sendAgreementResponse = RestApiAgreements.sendAgreement(accessToken, sendAgreementJSONFileName, libraryDocumentId, groupId,
                                                                         RestApiAgreements.DocumentIdentifierName.LIBRARY_DOCUMENT_ID);

      // Parse and read response.
      System.out.println("Agreement Sent. Agreement ID = " + sendAgreementResponse.get("id"));
    }
    else {
      System.err.println("No library documents found.");
    }
  }
}