/*
 *
 * File  : GetRegistryInfoServiceMarshaller.java
 * Project:     BBB
 * 
 */
package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;

import atg.core.util.StringUtils;

import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.GetRegistryItemListDocument;
import com.bedbathandbeyond.www.RegistryItemsSortType;
import com.bedbathandbeyond.www.RegistryItemsView;
import com.bedbathandbeyond.www.RegistrySortType;
import com.bedbathandbeyond.www.GetRegistryItemListDocument.GetRegistryItemList;

/**
 * 
 * This class contain methods used for marshalling the Get registry item list webservice request.
 * 
 * @author skalr2
 * 
 */
public class GetRegistryItemListServiceMarshaller extends RequestMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a request.
	 * 
	 *  * @param ServiceRequestIF
	 *            the validate address pRreqVO vo
	 */
	public XmlObject buildRequest(ServiceRequestIF pRreqVO)
			throws BBBSystemException {

		logDebug("GetRegistryItemListServiceMarshaller.buildRequest() method start");
		
		BBBPerformanceMonitor
				.start("GetRegistryItemListServiceMarshaller-buildRequest");

		
		GetRegistryItemListDocument getRegistryItemListDocument = null;
		try {
			getRegistryItemListDocument = GetRegistryItemListDocument.Factory.newInstance();
			getRegistryItemListDocument.setGetRegistryItemList(mapGetRegistryItemListRequest(pRreqVO));
		} catch (Exception e) {

			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1373,e.getMessage(), e);
		}

		BBBPerformanceMonitor
				.end("GetRegistryItemListServiceMarshaller-buildRequest");
		
		logDebug("GetRegistryItemListServiceMarshaller.buildRequest() method ends");

		return getRegistryItemListDocument;
	}

	/**
	 * This method is used to map a page specific VO to web service VO.
	 * 
	 * @param ServiceRequestIF
	 *            the registry pReqVOs vo
	 * 
	 * @return the get registry item list.
	 * @throws BBBSystemException 
	 * 
	 */
	private GetRegistryItemList mapGetRegistryItemListRequest(
			ServiceRequestIF pRreqVO) throws BBBSystemException {

		logDebug("GetRegistryItemListServiceMarshaller.mapGetRegistryItemListRequest() method start");
		
		BBBPerformanceMonitor
				.start("GetRegistryItemListServiceMarshaller-mapGetRegistryItemListRequest");
		
		GetRegistryItemList getRegistryItemList = GetRegistryItemList.Factory.newInstance();
		try {
		//DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		getRegistryItemList.setBlkSize((((RegistrySearchVO)pRreqVO).getBlkSize()));
		getRegistryItemList.setIsGiftGiver(((RegistrySearchVO)pRreqVO).getGiftGiver());
		getRegistryItemList.setStartIdx(((RegistrySearchVO)pRreqVO).getStartIdx());
		getRegistryItemList.setUserToken(((RegistrySearchVO)pRreqVO).getUserToken());
		getRegistryItemList.setSiteFlag(((RegistrySearchVO)pRreqVO).getSiteId());
		getRegistryItemList.setRegistryNum(((RegistrySearchVO)pRreqVO).getRegistryId());
		getRegistryItemList.setIsAvailForWebPurchaseFlag(((RegistrySearchVO)pRreqVO).getAvailForWebPurchaseFlag());
		int view=1;
		if (((RegistrySearchVO)pRreqVO).getView()>0)
		{
			view=((RegistrySearchVO)pRreqVO).getView();
		}
		String sortSeq="DEPARTMENT";
		if (!StringUtils.isEmpty(((RegistrySearchVO)pRreqVO).getSortSeq()))
		{
			sortSeq=((RegistrySearchVO)pRreqVO).getSortSeq();
		}
		
		getRegistryItemList.setView(RegistryItemsView.Enum.forInt(view));
		getRegistryItemList.setSortSeq(RegistryItemsSortType.Enum.forString(sortSeq));
		}catch(Exception me) {
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10150 +" Exception from mapGetRegistryItemListRequest from GetRegistryItemListServiceMarshaller",me);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1373,me.getMessage(), me);
			
		}finally{
			BBBPerformanceMonitor
					.end("GetRegistryItemListServiceMarshaller-mapGetRegistryItemListRequest");
		}
		
		
		BBBPerformanceMonitor
				.end("GetRegistryItemListServiceMarshaller-mapGetRegistryItemListRequest");
		
		logDebug("GetRegistryItemListServiceMarshaller-mapGetRegistryItemListRequest() method ends");

		return getRegistryItemList;
	}

}