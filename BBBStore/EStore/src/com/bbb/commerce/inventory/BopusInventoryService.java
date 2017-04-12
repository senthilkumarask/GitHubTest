package com.bbb.commerce.inventory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.bbb.bopus.inventory.input.Header;
import com.bbb.bopus.inventory.input.LineItem;
import com.bbb.bopus.inventory.input.Message;
import com.bbb.bopus.inventory.input.ObjectFactory;
import com.bbb.bopus.inventory.input.SupplyBalanceInput;
import com.bbb.bopus.inventory.input.TXML;
import com.bbb.bopus.inventory.output.ViewByFacility;
import com.bbb.bopus.inventory.vo.SupplyBalanceRequestVO;
import com.bbb.bopus.inventory.vo.SupplyBalanceResponseVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.commerce.inventory.vo.InventoryRequestVO;
import com.bbb.commerce.inventory.vo.InventoryResponseVO;
import com.bbb.commerce.inventory.vo.SBAuthTokenResponseVO;
import com.bbb.commerce.inventory.vo.SBInventoryRequestVO;
import com.bbb.commerce.inventory.vo.SBInventoryResponseVO;
import com.bbb.commerce.inventory.vo.SupplyBalanceAvlCriteria;
import com.bbb.commerce.inventory.vo.SupplyBalanceAvlDetailsVO;
import com.bbb.commerce.inventory.vo.SupplyBalanceErrorMessageVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.utils.BBBUtility;

import atg.core.util.StringUtils;
import atg.json.JSONArray;
import atg.json.JSONException;
import atg.json.JSONObject;
import atg.repository.MutableRepository;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * The Class BopusInventoryService.
 *
 * @author skoner
 */
public class BopusInventoryService extends BBBGenericService {
	
	/** The catalog tools. */
	protected BBBCatalogTools catalogTools;
	
	/** The Config tools. */
	protected BBBConfigTools configTools;
	
	public BBBConfigTools getConfigTools() {
		return configTools;
	}

	public void setConfigTools(BBBConfigTools configTools) {
		this.configTools = configTools;
	}


	/** The local store repository. */
	private MutableRepository localStoreRepository;
	
	/** The http call invoker. */
	private HTTPCallInvoker httpCallInvoker;
	
	private long defaultBufferExpireTime;
	
	/**
	 * Gets the http call invoker.
	 *
	 * @return the httpCallInvoker
	 */
	public HTTPCallInvoker getHttpCallInvoker() {
		return httpCallInvoker;
	}

	/**
	 * Sets the http call invoker.
	 *
	 * @param httpCallInvoker the httpCallInvoker to set
	 */
	public void setHttpCallInvoker(HTTPCallInvoker httpCallInvoker) {
		this.httpCallInvoker = httpCallInvoker;
	}

	/**
	 * Sets the catalog tools.
	 *
	 * @param catalogTools the new catalog tools
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	
	/**
	 * Gets the local store repository.
	 *
	 * @return the local store repository
	 */
	public MutableRepository getLocalStoreRepository() {
		return localStoreRepository;
	}

	/**
	 * Sets the local store repository.
	 *
	 * @param localStoreRepository the new local store repository
	 */
	public void setLocalStoreRepository(MutableRepository localStoreRepository) {
		this.localStoreRepository = localStoreRepository;
	}


	/** The Constant inputContext. */
	static final JAXBContext inputContext = initInputContext();
	
	/** The Constant outputContext. */
	static final JAXBContext outputContext = initOutputContext();
	
