package com.bbb.tbs.selfservice.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.ezmorph.MorphException;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.selfservice.common.SelfServiceConstants;

public class TBSMapQuestManager extends BBBGenericService {
	
	private BBBCatalogTools mCatalogTools;
	HTTPCallInvoker mHttpCallInvoker;
	String searchResult = null;
	JSONObject jsonObject = null;
	JSONObject  jsonorigin = null;
	JSONObject  jsonlatlong = null;
	JSONObject  jsonInfo = null;
	Map<String,Object> latLongMap = new HashMap<String,Object>();
	
	/**
	 * Construct the search string from params and does an an HTTP call and then parses the search result and returns a map 
	 * consisting of latitude and longitude.
	 * @param pStoreType
	 * @param pRadius
	 * @param pOrigin
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public Map<String,Object> constructSearchString(String pStoreType, Object pRadius, String pOrigin) throws BBBSystemException, BBBBusinessException{
		vlogDebug("Inside TBSMapQuestManager :: constructSearchString() method :: start");
		
		StringBuilder searchStringBuilder = new StringBuilder();
		String searchString = "";
		
		searchStringBuilder.append(getMapQuestRadiusInfoString());

		searchStringBuilder.append("%7C");
		searchStringBuilder.append("store_type="+pStoreType);
		searchStringBuilder.append(BBBCoreConstants.AMPERSAND+"radius=").append(pRadius);
		searchStringBuilder.append(BBBCoreConstants.AMPERSAND+"origin=").append(pOrigin);
		
		
		searchString = searchStringBuilder.toString();
		vlogDebug("MapQuest Search String :: ", searchString);
		
		searchResult = getHttpCallInvoker().executeQuery(searchString);
		vlogDebug("MapQuest searchResult :: ", searchResult);
		
		latLongMap = parseSearchJsonResult(searchResult);
		vlogDebug("Inside TBSMapQuestManager :: constructSearchString() method :: end");
		return latLongMap;
		
	}
	
	/**
	 * This Method takes in Json search string and renders a map with latitude and longitude details
	 * @param pSearchResult
	 * @return latLongMap
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> parseSearchJsonResult(String pSearchResult) {
		vlogDebug("Inside TBSMapQuestManager :: parseSearchJsonResult() method :: start");
		if(searchResult!=null){
			
			jsonObject = (JSONObject) JSONSerializer.toJSON(searchResult);
			vlogDebug("JSON object :: ", jsonObject);
			if(jsonObject!=null){
				jsonInfo = jsonObject.getJSONObject("info");;
			}else{
				logError("There was a problem calling the webservice");
			}
		    
			if(jsonInfo.get("statusCode")!=null && jsonInfo.get("statusCode").equals(0)){
				
			    jsonorigin = jsonObject.getJSONObject("origin");
			    jsonlatlong = jsonorigin.getJSONObject("latLng");
				
				if(jsonlatlong!=null){
					Iterator iter = jsonlatlong.keys();
				    while(iter.hasNext()){
				        String key = (String)iter.next();
				        Object value = jsonlatlong.get(key);
				        latLongMap.put(key,value);
				    }
				}
			    
			}else if(jsonInfo.get("statusCode")!=null && jsonInfo.get("statusCode").equals(610)){
				// If response has multiple address suggestions then statuscode is 610.
				DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
				List<String> dynaBeanProperties = getPropertyNames(JSONResultbean);
				
				List<DynaBean> alResults = null;
				List<MorphDynaBean> alAddress = null;
				try{
					alResults = (ArrayList<DynaBean>) JSONResultbean.get(SelfServiceConstants.COLLECTIONS);
								
				for (Iterator<DynaBean> iterator = alResults.iterator(); iterator.hasNext();) {
					alAddress = (ArrayList<MorphDynaBean>)iterator.next();
					
					for (Iterator<MorphDynaBean> iterator1 = alAddress.iterator(); iterator1.hasNext();) {
					MorphDynaBean searchResultBean = iterator1.next();
				
					List<String> searchResultBeanProperties = getPropertyNames(searchResultBean);

								if(searchResultBeanProperties.contains("adminArea1")) {
									String adminArea1 = (String)searchResultBean.get("adminArea1");
									if(adminArea1 != null && adminArea1.equals("US")){
										if (searchResultBeanProperties.contains("latLng")) {
											MorphDynaBean latLngBean = (MorphDynaBean) searchResultBean.get("latLng");
											List<String> beanProperties = getPropertyNames(latLngBean);
											if(beanProperties!=null){
												Iterator iter = beanProperties.iterator();
											    while(iter.hasNext()){
											        String key = (String)iter.next();
											        Object value = latLngBean.get(key);
											        latLongMap.put(key,value);
											    	}
												}
											}
									}
								}
						}
					}
				}catch (MorphException e){
					//Handling Exception in case of Ambiguous Result
					logError("There was a MorphException :: "+e);
				}
				
			}else if(jsonInfo.get("statusCode")!=null && jsonInfo.get("statusCode").equals(1)){
				logError("There was a problem calling the webservice ");
			}
		}
		vlogDebug("Inside TBSMapQuestManager :: parseSearchJsonResult() method :: end");
		return latLongMap;
		
	}
	
	/**
	 * @return the mapQuestRadiusString
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public String getMapQuestRadiusInfoString() throws BBBSystemException, BBBBusinessException {
		vlogDebug("Inside TBSMapQuestManager :: getMapQuestRadiusInfoString() method :: start");
		String configValue = null;
		List<String> keysList = getCatalogTools().getAllValuesForKey("ThirdPartyURLs", "mapQuestRadiusString");
		if(keysList != null && keysList.size()>0)
		{
			configValue =  keysList.get(0);
		}
		vlogDebug("Inside TBSMapQuestManager :: getMapQuestRadiusInfoString() method :: end");
		return configValue;
	}
	
	/**
	 * To get the properties names from JSON result string
	 * @param pDynaBean
	 * @return propertyNames
	 */
	private List<String> getPropertyNames(DynaBean pDynaBean) {
		vlogDebug("Inside TBSMapQuestManager :: getPropertyNames() method :: start");
		DynaClass dynaClass = pDynaBean.getDynaClass();
		DynaProperty properties[] = dynaClass.getDynaProperties();
		List<String> propertyNames = new ArrayList<String>();
		for (int i = 0; i < properties.length; i++) {
			String name = properties[i].getName();
			propertyNames.add(name);
		}
		vlogDebug("Inside TBSMapQuestManager :: getPropertyNames() method :: end");
		return propertyNames;
	}

	
	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param mCatalogTools the mCatalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
	
	/**
	 * @return HTTPCallInvoker
	 */
	public HTTPCallInvoker getHttpCallInvoker() {
		return this.mHttpCallInvoker;
	}

	/**
	 * @param httpCallInvoker set HTTPCallInvoker
	 */
	public void setHttpCallInvoker(final HTTPCallInvoker pHttpCallInvoker) {
		this.mHttpCallInvoker = pHttpCallInvoker;
	}
	
}
