package com.bbb.commerce.inventory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringUtils;
import atg.json.JSONArray;
import atg.json.JSONException;
import atg.json.JSONObject;
import atg.servlet.ServletUtil;

import com.bbb.bopus.inventory.input.LineItem;
import com.bbb.bopus.inventory.input.Message;
import com.bbb.bopus.inventory.input.SupplyBalanceInput;
import com.bbb.bopus.inventory.input.TXML;
import com.bbb.bopus.inventory.vo.SupplyBalanceRequestVO;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.selfservice.vo.NICAvailabilityRequestVO;
import com.bbb.selfservice.vo.NICAvailabilityResponseDetailVO;
import com.bbb.selfservice.vo.NICAvailabiltyResponseVO;
import com.bbb.selfservice.vo.NICResponseErrorMessageVO;
import com.bbb.selfservice.vo.NetworkInventoryRequestVO;
import com.bbb.selfservice.vo.NetworkInventoryResponseVO;
import com.bbb.utils.BBBUtility;

public class TBSBopusInventoryService extends BopusInventoryService{
	
	private HTTPCallInvoker httpCallInvoker;
	
	/**
	 * This property is used as a flag to used session cache.
	 */
	private boolean enableSessionCache;
	
	/**
	 * This property is used as list of usable attribute for network inventory.
	 */
	private List<String> networkInventoryProperty;

	/**
	 * This method is used to the get the INVENTORY for a bopus item and returns
	 * the inventory level by calculating TotalOnHand-TotalAllocatedQtyOnhand
	 * that are obtained from the web service call
	 * 
	 * @param skuList
	 * @param storeIds
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws IOException
	 */
	public Map<String, Integer> getInventoryForBopusItemByViewType(final List<String> skuList)
			throws BBBBusinessException, BBBSystemException, IOException {
		vlogDebug("TBSBopusInventoryService.getInventoryForBopusItemByViewType: Starts with skuList: {0}", skuList);
		final Map<String, Integer> inventoryByNetwork = new ConcurrentHashMap<String, Integer>();
		List<String> networkInvPropList = new ArrayList<>();
		BBBStoreInventoryContainer storeInventoryContainer = (BBBStoreInventoryContainer) ServletUtil
				.getCurrentRequest().resolveName("/com/bbb/commerce/common/BBBStoreInventoryContainer");
		if (isEnableSessionCache() && !getNetworkInventoryProperty().isEmpty()) {
			populateInvAttrForSku(skuList, networkInvPropList);
		}
		/*
		 * If all attributes for a SKU from network call are present in session
		 * then we are not calling service again
		 */
		if (!networkInvPropList.isEmpty()
				&& storeInventoryContainer.getNetworkInventoryMap().keySet().containsAll(networkInvPropList)) {
			vlogDebug("TBSBopusInventoryService.getInventoryForBopusItem: network Invetory present in session: {0}",
					storeInventoryContainer.getNetworkInventoryMap());
			return storeInventoryContainer.getNetworkInventoryMap();
		} else {
			vlogDebug(
					"TBSBopusInventoryService.getInventoryForBopusItem: network Invetory doesn't present in session, calling SB to get network inventory");
			/*
			 * BBBH-1493 starts Modified the existing soap based network
			 * inventory call to rest
			 */
			String networkCallRequest = this.populatenetworkCallRequest(skuList);
			String endPoint = this.getCatalogTools()
					.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.SUPPLY_BALANCE_HTTP_URL)
					.get(0);
			StringBuilder sbcHttpUrl = new StringBuilder(endPoint);
			final String userName = this.getCatalogTools()
					.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.SBC_USERNAME).get(0);
			sbcHttpUrl.append(BBBCoreConstants.QUESTION_MARK);
			sbcHttpUrl.append(BBBCoreConstants.J_USERNAME).append(BBBCoreConstants.EQUAL).append(userName);
			long startTime = new Date().getTime();
			Map<String, String> header = getHeader(false);
			vlogInfo(
					"TBSBopusInventoryService.getInventoryForBopusItem: Total time taken to call Oauth service is : {0} milliseconds",
					new Date().getTime() - startTime);
			startTime = new Date().getTime();
			String networkCallResponse = getHttpCallInvoker().callPostRestService(sbcHttpUrl.toString(),
					networkCallRequest, header, false);
			vlogInfo(
					"TBSBopusInventoryService.getInventoryForBopusItem: Total time taken to call Network inventory service is : {0} milliseconds",
					new Date().getTime() - startTime);
			JSONObject jsonObj;
			boolean isError = false;
			boolean isAvailable = false;
			NetworkInventoryResponseVO networkInventoryResponseVO = null;
			try {
				jsonObj = new JSONObject(networkCallResponse);
				isError = jsonObj.getJSONObject(BBBCoreConstants.SB_AVAILABILITY)
						.has(BBBCoreConstants.SB_ERROR_MESSAGES);
				isAvailable = jsonObj.getJSONObject(BBBCoreConstants.SB_AVAILABILITY)
						.has(BBBCoreConstants.SB_AVL_DETAILS);
				if (isError) {
					final JSONObject msgObj = jsonObj.getJSONObject(BBBCoreConstants.SB_AVAILABILITY)
							.getJSONObject(BBBCoreConstants.SB_ERROR_MESSAGES);
					final Object errorMsgObj = msgObj != null ? msgObj.get(BBBCoreConstants.SB_ERROR_MESSAGE) : null;
					networkInventoryResponseVO = populateNetworkInventoryErrorResp(networkCallResponse, errorMsgObj);
				} else if (isAvailable) {
					final JSONObject availabilityObj = jsonObj.getJSONObject(BBBCoreConstants.SB_AVAILABILITY)
							.getJSONObject(BBBCoreConstants.SB_AVL_DETAILS);
					final Object availabilityDetailObj = availabilityObj != null
							? availabilityObj.get(BBBCoreConstants.SB_AVL_DETAIL) : null;
					networkInventoryResponseVO = populateNetworkInventoryResponse(networkCallResponse, jsonObj,
							availabilityDetailObj);
				}
			} catch (JSONException e) {
				String errorMsg = "TBSBopusInventoryService.getInventoryForBopusItemByViewType - JSONException Occurred - Invalid JSON Response received from EOM service.";
				vlogError(errorMsg, e);
				throw new BBBSystemException(
						"TBSBopusInventoryService.getInventoryForBopusItemByViewType - JSONException Occurred - Invalid JSON Response received from EOM service.");
			}
			if (networkInventoryResponseVO == null || networkInventoryResponseVO.getAvailability() == null) {
				vlogDebug(
						"TBSBopusInventoryService.getInventoryForBopusItem:: networkInventoryResponse doesn't contain availability, Request sent to inventory web service is :  {0} and Response returned from inventory web service is : {1}",
						networkCallRequest, networkCallResponse);
				return inventoryByNetwork;
			}