	/**
	 * Inits the input context.
	 *
	 * @return the JAXB context
	 */
	private static JAXBContext initInputContext() {
		try {
			return JAXBContext.newInstance("com.bbb.bopus.inventory.input");
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
    }
	
	/**
	 * Inits the output context.
	 *
	 * @return the JAXB context
	 */
	private static JAXBContext initOutputContext() {
		try {
			return JAXBContext.newInstance("com.bbb.bopus.inventory.output");
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
    }
	
	/**
	 * Instantiates a new bopus inventory service.
	 */
	public BopusInventoryService() {
		super();
	}

	/** The Constant OBJECT_FACTORY. */
	protected static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

	/**
	 * This method is used to the get the INVENTORY for a bopus item and returns
	 * the inventory level by calculating TotalOnHand-TotalAllocatedQtyOnhand
	 * that are obtained from the web service call.
	 *
	 * @param skuId the sku id
	 * @param storeIds the store ids
	 * @param isFromLocalRepository the is from local repository
	 * @return the inventory for bopus item
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public Map<String, Integer> getInventoryForBopusItem(final String skuId,
			final List<String> storeIds, boolean isFromLocalRepository)
			throws BBBBusinessException, BBBSystemException {
		List<String> skuIdList = new ArrayList<String>();
		skuIdList.add(skuId);
		if (isFromLocalRepository)
			return getLocalInventory(skuIdList, storeIds);
		else
			return getInventory(skuIdList, storeIds, false);

	}

	/**
	 * This method is used to the get the INVENTORY for a bopus item and returns
	 * the inventory level by calculating TotalOnHand-TotalAllocatedQtyOnhand
	 * that are obtained from the web service call.
	 *
	 * @param skuIdList the sku id list
	 * @param storeId the store id
	 * @return inventory stock status
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
    public Map<String, Integer> getMultiSkusStoreInventory (final List<String> skuIdList,
			final String storeId) throws BBBBusinessException, BBBSystemException {
		List<String> storeIds = new ArrayList<String>();
		storeIds.add(storeId);
		return getInventory(skuIdList, storeIds, true);
	}
	
	/**
	 * This method is used to the get the INVENTORY for a bopus item and returns
	 * the inventory level by calculating TotalOnHand-TotalAllocatedQtyOnhand
	 * that are obtained from the web service call.
	 *
	 * @param skuIdList the sku id list
	 * @param storeIds the store ids
	 * @param favStoreFlow the fav store flow
	 * @return the inventory
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public Map<String, Integer> getInventory (final List<String> skuIdList,
			final List<String> origStoreIds, final boolean favStoreFlow) 
					throws BBBBusinessException, BBBSystemException {
		List<String> storeIds = new ArrayList<String>();
        storeIds.addAll(origStoreIds);
		logDebug("BopusInventoryService.getInventory method starts for sku Ids: " + skuIdList + " and store Ids: " + storeIds);
		//getting original store id for dummy stores
		Map<String, String> mStoreIds = getOrignalStoreId(storeIds);
		//setting map in session to be used to set 'Call Store For Availability' as inventory status in later logic.
		ServletUtil.getCurrentRequest().setParameter(SelfServiceConstants.DUMMY_STORE_MAP, mStoreIds);
		boolean errorFlag= false;
		Boolean enableEOMFlag = getCatalogTools().getValueForConfigKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.EOM_ENABLE_FLAG,false);
		final Map<String, Integer> inventoryDetails = new ConcurrentHashMap<String,Integer>();
		
		if(mStoreIds != null)
		{
			for (Map.Entry<String,String> entry : mStoreIds.entrySet()) {
				storeIds.remove(entry.getKey());
				storeIds.add(entry.getValue());
			}
		}
		
        if (!enableEOMFlag) {
        	    ServiceRequestIF sreqVo;
		    	com.bbb.bopus.inventory.output.TXML txml;
		    	sreqVo = prepareRequestObjectForSupplyBalance(skuIdList, storeIds);
		    	if(!BBBUtility.isListEmpty(storeIds)){
					txml = invokeService(sreqVo);
			    	}
			    	else
			    	{ 
			    		txml = null; 
			    	}
				if(txml != null)
				{
					List<ViewByFacility> viewFacilityList = txml.getMessage().getSupplyBalanceOutput().getViewByFacility();
					if (viewFacilityList != null && viewFacilityList.size() > 0) {
						final ViewByFacility viewByFacility = viewFacilityList.get(0);
						if(null != viewByFacility.getLineItem() && viewByFacility.getLineItem().size() > 0){
		
							for (int i = 0; i < viewByFacility.getLineItem().size(); i++) {
		
								final com.bbb.bopus.inventory.output.LineItem litem = viewByFacility.getLineItem().get(i);
		
								if(litem.getErrorList()==null 
										&& BBBUtility.isInteger(litem.getTotalOnHand()) 
										&& BBBUtility.isInteger(litem.getTotalAllocatedQtyOnhand())) {
									if (favStoreFlow)
										setInventoryBySku(inventoryDetails, litem);
									else
										setInventoryByStores(inventoryDetails, litem);
								}
								else if (litem.getErrorList()!=null)
								{
									errorFlag = true;
									for(com.bbb.bopus.inventory.output.Error err: litem.getErrorList().getError())
									{
										logError("\nError while calling Bopus Inventory Service. Error Code:" + err.getErrorCode() + 
												", Error Description:" + err.getErrorDescription());
									}
								}
							}
						}
					}
				}
				else
				{
					DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
					if(request != null)
					{
						request.setParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM, BBBCoreConstants.TRUE);
					}
				}
				if(errorFlag){
					logError("\n BopusInventoryService.getInventoryForBopusItem:: Request sent to inventory web service is :" + sreqVo
							+"\n BopusInventoryService.getInventoryForBopusItem:: Response returned from inventory web service is :" + txml);
		
				}
        } else {
        	//Modified the existing soap based network inventory call to rest
		int batchSize = getConfigTools().getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.EOM_BATCH_SIZE,BBBCoreConstants.FIFTY);
		final List<List<String>> skuIdLists = BBBUtility.getBatchList(skuIdList, batchSize);
		final List<List<String>> storeIdLists = BBBUtility.getBatchList(storeIds, batchSize);
		if (skuIdList != null && skuIdList.size() > batchSize) {
			if (storeIds != null && storeIds.size() > batchSize) {
				for (final List<String> skuList : skuIdLists) {
					for (final List<String> storeList : storeIdLists) {
						getBatchInventoryDetails(inventoryDetails, skuList, storeList, favStoreFlow);
					}
				}
			} else {
				for (final List<String> skuList : skuIdLists) {
					getBatchInventoryDetails(inventoryDetails, skuList, storeIds, favStoreFlow);
				}
			}
		} else if (storeIds != null && storeIds.size() > batchSize) {
			for (final List<String> storeList : storeIdLists) {
				getBatchInventoryDetails(inventoryDetails, skuIdList, storeList, favStoreFlow);
			}
		} else {
			getBatchInventoryDetails(inventoryDetails, skuIdList, storeIds, favStoreFlow);
		}
        
        }
		logDebug("getInventoryForBopusItem medhod exit by returning inventories " + inventoryDetails);
		//reseting back to dummy id for display purpose
		resetDummyStoreIdList(storeIds, mStoreIds, inventoryDetails);
		return inventoryDetails;
	}

	/**
	 * This method used to fetch inventory in batch of 50. Because EOM
	 * has limitation on number of store ids i.e.50 in JSON request.
	 * 
	 * @param inventoryDetails
	 * @param skuIdList
	 * @param storeIds
	 * @param favStoreFlow
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private void getBatchInventoryDetails(Map<String, Integer> inventoryDetails, List<String> skuIdList, List<String> storeIds, boolean favStoreFlow) throws BBBSystemException, BBBBusinessException {
		logDebug("BopusInventoryService.getBatchInventoryDetails start with skus: " + skuIdList + " , stores: " + storeIds + " and fav store: " + favStoreFlow);
		SBInventoryResponseVO inventoryResVO = getSBCResponse(skuIdList, storeIds, false);
		if (inventoryResVO != null && inventoryResVO.getAvailability() != null) {
			List<SupplyBalanceErrorMessageVO> sbem = null;
			if(inventoryResVO.getAvailability().getMessages() != null) {
				sbem = inventoryResVO.getAvailability().getMessages().get(BBBCoreConstants.SB_ERROR_MESSAGE);
				if (sbem != null) {
					for(final SupplyBalanceErrorMessageVO error : sbem)
					{
						if (BBBCoreConstants.SEVERITY_WARNING.equalsIgnoreCase(error.getSeverity())) {
							logWarning("\nWarning while calling Bopus Inventory Service. Error Code:" + error.getCode() + ", Error Description:" + error.getDescription());
						} else {
							logError("\nError while calling Bopus Inventory Service. Error Code:" + error.getCode() + ", Error Description:" + error.getDescription());
						}
					}
				}
			}
			if (sbem == null && inventoryResVO.getAvailability().getAvailabilityDetails() != null) {
				int avlDetails = inventoryResVO.getAvailability().getAvailabilityDetails().get(BBBCoreConstants.SB_AVL_DETAIL).size();	
				for (int i = 0; i < avlDetails; i++) {
					final SupplyBalanceAvlDetailsVO sbac = inventoryResVO.getAvailability().getAvailabilityDetails().get(BBBCoreConstants.SB_AVL_DETAIL).get(i);
					if (sbac != null && sbac.getFacilityName() != null && sbac.getItemName() != null && BBBUtility.isInteger(sbac.getAtcQuantity())) {
						if (favStoreFlow) {
							setInventoryBySku(inventoryDetails, sbac);
						} else {
							setInventoryByStores(inventoryDetails, sbac);
						}
					}
				}
			}
		} else {
			DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
			if(request != null) {
				request.setParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM, BBBCoreConstants.TRUE);
			}
		}
		logDebug("BopusInventoryService.getBatchInventoryDetails ends.");
	}

	/**
	 * Gets the inventory for multi store and sku.
	 *
	 * @param skuIdList the sku id list
	 * @param storeIds the store ids
	 * @param getInvBysku the get inv bysku
	 * @return the inventory for multi store and sku
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public Map<String, Map<String, Integer>> getInventoryForMultiStoreAndSku (final List<String> skuIdList,
			final List<String> storeIds, boolean getInvBysku, boolean isSDDCommitOrderFlow) 
					throws BBBBusinessException, BBBSystemException {
		logDebug("BopusInventoryService.getInventoryForMultiStoreAndSku method starts for sku Ids: " + skuIdList + " and store Ids: " + storeIds);
		Map<String, Map<String, Integer>> inventoryMap = new ConcurrentHashMap<String, Map<String, Integer>>();
		Map<String, String> mStoreIds = getOriginalStoreIdList(storeIds);
		final int batchSize = getConfigTools().getValueForConfigKey(BBBCoreConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.EOM_BATCH_SIZE,BBBCoreConstants.FIFTY);
		final List<List<String>> skuIdLists = BBBUtility.getBatchList(skuIdList, batchSize);
		final List<List<String>> storeIdLists = BBBUtility.getBatchList(storeIds, batchSize);
		if (skuIdList != null && skuIdList.size() > batchSize) {
			if (storeIds != null && storeIds.size() > batchSize) {
				for (final List<String> skuList : skuIdLists) {
					for (final List<String> storeList : storeIdLists) {
						getMultiBatchInventoryDetails(inventoryMap, skuList, storeList, getInvBysku, isSDDCommitOrderFlow);
					}
				}
			} else {
				for (final List<String> skuList : skuIdLists) {
					getMultiBatchInventoryDetails(inventoryMap, skuList, storeIds, getInvBysku, isSDDCommitOrderFlow);
				}
			}
		} else if (storeIds != null && storeIds.size() > batchSize) {
			for (final List<String> storeList : storeIdLists) {
				getMultiBatchInventoryDetails(inventoryMap, skuIdList, storeList, getInvBysku, isSDDCommitOrderFlow);
			}
		} else {
			getMultiBatchInventoryDetails(inventoryMap, skuIdList, storeIds, getInvBysku, isSDDCommitOrderFlow);
		}

		logDebug("getInventoryForMultiStoreAndSku ends by returning inventoryMap " + inventoryMap);
		resetDummyStoreIdListInMultiStore(storeIds, mStoreIds, inventoryMap);
		return inventoryMap;
	}

	/**
	 * This method used to fetch inventory in batch of 50. Because EOM has
	 * limitation on number of store ids i.e.50 in JSON request.
	 * 
	 * @param inventoryMap
	 * @param skuIdList
	 * @param storeIds
	 * @param getInvBysku
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private void getMultiBatchInventoryDetails(Map<String, Map<String, Integer>> inventoryMap,
			List<String> skuIdList, List<String> storeIds, boolean getInvBysku, boolean isSDDCommitOrderFlow) throws BBBSystemException, BBBBusinessException {
		SBInventoryResponseVO inventoryResVO = getSBCResponse(skuIdList, storeIds, isSDDCommitOrderFlow);
		if (inventoryResVO != null && inventoryResVO.getAvailability() != null) {
			List<SupplyBalanceErrorMessageVO> sbem = null;
			if(inventoryResVO.getAvailability().getMessages() != null) {
				sbem = inventoryResVO.getAvailability().getMessages().get(BBBCoreConstants.SB_ERROR_MESSAGE);
				if (sbem != null) {
					for(final SupplyBalanceErrorMessageVO error : sbem)
					{
						if (BBBCoreConstants.SEVERITY_WARNING.equalsIgnoreCase(error.getSeverity())) {
							logWarning("\nWarning while calling Bopus Inventory Service. Error Code:" + error.getCode() + ", Error Description:" + error.getDescription());
						} else {
							logError("\nError while calling Bopus Inventory Service. Error Code:" + error.getCode() + ", Error Description:" + error.getDescription());
						}
					}
				}
			}
			if (sbem == null && inventoryResVO.getAvailability().getAvailabilityDetails() != null) {
				int avlDetails = inventoryResVO.getAvailability().getAvailabilityDetails().get(BBBCoreConstants.SB_AVL_DETAIL).size();	
				for (int i = 0; i < avlDetails; i++) {
					final SupplyBalanceAvlDetailsVO sbac = inventoryResVO.getAvailability().getAvailabilityDetails().get(BBBCoreConstants.SB_AVL_DETAIL).get(i);
					if (sbac != null && BBBUtility.isInteger(sbac.getAtcQuantity())) {
						inventoryMap = populateInventoryAvailabilityDetails(inventoryMap, sbac, getInvBysku);
					}
				}
			}
		} else {
			DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
			if(request != null) {
				request.setParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM, BBBCoreConstants.TRUE);
			}
		}
	}

	/**
	 * Gets the SBC response.
	 *
	 * @param skuIdList the sku id list
	 * @param storeIds the store ids
	 * @return the SBC response
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	private SBInventoryResponseVO getSBCResponse(final List<String> skuIdList,
			final List<String> storeIds, boolean isSDDCommitOrderFlow) throws BBBSystemException,
			BBBBusinessException {
		// BBBH-1492
		SBInventoryRequestVO inventoryReqVO = populateInventoryRequestVO(skuIdList, storeIds);
		String inputReqJson = null;
		String outputResJson = null;
		SBInventoryResponseVO inventoryResVO = null;
		try {
			final String httpURL = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.SUPPLY_BALANCE_HTTP_URL).get(0);
			StringBuilder sbcHttpUrl = new StringBuilder(httpURL);
			final String userName = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.SBC_USERNAME).get(0);
			sbcHttpUrl.append(BBBCoreConstants.QUESTION_MARK);
			sbcHttpUrl.append(BBBCoreConstants.J_USERNAME).append(BBBCoreConstants.EQUAL).append(userName);
			inputReqJson = this.getHttpCallInvoker().parseJSONRequest(inventoryReqVO);
			outputResJson = getHttpCallInvoker().callPostRestService(sbcHttpUrl.toString(), inputReqJson, getHeader(isSDDCommitOrderFlow), isSDDCommitOrderFlow);

			JSONObject jsonObj = new JSONObject(outputResJson);
			boolean isError = jsonObj.getJSONObject(BBBCoreConstants.SB_AVAILABILITY).has(BBBCoreConstants.SB_ERROR_MESSAGES);
			boolean isAvailable = jsonObj.getJSONObject(BBBCoreConstants.SB_AVAILABILITY).has(BBBCoreConstants.SB_AVL_DETAILS);
			if (isError) {
				final JSONObject msgObj = jsonObj.getJSONObject(BBBCoreConstants.SB_AVAILABILITY).getJSONObject(BBBCoreConstants.SB_ERROR_MESSAGES);
				final Object errorMsgObj = msgObj != null ? msgObj.get(BBBCoreConstants.SB_ERROR_MESSAGE) : null;
				if (errorMsgObj != null && errorMsgObj instanceof JSONArray) {
					logDebug("BopusInventoryService.getSBCResponse - Recieved Error Message objects as a JSONArray.");
					inventoryResVO = getHttpCallInvoker().parseJSONResponse(SBInventoryResponseVO.class, outputResJson);
				} else if (errorMsgObj != null) {
					inventoryResVO = getSBCErrMsgResponse(errorMsgObj);
				} 
			} else if (isAvailable) {
				final JSONObject availabilityObj = jsonObj.getJSONObject(BBBCoreConstants.SB_AVAILABILITY).getJSONObject(BBBCoreConstants.SB_AVL_DETAILS);
				final Object availabilityDetailObj = availabilityObj != null ? availabilityObj.get(BBBCoreConstants.SB_AVL_DETAIL) : null;
				if (availabilityDetailObj != null && availabilityDetailObj instanceof JSONArray) {
					logDebug("BopusInventoryService.getSBCResponse - Recieved Availability Details objects as a JSONArray.");
					inventoryResVO = getHttpCallInvoker().parseJSONResponse(SBInventoryResponseVO.class, outputResJson);
				} else if (availabilityDetailObj != null) {
					inventoryResVO = getSBCAvailabilityDetails(availabilityDetailObj, jsonObj);
				}
			}
		} catch (IOException e) {
			String errorMsg = "IOException Occurred while parsing the Object to JSON.";
			logError(errorMsg, e);
			throw new BBBSystemException(errorMsg, e);
		} catch (JSONException js) {
			String errorMsg = "BopusInventoryService.getSBCResponse - JSONException Occurred - Invalid JSON Response received from EOM service.";
			logError(errorMsg, js);
			js.printStackTrace();
		} catch (BBBSystemException be) {
			String errorMsg = "BBBSystemException Occurred while calling HttpCallInvoker.";
			logError(errorMsg, be);
			be.printStackTrace();
			throw new BBBSystemException(errorMsg, be);
		} 
		logDebug("\n BopusInventoryService.getSBCResponse:: JSON Request sent to inventory web service is : " + inputReqJson
				+"\n BopusInventoryService.getSBCResponse:: JSON Response returned from inventory web service is: " + outputResJson);
		// BBBH-1492
		return inventoryResVO;
	}

	/**
	 * This method used to sanitize the SupplyBalanceAvlDetailsVO when response
	 * contains single object only, not the JSONArray.
	 * 
	 * @param availabilityDetailObj
	 * @param jsonObj 
	 * @return
	 * @throws BBBSystemException
	 * @throws IOException
	 * @throws JSONException
	 */
	private SBInventoryResponseVO getSBCAvailabilityDetails(Object availabilityDetailObj, JSONObject jsonObj)
			throws BBBSystemException, IOException, JSONException {
		logDebug("BopusInventoryService.getSBCAvlDetailsResponse - Recieved Single Availability Details object only, Not JSONArray.");
		SBInventoryResponseVO inventoryResVO = new SBInventoryResponseVO();
		InventoryResponseVO inventoryResponseVO = new InventoryResponseVO();
		List<SupplyBalanceAvlDetailsVO> availabilityDetailsVOList = new ArrayList<SupplyBalanceAvlDetailsVO>();
		Map<String, List<SupplyBalanceAvlDetailsVO>> availabilityDetailVOMap = new HashMap<String, List<SupplyBalanceAvlDetailsVO>>();

		final SupplyBalanceAvlDetailsVO availabilityDetailVO = getHttpCallInvoker().parseJSONResponse(SupplyBalanceAvlDetailsVO.class, availabilityDetailObj.toString());

		availabilityDetailsVOList.add(availabilityDetailVO);
		availabilityDetailVOMap.put(BBBCoreConstants.SB_AVL_DETAIL, availabilityDetailsVOList);
		inventoryResponseVO.setAvailabilityDetails(availabilityDetailVOMap);
		inventoryResponseVO.setTransactionDateTime((String) jsonObj.getJSONObject(BBBCoreConstants.SB_AVAILABILITY).get(BBBCoreConstants.SB_TRANSACTION_DATETIME));
		inventoryResponseVO.setViewConfiguration((String) jsonObj.getJSONObject(BBBCoreConstants.SB_AVAILABILITY).get(BBBCoreConstants.SB_VIEW_CONFIG));
		inventoryResponseVO.setViewName((String) jsonObj.getJSONObject(BBBCoreConstants.SB_AVAILABILITY).get(BBBCoreConstants.SB_VIEW_NAME_IN_RESPONSE));
		inventoryResVO.setAvailability(inventoryResponseVO);
		logDebug("BopusInventoryService.getSBCAvlDetailsResponse ends.");
		return inventoryResVO;
	}

	/**
	 * This method used to sanitize the SupplyBalanceErrorMessageVO when
	 * response contains single object only, not the JSONArray
	 * 
	 * @param msgObj
	 * @param errorMsgObj
	 * @return
	 * @throws BBBSystemException
	 * @throws IOException
	 */
	private SBInventoryResponseVO getSBCErrMsgResponse(Object errorMsgObj) throws BBBSystemException, IOException {
		logDebug("BopusInventoryService.getSBCErrMsgResponse - Recieved Single Error Message object only, Not JSONArray.");
		SBInventoryResponseVO inventoryResVO = new SBInventoryResponseVO();
		InventoryResponseVO inevtoryResponseVO = new InventoryResponseVO();
		List<SupplyBalanceErrorMessageVO> errMsgVOList = new ArrayList<SupplyBalanceErrorMessageVO>();
		Map<String, List<SupplyBalanceErrorMessageVO>> messages = new HashMap<String, List<SupplyBalanceErrorMessageVO>>();

		final SupplyBalanceErrorMessageVO errorMsgVO = getHttpCallInvoker().parseJSONResponse(SupplyBalanceErrorMessageVO.class, errorMsgObj.toString());

		errMsgVOList.add(errorMsgVO);
		messages.put(BBBCoreConstants.SB_ERROR_MESSAGE, errMsgVOList);
		inevtoryResponseVO.setMessages(messages);
		inventoryResVO.setAvailability(inevtoryResponseVO);
		logDebug("BopusInventoryService.getSBCErrMsgResponse ends.");
		return inventoryResVO;
	}

	/**
	 * This method creates header for SBC.
	 * 
	 * @param isSDDCommitOrderFlow
	 * 
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws IOException
	 */
	protected Map<String, String> getHeader(boolean isSDDCommitOrderFlow) throws BBBSystemException,
			BBBBusinessException, IOException {
		logDebug("BopusInventoryService.getHeader: Starts.");
		Map<String, String> headerParam = new HashMap<String, String>();
		String authTokenHeader = null;
		BBBSessionBean sessionBean = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(
				BBBCoreConstants.SESSION_BEAN);
		if (StringUtils.isNotEmpty(sessionBean.getSbcOauthToken()) && sessionBean.getSbcTokenExpireTime() > 0) {
			if (sessionBean.getSbcTokenExpireTime() > new Date().getTime()) {
				logDebug("BopusInventoryService.getHeader: Token is not expired, calling SBC with previous token.");
				authTokenHeader = sessionBean.getSbcOauthToken();
			} else {
				logDebug("BopusInventoryService.getHeader: Token is Expired, calling service for new token.");
				authTokenHeader = assignOauthTokenIntoSession(sessionBean, isSDDCommitOrderFlow);
			}
		} else {
			logDebug("BopusInventoryService.getHeader: Token does not exist, calling service for new token.");
			authTokenHeader = assignOauthTokenIntoSession(sessionBean, isSDDCommitOrderFlow);
		}
		headerParam.put(HttpHeaders.AUTHORIZATION, authTokenHeader);
		headerParam.put(BBBCoreConstants.HTTP_INVKR_CNT_TYPE, BBBCoreConstants.HEADERS_APP_JSON);
		logDebug("BopusInventoryService.getHeader() parameters count= " + headerParam.size());
		for (Map.Entry<String, String> entry : headerParam.entrySet()) {
			logDebug(entry.getKey() + " = " + entry.getValue());
		}
		logDebug("BopusInventoryService.getHeader: Ends.");
		return headerParam;
	}

	/**
	 * This method gets token form service and assign them to session.
	 * 
	 * @param sessionBean
	 * @param isSDDCommitOrderFlow 
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws IOException
	 */
	private String assignOauthTokenIntoSession(BBBSessionBean sessionBean, boolean isSDDCommitOrderFlow) throws BBBSystemException,
			BBBBusinessException, IOException {
		logDebug("BopusInventoryService.assignOauthTokenIntoSession: Starts.");
		final String bufferExpireTime = this.getCatalogTools()
				.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.OAUTH_TOKEN_BUFFER_EXPIRE_TIME)
				.get(0);
		long bufferTime = getDefaultBufferExpireTime();
		if (null != bufferExpireTime && org.apache.commons.lang.StringUtils.isNumeric(bufferExpireTime.trim())) {
			bufferTime = Integer.parseInt(bufferExpireTime.trim());
		}
		SBAuthTokenResponseVO oauthTokRes = getOauthToken(isSDDCommitOrderFlow);
		String authTokenHeader = oauthTokRes.getToken_type() + BBBCoreConstants.WHITE_SPACE
				+ oauthTokRes.getAccess_token();
		sessionBean.setSbcOauthToken(authTokenHeader);
		sessionBean.setSbcTokenExpireTime(new Date().getTime() + (oauthTokRes.getExpires_in() - bufferTime));
		logDebug("BopusInventoryService.assignOauthTokenIntoSession: Ends.");
		return authTokenHeader;
	}

	/**
	 * This method is for getting Oauth token for SBC.
	 * 
	 * @param isSDDCommitOrderFlow
	 * 
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws IOException
	 */
	private SBAuthTokenResponseVO getOauthToken(boolean isSDDCommitOrderFlow) throws BBBSystemException,
			BBBBusinessException, IOException {
		logDebug("BopusInventoryService.getOauthToken: Starts.");
		long startTime = new Date().getTime();
		final String oauthTokenUrl = this.getCatalogTools()
				.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.OAUTH_TOKEN_HTTP_URL).get(0);
		final String grantType = this.getCatalogTools()
				.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.OAUTH_TOKEN_GRANT_TYPE).get(0);
		final String userName = this.getCatalogTools()
				.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.SBC_USERNAME).get(0);
		final String password = this.getCatalogTools()
				.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.SBC_PASSWORD).get(0);
		final String clientId = this.getCatalogTools()
				.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.OAUTH_TOKEN_CLIENT_ID).get(0);
		final String clientSecret = this.getCatalogTools()
				.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.OAUTH_TOKEN_CLIENT_SECRET)
				.get(0);
		final String scope = this.getCatalogTools()
				.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.OAUTH_TOKEN_SCOPE).get(0);
		List<NameValuePair> queryParam = new ArrayList<NameValuePair>();
		queryParam.add(new BasicNameValuePair(BBBCoreConstants.GRANT_TYPE, grantType));
		queryParam.add(new BasicNameValuePair(BBBCoreConstants.USERNAME, userName));
		queryParam.add(new BasicNameValuePair(BBBCoreConstants.PASSWORD, password));
		queryParam.add(new BasicNameValuePair(BBBCoreConstants.REQ_PARAM_CLIENT_ID, clientId));
		queryParam.add(new BasicNameValuePair(BBBCoreConstants.CLIENT_SECRET, clientSecret));
		queryParam.add(new BasicNameValuePair(BBBCoreConstants.SCOPE, scope));
		logDebug("BopusInventoryService.getOauthToken: Calling service, URL : " + oauthTokenUrl);
		final SBAuthTokenResponseVO oauthTokRes = getHttpCallInvoker().doHttpGetWithParam(SBAuthTokenResponseVO.class,
				new HashMap<String, String>(), oauthTokenUrl, queryParam, isSDDCommitOrderFlow);
		long endTime = new Date().getTime();
		vlogInfo(
				"BopusInventoryService.getOauthToken: Total time taken to call Oauth token service is : {0} milliseconds",
				endTime - startTime);
		logDebug("BopusInventoryService.getOauthToken: Ends with response : " + oauthTokRes);
		return oauthTokRes;
	}

	/**
	 * Reset dummy store id list.
	 *
	 * @param storeIds the store ids
	 * @param mStoreIds the m store ids
	 * @param inventoryDetails the inventory details
	 */
	private void resetDummyStoreIdList(final List<String> storeIds, final Map<String, String> mStoreIds,
			final Map<String, Integer> inventoryDetails) {
		if (inventoryDetails != null && mStoreIds != null) {
			for (final Map.Entry<String, String> storeEntry : mStoreIds.entrySet()) {
			    final Integer stockLevel = inventoryDetails.get(storeEntry.getValue());
				if (null != stockLevel) {
					inventoryDetails.put(storeEntry.getKey(), stockLevel);
					inventoryDetails.remove(storeEntry.getValue());
				}
				storeIds.remove(storeEntry.getValue());
				storeIds.add(storeEntry.getKey());
			}
		}
	}

	/**
	 * Reset dummy store id list in multi store.
	 *
	 * @param storeIds the store ids
	 * @param mStoreIds the m store ids
	 * @param inventoryDetails the inventory details
	 */
	private void resetDummyStoreIdListInMultiStore(final List<String> storeIds,
			Map<String, String> mStoreIds,
			final Map<String, Map<String, Integer>> inventoryDetails) {
		if(inventoryDetails != null && mStoreIds != null)
		{
			for (Map.Entry<String,Map<String,Integer>> bopusEntry : inventoryDetails.entrySet()) {
				for (Map.Entry<String,String> storeEntry : mStoreIds.entrySet()) {
					logDebug(("Bopus Key = " + bopusEntry.getKey() + ", Store Value = " + storeEntry.getValue()));
					if(bopusEntry.getKey().equals(storeEntry.getValue()))
					{
						inventoryDetails.remove(bopusEntry.getKey());
						inventoryDetails.put(storeEntry.getKey(), bopusEntry.getValue());
						storeIds.remove(bopusEntry.getKey());
						storeIds.add(storeEntry.getKey());
					}
				}
			}
		}
	}
	
	/**
	 * Gets the original store id list.
	 *
	 * @param storeIds the store ids
	 * @return the original store id list
	 */
	private Map<String, String> getOriginalStoreIdList(
			final List<String> storeIds) {
		Map<String,String> mStoreIds = getOrignalStoreId(storeIds);
		if(mStoreIds != null)
		{
			for (Map.Entry<String,String> entry : mStoreIds.entrySet()) {
				storeIds.remove(entry.getKey());
				storeIds.add(entry.getValue());
			}
		}
		return mStoreIds;
	}
	
	

	/**
	 * Populate inventory availability details.
	 * @param inventoryMap 
	 *
	 * @param avlDetailsVO the avl details vo
	 * @param getInvBysku the get inv bysku
	 * @return the map
	 */
	public Map<String, Map<String, Integer>> populateInventoryAvailabilityDetails(Map<String, Map<String, Integer>> inventoryMap, final SupplyBalanceAvlDetailsVO avlDetailsVO, boolean getInvBysku) {
		logDebug("BopusInventoryService.populateInventoryAvailabilityDetails() - start with params avlDetailsVO" + avlDetailsVO);
		int quantity = 0;
		if (!BBBUtility.isEmpty(avlDetailsVO.getAtcQuantity())) {
			quantity = Integer.parseInt(avlDetailsVO.getAtcQuantity());
		}
		if(getInvBysku){
			// setting inventory by sku
			if(inventoryMap.containsKey(avlDetailsVO.getItemName())) {
				inventoryMap.get(avlDetailsVO.getItemName()).put(avlDetailsVO.getFacilityName(), quantity);
			} else {
				Map<String, Integer> initMap = new HashMap<String, Integer>();
				initMap.put(avlDetailsVO.getFacilityName(), quantity);
				inventoryMap.put(avlDetailsVO.getItemName(), initMap);
			}
		} else{
			// setting inventory by store
			if(inventoryMap.containsKey(avlDetailsVO.getFacilityName())) {
				inventoryMap.get(avlDetailsVO.getFacilityName()).put(avlDetailsVO.getItemName(), quantity);
			} else {
				Map<String, Integer> initMap = new HashMap<String, Integer>();
				initMap.put(avlDetailsVO.getItemName(), quantity);
				inventoryMap.put(avlDetailsVO.getFacilityName(), initMap);
			}
		}
		
		logDebug("BopusInventoryService.populateInventoryAvailabilityDetails() - SKU: " + avlDetailsVO.getItemName() + " , "
				+ "storeId: " + avlDetailsVO.getFacilityName() + " and quantity: " + quantity);
		
		return inventoryMap;
		}

     
	/**
	 * This method is used to populate the inventory request VO by the provided
	 * the SKU IDs and Store IDs.
	 *
	 * @param skuIdList the sku id list
	 * @param storeIds the store ids
	 * @return the SB inventory request vo
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	private SBInventoryRequestVO populateInventoryRequestVO(List<String> skuIdList, List<String> storeIds) throws BBBSystemException, BBBBusinessException {
		logDebug("BopusInventoryService.populateInventoryRequestVO started...SKUs: " + skuIdList + " and Store IDs: " + storeIds);
		SBInventoryRequestVO sbInventoryRequestVO = new SBInventoryRequestVO();
		InventoryRequestVO inventoryReqVO = new InventoryRequestVO();
		SupplyBalanceAvlCriteria sbac = new SupplyBalanceAvlCriteria();
		// Created key in BCC for view Name : FACILITY_VIEW_NAME[BBAB ROPIS] in THIRD_PARTY_URL
		final String viewName = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL,BBBCoreConstants.SB_VIEW_NAME).get(0);
		inventoryReqVO.setViewName(viewName);
		
		Map<String, List<String>> itemNames = new HashMap<String, List<String>>();
		itemNames.put(BBBCoreConstants.SB_ITEM_NAME, skuIdList);
		sbac.setItemNames(itemNames);
		
		Map<String, List<String>> faclities = new HashMap<String, List<String>>();
		faclities.put(BBBCoreConstants.SB_FACILITY_NAME, storeIds);
		sbac.setFacilityNames(faclities);
		
		inventoryReqVO.setAvailabilityCriteria(sbac);
		sbInventoryRequestVO.setAvailabilityRequest(inventoryReqVO);
		logDebug("BopusInventoryService.populateInventoryRequestVO End.");
		
		return sbInventoryRequestVO;
	}

	/**
	 * This method is used to the get the INVENTORY for a bopus item and returns
	 * the inventory level from local repository.
	 *
	 * @param skuIdList the sku id list
	 * @param storeIds the store ids
	 * @return the local inventory
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	@SuppressWarnings("unused")
	public Map<String, Integer> getLocalInventory(final List<String> skuIdList,
			final List<String> storeIds)
			throws BBBBusinessException, BBBSystemException {
		int inForBopusItem = 0; // NOPMD by skoner on 1/18/12 6:19 PM
		boolean errorFlag = false;
		final Map<String, Integer> inventoryDetails = new HashMap<String, Integer>();
		logDebug("getInventoryForBopusItem called with params " + "skuId-"
				+ skuIdList + "\n" + "storeIds-" + storeIds);
		try {
			RepositoryView localInventoryView = getLocalStoreRepository()
					.getView(BBBCoreConstants.STORE_LOCAL_INVENTORY);
			for (final String skuId : skuIdList) {
				for (final String storeId : storeIds) {

					RqlStatement rqlStatement = RqlStatement
							.parseRqlStatement("storeId = ?0 AND skuId = ?1");

					Object[] params = new Object[2];
					params[0] = storeId;
					params[1] = skuId;
					RepositoryItem[] repositoryItems = rqlStatement
							.executeQuery(localInventoryView, params);
					if (repositoryItems != null) {
						for (RepositoryItem repositoryItem : repositoryItems) {
							inventoryDetails.put(repositoryItem
									.getPropertyValue(BBBCoreConstants.SKUID)
									.toString(), Integer.parseInt(repositoryItem
									.getPropertyValue(
											BBBCoreConstants.STOCK_LEVEL)
									.toString()));
						}
					}
				}
			}
		} catch (Exception e) {
			throw new BBBBusinessException(
					"Exception occurred while getting inventory status from Local Store Repository",
					e);
		}
		logDebug("getLocalInventory medhod exit by returning inventories "
				+ inventoryDetails);
		return inventoryDetails;
	}
	/**
	 * This method sets inventory status in map 
	 * with store id as its key.
	 * @param inventoryByStores
	 * @param litem
	 * @return inventory status
	 */
	private Map<String, Integer> setInventoryByStores(
			final Map<String, Integer> inventoryByStores,
			final com.bbb.bopus.inventory.output.LineItem litem) {
		logDebug("BopusInventoryService.setInventoryByStores() - start "
				+ "with params inventoryByStores: " + inventoryByStores
				+ "litem" + litem);
		if (BBBUtility.isEmpty(litem.getOnHandStatus()) || BBBCoreConstants.STRING_ZERO.equalsIgnoreCase(litem.getOnHandStatus())){
			inventoryByStores.put(litem.getFacility(), Integer.valueOf(Integer.parseInt(BBBCoreConstants.STRING_ZERO)));
		} else {
			inventoryByStores.put(litem.getFacility(), Integer.valueOf(Integer.parseInt(litem.getTotalOnHand()) - 
					Integer.parseInt(litem.getTotalAllocatedQtyOnhand()) - Integer.parseInt(litem.getInventoryWatermark())));
		}
		logDebug("BopusInventoryService.setInventoryByStores() - end "
				+ "with return params inventoryByStores: " + inventoryByStores);
		return inventoryByStores;
	}
	
	/**
	 * This methid sets inventory stauts in map 
	 * with sku id as its key.
	 * @param inventoryBySku
	 * @param litem
	 * @return inventory status
	 */
	private Map<String, Integer> setInventoryBySku(
			final Map<String, Integer> inventoryBySku,
			final com.bbb.bopus.inventory.output.LineItem litem) {
		logDebug("BopusInventoryService.setInventoryBySku() - start "
				+ "with params inventoryBySku: " + inventoryBySku
				+ "litem" + litem);
		if (BBBUtility.isEmpty(litem.getOnHandStatus()) || BBBCoreConstants.STRING_ZERO.equalsIgnoreCase(litem.getOnHandStatus())){
			inventoryBySku.put(litem.getItem(), Integer.valueOf(Integer.parseInt(BBBCoreConstants.STRING_ZERO)));
		} else {
			if(StringUtils.isEmpty(litem.getInventoryWatermark())){
				litem.setInventoryWatermark("0");
			}
			inventoryBySku.put(litem.getItem(), Integer.valueOf(Integer.parseInt(litem.getTotalOnHand()) - 
					Integer.parseInt(litem.getTotalAllocatedQtyOnhand()) - Integer.parseInt(litem.getInventoryWatermark())));
		}
		logDebug("BopusInventoryService.setInventoryBySku() - end "
				+ "with return params inventoryBySku: " + inventoryBySku);
		return inventoryBySku;
	}
	
	/**
	 * BBBH-1492.
	 *
	 * @param inventoryByStores the inventory by stores
	 * @param litem the litem
	 * @return the map
	 */
	private Map<String, Integer> setInventoryByStores(final Map<String, Integer> inventoryByStores, final SupplyBalanceAvlDetailsVO litem) {
		logDebug("BopusInventoryService.setInventoryByStores() - start "
				+ "with params inventoryByStores: " + inventoryByStores
				+ "litem" + litem);
		if (BBBUtility.isEmpty(litem.getAtcQuantity()) || BBBCoreConstants.STRING_ZERO.equalsIgnoreCase(litem.getAtcQuantity())){
			inventoryByStores.put(litem.getFacilityName(), Integer.valueOf(Integer.parseInt(BBBCoreConstants.STRING_ZERO)));
		} else {
			inventoryByStores.put(litem.getFacilityName(), Integer.parseInt(litem.getAtcQuantity()));
		}
		logDebug("BopusInventoryService.setInventoryByStores() - end "
				+ "with return params inventoryByStores: " + inventoryByStores);
		return inventoryByStores;
	}

	/**
	 * BBBH-1492.
	 *
	 * @param inventoryBySku the inventory by sku
	 * @param litem the litem
	 * @return the map
	 */
	private Map<String, Integer> setInventoryBySku(final Map<String, Integer> inventoryBySku, final SupplyBalanceAvlDetailsVO litem) {
		logDebug("BopusInventoryService.setInventoryBySku() - start "
				+ "with params inventoryBySku: " + inventoryBySku
				+ "litem" + litem);
		if (BBBUtility.isEmpty(litem.getAtcQuantity()) || BBBCoreConstants.STRING_ZERO.equalsIgnoreCase(litem.getAtcQuantity())){
			inventoryBySku.put(litem.getItemName(), Integer.valueOf(Integer.parseInt(BBBCoreConstants.STRING_ZERO)));
		} else {
			inventoryBySku.put(litem.getItemName(), Integer.parseInt(litem.getAtcQuantity()));
		}
		logDebug("BopusInventoryService.setInventoryBySku() - end "
				+ "with return params inventoryBySku: " + inventoryBySku);
		return inventoryBySku;
	}
	
	/**
	 * This method prepares the input xml input string and stores it in
	 * SupplyBalanceRequestVO
	 * 
	 * @param skuId
	 * @param storeIds
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException 
	 */
	private ServiceRequestIF prepareRequestObjectForSupplyBalance(
			final List<String> skuIdList, final List<String> storeIds)
			throws BBBBusinessException, BBBSystemException {


		logDebug("prepareRequestObjectForSupplyBalance called with params "
					+ "skuId-" + skuIdList + "\n" + "storeIds-" + storeIds);
		

		final StringWriter stw = new StringWriter();
		final org.xmlsoap.schemas.soap.encoding.String soapEncodedString = org.xmlsoap.schemas.soap.encoding.String.Factory
				.newInstance();
		final SupplyBalanceRequestVO sreqVo = new SupplyBalanceRequestVO();
		

		try {
			

			final TXML txml = OBJECT_FACTORY.createTXML();
			txml.setHeader(createheader());
			txml.setMessage(createMessage(skuIdList, storeIds));
			Marshaller marshaller = inputContext.createMarshaller();
			marshaller.marshal(txml, stw);

		} catch (JAXBException e) {
			if (isLoggingError()) {
				logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1022 + ": " +
						e.getMessage()
								+ "/n"
								+ "Exception in the Method prepareRequestObjectForSupplyBalance of"
								+ this.getClass().getName(), e);
			}

			throw new BBBBusinessException(e.getErrorCode(),e.getMessage(), e);
		}

		soapEncodedString.setStringValue(stw.toString());

		sreqVo.setATPXml(soapEncodedString);

		logDebug("prepareRequestObjectForSupplyBalance medhod exit");

		return sreqVo;

	}



	/**
	 * Invoke service.
	 *
	 * @param sreqVo the sreq vo
	 * @return the com.bbb.bopus.inventory.output. txml
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	protected com.bbb.bopus.inventory.output.TXML invokeService(
			final ServiceRequestIF sreqVo) throws BBBBusinessException,
			BBBSystemException {

		logDebug("invokeService method Start with request "
					+ ((SupplyBalanceRequestVO) sreqVo).getATPXml().toString());
		long start = System.currentTimeMillis();
		final SupplyBalanceResponseVO sResVo = (SupplyBalanceResponseVO) ServiceHandlerUtil
				.invoke(sreqVo);
		long end = System.currentTimeMillis();
		
		logInfo("Total time taken to execute DOM service=" + (end - start));
		
		com.bbb.bopus.inventory.output.TXML outputTxml = null;
		
		try {
			 
			Unmarshaller unmarshaller = outputContext.createUnmarshaller();
			if(sResVo.getResponse() != null)
			{
			outputTxml = (com.bbb.bopus.inventory.output.TXML) unmarshaller
					.unmarshal(new StringReader(sResVo.getResponse()));
			logDebug("invokeService method Start");
			logDebug("invokeService method Ended returning response "
						+ sResVo.getResponse());
			}
			return outputTxml;
		} catch (JAXBException e) {
			logError("BopusInventoyService failed for request:" +((SupplyBalanceRequestVO) sreqVo).getATPXml().toString());
			logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1023 + ": " +
						e.getMessage() + "\n"
						+ "Exception in the method invokeService of"
						+ this.getClass().getName(), e);
			throw new BBBBusinessException(e.getErrorCode(),e.getMessage(), e);
		}

	}
	
	/**
	 * This method is used to prepare the LineElement element as part of Request
	 * 
	 * @param supplyInput
	 * @param skuId
	 * @param storeIds
	 * @return
	 */
	private List<LineItem> createLineItem(final SupplyBalanceInput supplyInput,
			final String skuId, final List<String> storeIds) {
		List<LineItem> listOfLineItems = supplyInput.getLineItem();
		final StringBuffer storeIdString = new StringBuffer();
		if (listOfLineItems == null) {
			listOfLineItems = new ArrayList<LineItem>();
			supplyInput.setLineItem(listOfLineItems);
		}
		final LineItem lineItem = OBJECT_FACTORY.createLineItem();
		for (int i = 0; i < storeIds.size(); i++) {

			storeIdString.append(storeIds.get(i));
			if (i != storeIds.size() - 1) {
				storeIdString.append(",");

			}
		}

		lineItem.setFacility(storeIdString.toString());
		lineItem.setFromDate("");
		lineItem.setFutureSupply("");
		lineItem.setItem(skuId);
		lineItem.setSupplyType("");
		lineItem.setSegment("");

		listOfLineItems.add(lineItem);

		return listOfLineItems;
	}

	/**
	 * This method is used to prepare the SupplyBalanceInput element as part of
	 * Request
	 * 
	 * @param message
	 * @param skuId
	 * @param storeIds
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	@SuppressWarnings("rawtypes")
    private List createSupplyBalanceInput(final Message message,
			final List<String> skuIdList, final List<String> storeIds) throws BBBSystemException, BBBBusinessException {
		List<SupplyBalanceInput> supplyBalanceList;

		if (message.getSupplyBalanceInput() == null) {
			supplyBalanceList = new ArrayList<SupplyBalanceInput>();
		} else {
			supplyBalanceList = message.getSupplyBalanceInput();
		}

		final SupplyBalanceInput supplyInput = OBJECT_FACTORY
				.createSupplyBalanceInput();
		supplyInput.setViewType(this.catalogTools
				.getConfigValueByconfigType(BBBCheckoutConstants.BOPUSCONFIG).get("viewType"));
		for (String skuId : skuIdList){
			supplyInput.setLineItem(createLineItem(supplyInput, skuId, storeIds));	
		}
		supplyBalanceList.add(supplyInput);

		return supplyBalanceList;
	}


	/**
	 * This method is used to prepare the Header element as part of Request.
	 *
	 * @return the header
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	@SuppressWarnings("rawtypes")
	protected Header createheader() throws BBBSystemException, BBBBusinessException {
		final Header header = OBJECT_FACTORY.createHeader();
		Map bopusRequestConfigMap = this.catalogTools
				.getConfigValueByconfigType(BBBCheckoutConstants.BOPUSCONFIG);
		
		header.setSource((String) bopusRequestConfigMap.get("source"));
		header.setActionType((String) bopusRequestConfigMap.get("actionType"));
		header.setSequenceNumber((String) bopusRequestConfigMap
				.get("sequenceNumber"));
		header.setBatchID((String) bopusRequestConfigMap.get("batchID"));
		header.setReferenceID((String) bopusRequestConfigMap.get("referenceID"));
		header.setMessageType((String) bopusRequestConfigMap.get("messageType"));
		header.setCompanyID((String) bopusRequestConfigMap.get("companyID"));
		header.setMsgLocale((String) bopusRequestConfigMap.get("msgLocale"));
		header.setVersion((String) bopusRequestConfigMap.get("version"));
		header.setMsgTimeZone((String) bopusRequestConfigMap.get("msgTimeZone"));
		header.setPassword((String) bopusRequestConfigMap.get("password"));
		header.setUserID((String) bopusRequestConfigMap.get("userID"));

		return header;
	}
	
		/**
	 * This method is used to prepare the Message element as part of Request
	 * 
	 * @param skuId
	 * @param storeIds
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	@SuppressWarnings("unchecked")
    private Message createMessage(final List<String> skuIdList,
			final List<String> storeIds) throws BBBSystemException, BBBBusinessException {
		final Message message = OBJECT_FACTORY.createMessage();
		message.setSupplyBalanceInput(createSupplyBalanceInput(message, skuIdList,
				storeIds));

		return message;
	}


	/**
	 * This method gets the original store id for buy buy baby stores.
	 *
	 * @param storeIds the store ids
	 * @return the orignal store id
	 */
	public Map<String, String> getOrignalStoreId(List<String> storeIds) {
		vlogDebug("BopusInventoryService.getOrignalStoreId: Starts, with store ids : {0}", storeIds);
		Map<String, String> mStoreIds = new ConcurrentHashMap<String, String>();
		try {
			if (!BBBUtility.isListEmpty(storeIds)) {
				MutableRepository storeRepository = ((BBBCatalogToolsImpl) getCatalogTools()).getStoreRepository();
				RepositoryView storeView = storeRepository.getView(SelfServiceConstants.DUMMY_STORES);
				QueryBuilder queryBuilder = storeView.getQueryBuilder();
				QueryExpression thePropertyExpression = queryBuilder
						.createPropertyQueryExpression(SelfServiceConstants.DUMMY_STORE_ID);
				List<Query> allSubQuery = new ArrayList<Query>();
				final int sqlLimit = 1000;
				/*
				 * Splitting includes SQL query as maximum number of expressions
				 * in a list is 1000
				 */
				while (storeIds.size() / sqlLimit > 0) {
					final List<String> subList = storeIds.subList(0, sqlLimit);
					vlogDebug("BopusInventoryService.getOrignalStoreId: processing store ids : {0}", subList);
					QueryExpression tempValueSubQueryExp = queryBuilder
							.createConstantQueryExpression(subList.toArray());
					Query subQuery = queryBuilder.createIncludesQuery(tempValueSubQueryExp, thePropertyExpression);
					allSubQuery.add(subQuery);
					storeIds = storeIds.subList(sqlLimit, sqlLimit + (storeIds.size() - sqlLimit));
				}
				if (storeIds.size() > 0) {
					vlogDebug("BopusInventoryService.getOrignalStoreId: processing remaining store ids : {0}", storeIds);
					QueryExpression tempValueSubQueryExp = queryBuilder.createConstantQueryExpression(storeIds
							.toArray());
					Query subQuery = queryBuilder.createIncludesQuery(tempValueSubQueryExp, thePropertyExpression);
					allSubQuery.add(subQuery);
				}
				Query[] queryArray = new Query[allSubQuery.size()];
				int count = 0;
				for (Query query : allSubQuery) {
					queryArray[count] = query;
					count++;
				}
				Query query = queryBuilder.createOrQuery(queryArray);
				vlogDebug("BopusInventoryService.getOrignalStoreId: Querying store with dummy store id query : {0}",
						query);
				RepositoryItem[] repositoryItems = storeView.executeQuery(query);
				vlogDebug(
						"BopusInventoryService.getOrignalStoreId: Dummy store in repository is: {0} by executing query : {1}",
						repositoryItems, query);
				if (repositoryItems != null) {
					for (RepositoryItem repositoryItem : repositoryItems) {
						if (repositoryItem.getPropertyValue(SelfServiceConstants.ORIGNAL_STORE_ID) != null) {
							mStoreIds.put(repositoryItem.getPropertyValue(SelfServiceConstants.DUMMY_STORE_ID)
									.toString(), repositoryItem.getPropertyValue(SelfServiceConstants.ORIGNAL_STORE_ID)
									.toString());
						}
					}
				}
			}
		} catch (RepositoryException e) {
			logError("Error occurred while getting dummy store id ", e);
		}
		vlogDebug("BopusInventoryService.getOrignalStoreId: Ends, with store ids : {0}", mStoreIds);
		return mStoreIds;
	}


	/**
	 * Gets the catalog tools.
	 *
	 * @return the catalog tools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param defaultBufferExpireTime the defaultBufferExpireTime to set
	 */
	public void setDefaultBufferExpireTime(long defaultBufferExpireTime){
		this.defaultBufferExpireTime = defaultBufferExpireTime;
	}
	
	/**
	 * @return the defaultBufferExpireTime
	 */
	public long getDefaultBufferExpireTime() {
		return defaultBufferExpireTime;
	}

}
