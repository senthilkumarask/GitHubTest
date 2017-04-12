/**
 * 
 */
package com.bbb.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.porch.service.PorchServiceManager;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.profile.session.BBBSessionBean;

import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;

/**
 * @author dk0004
 *
 */

public class BBBPorchConfigDroplet extends DynamoServlet {

	public final static ParameterName DEFAULT  = ParameterName.getParameterName("default");	
	public final static ParameterName PDP_PORCH  = ParameterName.getParameterName("PDPPorch");	
	public final static ParameterName CART_PORCH  = ParameterName.getParameterName("CartPorch");	
	// public final static ParameterName SITE_ID  = ParameterName.getParameterName("siteId");	
	public final static ParameterName PAGE_NAME  = ParameterName.getParameterName("pageName");	
	public final static ParameterName IN_STOCK  = ParameterName.getParameterName("inStock");	
	public final static ParameterName REGISTRY_ID  = ParameterName.getParameterName("registryId");
	
	private final static String CART_PAGE_NAME="cartPage";	
	private final static String PDP_PAGE_NAME="PDPPage";	
	private String flagDrivenFunctionsItemDesc;
	private BBBCatalogToolsImpl BBBCatalogTools; 	
	private PorchServiceManager porchServiceManager;	
		
	@Override
	public void service(DynamoHttpServletRequest req, DynamoHttpServletResponse res) throws ServletException, IOException {

		// siteId, pageName and inStock are expected from the JSPs Page
		boolean inStock=false;
		String pageName=req.getParameter(PAGE_NAME);
		String s_inStock=req.getParameter(IN_STOCK) ;
		String registryId=req.getParameter(REGISTRY_ID);
		RepositoryItem productItem=null;
		BBBCommerceItem commerceItem=(BBBCommerceItem) req.getObjectParameter("commerceItem");
		String productId=null;
		if(pageName.equalsIgnoreCase("PDPPage")){
			ProductVO productVO1 = (ProductVO) req.getObjectParameter("productVO");
			if(null!=productVO1){
				productId=productVO1.getProductId();
			}
			
		}
		if(StringUtils.isBlank(productId) && null!=commerceItem){
			  productItem = (RepositoryItem) commerceItem.getAuxiliaryData().getProductRef();
			  productId=productItem.getRepositoryId();				 
		}
		String zipcode = getUserPostalCode(req);
		boolean zipcodeValidation = checkPorchServiceWithZipcode(productId,zipcode);
		if(isLoggingDebug()){
			vlogDebug(" pageName: {0} , s_inStock: {1} , registryId: {2}",  pageName, s_inStock, registryId);	
		}
		
			
		
		 boolean globalPorchEnabled = false;
		
		 boolean porchEnabledForPDP = false;
		
		 boolean porchEnabledForCart = false;
		
		 if(zipcodeValidation){
			 
				if(null!=commerceItem && pageName.equalsIgnoreCase(CART_PAGE_NAME)){					 
					ProductVO productVO=new ProductVO();
					List<String> porchServiceFamilycodes=getPorchServiceManager().getPorchServiceFamilyCodes(productItem.getRepositoryId(),productVO);
					req.setParameter("porchServiceTypeCodes", porchServiceFamilycodes);	
					req.setParameter("porchServiceFamilyType", productVO.getPorchServiceFamilyType());
				
				}
				
				
			 if(StringUtils.isNotBlank(s_inStock)){
					inStock=Boolean.valueOf(s_inStock).booleanValue();
				}	
		 try {			
				final Map<String, String> configMap = getBBBCatalogTools().getConfigValueByconfigType(getFlagDrivenFunctionsItemDesc());							
				if(isLoggingDebug()){
					vlogDebug("configMap {0}", configMap);
				}
				if(configMap!=null && !configMap.isEmpty()){
				String v_globalPorchEnabled=configMap.get("globalPorchEnabled");
				if(StringUtils.isNotBlank(v_globalPorchEnabled)){
					globalPorchEnabled=(Boolean.valueOf(v_globalPorchEnabled.trim()));
				}
				
				String v_porchEnabledForPDP=configMap.get("porchEnabledForPDP");
				if(StringUtils.isNotBlank(v_porchEnabledForPDP)){
					porchEnabledForPDP=(Boolean.valueOf(v_porchEnabledForPDP.trim()));
				}
				String v_porchEnabledForCart=configMap.get("porchEnabledForCart");
				if(StringUtils.isNotBlank(v_porchEnabledForCart)){
					porchEnabledForCart=(Boolean.valueOf(v_porchEnabledForCart.trim()));
				}
			}
		} catch (BBBSystemException e) {
			if(isLoggingError()){
				logError("Error while fetching porch details"+e,e);
			}
		} catch (BBBBusinessException e) {
			if(isLoggingError()){
				logError("Error while fetching porch details"+e,e);
			}
		}
	}
		
		//if(StringUtils.isNotBlank(siteId) && getPorchEligibleSites()!=null && getPorchEligibleSites().contains(siteId) && isGlobalPorchEnabled()){
		
		if(globalPorchEnabled){
			req.serviceLocalParameter(DEFAULT, req, res);
		}
				
		if(globalPorchEnabled && porchEnabledForPDP && inStock && PDP_PAGE_NAME.equalsIgnoreCase(pageName) &&  StringUtils.isBlank(registryId) ){
			req.serviceLocalParameter(PDP_PORCH, req, res);
		}
		
		if(globalPorchEnabled && porchEnabledForCart && CART_PAGE_NAME.equalsIgnoreCase(pageName) && StringUtils.isBlank(registryId)){
			req.serviceLocalParameter(CART_PORCH, req, res);
		}
	}
	
	
	/**
	 * @param productId
	 * @param zipCode
	 * @return
	 */
	public boolean checkPorchServiceWithZipcode(String productId, String zipCode) {
		boolean globalPorchZipCodeValidationEnabled=false;
		try {
			final Map<String, String> configMap = getBBBCatalogTools().getConfigValueByconfigType(getFlagDrivenFunctionsItemDesc());
			String v_globalPorchZipCodeValidationEnabled =configMap.get("porchGlobalZipCodeValidation");
			
			
			if(StringUtils.isNotBlank(v_globalPorchZipCodeValidationEnabled)){
				globalPorchZipCodeValidationEnabled=(Boolean.valueOf(v_globalPorchZipCodeValidationEnabled.trim()));
			}
		} catch (BBBSystemException e) {
			if(isLoggingError()){
				logError("Error Getting flagdriven config values "+e,e);
			}
		} catch (BBBBusinessException e) {
			if(isLoggingError()){
				logError("Error Getting flagdriven config values "+e,e);
			}
		}	
		if(!globalPorchZipCodeValidationEnabled){
			return true;
		}
		if(null==productId || StringUtils.isBlank(zipCode)){
			return true;
		}
		
		List<String> porchServiceFamilycodes=getPorchServiceManager().getPorchServiceFamilyCodes(productId,null);
		if(porchServiceFamilycodes.isEmpty()){
			return true;
		}
		Object responseVO=null;
		try {
			  responseVO =getPorchServiceManager().invokeValidateZipCodeAPI(zipCode, porchServiceFamilycodes.get(0));
		} catch (BBBSystemException e) {
			if(isLoggingError()){
				logError("Error While invoking porch zip code validate api "+e,e);
			}
		} catch (BBBBusinessException e) {
			if(isLoggingError()){
				logError("Error While invoking porch zip code validate api "+e,e);
			}
		}
		
		if(null==responseVO){
			return false;
		}
		else {
		return true;
		}
	}