			if (networkInventoryResponseVO.getAvailability().getMessages() != null) {
				List<NICResponseErrorMessageVO> errorMessage = networkInventoryResponseVO.getAvailability()
						.getMessages().get(BBBCoreConstants.SB_ERROR_MESSAGE);
				if (!BBBUtility.isListEmpty(errorMessage)) {
					for (NICResponseErrorMessageVO nicErrorMsz : errorMessage) {
						if (nicErrorMsz.getSeverity().equalsIgnoreCase(BBBCoreConstants.SEVERITY_ERROR)) {
							vlogError(
									"TBSBopusInventoryService.getInventoryForBopusItemByViewType: Error while calling Bopus Inventory Service. Error Code: {0} , Error Description: ",
									nicErrorMsz.getCode(), nicErrorMsz.getDescription());
						} else if (nicErrorMsz.getSeverity().equalsIgnoreCase(BBBCoreConstants.SEVERITY_WARNING)) {
							vlogWarning(
									"TBSBopusInventoryService.getInventoryForBopusItemByViewType : Warning while calling Bopus Inventory Service. Error Code: {0} Error Description: {1}",
									nicErrorMsz.getCode(), nicErrorMsz.getDescription());
						}

					}
				}
			} else if (networkInventoryResponseVO.getAvailability().getAvailabilityDetails() != null) {
				List<NICAvailabilityResponseDetailVO> availabilityDetailList = networkInventoryResponseVO
						.getAvailability().getAvailabilityDetails().get(BBBCoreConstants.SB_AVL_DETAIL);
				if (!BBBUtility.isListEmpty(availabilityDetailList)) {
					for (NICAvailabilityResponseDetailVO networkInventory : availabilityDetailList) {
						populateNetInventoryResponseToSession(inventoryByNetwork, networkInventory);
					}
					storeInventoryContainer.getNetworkInventoryMap().putAll(inventoryByNetwork);
				}
			}

