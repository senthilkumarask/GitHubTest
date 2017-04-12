package com.bbb.search.droplet;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.core.util.NumberTable;
import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.order.TBSOrderManager;
import com.bbb.commerce.order.TBSOrderTools;
import com.bbb.commerce.vo.OrderVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

public class TBSOrderPaginationDroplet extends DynamoServlet {
	
	private TBSOrderManager mOrderManager;
	private BBBCatalogToolsImpl mCatalogTools;
	private int resultThreshold=200;
	
	public int getResultThreshold() {
		return resultThreshold;
	}
	public void setResultThreshold(int resultThreshold) {
		this.resultThreshold = resultThreshold;
	}
	public BBBCatalogToolsImpl getCatalogTools() {
		return mCatalogTools;
	}
	public void setCatalogTools(BBBCatalogToolsImpl mCatalogTools) {
		this.mCatalogTools = mCatalogTools;
	}
	/**
	 * @return the orderManager
	 */
	public TBSOrderManager getOrderManager() {
		return mOrderManager;
	}
	/**
	 * @param pOrderManager the orderManager to set
	 */
	public void setOrderManager(TBSOrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}

	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
	
		
		int pageNumber = Integer.parseInt(pRequest.getParameter("pageNumber"));
		int perPage = Integer.parseInt(pRequest.getParameter("perPage"));
		String orderId = pRequest.getParameter("orderId");
		List<OrderVO> resultedOrders = new ArrayList<OrderVO>();
		List<OrderVO> resultedArchivedOrders = null;
		if(orderId != null){
			orderId = orderId.trim();
		}
		String email = pRequest.getParameter("email");
		if(email != null){
			email = email.trim();
		}
		String firstName = pRequest.getParameter("firstName");
		if(firstName != null){
			firstName = firstName.trim();
		}
		String lastName = pRequest.getParameter("lastName");
		if(lastName != null){
			lastName = lastName.trim();
		}
		String registryNum = pRequest.getParameter("registryNum");
		if(registryNum != null){
			registryNum = registryNum.trim();
		}
		String storeNum = pRequest.getParameter("storeNum");
		if(storeNum != null){
			storeNum = storeNum.trim();
		}
		String startDate = pRequest.getParameter("startDate");
		String endDate = pRequest.getParameter("endDate");
		List<RepositoryItem> orders = null;
		List<RepositoryItem> archivedOrders = null;
		final String fetchArchivedOrder = getOrderManager().getCatalogUtil().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, 
				BBBCoreConstants.FETCH_ARCHIVED_ORDERS, BBBCoreConstants.FALSE);
		
		vlogDebug("Value of fetchArchivedOrder is {0} ", fetchArchivedOrder);
		
		try {
			if (getMinimalOrderFlag()) {
				if (!StringUtils.isBlank(email) || !StringUtils.isBlank(orderId)) {
					List<String> resultSetSizeValueFromConfigKey;
					 int numResultSetSize;
					 resultSetSizeValueFromConfigKey = getCatalogTools().getAllValuesForKey("AdvancedOrderInquiryKeys", "ResultSetSize");
					 if (!BBBUtility.isListEmpty(resultSetSizeValueFromConfigKey)
								&& !BBBUtility.isEmpty(resultSetSizeValueFromConfigKey.get(TBSConstants.ZERO))){
						 numResultSetSize = Integer.parseInt(resultSetSizeValueFromConfigKey.get(TBSConstants.ZERO));
					 } else {
						 numResultSetSize = getResultThreshold();
					 }
					vlogDebug("TBSOrderPaginationDroplet() : ResultSetSize: "+numResultSetSize);
					resultedOrders = ((TBSOrderTools) getOrderManager().getOrderTools()).minimalOrderSearch(orderId, email, numResultSetSize);
				}
			} else {
				if (!StringUtils.isBlank(email) || !StringUtils.isBlank(orderId)) {
					orders = ((TBSOrderTools) getOrderManager().getOrderTools()).orderSearch(orderId, email);

					if (Boolean.valueOf(fetchArchivedOrder)) {
						archivedOrders = ((TBSOrderTools) getOrderManager().getOrderTools()).archiveOrderSearch(orderId, email);
						vlogDebug("Archived orders {0} ", archivedOrders);
					}
				} else {
					orders = ((TBSOrderTools) getOrderManager().getOrderTools()).orderSearch(firstName, lastName, startDate, endDate, registryNum, storeNum, false);

					if (Boolean.valueOf(fetchArchivedOrder)) {
						archivedOrders = ((TBSOrderTools) getOrderManager().getOrderTools()).orderSearch(firstName, lastName, startDate, endDate, registryNum, storeNum, true);
						vlogDebug("Archived orders {0} ", archivedOrders);
					}
				}
			}
			if(orders != null && orders.size() > TBSConstants.ZERO){
			   resultedOrders = ((TBSOrderTools)getOrderManager().getOrderTools()).processOrderVO(orders, false);
			}
			if(!BBBUtility.isListEmpty(archivedOrders)){
				resultedArchivedOrders = ((TBSOrderTools)getOrderManager().getOrderTools()).processOrderVO(archivedOrders, false);
			}
			
			if (!BBBUtility.isListEmpty(resultedOrders)) {
				if (!BBBUtility.isListEmpty(archivedOrders)) {
					resultedOrders.addAll(resultedArchivedOrders);
				}
			} else if (!BBBUtility.isListEmpty(archivedOrders)) {
				resultedOrders = resultedArchivedOrders;
			}
			
			if(!BBBUtility.isListEmpty(resultedOrders)) {
				
				int totalOrers = resultedOrders.size();
				int startIndex = (pageNumber - 1) * perPage;
				int endIndex = startIndex + perPage;
				if(endIndex > totalOrers){
					endIndex = totalOrers;
				}
				//getting totalpages
				int totalPages = totalOrers / perPage;
				//setting balance order to last page
				int remaingPages = totalOrers % perPage;
				if(remaingPages > 0 ){
					totalPages +=1;
				}
				pRequest.setParameter("startIndex", startIndex+1);
				pRequest.setParameter("endIndex", endIndex);
				pRequest.setParameter("orders", resultedOrders);
				pRequest.setParameter("totalOrers", totalOrers);
				pRequest.serviceLocalParameter("outputStart", pRequest, pResponse);
				
				for (int currentPage = 1; currentPage < totalPages+1; currentPage++) {
					pRequest.setParameter("currentPage", NumberTable.getInteger(currentPage));
					pRequest.serviceLocalParameter("output", pRequest, pResponse);
				}
			}else{
				pRequest.serviceLocalParameter("empty", pRequest, pResponse);
			}

		} catch (RepositoryException e) {
			vlogError("Exception occurred "+e);
		} catch (CommerceException e) {
			vlogError("CommerceException occurred "+e);
		} catch (ParseException e) {
			vlogError("ParseException occurred "+e);
		} catch (BBBSystemException e) {
			vlogError("BBBSystemException occurred ",e);
		} catch (BBBBusinessException e) {
			vlogError("BBBBusinessException occurred ",e);
		}
	}
	private boolean getMinimalOrderFlag()
	{
		//Fetch key to check if we need to call Store Proc for Order Inquiry
		List<String> configKeyValues;
		try {
			configKeyValues = getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "TBS_MINIMAL_ORDERDETAIL");
			String minimalOrderKey = null;
			boolean minimalOrderRequired=false;
			if(!BBBUtility.isListEmpty(configKeyValues))
			{
				minimalOrderKey = configKeyValues.get(0);
			}
		
			if(BBBUtility.isNotEmpty(minimalOrderKey) && minimalOrderKey.equalsIgnoreCase(BBBCoreConstants.TRUE))
			{
				minimalOrderRequired=true;
			}
			return minimalOrderRequired;
		} catch (BBBSystemException | BBBBusinessException e) {
			vlogError("Exception occurred while fetching key: [TBS_MINIMAL_ORDERDETAIL]", e);
		}		 
		return false;
	 }
}
