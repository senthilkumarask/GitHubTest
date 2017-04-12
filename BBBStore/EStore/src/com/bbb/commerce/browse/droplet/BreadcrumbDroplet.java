package com.bbb.commerce.browse.droplet;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * Droplet to access breadcrumb related to Product details page.
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 * Reproduction or use of this file without explicit written consent is prohibited.
 * Created by: snaya2
 * Created on: January-2012
 * @author snaya2
 *
 */
public class BreadcrumbDroplet extends BBBDynamoServlet {
	
	public final static String  OPARAM_BREADCRUMB="breadCrumb";
	public final static String  ISORPHANPRODUCT = "isOrphanProduct";
	public final static String  OPARAM_OUTPUT="output";
	public final static String  OPARAM_CIRCULAR="output_circular";
	public final static String  OPARAM_ERROR="error";
	public final static String  PARAMETER_CATEGORYID ="categoryId";
	public final static String  PARAMETER_PRODUCTID ="productId";
	public final static String  PARAMETER_POC ="poc";
	public final static String  PARAMETER_OMNITURE ="forOmniture";
	public final static String  PARAMETER_SITEID ="siteId";
	public final static String  PARAMETER_RFXPAGE ="rfx_page=";
	public final static String  PARAMETER_RFXPASSBACK ="&rfx_passback=";
	private static final String CLS_NAME = "CLS=[BreadcrumbDroplet]/MSG::";
	
	/* ===================================================== *
		MEMBER VARIABLES
	 * ===================================================== */
	private ProductManager productManager;
	/* ===================================================== *
		GETTERS and SETTERS
 * ===================================================== */
	private BBBCatalogTools bbbCatalogTools;
	private MutableRepository catalogRepository;
	
	public BBBCatalogTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	
	
	/**
	 * This method get the Product id or Category id from the jsp and pass these
	 * value to manager class and get the Map<String, CategoryVO> from manager class
	 * 
	 * @param DynamoHttpServletRequest
	 *            , DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	public void service(final DynamoHttpServletRequest request,
			final DynamoHttpServletResponse response) throws ServletException, IOException {
	    BBBPerformanceMonitor.start("BreadcrumbDroplet", "service");
		/**
		 * Category id from the JSP page.
		 */
		final String categoryId =request.getParameter(PARAMETER_CATEGORYID);
		/**
		 * Product id from the JSP page.
		 */
		final String productId =request.getParameter(PARAMETER_PRODUCTID);
		final String poc =request.getParameter(PARAMETER_POC);
		final String siteId = request.getParameter(PARAMETER_SITEID);
		final String forOmniture = request.getParameter(PARAMETER_OMNITURE);
		Boolean bts=false;
		final String rfxPage = request.getQueryParameter("rfx_page");
		final String rfxPassback = request.getQueryParameter("rfx_passback");
		Map<String, CategoryVO> breadCrumb = null;
		
		/**
		 * added variables as part of release 2.1 scope#29.
		 */
		Boolean isPrimaryCat =false;
		String primaryCategory = null;
		boolean everLivingProduct= false;
		boolean isOrphanProduct=false;
		Boolean catdisable = true;
		
		logDebug("Entering BreadcrumbDroplet.service()");
		logDebug("Site ID = "+siteId);
		