	/**
	 * @param req
	 * @return
	 */
	public String getUserPostalCode(DynamoHttpServletRequest req){
		
		
	/*	BBBSessionBean bbbsessionBean = (BBBSessionBean) req.resolveName("/com/bbb/profile/session/SessionBean");
		String porchZipCode = bbbsessionBean.getPorchZipCode();
		if(!StringUtils.isBlank(porchZipCode)){
			return porchZipCode;
		} */
		
		//else{
		Profile userProfile = (Profile) req.getObjectParameter("profile");
		if(null==userProfile){
			return null;
		}
		String zipCode=null;	 
		if(userProfile.isTransient()){
			zipCode = getPorchServiceManager().zipCodeFromAkamaiHeader(req);
		}
		else {
			 RepositoryItem shippingAddress = (RepositoryItem) userProfile.getPropertyValue("shippingAddress");
			 if(null!=shippingAddress && null!=shippingAddress.getPropertyValue("postalCode")){
				 zipCode = (String) shippingAddress.getPropertyValue("postalCode");
			 }
			 else{
				 zipCode = getPorchServiceManager().zipCodeFromAkamaiHeader(req);
			 }
			}
		if(!StringUtils.isBlank(zipCode)){
		String[] splitZipCode= zipCode.split("-");
		return splitZipCode[0];
		}
		return null;
	//}
	}
	
	public String getFlagDrivenFunctionsItemDesc() {
		return flagDrivenFunctionsItemDesc;
	}

	public void setFlagDrivenFunctionsItemDesc(String flagDrivenFunctionsItemDesc) {
		this.flagDrivenFunctionsItemDesc = flagDrivenFunctionsItemDesc;
	}

	public BBBCatalogToolsImpl getBBBCatalogTools() {
		return BBBCatalogTools; 
	}

	public void setBBBCatalogTools(BBBCatalogToolsImpl bBBCatalogTools) {
		BBBCatalogTools = bBBCatalogTools;
	}
	
	public PorchServiceManager getPorchServiceManager() {
		return porchServiceManager;
	}

	public void setPorchServiceManager(PorchServiceManager porchServiceManager) {
		this.porchServiceManager = porchServiceManager;
	}			
}
