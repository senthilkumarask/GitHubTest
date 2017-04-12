/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ReferralInfoDroplet.java
 *
 *  DESCRIPTION: This droplet Fetch Referral Info from the request url & set a url which is to
 * 				be fired as a pixel image on the desired pages.
 *  HISTORY:
 *  02/06/12 Initial version
 *
 */

package com.bbb.tag.droplet;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.order.manager.OrderDetailsManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.common.BBBOrderVO;
import com.bbb.commerce.giftregistry.droplet.BBBPresentationDroplet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
/*import com.bbb.profile.session.BBBSessionBean;
import com.bbb.selfservice.vo.ReferralVO;*/
import com.bbb.utils.BBBUtility;

/**
 * This droplet Fetch Referral Info from the request url & set a url which is to
 * be fired as a pixel image on the desired pages.
 * 
 * @author skalr2
 * 
 */
public class ReferralInfoDroplet extends BBBPresentationDroplet {

	/**
	 * catalogTools
	 */
	private BBBCatalogTools catalogTools;

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * refCreateRegString
	 */
	private String refCreateRegString;

	/**
	 * refSaleRegString
	 */
	private String refSaleRegString;

	/**
	 * refProdSaleString
	 */
	private String refProdSaleString;

	/**
	 * @return the refCreateRegString
	 */
	public String getRefCreateRegString() {
		return refCreateRegString;
	}

	/**
	 * @param refCreateRegString
	 *            the refCreateRegString to set
	 */
	public void setRefCreateRegString(final String refCreateRegString) {
		this.refCreateRegString = refCreateRegString;
	}

	/**
	 * @return the refSaleRegString
	 */
	public String getRefSaleRegString() {
		return refSaleRegString;
	}

	/**
	 * @param refSaleRegString
	 *            the refSaleRegString to set
	 */
	public void setRefSaleRegString(final String refSaleRegString) {
		this.refSaleRegString = refSaleRegString;
	}

	/**
	 * @return the refCJSaleString
	 */
	public String getRefProdSaleString() {
		return refProdSaleString;
	}

	/**
	 * @param refCJSaleString
	 *            the refCJSaleString to set
	 */
	public void setRefProdSaleString(final String refProdSaleString) {
		this.refProdSaleString = refProdSaleString;
	}

	private OrderDetailsManager orderDetailsManager;
	