		try {
			if(BBBUtility.isEmpty(categoryId))
			{
				 //  added this condition as part of release 2.1 scope#29.				 
				primaryCategory = productManager.getBbbCatalogTools().getPrimaryCategory(productId);
				if(BBBUtility.isNotEmpty(primaryCategory))
				{
					final RepositoryItem catRepo = this.getCatalogRepository().getItem(
	                        primaryCategory, BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
					catdisable = (Boolean)catRepo.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME);
					
				}
				
				if (catdisable !=null && !catdisable) {
					breadCrumb = productManager.getParentCategory(primaryCategory,siteId);					
					isPrimaryCat = true;
				}
				else
				{
					everLivingProduct = getProductManager().getProductStatus(siteId, productId);
					if(!everLivingProduct){
						breadCrumb = productManager.getParentCategoryForProduct(productId, siteId);
						if(BBBUtility.isMapNullOrEmpty(breadCrumb) && BBBUtility.isNotEmpty(poc))
						{
							breadCrumb = productManager.getParentCategoryForProduct(poc, siteId);
						}
					}else{
						RepositoryItem productRepositoryItem = getCatalogRepository().getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
						if (productRepositoryItem != null) {
		                    this.logDebug("productRepositoryItem is not null for product id " + productId);
		                    @SuppressWarnings ("unchecked")
		                   final Set<RepositoryItem> parentCategorySet = (Set<RepositoryItem>) productRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME);
					                    RepositoryItem categoryRepositoryItem = getBbbCatalogTools().getCategoryForSite(parentCategorySet, siteId);
					                    if (categoryRepositoryItem != null) {
					                    	breadCrumb = productManager.getParentCategoryForProduct(productId, siteId);	
					                    }else{
					                    	isOrphanProduct =true;
					                    }
		                    }
					
					}
				}
			}
			else {				
				final RepositoryItem catRepo = this.getCatalogRepository().getItem(
						categoryId, BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
				if(null!=catRepo){
					catdisable = (Boolean)catRepo.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME);
					if(catdisable !=null && !catdisable) {
						breadCrumb = productManager.getParentCategory(categoryId,siteId);
					} else {
						breadCrumb = productManager.getParentCategoryForProduct(productId, siteId);
					}
				} else{
					throw new BBBBusinessException(BBBCatalogErrorCodes.CATEGORY_NOT_AVAILABLE_IN_REPOSITORY,
                            BBBCatalogErrorCodes.CATEGORY_NOT_AVAILABLE_IN_REPOSITORY);
			}
			}
			if(null!=rfxPage && null!=rfxPassback){
				final String circularLink = PARAMETER_RFXPAGE+rfxPage+PARAMETER_RFXPASSBACK+rfxPassback;
				request.setParameter(OPARAM_CIRCULAR, circularLink);
			}

			final String channel = BBBUtility.getChannel();
            if (!((BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(channel) || BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(channel)) && forOmniture != null && forOmniture.equalsIgnoreCase(BBBCoreConstants.TRUE))){ 
            
				// check if breadcrumb is phantom category then hide it.
				if (null != breadCrumb && !breadCrumb.isEmpty()) {
					Integer breadcrumbKey = breadCrumb.size() - 1;
					CategoryVO categoryVO = breadCrumb.get(breadcrumbKey.toString());
					if (null != categoryVO && null != categoryVO.getPhantomCategory() && categoryVO.getPhantomCategory()) {
						breadCrumb.remove(breadcrumbKey.toString());
					}
				}
            }
            
            if (null != breadCrumb && !breadCrumb.isEmpty()) {
				Collection<CategoryVO> category = breadCrumb.values();
				for(CategoryVO categoryVO:category){
					if((null != categoryVO.getIsCollege()) && categoryVO.getIsCollege()){
						bts=true;
						break;
					}
				}
            }
			/*Iterator<Map.Entry<String, CategoryVO>> it = breadCrumb.entrySet().iterator();
			Map.Entry<String, CategoryVO> entry;
			while (it.hasNext()) {
				entry = it.next();
				CategoryVO categoryVO = breadCrumb.get(entry.getKey());
				if((null != categoryVO.getPhantomCategory()) && categoryVO.getPhantomCategory()){
					it.remove();
				}
			}*/
			
