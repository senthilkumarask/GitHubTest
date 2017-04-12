/**
 * 
 */
package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.RollupTypeVO;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

/**
 * @author snaya2
 *
 */
public class RollUpListDroplet extends BBBDynamoServlet {
	
	/* ===================================================== *
		MEMBER VARIABLES
	 * ===================================================== */
		private ProductManager productManager;
	
	/* ===================================================== *
 		CONSTANTS
	 * ===================================================== */	
		public final static String  OPARAM_OUTPUT="output";
		public final static String  OPARAM_ERROR="error";	
		public final static String  COLOR_PARAM="COLOR";
		public final static String  SIZE_PARAM="SIZE";
		public final static String  FINISH_PARAM="FINISH";
		public final static String  JSON_COLOR_PARAM="prodColor";
		public final static String  JSON_SIZE_PARAM="prodSize";
		public final static String  JSON_FINISH_PARAM="prodFinish";
		public final static String  JSON_OBJECT="jsonObject";
		public final static String  ROLLUP_LIST="rollupList";
		
	/* ===================================================== *
 		GETTERS and SETTERS
	 * ===================================================== */
	

	public ProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	
	/**
	 * This method get the product id and site id from the jsp and pass these
	 * value to manager class and get the productVo from manager class
	 * 
	 * @param DynamoHttpServletRequest
	 *            , DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		/* ===================================================== *
		   MEMBER VARIABLES
	    * ===================================================== */
		String pSiteId;
		String pProductId;
		List<RollupTypeVO> rollUpList = null;
		String firstRollUpValue = null;
		String firstRollUpType = null;
		String secondRollUpType = null;
		String sizeJsonObject = null;
		ProductVO productVO = null;
		String rollupType[] = new String[2];

		
		try {

			/**
			 * Product id from the JSP page.
			 */
			pProductId = pRequest
					.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER);
			
			firstRollUpValue = pRequest
					.getParameter("firstRollUpValue");
			
			firstRollUpType = pRequest
					.getParameter("firstRollUpType");
			

			/**
			 * siteId from the JSP page.if site id is null then get it from the
			 * SiteContextManager
			 */
			
			pSiteId = pRequest.getParameter(BBBCmsConstants.SITE_ID);
			if (pSiteId == null) {
				pSiteId = getCurrentSiteId();
			}
			logDebug("pSiteId["+pSiteId+"]");
			logDebug("pProductId["+pProductId+"]");
			
			if(firstRollUpType.equals(JSON_COLOR_PARAM)){
				firstRollUpType = COLOR_PARAM;
			} else {
				if(firstRollUpType.equals(JSON_SIZE_PARAM)){
					firstRollUpType = SIZE_PARAM;
				} else {
					firstRollUpType = FINISH_PARAM;
				}
			}
			boolean everLivingProduct= false;
			everLivingProduct = getProductManager().getProductStatus(pSiteId, pProductId);
			if(everLivingProduct){
				productVO = getProductManager().getEverLivingProductDetails(pSiteId, pProductId);
			}else{
			productVO = getProductManager().getProductDetails(pSiteId, pProductId);
			}
			if((productVO.getRollupAttributes() != null) && (productVO.getRollupAttributes().size() > 1)){
					Set<String> keys = productVO.getRollupAttributes().keySet();
					
					for (Iterator iter = keys.iterator(); iter.hasNext();) {
						for(int count=0; count<2; count++){
							String next = (String) iter.next();
							rollupType[count] = next;
						}
					}
					
					if(firstRollUpType.equalsIgnoreCase(rollupType[0])){
						secondRollUpType = rollupType[1];
					} else {
						secondRollUpType = rollupType[0];
					}
			} 
			
			if((secondRollUpType != null) && (null!=pProductId)){
				if(everLivingProduct){
					rollUpList = getProductManager().getEverLivingRollupDetails(pProductId, firstRollUpValue, firstRollUpType, secondRollUpType);	
				}else{	
				rollUpList = getProductManager().getRollupDetails(pProductId, firstRollUpValue, firstRollUpType, secondRollUpType);
				}
				}
			
			
			if(null != rollUpList){
				logDebug("rollUpList["+rollUpList+"]");
				
				if(secondRollUpType.equalsIgnoreCase(SIZE_PARAM)){
					sizeJsonObject = SIZE_PARAM;
				} else {
					if(secondRollUpType.equalsIgnoreCase(COLOR_PARAM)){
					sizeJsonObject = COLOR_PARAM;
					} else {
						sizeJsonObject = FINISH_PARAM;
					}
				}
				pRequest.setParameter(JSON_OBJECT, sizeJsonObject);
				pRequest.setParameter(ROLLUP_LIST, rollUpList);
				
				pRequest.serviceParameter(OPARAM_OUTPUT, pRequest,
						pResponse);
							
			} else {
				pRequest.serviceParameter(OPARAM_ERROR, pRequest,
						pResponse);
			}
			
		}	catch (BBBBusinessException bbbbEx) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Business Exception  from service of RollUpListDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1034),bbbbEx);
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,
					pResponse);
		} catch (BBBSystemException bbbsEx) {
			logError(LogMessageFormatter.formatMessage(pRequest, "System Exception  from service of RollUpListDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1035),bbbsEx);
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,
					pResponse);
		}
		
	}

	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

}

