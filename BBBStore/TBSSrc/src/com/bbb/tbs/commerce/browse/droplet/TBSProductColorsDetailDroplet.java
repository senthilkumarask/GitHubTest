package com.bbb.tbs.commerce.browse.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.vo.CollectionProductVO;
import com.bbb.commerce.catalog.vo.ImageVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.tbs.catalog.bean.TBSSkuVO;
import com.bbb.utils.BBBUtility;

/**
 * The Class is used to fetch the Colors details from the Product's child skus.
 */
public class TBSProductColorsDetailDroplet extends BBBDynamoServlet {

	/** The Product manager. */
	private ProductManager mProductManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest,
	 * atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(DynamoHttpServletRequest pReq, DynamoHttpServletResponse pRes) throws ServletException, IOException {

		boolean isCollection = false;
		CollectionProductVO collectionProdVO = null;
		ProductVO prodVO = null;
		boolean calculateAboveBelowLine;
		List<SKUDetailVO> lSkuDetailVOs = new ArrayList<SKUDetailVO>();

		Map<String, TBSSkuVO> colorSkuSet = new TreeMap<String, TBSSkuVO>();

		isCollection = (Boolean) pReq.getObjectParameter("isCollection");

		Object lObjectParameter = pReq.getObjectParameter("productVO");

		if (lObjectParameter != null) {

			if (isCollection) {
				collectionProdVO = (CollectionProductVO) lObjectParameter;
			} else {
				prodVO = (ProductVO) lObjectParameter;
			}

		}

		String registryId = pReq.getParameter("registryId");
		String siteId = pReq.getParameter("siteId");

		if (!BBBUtility.isEmpty(registryId)) {
			calculateAboveBelowLine = true;
		} else {
			calculateAboveBelowLine = false;
		}

		try {
			if (collectionProdVO != null) {

				List<String> lChildSKUs = collectionProdVO.getChildSKUs();
				if (collectionProdVO.getLeadSKU()) {
					logDebug("product id : " + collectionProdVO.getProductId());
					if (lChildSKUs != null) {
						getChildSkuVos(lChildSKUs, siteId, calculateAboveBelowLine, lSkuDetailVOs);
					}
				} else {
					List<ProductVO> lChildProducts = collectionProdVO.getChildProducts();
					if (lChildProducts != null) {
						for (ProductVO lProductVO : lChildProducts) {
							getSkuVOs(siteId, lProductVO, calculateAboveBelowLine, lSkuDetailVOs);
						}
					}
				}

			} else if (prodVO != null) {
				getSkuVOs(siteId, prodVO, calculateAboveBelowLine, lSkuDetailVOs);
			}
			List<TBSSkuVO> lSkuVOs = populateSKUColors(lSkuDetailVOs);

			for (TBSSkuVO skuVO : lSkuVOs) {

				if (null != skuVO.getColor() && !colorSkuSet.containsKey(skuVO.getColor())) {
					colorSkuSet.put(skuVO.getColor(), skuVO);
				}
			}

		} catch (BBBSystemException e) {
			logError(e);
		} catch (BBBBusinessException e) {
			logError(e);
		}

		pReq.setParameter("colorSkuSet", colorSkuSet);
		pReq.serviceParameter("output", pReq, pRes);

	}

	/**
	 * Gets the sku vos.
	 * 
	 * @param pSiteId
	 *            the site id
	 * @param pProductVO
	 *            the product vo
	 * @param pCalculateAboveBelowLine
	 *            the calculate above below line
	 * @param pSkuDetailVOs
	 *            the sku detail v os
	 * @return the sku v os
	 * @throws BBBSystemException
	 *             the BBB system exception
	 * @throws BBBBusinessException
	 *             the BBB business exception
	 */
	protected void getSkuVOs(String pSiteId, ProductVO pProductVO, boolean pCalculateAboveBelowLine, List<SKUDetailVO> pSkuDetailVOs)
			throws BBBSystemException, BBBBusinessException {

		if (pProductVO != null) {
			List<String> lChildSKUs = pProductVO.getChildSKUs();
			logDebug("product id : " + pProductVO.getProductId());

			if (lChildSKUs != null) {
				getChildSkuVos(lChildSKUs, pSiteId, pCalculateAboveBelowLine, pSkuDetailVOs);
			}
		}

	}