	/**
	 * Fetch Referral Info from referral Id and get the session.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */

	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {

		try {
			String queryString = null;
			String refId = null;
			String wcRegString = null;
			String wcRegParam = null;
			String bpRegString = null;
			String bpRegParam = null;
			String curUrl = null;
			/*String pdpUrl = null;*/
			String wcSaleString = null;
			String wcSaleParam = null;
			String bpSaleString = null;
			String bpSaleParam = null;
			String cjSaleString = null;
			String cjSaleParam = null;
			String oracleResponsys = null;
			
			String currentPage = pRequest.getParameter(CURRENT_PAGE);
					
			// getting params for referrals from session bean
			/*final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
					.resolveName("/com/bbb/profile/session/SessionBean");

			final HashMap sessionMap = sessionBean.getValues();*/
			/*final ReferralVO refVO = (ReferralVO) sessionMap
					.get(BBBCoreConstants.REF_URL_VO);*/

			final Calendar cal = new GregorianCalendar();
			//as in gregorian calendar month starts from 0 and not 1, we need to add 1 to the month.
			final int month = cal.get(Calendar.MONTH)+1;
			final int year = cal.get(Calendar.YEAR);
			final int day = cal.get(Calendar.DAY_OF_MONTH);
			final String todayDate = month + "/" + day + "/" + year;

			Map<String,String> paramValueMap = null;
			queryString = pRequest.getCookieParameter("queryString");
			refId = pRequest.getCookieParameter("refId");
			curUrl =pRequest.getCookieParameter("curUrl");
			if (refId != null &&queryString!=null &&curUrl!=null) {
				/*queryString = refVO.getReferralUrl();
				refId = refVO.getReferralId();
				curUrl = refVO.getCurrentUrl();*/
				

				//refVO.getReferrerDomain();
				
				/*if (!(curUrl.length() > 0 && curUrl.length() < 9)) {
					pdpUrl = curUrl.substring(0, 9);
				}*/
				if(queryString!=null){
					String[]  parameterValues= queryString.split(BBBCoreConstants.AMPERSAND);
					if(parameterValues !=null ){
						paramValueMap=new HashMap<String, String>();
						for(String parameterValue: parameterValues){
							if(parameterValue.indexOf(BBBCoreConstants.EQUAL)>0){
								String[] tokens = parameterValue.split(BBBCoreConstants.EQUAL);
								paramValueMap.put( tokens[0] , ((tokens.length == 2) && (tokens[1] != null))?tokens[1]:"");
							}
						}
					}
					
				}

				// creating a url to be fired as an image pixel
				if (refId != null && queryString != null) {

					if (refId.contains(BBBCoreConstants.WED_CHANNEL_REF)) {

						//if current page is create registry confirmation
						if (currentPage!=null
							&& getRefCreateRegString().contains(currentPage)
								&& paramValueMap !=null) {
							
							final List<String> params = (List<String>) getCatalogTools().
									getAllValuesForKey(BBBCoreConstants.REFERRAl_CONTROLS, 
											BBBCoreConstants.WC_CREATE_REGISTRY_PARAMS);
							
							wcRegParam = createReferrerQueryString(params, paramValueMap);
							wcRegString = getCatalogTools().getAllValuesForKey(
									BBBCoreConstants.THIRD_PARTY_URL,
									BBBCoreConstants.WC_REF_REG_URL).get(0);
							
							logDebug("CLS=[ReferralInfoDroplet] CreateRegistry wcRegParam=" 
									+ wcRegParam + " wcRegString="+wcRegString);
							
						} else if (currentPage!=null && getRefSaleRegString().contains(currentPage)
								&& paramValueMap !=null) {
							//if current page is registry sale confirmation page i.e checkout page
							
							final List<String> params = (List<String>) getCatalogTools().
									getAllValuesForKey(BBBCoreConstants.REFERRAl_CONTROLS, 
											BBBCoreConstants.WC_SALE_REGISTRY_PARAMS);
							
							wcSaleParam = createReferrerQueryString(params, paramValueMap);
							wcSaleString = getCatalogTools()
									.getAllValuesForKey(
											BBBCoreConstants.THIRD_PARTY_URL,
											BBBCoreConstants.WC_REF_SALE_URL)
											.get(0);
							
							logDebug("CLS=[ReferralInfoDroplet] SaleRegistry wcRegParam=" 
							+ wcSaleParam + " wcRegString="+wcSaleString);
						} 
						
					} else if (refId.contains(BBBCoreConstants.COM_JUN_REF)
//							&& getRefProdSaleString().contains(pdpUrl)
							&& paramValueMap != null) {
						
						final List<String> params = (List<String>) getCatalogTools().
								getAllValuesForKey(BBBCoreConstants.REFERRAl_CONTROLS, 
										BBBCoreConstants.CJ_SALE_REQ_PARAMS);
						
						cjSaleParam = createReferrerQueryString(params, paramValueMap);
						cjSaleString = getCatalogTools().getAllValuesForKey(
								BBBCoreConstants.THIRD_PARTY_URL,
								BBBCoreConstants.CJ_REF_SALE_URL).get(0);
						
						logDebug("CLS=[ReferralInfoDroplet] CommissionJunction product cjSaleParam=" 
								+ cjSaleParam + " cjSaleString="+cjSaleString);
						
					}else if (refId.contains(BBBCoreConstants.THEBUMP_REF )) {

						if (getRefCreateRegString().contains(currentPage)
								&& paramValueMap !=null) {
							
							final List<String> params = (List<String>) getCatalogTools().
									getAllValuesForKey(BBBCoreConstants.REFERRAl_CONTROLS, 
											BBBCoreConstants.BP_CREATE_REGISTRY_PARAMS);
							
							bpRegParam = createReferrerQueryString(params, paramValueMap);
							bpRegString = getCatalogTools().getAllValuesForKey(
									BBBCoreConstants.THIRD_PARTY_URL,
									BBBCoreConstants.BP_REF_REG_URL).get(0);
							
							logDebug("CLS=[ReferralInfoDroplet] bpRegParam=" + bpRegParam + " bpRegString="+bpRegString);
							
						}else if (getRefSaleRegString().contains(currentPage)
								&& paramValueMap !=null) {

							final List<String> params = (List<String>) getCatalogTools().
									getAllValuesForKey(BBBCoreConstants.REFERRAl_CONTROLS, 
											BBBCoreConstants.BP_SALE_REGISTRY_PARAMS);

							bpSaleParam = createReferrerQueryString(params, paramValueMap);
							bpSaleString = getCatalogTools()
									.getAllValuesForKey(
											BBBCoreConstants.THIRD_PARTY_URL,
											BBBCoreConstants.BP_REF_SALE_URL)
											.get(0);
							logDebug("CLS=[ReferralInfoDroplet] bpSaleParam=" 
									+ bpSaleParam + " bpSaleString="+bpSaleString);
						}
					} 
				}
				pRequest.setParameter(BBBCoreConstants.REF_ID, refId);
				pRequest.setParameter(BBBCoreConstants.TODAY_DATE, todayDate);
				
				pRequest.setParameter(BBBCoreConstants.WC_REG_URL, wcRegString);
				pRequest.setParameter(BBBCoreConstants.WC_REG_PARAM, wcRegParam);
				pRequest.setParameter(BBBCoreConstants.WC_SALE_URL,	wcSaleString);
				pRequest.setParameter(BBBCoreConstants.WC_SALE_PARAM,wcSaleParam);
				
				pRequest.setParameter(BBBCoreConstants.BP_REG_URL, bpRegString);
				pRequest.setParameter(BBBCoreConstants.BP_REG_PARAM, bpRegParam);
				pRequest.setParameter(BBBCoreConstants.BP_SALE_URL,	bpSaleString);
				pRequest.setParameter(BBBCoreConstants.BP_SALE_PARAM,bpSaleParam);

				
				pRequest.setParameter(BBBCoreConstants.CJ_SALE_URL,	cjSaleString);
				pRequest.setParameter(BBBCoreConstants.CJ_SALE_PARAM,cjSaleParam);
				
				//pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
			}
			
			//BBBP-8308 - start
			/*String responsysCookie = getValueFromBCC(BBBCoreConstants.CONTENT_CATALOG_KEYS,
					BBBCoreConstants.RESPONSYS_COOKIE_NAME);
			oracleResponsys = pRequest.getCookieParameter(responsysCookie);
			if(oracleResponsys!=null && !BBBUtility.isEmpty(oracleResponsys)){*/
				String responsysEnabled = getValueFromBCC(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.RESPONSYS_ENABLED);
				String oracleResponsysURL = getValueFromBCC(BBBCoreConstants.THIRD_PARTY_URL,BBBCoreConstants.ORACLE_RESPONSYS_URL);
				pRequest.setParameter(BBBCoreConstants.ORACLE_RESPONSYS_FLAG, true);	
				pRequest.setParameter(BBBCoreConstants.RESPONSYS_ENABLED, responsysEnabled);	
				pRequest.setParameter(BBBCoreConstants.ORACLE_RESPONSYS_URL, oracleResponsysURL);	
			//}
			//BBBP-8308 - end
			pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
		} catch (BBBBusinessException bExcep) {
				logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException in ReferralInfoDroplet", BBBCoreErrorConstants.ACCOUNT_ERROR_1249 ), bExcep);
			
		} catch (BBBSystemException sysExcep) {
				logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException in ReferralInfoDroplet", BBBCoreErrorConstants.ACCOUNT_ERROR_1250 ), sysExcep);
			
		}
	}

	

