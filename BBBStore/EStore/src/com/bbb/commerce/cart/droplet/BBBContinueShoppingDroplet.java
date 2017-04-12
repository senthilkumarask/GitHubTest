package com.bbb.commerce.cart.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.utils.BBBUtility;

/**
 * @author jpadhi
 * @version $Revision: #1 $
 */
public class BBBContinueShoppingDroplet extends BBBDynamoServlet {
	
	private static final String REFERER = "Referer";
	
	private List<String> urlPatterns;

	/**
	 * @return
	 */
	public List<String> getUrlPatterns() {
		return urlPatterns;
	}

	/**
	 * @param urlPatterns
	 */
	public void setUrlPatterns(List<String> urlPatterns) {
		this.urlPatterns = urlPatterns;
	}

	/**
	 * this methods checks the request header for the Referring URL
	 * if request comes from PDP redirect to same page
	 * 
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @throws ServletException
	 *             , IOException
	 */
	
    @Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
    
			logInfo("START: BBBContinueShoppingDroplet");
	
		String continueShoppingURL = "";
		if(pRequest.getContextPath().length() > 1){
			continueShoppingURL = pRequest.getContextPath()+"/";
		}else{
			continueShoppingURL = pRequest.getContextPath();
		}
	
		if (pRequest.getHeader(REFERER) != null) {
			for(String urlPattern: getUrlPatterns()){
				if(BBBUtility.isStringPatternValid(urlPattern, pRequest.getHeader(REFERER))){
					continueShoppingURL = pRequest.getHeader(REFERER);
					break;
				}
			}			
		}
		
		//Removed /store in the home page url redirection from Continue Shopping Link on cart page.
		if(continueShoppingURL.equalsIgnoreCase("/store/")){
			continueShoppingURL = "/";
		}
	
		logDebug("BBBContinueShoppingDroplet : Referer URL from Header : "+pRequest.getHeader(REFERER));
		logDebug("BBBContinueShoppingDroplet : Continue Shopping URL : "+continueShoppingURL);
		
		
		pRequest.setParameter(BBBCheckoutConstants.CART_CONTINUE_SHOPPING_URL,
				continueShoppingURL);
		pRequest.serviceParameter(BBBCheckoutConstants.OUTPUT, pRequest, pResponse);
		
	
		logInfo("END: BBBContinueShoppingDroplet");
		
	}
}