	/**
	 * Gets the child sku vos.
	 * 
	 * @param pChildSKUs
	 *            the child sk us
	 * @param pSiteId
	 *            the site id
	 * @param pCalculateAboveBelowLine
	 *            the calculate above below line
	 * @param pSkuDetailVOs
	 *            the sku detail v os
	 * @return the child sku vos
	 * @throws BBBSystemException
	 *             the BBB system exception
	 * @throws BBBBusinessException
	 *             the BBB business exception
	 */
	protected void getChildSkuVos(List<String> pChildSKUs, String pSiteId, boolean pCalculateAboveBelowLine, List<SKUDetailVO> pSkuDetailVOs)
			throws BBBSystemException, BBBBusinessException {
		logDebug("TBSProductColorsDetailDroplet:getChildSkuVos:Start");
		for (String lString : pChildSKUs) {
			try {
				long starttime = Calendar.getInstance().getTimeInMillis();
				//Commented as part of TBXPS-2635 : Improve TBS collection page performance
		 		//SKUDetailVO lSkuDetails = getProductManager().getSKUDetails(pSiteId, lString, pCalculateAboveBelowLine);
 				SKUDetailVO lSkuDetails = getProductManager().getSkuImageColorDetails(lString);
				long endtime = Calendar.getInstance().getTimeInMillis();
				logDebug("sku id : " + lSkuDetails.getSkuId() + " time taken (ms) =" + (endtime-starttime));
				if (lSkuDetails != null) {
					logDebug("sku id : " + lSkuDetails.getSkuId());
					logDebug("sku color : " + lSkuDetails.getColor());
					pSkuDetailVOs.add(lSkuDetails);
				}
			} catch (BBBSystemException e) {
				logError("SystemException in TBSProductColorsDetailDroplet:getChildSkuVos" , e);
			} catch (BBBBusinessException e) {
				logError("BusinessException in TBSProductColorsDetailDroplet:getChildSkuVos" , e);
			}
		}

	}

	/**
	 * Populate sku colors.
	 * 
	 * @param pSkuDetailVOs
	 *            the sku detail v os
	 * @return the list
	 */
	protected List<TBSSkuVO> populateSKUColors(List<SKUDetailVO> pSkuDetailVOs) {

		List<TBSSkuVO> lSkuVOs = new ArrayList<TBSSkuVO>();
		TBSSkuVO skuVO = null;
		if(pSkuDetailVOs != null){
			for (SKUDetailVO lSkuDetailVO : pSkuDetailVOs) {
				skuVO = new TBSSkuVO();

				skuVO.setSkuID(lSkuDetailVO.getSkuId());

				skuVO.setColor(lSkuDetailVO.getColor());

				ImageVO lSkuImages = lSkuDetailVO.getSkuImages();
				skuVO.setSkuSwatchImageURL(lSkuImages.getSwatchImage());
				skuVO.setLargeImage(lSkuDetailVO.getSkuImages().getLargeImage());
				skuVO.setThumbnailImage(lSkuDetailVO.getSkuImages().getThumbnailImage());

				lSkuVOs.add(skuVO);
			}
		}

		return lSkuVOs;
	}

	/**
	 * Gets the product manager.
	 * 
	 * @return the product manager
	 */
	public ProductManager getProductManager() {
		return mProductManager;
	}

	/**
	 * Sets the product manager.
	 * 
	 * @param pProductManager
	 *            the new product manager
	 */
	public void setProductManager(ProductManager pProductManager) {
		mProductManager = pProductManager;
	}

}