	/**
	 * This method creates confirm URL to be sent back to Referrer
	 * 
	 * @param params
	 * @param paramValuesMap
	 * @return
	 */
	private String createReferrerQueryString(List<String> params, Map<String,String> paramValuesMap){
		
		if(params == null){
			return null;
		}
		String value = null;
		StringBuilder sbuilder = new StringBuilder();
		
		int index =0;
		for(String param: params){
			
			value  = paramValuesMap.get(param);
			if(value !=null){
				if(index !=0){
					sbuilder.append(BBBCoreConstants.AMPERSAND);
				}
				sbuilder.append(param).append(BBBCoreConstants.EQUAL).append(value);
				index++;
			}
		}
		
		return sbuilder.toString();
		
	}
	
	public OrderDetailsManager getOrderDetailsManager() {
		return orderDetailsManager;
	}

	public void setOrderDetailsManager(OrderDetailsManager orderDetailsManager) {
		this.orderDetailsManager = orderDetailsManager;
	}

	/** The parameter currentPage*/
	public static final ParameterName CURRENT_PAGE = ParameterName
			.getParameterName("currentPage");
	
	
	
	
	/**
	 * This method gets key value  from BCC
	 * 
	 * @return keyValue
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private String getValueFromBCC(String configType, String key) throws BBBSystemException,
			BBBBusinessException {
		String keyValue = "";
		List<String> keyValueList = getCatalogTools().getAllValuesForKey(
				configType,
				key);

		if (!BBBUtility.isListEmpty(keyValueList)) {
			keyValue = keyValueList.get(0);
		}
		else
		{
			logDebug("Config Key is empty for " + key);
		}
		return keyValue;
	}
}