package com.bbb.commerce.checkout.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.commerce.order.vo.SkuRestrictedZipVO;
import com.bbb.commerce.order.vo.SkuShipRestrictionsVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * @author ajosh8
 * @version $Revision: #1 $
 */
public class DisplayShippingRestrictionsDroplet extends BBBDynamoServlet {

	private static final String EMPTY = "empty";
	private static final String OUTPUT = "output";
	private static final String MAP_SKU_RESTRICTED_ZIP = "mapSkuRestrictedZip";
	private static final String ORDER2 = "order";
	private BBBCatalogTools mCatalogTools;

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}




	/**
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */


	@SuppressWarnings("unused")
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		BBBPerformanceMonitor.start("DisplayShippingRestrictionsDroplet", "service");
		logDebug("CLS=[DisplayShippingRestrictionsDroplet] MTHD=[Service starts]");
		String pSkuId = (String)pRequest.getObjectParameter("skuId");
		if(BBBUtility.isEmpty(pSkuId)){
		Order order=null;
		boolean isRestricted=false;
		Map<String,List<SkuRestrictedZipVO>> mapSkuRestrictedZip = new HashMap<String,List<SkuRestrictedZipVO>>();
		if(pRequest.getObjectParameter(ORDER2) !=null){
			order = (Order)pRequest.getObjectParameter(ORDER2);
			logDebug("Order Details: "+order);
			
			//For each shipping group in order
			for(Object shippingGroupObj: order.getShippingGroups()){
				List<String> lstSkuId = new ArrayList<String>();
				ShippingGroup sg = (ShippingGroup) shippingGroupObj;
				//if shippingGroup is of HardGoodShippingGroup
				if (sg instanceof BBBHardGoodShippingGroup) {
					//Retrieve commerce items associated in the shipping group
					for (Object sgcirObj : sg.getCommerceItemRelationships()) {
						ShippingGroupCommerceItemRelationship sgcir = (ShippingGroupCommerceItemRelationship) sgcirObj;
						//Retrieve skuId
						String skuId = sgcir.getCommerceItem().getCatalogRefId();
						//Retrieve shipping group's address zip code
						String zipCode = ((BBBHardGoodShippingGroup)sg).getShippingAddress().getPostalCode();
						if(zipCode!=null){
							zipCode=zipCode.trim();
						}
						//Call CatalogAPI to check if shipping address's zip code is restricted for the sku
						try {
							isRestricted = getCatalogTools().isShippingZipCodeRestrictedForSku(skuId, order.getSiteId(), zipCode);
							if(isRestricted){
								lstSkuId.add(skuId);
							}
						} catch (BBBBusinessException e) {
							logError(LogMessageFormatter.formatMessage(null, "Repository Exception for sku : "+skuId), e);
						} catch (BBBSystemException e) {
							logError(LogMessageFormatter.formatMessage(null, "Error getting sku detail for sku :"+skuId ), e);
						}
					}
				}
				if(!lstSkuId.isEmpty()){
				mapSkuRestrictedZip=getSkuRestrictionDetails(lstSkuId,sg,order.getSiteId(),mapSkuRestrictedZip);
				}
			}
			if(!mapSkuRestrictedZip.isEmpty()){
				pRequest.setParameter(MAP_SKU_RESTRICTED_ZIP, mapSkuRestrictedZip); 
				pRequest.serviceParameter(OUTPUT, pRequest, pResponse);	
			}else{
				pRequest.serviceParameter(EMPTY, pRequest, pResponse);
			}
		 }
		}
		else{
			SkuShipRestrictionsVO skuShipRestrictionsVO = getShippingRestrictionDetails(pSkuId);
		    pRequest.setParameter("skuShipRestrictionsVO", skuShipRestrictionsVO); 
			pRequest.serviceParameter(OUTPUT, pRequest, pResponse);	
		}
			logDebug("CLS=[DisplayShippingRestrictionsDroplet] MTHD=[Service ends]");
			BBBPerformanceMonitor.end("DisplayShippingRestrictionsDroplet", "service");
	}
	
	
	/**
	 * This method returns the shipping restrictions applied for a sku
	 * 
	 * @param pSkuId
	 * @return SkuShipRestrictionsVO
	 * @throws BBBBusinessException
	 *             , BBBSystemException
	 */
	public SkuShipRestrictionsVO getShippingRestrictionDetails(String pSkuId){
		
		logDebug("CLS=[DisplayShippingRestrictionsDroplet] MTHD=[getShippingRestrictionDetails starts] with parameter: skuId" + pSkuId);
		SkuShipRestrictionsVO skuShipRestrictionsVO = new SkuShipRestrictionsVO();
		try{
		this.logDebug("Setting Region-Zipcodes map for sku");
		String siteId = SiteContextManager.getCurrentSiteId();
		skuShipRestrictionsVO.setZipCodesRestrictedForSkuMap(getCatalogTools().getZipCodesRestrictedForSku(pSkuId));
		final List<StateVO> nonShippableStates = getCatalogTools().getNonShippableStatesForSku(siteId, pSkuId);
		
		if (nonShippableStates != null) {
		    this.logDebug("Setting nonShippableStates for sku");
		    final StringBuffer statesString = new StringBuffer();
		    final int nonShippableStatesSize = nonShippableStates.size();
		    for (int counter = 0; counter < nonShippableStatesSize; counter++) {
		        if (nonShippableStates.get(counter).getStateName() != null) {
		        	
		        	 if (counter == (nonShippableStatesSize - 1)) {
				                statesString.append(nonShippableStates.get(counter).getStateName());
				            } else {
				                statesString.append(nonShippableStates.get(counter).getStateName()).append(",");
				            }
		        			           
		        }
		    }
		    skuShipRestrictionsVO.setNonShippableStates(statesString.toString());
		}
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null, "Repository Exception for sku : "+pSkuId), e);
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null, "Error getting sku detail for sku :"+pSkuId ), e);
		}
		logDebug("CLS=[DisplayShippingRestrictionsDroplet] MTHD=[getShippingRestrictionDetails ends]");
		return skuShipRestrictionsVO;
	}



	/**
	 * This method will provide sku zip code details.
	 * @param lstSkuId
	 * @param sg
	 * @param pStieId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */

	private Map<String, List<SkuRestrictedZipVO>> getSkuRestrictionDetails(
			List<String> lstSkuId, ShippingGroup sg,String pStieId,Map<String,List<SkuRestrictedZipVO>> mapSkuRestrictedZip){
		logDebug("CLS=[DisplayShippingRestrictionsDroplet] MTHD=[getSkuRestrictionDetails starts]");

		logDebug("List of Sku :"+lstSkuId +"ShippingGroup :"+sg+"SiteId"+pStieId);
		List<SkuRestrictedZipVO> lstSkuRestrictedZip = new ArrayList<SkuRestrictedZipVO>();
		SkuRestrictedZipVO skuRestrictedZipVO = null;
		SKUDetailVO skuDetailVO = null;
		String skuRegionName = null;
		StringBuilder address = null;
		address=getShippingAddress(sg);
		String zipCode = ((BBBHardGoodShippingGroup)sg).getShippingAddress().getPostalCode();
		for(String skuId  : lstSkuId){
			try {
			skuRegionName= getCatalogTools().getRestrictedSkuDetails(skuId,zipCode);
			skuRestrictedZipVO = new SkuRestrictedZipVO();
		 	skuDetailVO =  getCatalogTools().getSKUDetails(pStieId,skuId, false);
			skuRestrictedZipVO.setSkuDescription(skuDetailVO.getLongDescription());
			skuRestrictedZipVO.setSkuImage(skuDetailVO.getSkuImages().getMediumImage());
			skuRestrictedZipVO.setSkuDescription(skuDetailVO.getDisplayName());
			if(address.length() != 0){
				skuRestrictedZipVO.setAddress(address);	
			}
			} catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(null, "Repository Exception for sku :"+skuId ), e);
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(null, "sku not present in catalog : "+skuId), e);
			}

			lstSkuRestrictedZip.add(skuRestrictedZipVO);
		}
		mapSkuRestrictedZip.put(skuRegionName, lstSkuRestrictedZip);
		logDebug("CLS=[DisplayShippingRestrictionsDroplet] MTHD=[getSkuRestrictionDetails ends]");
		return mapSkuRestrictedZip;
	}

	/**
	 * This method will return shipping address.
	 * @param sg
	 * @return
	 */
	private StringBuilder getShippingAddress(ShippingGroup sg) {
		logDebug("CLS=[DisplayShippingRestrictionsDroplet] MTHD=[getShippingAddress starts]");
		logDebug("ShippingGroup :"+sg);
		StringBuilder strAddress= new StringBuilder();
		if(((BBBHardGoodShippingGroup) sg).getShippingAddress().getAddress1() != null){
			strAddress.append(((BBBHardGoodShippingGroup) sg).getShippingAddress().getAddress1());
			strAddress.append(",");
		}
		if(((BBBHardGoodShippingGroup) sg).getShippingAddress().getAddress2() != null  && !StringUtils.isEmpty(((BBBHardGoodShippingGroup) sg).getShippingAddress().getAddress2())){
			strAddress.append(((BBBHardGoodShippingGroup) sg).getShippingAddress().getAddress2());
			strAddress.append(",");
		}
		if(((BBBHardGoodShippingGroup) sg).getShippingAddress().getCity() != null){
			strAddress.append(((BBBHardGoodShippingGroup) sg).getShippingAddress().getCity());
			strAddress.append(",");
		}
		if(((BBBHardGoodShippingGroup) sg).getShippingAddress().getState() != null){
			strAddress.append(((BBBHardGoodShippingGroup) sg).getShippingAddress().getState());

		}
		if(((BBBHardGoodShippingGroup) sg).getShippingAddress().getPostalCode() != null){
			strAddress.append(((BBBHardGoodShippingGroup) sg).getShippingAddress().getPostalCode());
		}

		logDebug("ShippingAddress :"+strAddress);

		logDebug("CLS=[DisplayShippingRestrictionsDroplet] MTHD=[getShippingAddress ends]");
		return strAddress;
	}

}
