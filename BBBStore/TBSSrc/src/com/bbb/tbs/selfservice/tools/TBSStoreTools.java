package com.bbb.tbs.selfservice.tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import atg.adapter.gsa.GSARepository;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.selfservice.common.StoreDetailsWrapper;
import com.bbb.selfservice.tools.StoreTools;
import com.bbb.utils.BBBUtility;

/**
 * Created by acer on 9/3/2014.
 */
public class TBSStoreTools extends StoreTools{
	
	String orfQuery;
	
    public StoreDetailsWrapper searchStore(String pSearchString)
            throws ClientProtocolException, IOException, BBBBusinessException {
        String logMessage = getClass().getName() + "searchStore";
        logDebug(logMessage + " || " + " Starts here.");
        logDebug(logMessage + " || " + " Search Criteria String --> "
                + pSearchString);
        StoreDetailsWrapper objStoreDetailsWrapper = null;
        String searchResult = null;


        String searchString = pSearchString;
        searchString = parseSearchString(searchString);
        try {
            searchResult = getHttpCallInvoker().executeQuery(searchString);
        } catch (BBBSystemException e) {
            logError("\n StoreTools.searchStore Request sent to MapQuest web service call is " + searchString);
            BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_MAPQUEST_1001, "Exception in calling Mapquest in searchStore");
        }

        if (!BBBUtility.isEmpty(searchResult)) {
//            objStoreDetailsWrapper = storeJSONObjectParser(searchResult,searchString);
        }
        return objStoreDetailsWrapper;
    }



    private String parseSearchString(String pSearchString) throws UnsupportedEncodingException {
        String searchString = pSearchString;
        if(!BBBUtility.isEmpty(pSearchString)){

            if (pSearchString.contains("|")) {
                String pipe = URLEncoder.encode("|", "UTF-8");
                searchString = pSearchString.replace("|", pipe);
            }
            if (pSearchString.contains(" ")) {
                String space = URLEncoder.encode(" ", "UTF-8");
                searchString = pSearchString.replaceAll(" ", space);
            }
        }
        return searchString;
    }

	public String getOrfQuery() {
		return orfQuery;
	}

	public void setOrfQuery(String orfQuery) {
		this.orfQuery = orfQuery;
	}

	public Map<String, Integer> fetchORFInventory(String siteId, String skuId) throws BBBBusinessException {
		logDebug("TBSStoreTools.fetchORFInventory start :: siteId " + siteId + " skuId :: " + skuId);
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Connection connection = null;
		Map<String, Integer> inventoryByNetwork = new HashMap<String, Integer>();
		int inventoryStock = 0;
		int minStoreId = 0, maxStoreId = 0;
		if(siteId.contains(TBSConstants.BED_BATH_US)){
			minStoreId = 0;
			maxStoreId = 1999;
	    } else if(siteId.contains(TBSConstants.BED_BATH_CA)){
	    	minStoreId = 2000;
	    	maxStoreId = 2999;
	    } else if(siteId.contains(TBSConstants.BUY_BUY_BABY)){
	    	minStoreId = 3000;
	    	maxStoreId = 3999;
	    }
		
		try {
			connection = ((GSARepository) this.getLocalStoreRepository())
					.getDataSource().getConnection();
			if (connection != null) {
				preparedStatement = connection.prepareStatement(getOrfQuery());
	            preparedStatement.setString(1, skuId);
	            preparedStatement.setInt(2, minStoreId);
	            preparedStatement.setInt(3, maxStoreId);
	            resultSet = preparedStatement.executeQuery();
	            logDebug("Result set from local store inventory ::" + resultSet.toString());
	            if (resultSet != null) {
					while (resultSet.next()) {
						int stockLevel = resultSet.getInt(BBBCoreConstants.STOCK_LEVEL_COLUMN);
							inventoryStock += stockLevel;
					}
	            }
			}
		} catch (SQLException sqlException) {
			logError("SQL exception occurred while fetching the data from the local store repository");
			throw new BBBBusinessException(
					"SQL exception occurred while fetching the data from the local store repository",
					sqlException);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logError("SQL exception occurred while closing the connection");
			}
		}
		inventoryByNetwork.put(skuId, inventoryStock);
		logDebug("TBSStoreTools.fetchORFInventory end :: inventoryByNetwork " + inventoryByNetwork);
		return inventoryByNetwork;
	}
	
}