			vlogDebug(
					"TBSBopusInventoryService.getInventoryForBopusItem:: Request sent to inventory web service is :  {0} and Response returned from inventory web service is : {1}",
					networkCallRequest, networkCallResponse);
		}
		vlogDebug("TBSBopusInventoryService.getInventoryForBopusItem: method exit by returning inventories: {0}",
				inventoryByNetwork);
		return inventoryByNetwork;
	}

	/**
	 * This method is used to populate network inventory details into compact
	 * map.
	 * 
	 * @param inventoryByNetwork
	 * @param networkInventory
	 */
	private void populateNetInventoryResponseToSession(final Map<String, Integer> inventoryByNetwork,
			NICAvailabilityResponseDetailVO networkInventory) {
		if (StringUtils.isNumeric(networkInventory.getAtcQuantity())) {
			inventoryByNetwork.put(networkInventory.getItemName(), Integer.valueOf(networkInventory.getAtcQuantity()));
		}
		if (StringUtils.isNumeric(networkInventory.getDcGroupQuantity())) {
			inventoryByNetwork.put(
					networkInventory.getItemName() + BBBCoreConstants.PIPE_SYMBOL + TBSConstants.DC_GROUP_QUANTITY,
					Integer.valueOf(networkInventory.getDcGroupQuantity()));
		}
		if (StringUtils.isNumeric(networkInventory.getReferenceNumber1())) {
			inventoryByNetwork.put(
					networkInventory.getItemName() + BBBCoreConstants.PIPE_SYMBOL + TBSConstants.REFERENCE_NUMBER1,
					Integer.valueOf(networkInventory.getReferenceNumber1()));
		}
		if (StringUtils.isNumeric(networkInventory.getReferenceNumber2())) {
			inventoryByNetwork.put(
					networkInventory.getItemName() + BBBCoreConstants.PIPE_SYMBOL + TBSConstants.REFERENCE_NUMBER2,
					Integer.valueOf(networkInventory.getReferenceNumber2()));
		}
		if (StringUtils.isNumeric(networkInventory.getReferenceNumber3())) {
			inventoryByNetwork.put(
					networkInventory.getItemName() + BBBCoreConstants.PIPE_SYMBOL + TBSConstants.REFERENCE_NUMBER3,
					Integer.valueOf(networkInventory.getReferenceNumber3()));
		}
		if (StringUtils.isNumeric(networkInventory.getReferenceNumber4())) {
			inventoryByNetwork.put(
					networkInventory.getItemName() + BBBCoreConstants.PIPE_SYMBOL + TBSConstants.REFERENCE_NUMBER4,
					Integer.valueOf(networkInventory.getReferenceNumber4()));
		}
	}

	/**
	 * This method populates error network inventory details into
	 * NetworkInventoryResponseVO
	 * 
	 * @param networkCallResponse
	 * @param networkInventoryResponseVO
	 * @param errorMsgObj
	 * @return
	 * @throws BBBSystemException
	 * @throws IOException
	 */
	private NetworkInventoryResponseVO populateNetworkInventoryErrorResp(String networkCallResponse,
			final Object errorMsgObj) throws BBBSystemException, IOException {
		NetworkInventoryResponseVO networkInventoryResponseVO = null;
		if (errorMsgObj != null && errorMsgObj instanceof JSONArray) {
			vlogDebug(
					"TBSBopusInventoryService.getInventoryForBopusItemByViewType - Recieved Error Message objects as a JSONArray.");
			networkInventoryResponseVO = getHttpCallInvoker().parseJSONResponse(NetworkInventoryResponseVO.class,
					networkCallResponse);
		} else if (errorMsgObj != null) {
			networkInventoryResponseVO = getNetworkErrMsgResponse(errorMsgObj);
		}
		return networkInventoryResponseVO;
	}

	/**
	 * This method populates availability network inventory details into
	 * NetworkInventoryResponseVO
	 * 
	 * @param networkCallResponse
	 * @param jsonObj
	 * @param networkInventoryResponseVO
	 * @param availabilityDetailObj
	 * @return
	 * @throws BBBSystemException
	 * @throws IOException
	 * @throws JSONException
	 */
	private NetworkInventoryResponseVO populateNetworkInventoryResponse(String networkCallResponse, JSONObject jsonObj,
			final Object availabilityDetailObj) throws BBBSystemException, IOException, JSONException {
		NetworkInventoryResponseVO networkInventoryResponseVO = null;
		if (availabilityDetailObj != null && availabilityDetailObj instanceof JSONArray) {
			vlogDebug(
					"TBSBopusInventoryService.getInventoryForBopusItemByViewType - Recieved Availability Details objects as a JSONArray.");
			networkInventoryResponseVO = getHttpCallInvoker().parseJSONResponse(NetworkInventoryResponseVO.class,
					networkCallResponse);
		} else if (availabilityDetailObj != null) {
			networkInventoryResponseVO = getNetworkAvailabilityDetails(availabilityDetailObj, jsonObj);
		}
		return networkInventoryResponseVO;
	}

	/**
	 * This method populates inventory attributes for a list of SKUs.
	 * 
	 * @param skuList
	 * @param networkInvPropList
	 */
	private void populateInvAttrForSku(final List<String> skuList, List<String> networkInvPropList) {
		for (String skuId : skuList) {
			for (String networkInvProp : getNetworkInventoryProperty()) {
				networkInvPropList.add(skuId + BBBCoreConstants.PIPE_SYMBOL + networkInvProp);
			}
		}
	}
	
	/**
	 * This method used to sanitize the NICAvailabilityResponseDetailVO when response
	 * contains JSONObject only, not the JSONArray.
	 * 
	 * @param availabilityDetailObj
	 * @param jsonObj 
	 * @return
	 * @throws BBBSystemException
	 * @throws IOException
	 * @throws JSONException
	 */
	private NetworkInventoryResponseVO getNetworkAvailabilityDetails(Object availabilityDetailObj, JSONObject jsonObj)
			throws BBBSystemException, IOException, JSONException {
		logDebug("BopusInventoryService.getSBCAvlDetailsResponse - Recieved Single Availability Details object only, Not JSONArray.");
		NetworkInventoryResponseVO inventoryResVO = new NetworkInventoryResponseVO();
		NICAvailabiltyResponseVO inventoryResponseVO = new NICAvailabiltyResponseVO();
		List<NICAvailabilityResponseDetailVO> availabilityDetailsVOList = new ArrayList<NICAvailabilityResponseDetailVO>();
		Map<String, List<NICAvailabilityResponseDetailVO>> availabilityDetailVOMap = new HashMap<String, List<NICAvailabilityResponseDetailVO>>();

		final NICAvailabilityResponseDetailVO availabilityDetailVO = getHttpCallInvoker().parseJSONResponse(NICAvailabilityResponseDetailVO.class, availabilityDetailObj.toString());

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
	 * This method used to sanitize the NICResponseErrorMessageVO when
	 * response contains JSONObject only, not the JSONArray
	 * 
	 * @param msgObj
	 * @param errorMsgObj
	 * @return
	 * @throws BBBSystemException
	 * @throws IOException
	 */
	private NetworkInventoryResponseVO getNetworkErrMsgResponse(Object errorMsgObj) throws BBBSystemException, IOException {
		logDebug("TBSBopusInventoryService.getNetworkErrMsgResponse - Recieved Single Error Message object only, Not JSONArray.");
		NetworkInventoryResponseVO inventoryResVO = new NetworkInventoryResponseVO();
		NICAvailabiltyResponseVO inevtoryResponseVO = new NICAvailabiltyResponseVO();
		List<NICResponseErrorMessageVO> errMsgVOList = new ArrayList<NICResponseErrorMessageVO>();
		Map<String, List<NICResponseErrorMessageVO>> messages = new HashMap<String, List<NICResponseErrorMessageVO>>();

		final NICResponseErrorMessageVO errorMsgVO = getHttpCallInvoker().parseJSONResponse(NICResponseErrorMessageVO.class, errorMsgObj.toString());

		errMsgVOList.add(errorMsgVO);
		messages.put(BBBCoreConstants.SB_ERROR_MESSAGE, errMsgVOList);
		inevtoryResponseVO.setMessages(messages);
		inventoryResVO.setAvailability(inevtoryResponseVO);
		logDebug("TBSBopusInventoryService.getNetworkErrMsgResponse ends.");
		return inventoryResVO;
	}
	
	public HTTPCallInvoker getHttpCallInvoker() {
		return httpCallInvoker;
	}

	public void setHttpCallInvoker(HTTPCallInvoker httpCallInvoker) {
		this.httpCallInvoker = httpCallInvoker;
	}

	private String populatenetworkCallRequest(List<String> skuList) throws BBBSystemException, IOException, BBBBusinessException {
		NetworkInventoryRequestVO nreq = new NetworkInventoryRequestVO();
		NICAvailabilityRequestVO availabilityRequest = new NICAvailabilityRequestVO();
		Map<String, List<String>> itemNames = new HashMap<String, List<String>>(); 
		Map<String, Map<String, List<String>>> availabilityCriteria = new HashMap<String, Map<String, List<String>>>(); 
		String viewName = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.TBS_NETWORK_CALL_VIEW_NAME).get(0);
		availabilityRequest.setViewName(viewName);
		itemNames.put("itemName", skuList);
		availabilityCriteria.put("itemNames", itemNames);
		availabilityRequest.setAvailabilityCriteria(availabilityCriteria);
		nreq.setAvailabilityRequest(availabilityRequest);
		String response = getHttpCallInvoker().parseJSONRequest(nreq);
		return response;
	}

	/**
	 * This method prepares the input xml input string and stores it in
	 * SupplyBalanceRequestVO
	 * 
	 * @param skuId
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException 
	 */
	private ServiceRequestIF prepareRequestObjectForSupplyBalanceByNetwork(
			final String skuId)
			throws BBBBusinessException, BBBSystemException {

		if (isLoggingDebug()) {
			logDebug("prepareRequestObjectForSupplyBalance called with params "
					+ "skuId-" + skuId);
		}

		

		final StringWriter stw = new StringWriter();
		final org.xmlsoap.schemas.soap.encoding.String soapEncodedString = org.xmlsoap.schemas.soap.encoding.String.Factory
				.newInstance();
		final SupplyBalanceRequestVO sreqVo = new SupplyBalanceRequestVO();
		

		try {
			

			final TXML txml = OBJECT_FACTORY.createTXML();
			txml.setHeader(this.createheader());
			txml.setMessage(createMessageByNetwork(skuId));
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

		if (isLoggingDebug()) {
			logDebug("prepareRequestObjectForSupplyBalance medhod exit");
		}

		return sreqVo;

	}
	
	/**
	 * This method is used to prepare the Message element as part of Request
	 * 
	 * @param skuId
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	@SuppressWarnings("unchecked")
    private Message createMessageByNetwork(final String skuId) throws BBBSystemException, BBBBusinessException {
		final Message message = OBJECT_FACTORY.createMessage();
		message.setSupplyBalanceInput(createSupplyBalanceInputByNetwork(message, skuId));

		return message;
	}
	
	/**
	 * This method is used to prepare the SupplyBalanceInput element as part of
	 * Request
	 * 
	 * @param message
	 * @param skuId
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	@SuppressWarnings("rawtypes")
    private List createSupplyBalanceInputByNetwork(final Message message,
			final String skuId) throws BBBSystemException, BBBBusinessException {
		List<SupplyBalanceInput> supplyBalanceList;

		if (message.getSupplyBalanceInput() == null) {
			supplyBalanceList = new ArrayList<SupplyBalanceInput>();
		} else {
			supplyBalanceList = message.getSupplyBalanceInput();
		}

		final SupplyBalanceInput supplyInput = OBJECT_FACTORY
				.createSupplyBalanceInput();
		supplyInput.setViewType("By_network");
		supplyInput.setLineItem(createLineItem(supplyInput, skuId));
		supplyBalanceList.add(supplyInput);

		return supplyBalanceList;
	}
	
	/**
	 * This method is used to prepare the LineElement element as part of Request
	 * 
	 * @param supplyInput
	 * @param skuId
	 * @return
	 */
	protected List<LineItem> createLineItem(final SupplyBalanceInput supplyInput,
			final String skuId) {
		List<LineItem> listOfLineItems = supplyInput.getLineItem();
		if (listOfLineItems == null) {
			listOfLineItems = new ArrayList<LineItem>();
			supplyInput.setLineItem(listOfLineItems);
		}
		final LineItem lineItem = OBJECT_FACTORY.createLineItem();

		lineItem.setFacility("ALL");
		lineItem.setFromDate(null);
		lineItem.setFutureSupply("ALL");
		lineItem.setItem(skuId);
		lineItem.setSupplyType("ALL");
		lineItem.setSegment(null);

		listOfLineItems.add(lineItem);

		return listOfLineItems;
	}

	/**
	 * @return the networkInventoryProperty
	 */
	public List<String> getNetworkInventoryProperty() {
		return networkInventoryProperty;
	}

	/**
	 * @param networkInventoryProperty the networkInventoryProperty to set
	 */
	public void setNetworkInventoryProperty(List<String> networkInventoryProperty) {
		this.networkInventoryProperty = networkInventoryProperty;
	}

	/**
	 * @return the enableSessionCache
	 */
	public boolean isEnableSessionCache() {
		return enableSessionCache;
	}
	

	/**
	 * @param enableSessionCache the enableSessionCache to set
	 */
	public void setEnableSessionCache(boolean enableSessionCache) {
		this.enableSessionCache = enableSessionCache;
	}
	 

}
