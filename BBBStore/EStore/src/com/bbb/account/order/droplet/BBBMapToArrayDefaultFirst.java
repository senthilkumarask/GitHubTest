package com.bbb.account.order.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.util.MapToArrayDefaultFirst;
import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;

public class BBBMapToArrayDefaultFirst extends MapToArrayDefaultFirst {

	/**
	 * This droplet is used to convert a map property to a sorted array with the
	 * value of the first item being the item that matched the defaultId param.
	 * The droplet is primarily to sort the shipping address and credit card
	 * properties so that the default address or card is displayed as the first
	 * item on the jsp page. Each item in the sorted array is a repository item.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */

	@SuppressWarnings("unchecked")
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		if(isLoggingDebug()){
			logDebug("BBBMapToArrayDefaultFirst.service() method Started");
		}
		String defaultId2 = pRequest.getParameter(BBBCoreConstants.DEFAULT_ID_2);
		String defaultId = pRequest.getParameter(DEFAULT_ID);
		Map map = (Map) pRequest.getObjectParameter(MAP);
		String currentAddressID = pRequest.getParameter("currentAddressID");
		String nickName= null;
		HashMap<String, RepositoryItem> addressMap = new HashMap<String, RepositoryItem>();

		if (BBBUtility.isNotEmpty(defaultId2) && BBBUtility.isNotEmpty(defaultId) && map != null && map.size() > 2 && !defaultId.equals(defaultId2)) {

			boolean sortByKeys = false;
			String sortByKeysParameter = pRequest.getParameter(SORT_BY_KEYS);
			if (!(StringUtils.isEmpty(sortByKeysParameter))) {
				sortByKeys = Boolean.parseBoolean(sortByKeysParameter);
			}
			String defaultMapKey = getDefaultMapKey(map, defaultId);
			String defaultMapKey1 = getDefaultMapKey(map, defaultId2);
			Object[] sortedArray =null;
			if(BBBUtility.isEmpty(defaultMapKey1) || BBBUtility.isEmpty(defaultMapKey)){
				sortedArray = getSortedArray(map, defaultMapKey, sortByKeys, pRequest);
			}else{
				sortedArray = getSortedArray(map, defaultMapKey, defaultMapKey1, sortByKeys, pRequest);	
			}
			if(currentAddressID !=null){
				Iterator iter = map.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();

					String key = (String) entry.getKey();
					RepositoryItem value = (RepositoryItem) entry.getValue();
					if(value.getRepositoryId().equalsIgnoreCase(currentAddressID)){
						nickName= key;
					}
				}
			}
			pRequest.setParameter("nickname", nickName);
			pRequest.setParameter(SORTED_ARRAY, sortedArray);
			pRequest.setParameter(SORTED_ARRAY_SIZE, Integer.valueOf(sortedArray.length));
			pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);

		} else {
			if(currentAddressID !=null && null!=map){
				Iterator iter = map.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();

					String key = (String) entry.getKey();
					RepositoryItem value = (RepositoryItem) entry.getValue();
					if(value.getRepositoryId().equalsIgnoreCase(currentAddressID)){
						nickName= key;
					}
				}
			}
			pRequest.setParameter("nickname", nickName);
			/*pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);*/
			super.service(pRequest, pResponse);
		}
		if(isLoggingDebug()){
			logDebug("BBBMapToArrayDefaultFirst.service() method Started");
		}
	}

	/**
	 * This method is used by service to get element sorted
	 * 
	 * @param pMap
	 * @param pDefaultKey
	 * @param pDefaultKey2
	 * @param pSortByKeys
	 * @param pRequest
	 * @return Object[]
	 */
	protected Object[] getSortedArray(Map pMap, String pDefaultKey, String pDefaultKey2, boolean pSortByKeys, DynamoHttpServletRequest pRequest) {
		if(isLoggingDebug()){
			logDebug("BBBMapToArrayDefaultFirst.getSortedArray() method Started");
		}
		Map resultMap = pMap;
		Object[] sortedArrayDefaultFirst = new Object[resultMap.size()];
		Iterator iter = resultMap.entrySet().iterator();
		int index = 2;
		boolean defaultfound = false;
		boolean defaultfound2 = false;

		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();

			String key = (String) entry.getKey();
			if ((!(defaultfound)) && (key.equals(pDefaultKey))) {
				sortedArrayDefaultFirst[0] = entry;
				defaultfound = true;
			} else if ((!(defaultfound2)) && (key.equals(pDefaultKey2))) {
				sortedArrayDefaultFirst[1] = entry;
				defaultfound2 = true;
			} else {
				sortedArrayDefaultFirst[(index++)] = entry;
			}
		}
		if(isLoggingDebug()){
			logDebug("BBBMapToArrayDefaultFirst.getSortedArray() method Started");
		}
		return sortedArrayDefaultFirst;
	}

}
