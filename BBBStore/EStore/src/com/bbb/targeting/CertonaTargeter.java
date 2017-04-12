package com.bbb.targeting;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;




import atg.naming.NameResolver;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.targeting.DynamicContentTargeter;
import atg.targeting.TargetingException;

import com.bbb.certona.utils.CertonaResponseUtils;
import com.bbb.certona.vo.CertonaResponseVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

/**
 * This targeter extends DynamicContentTargeter and retrieves the content details
 * for a slot from Certona.
 * 
 * @author rjain40
 * 
 */
public class CertonaTargeter extends DynamicContentTargeter {

	private CertonaResponseUtils mCertRespUtils;
	private static final String CLS_NAME = "CLS=[CertonaTargeter]/MSG::";

	/**
	 * This is the overridden method which is used to get the recommendations
	 * from Certona for different schemes .Firstly the target function of super
	 * class is called to get the parameters values.
	 * 
	 * @param pNameResolver
	 *            Source Map
	 * @return Array of Object items in <code>Object[]</code> format
	 * @throws TargetingException
	 */
	@Override
	public Object[] target(final NameResolver pNameResolver,final int pStartIndex,final int pMaxNumber) throws TargetingException {
		
		BBBPerformanceMonitor.start(CertonaTargeter.class.getName() + " : " + "target");
		if (isLoggingDebug()) {
			logDebug("Enter.CertonaTargeter.target(pNameResolver,pStartIndex,pMaxNumber)");
		}
		// Calls the super method to get the source item having the parameter
		// values for Certona call
		final Object[] responseObject = super.target(pNameResolver);
		if (isLoggingDebug()) {
			logDebug("CertonaTargeter.target(pNameResolver,pStartIndex,pMaxNumber): The response Object for Source is: " + responseObject);
		}
		ProductVO[] productVOArray = null;
		if (null != responseObject && responseObject.length != 0) {
			final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
			final DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();

			String schemeName = "";
			final RepositoryItem responseItem = (RepositoryItem) responseObject[0];
			final Map<String, String> sourceParam = (Map<String, String>) (responseItem.getPropertyValue(BBBCoreConstants.SOURCE_PARAM));
			if (isLoggingDebug()) {
				logDebug("CertonaTargeter.target(pNameResolver,pStartIndex,pMaxNumber): Source Param Map is : " + sourceParam);
			}
			final Iterator<String> itr = (Iterator<String>) sourceParam.keySet().iterator();

			// Iterate over the key set and set the parameter values in request
			while (itr.hasNext()) {
				final String sourceKey = (String) itr.next();
				final String sourceValue = (String) sourceParam.get(sourceKey);
				if (isLoggingDebug()) {
					logDebug("CertonaTargeter.target(pNameResolver,pStartIndex,pMaxNumber): Source Key is: " + sourceKey + " And Source Value is : " + sourceValue);
				}
				request.setParameter(sourceKey, sourceValue);

				if (sourceKey.equalsIgnoreCase(BBBCoreConstants.SCHEME)) {
					schemeName = sourceValue;
				}
			}
			try {
				if (isLoggingDebug()) {
					logDebug("CertonaTargeter.target(pNameResolver,pStartIndex,pMaxNumber): Getting the product list from Certona For Scheme : " + schemeName);
				}
				// Calling CertonaUtils to get the recommendations
				final CertonaResponseVO certonaResponseVO = getCertonaResponseUtils().getCertonaResponseVO( request, response);
				if (null != certonaResponseVO) {
				
					final List<ProductVO> productsVOsList = (List<ProductVO>) certonaResponseVO.getResonanceMap().get(schemeName).getProductsVOsList();
					if (!BBBUtility.isListEmpty(productsVOsList)) {
						if (isLoggingDebug()) {
							logDebug("CertonaTargeter.target(pNameResolver,pStartIndex,pMaxNumber): productsVOsList is : " + productsVOsList.toString());
						}
						productVOArray = productsVOsList.toArray(new ProductVO[productsVOsList.size()]);
					}
					
					
					//BBBSL-6574 | Setting requestUrl and reponseXml of Certona
					printCertonaWSCall(request, certonaResponseVO);
			
					if(BBBUtility.isEmpty(request.getParameter(BBBCoreConstants.PAGE_ID))){
						request.setParameter(BBBCoreConstants.PAGE_ID, certonaResponseVO.getPageId());
					}
					
					String resxLinks = certonaResponseVO.getResxLinks();					
					if(!BBBUtility.isEmpty(resxLinks)){
						int finalIndex =0;
						for(int count=0; count<9; count++){
							int index = resxLinks.indexOf(";", finalIndex+1);
							 if(index == -1 ){
								 break;
							 } else {
								 finalIndex = index;
							 }
						}
						resxLinks = resxLinks.substring(0, finalIndex+1);
					}					
					
					if(null != request.getParameter(BBBCoreConstants.LINK_CERTONA)){
						resxLinks =request.getParameter(BBBCoreConstants.LINK_CERTONA) + resxLinks;
					}
					request.setParameter(BBBCoreConstants.LINK_CERTONA, resxLinks);
				}
				else{
					request.setParameter(BBBCoreConstants.ERROR_MESSAGES, BBBCoreErrorConstants.ERROR_CERTONA_1001);
				}
			} catch (final BBBBusinessException bExcep) {
				this.logError(CLS_NAME + " BBBBusinessException =" + bExcep.getMessage());
				request.setParameter(BBBCoreConstants.ERROR_MESSAGES, bExcep.getMessage());
			} catch (final BBBSystemException bExcep) {
				this.logError(CLS_NAME + " BBBSystemException =" + bExcep.getMessage());
				request.setParameter(BBBCoreConstants.ERROR_MESSAGES, bExcep.getMessage());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				BBBPerformanceMonitor.end(CertonaTargeter.class.getName() + " : " + "target");
			}

		}
		if (isLoggingDebug()) {
			logDebug("Exit.CertonaTargeter.target(pNameResolver,pStartIndex,pMaxNumber)");
		}
		return productVOArray;

	}


	/**
	 * This method sets certonaresponseVO with requestURL and responseXML
	 * @param request
	 * @param certonaResponseVO
	 */
	private void printCertonaWSCall(final DynamoHttpServletRequest request,
			final CertonaResponseVO certonaResponseVO) {
		request.setParameter(BBBCoreConstants.CERTONA_REQUEST_URL,request.getParameter(BBBCoreConstants.CERTONA_REQUEST_URL));
		request.setParameter(BBBCoreConstants.CERTONA_RESPONSE_XML,request.getParameter(BBBCoreConstants.CERTONA_RESPONSE_XML));
	}


	/**
	 * @return the mCertonaUtils
	 */
	public CertonaResponseUtils getCertonaResponseUtils() {
		return mCertRespUtils;
	}

	/**
	 * @param mCertonaUtils
	 *            the mCertonaUtils to set
	 */
	public void setCertonaResponseUtils(final CertonaResponseUtils mCertRespUtils) {
		this.mCertRespUtils = mCertRespUtils;
	}

}