			logDebug("BreadCrumb = "+breadCrumb);
			
			
		} catch (BBBBusinessException bbbbEx) {
			logError(LogMessageFormatter.formatMessage(request, "Business Exception from service of BreadcrumbDroplet for productId=" +productId +" |SiteId="+siteId,BBBCoreErrorConstants.BROWSE_ERROR_1022)+":"+bbbbEx.getMessage());
			request.serviceParameter(OPARAM_ERROR, request,
					response);
		} catch (BBBSystemException bbbsEx) {
			logError(LogMessageFormatter.formatMessage(request, "System Exception from service of BreadcrumbDroplet for productId=" +productId +" |SiteId="+siteId,BBBCoreErrorConstants.BROWSE_ERROR_1023)+":"+bbbsEx.getMessage());
			request.serviceParameter(OPARAM_ERROR, request,
					response);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			logError(e.getMessage(),e);
		}	
		request.setParameter("bts", bts);
		request.setParameter("isPrimaryCat", isPrimaryCat);
		request.setParameter("primaryCategory", primaryCategory);
		request.setParameter(OPARAM_BREADCRUMB, breadCrumb);
		request.setParameter(ISORPHANPRODUCT, isOrphanProduct);
		request.serviceParameter(OPARAM_OUTPUT, request,
				response);
		BBBPerformanceMonitor.end("BreadcrumbDroplet", "service");
	}	
	
	/**
	 * @return the productManager
	 */
	public ProductManager getProductManager() {
		return productManager;
	}

	/**
	 * @param productManager the productManager to set
	 */
	public void setProductManager(final ProductManager productManager) {
		this.productManager = productManager;
	}
	
	
	@SuppressWarnings("unchecked")
	public Map<String, CategoryVO> getProductBreadCrumb(String prodId, String catId, String forOmniture) throws BBBSystemException, BBBBusinessException
	{
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
		Map<String, CategoryVO> breadCrumb = null;
		if(prodId != null || catId != null)
		{
			request.setParameter(PARAMETER_CATEGORYID, catId);
			request.setParameter(PARAMETER_PRODUCTID, prodId);
			request.setParameter(PARAMETER_OMNITURE, forOmniture);
			request.setParameter(PARAMETER_SITEID,SiteContextManager.getCurrentSiteId());
			try {
				this.service(request, response);
				breadCrumb = (Map<String, CategoryVO>)request.getObjectParameter(OPARAM_BREADCRUMB);
				
			} catch (ServletException e) {
				logError("Method: BreadcrumbDroplet.getProductBreadCrumb, Servlet Exception while getting product bread crumb " + e);
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_PROD_BREADCRUMB, "Some error occurred while getting product bread crumb");
			} catch (IOException e) {
				logError("Method: BreadcrumbDroplet.getProductBreadCrumb, IO Exception while getting product bread crumb " + e);
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_PROD_BREADCRUMB, "Some error occurred while getting product bread crumb");
			}
		}
		else{
			logDebug("Method: BreadcrumbDroplet.getProductBreadCrumb, input parameter is null");
			throw new BBBBusinessException(BBBCoreErrorConstants.ERROR_PROD_BREADCRUMB_INPUT_NULL, "input parameter is null");
		}
		
		return breadCrumb;
	}
	
	/**
	 * added as part of release 2.1 scope#29.
	 */	
	/**
	 * This Method Fetches the breadcrumb 
	 * @param inputParam
	 * @throws BBBSystemException
	 * @throws BBBBusinessException 
	 */
	public Map<String, CategoryVO> getBreadcrumbDetails(Map <String,String> inputParam) throws BBBSystemException, BBBBusinessException {
		
		logDebug( CLS_NAME +"entering getBreadcrumbDetails method");
		
		if (null == inputParam || inputParam.isEmpty()) {
			throw new BBBSystemException("err_invalid_input", "Invalid Inputs");
		}
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse= ServletUtil.getCurrentResponse();		
//		pRequest.setParameter(SITE_ID,SiteContextManager.getCurrentSiteId());
		pRequest.setParameter(PARAMETER_CATEGORYID,inputParam.get(PARAMETER_CATEGORYID));
		pRequest.setParameter(PARAMETER_PRODUCTID,inputParam.get(PARAMETER_PRODUCTID)); 
		pRequest.setParameter(PARAMETER_SITEID,inputParam.get(PARAMETER_SITEID));
		pRequest.setParameter("rfx_page",inputParam.get("rfx_page"));
		pRequest.setParameter("rfx_passback",inputParam.get("rfx_passback"));
		
			Enumeration<String> paramsEnum = pRequest.getParameterNames();
			while (paramsEnum.hasMoreElements()) {
				String paramName = paramsEnum.nextElement();
				String paramValue = (String)pRequest.getParameter(paramName);
				logDebug("Param Name = " + paramName + "param value =" + paramValue);	
			}
		
		try {
			service(pRequest, pResponse);
			String error = pRequest.getParameter("errorMsg");
			if (error != null){
				logError("Certona Serivce throws Business Exception" + error);
				throw new BBBBusinessException(error);
			}
			Map<String, CategoryVO> breadCrumb = (Map<String, CategoryVO>) pRequest.getObjectParameter("breadCrumb");
			
			if(breadCrumb == null) {
				throw new BBBBusinessException("err_null_response_breadcrumb_details", "Received null response from breadcrumb"); 
			} else {
				return breadCrumb;
			}
		} catch (ServletException e) {
			 throw new BBBSystemException("err_servlet_exception_breadcrumb_details", "ServletException in Breadcrumb Droplet");
		} catch (IOException e) {
			 throw new BBBSystemException("err_io_exception_breadcrumb_details", "IO Exception in in Breadcrumb Droplet");
		} finally {
			
				logDebug( CLS_NAME +" getBreadcrumbDetails method ends");
			
		}
		
	}
}