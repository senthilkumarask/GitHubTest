package com.bbb.pipeline;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBAccountConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

public class BabyCanadaURLServlet extends InsertableServletImpl {

	private BBBCatalogTools catalogTools;
	private static final String BABY_CA_PARAM = "?babyCA=true";
	
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		if(isLoggingDebug()){
			logDebug("BabyCanadaURLServlet.service() method called");
		}	
		
		String buyBuyBabyCAConfig=null;
		String domainVsRefererConfig = null;
		String bedBathCAConfig=null;
		String categoryIdConfig = null;
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		String babyCanadaFlagFromCookie = getBabyCanadaFlag(pRequest); 
//		Retain session bean value for baby canada flag if user logs out.   
		if(babyCanadaFlagFromCookie != null && sessionBean.getBabyCA() == null){
			sessionBean.setBabyCA(babyCanadaFlagFromCookie);
			if(isLoggingDebug()){
				logDebug("Session Value for Baby Canada is NULL , setting it from Cookie ");
			}	
		}
		if(sessionBean.getBabyCA() != null &&  babyCanadaFlagFromCookie == null){
			addBabyCanadaCookie(pResponse , sessionBean.getBabyCA());
			if(isLoggingDebug()){
				logDebug("Cookie Value for Baby Canada is NULL , setting it from Session");
			}	
		}
		if(isLoggingDebug()){
			logDebug("Baby Canada Flag Value ["+sessionBean.getBabyCA()+"]");
		}
		try {
			List<String>  buyBuyBabyCAURL = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.REFERRAl_CONTROLS,
					BBBCoreConstants.BABY_CANADA_SOURCE_URL);
			List<String> domainVsReferer = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.REFERRAl_CONTROLS, BBBCoreConstants.DOMAN_VS_REFERER);
			if(buyBuyBabyCAURL!=null && !buyBuyBabyCAURL.isEmpty()){
				buyBuyBabyCAConfig=buyBuyBabyCAURL.get(0);
			}
			if(domainVsReferer != null && !domainVsReferer.isEmpty()){
				domainVsRefererConfig = domainVsReferer.get(0);
			}
			
		} catch (BBBSystemException e) {
			logError("Exception occurred while retrieving config keys", e);
		} catch (BBBBusinessException e) {
			logError("Exception occurred while retrieving config keys", e);
		}
		
		// Baby Canada redirection flag added based on the domain or Query Paramater
		// If flag is Domain, follow the same approach.
		// if flag is Referer, based on the Query Parameter, construct URL
		
					
		String requestURL = null;
		if(null!=domainVsRefererConfig && domainVsRefererConfig.equalsIgnoreCase(BBBCoreConstants.DOMAIN)){
			requestURL = pRequest.getServerName();
			if(isLoggingDebug()){
				logDebug("The referer is [ " + requestURL + " ] for Domain");
			}
		}else if(null!=domainVsRefererConfig && domainVsRefererConfig.equalsIgnoreCase(BBBCoreConstants.REFERER)){
			requestURL = pRequest.getQueryParameter(BBBCoreConstants.REFERER);
			if(isLoggingDebug()){
				logDebug("The referer is [ " + requestURL + " ]");
			}			
		}else{
			logError("DomainVsReferer Config Value doesnot matches Domain or Referer");
		}
				
		if(BBBUtility.isNotEmpty(buyBuyBabyCAConfig) && (StringUtils.equalsIgnoreCase(requestURL, buyBuyBabyCAConfig) )){
			List<String> bedBathCAUrl;
			try {
				bedBathCAUrl = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.REFERRAl_CONTROLS,
						BBBCoreConstants.BABY_CANADA_TARGET_URL);
				List<String>  categoryId = this.getCatalogTools().getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,
						BBBCatalogConstants.BABY_CANADA_L1_CATEGORY);
				
				if(bedBathCAUrl!=null && !bedBathCAUrl.isEmpty()){
					bedBathCAConfig=bedBathCAUrl.get(0);
				}
				if(categoryId!=null && !categoryId.isEmpty()){
					categoryIdConfig=categoryId.get(0);
				}
				if(BBBUtility.isNotEmpty(bedBathCAConfig) && BBBUtility.isNotEmpty(categoryIdConfig)){
					String newRedirectUrl = pRequest.getScheme() + BBBAccountConstants.SCHEME_APPEND + bedBathCAConfig + pRequest.getContextPath() + "/"+ categoryIdConfig+BABY_CA_PARAM;
					logInfo("BabyCanadaURLServlet.service() BuyBuyBaby Canada URL Detected. Redirecting to BedBathBeyond Canada site with URL: "+newRedirectUrl);
					//301 redirect - BBBSL-4339
					pResponse.sendRedirect(pResponse.encodeRedirectURL(newRedirectUrl));
					pResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
					return;
				}
			} catch (BBBSystemException e) {
				logError("Exception occurred while retrieving config keys", e);
			} catch (BBBBusinessException e) {
				logError("Exception occurred while retrieving config keys", e);
			}
		}
		passRequest(pRequest, pResponse);
	}
	
	private void addBabyCanadaCookie(DynamoHttpServletResponse pResponse , String babyCA) {
		Cookie babyCanadaCookie = new Cookie(BBBCoreConstants.BABY_CANADA_FLAG_COOKIE,babyCA);
//	Cookie expires when browser is closed
		babyCanadaCookie.setMaxAge(-1);
		BBBUtility.addCookie(pResponse, babyCanadaCookie, true);
	}
	private String getBabyCanadaFlag(DynamoHttpServletRequest pRequest) {
		String babyCanadaFlag = null ; 
		String[] prameterValues = pRequest.getCookieParameterValues(BBBCoreConstants.BABY_CANADA_FLAG_COOKIE) ; 
		if(prameterValues != null && prameterValues.length > 0){
			babyCanadaFlag = prameterValues[0]; 
		}
		return babyCanadaFlag;
	}
}