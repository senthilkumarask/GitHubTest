/*
 *
 * File  : GetRegistryInfoServiceUnMarshaller.java
 * Project:     BBB
 */
package com.bbb.commerce.giftregistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlObject;

import com.bbb.commerce.giftregistry.vo.RegistryItemVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemsListVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.GetRegistryItemListResponseDocument;
import com.bedbathandbeyond.www.GetRegistryItemListResponseDocument.GetRegistryItemListResponse;
import com.bedbathandbeyond.www.Item;


/**
 * This class contain methods used for unmarshalling the get registry info webservice response.
 * 
 * @author ssha53
 * 
 */
public class GetRegistryItemListServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {

		logDebug("GetRegistryItemListServiceUnMarshaller.processResponse() method start");

		BBBPerformanceMonitor.start("GetRegistryItemListServiceUnMarshaller-processResponse");

		final RegistryItemsListVO regItemsListVO = new RegistryItemsListVO();
		
		if (responseDocument != null) {

			final GetRegistryItemListResponse regItemListRes = ((GetRegistryItemListResponseDocument) 
					responseDocument).getGetRegistryItemListResponse();
			try {
				if (regItemListRes != null) {
					if (!regItemListRes.getGetRegistryItemListResult().getStatus().getErrorExists()) {
						//operation success
						//registryResVO.setOperationStatus(true);

						regItemListRes.getGetRegistryItemListResult().getRegItems();
						Item[] regItems=null;
						if(null!=regItemListRes.getGetRegistryItemListResult().getRegItems())
						{
							regItems = regItemListRes.getGetRegistryItemListResult().getRegItems().getItemArray();
						}

						final List<RegistryItemVO> registryItemsList  = new ArrayList<RegistryItemVO>();
						// Map to hold Sku specific Registry Item VO as Map<SkuId,RegistryItemVO>
						final Map<String,RegistryItemVO> skuRegItemVOMap = new HashMap<String, RegistryItemVO>();
						
						RegistryItemVO registryItemVO = null;
						
						if(regItems != null ){
							
							for(int index=0; index<regItems.length; index++){
								
								registryItemVO = new RegistryItemVO();
								
								// we will no more use these commented fields from webservice 
								
								//registryItemVO.setColorCD( regItems[index].getColorCD() );
								//registryItemVO.setColorDesc(regItems[index].getColorDesc());
								//registryItemVO.setDeptName( regItems[index].getDeptName() );
								registryItemVO.setJdaRetailPrice(regItems[index].getJDARetailPrice());
								//registryItemVO.setJdaDescription(regItems[index].getJDADescrip());
								//registryItemVO.setJdaDeptId(regItems[index].getJdaDeptId());
								//registryItemVO.setPrice(regItems[index].getPrice().substring(1));
								if(regItems[index].getJDARetailPrice() > 0)
									registryItemVO.setProductDescrip(regItems[index].getProductDescrip());
								//registryItemVO.setImageURL(regItems[index].getImageURL());
								//registryItemVO.setProductURL(regItems[index].getProductURL());
								//registryItemVO.setDeptSortSeq(regItems[index].getDeptSortSeq());
								//registryItemVO.setSmallDesc(regItems[index].getSmallDesc());
								//registryItemVO.setUpc(regItems[index].getUPC());
								
								registryItemVO.setGiftGiver(regItems[index].getGiftGiver());
								registryItemVO.setQtyFulfilled(regItems[index].getQtyFulfilled());
								registryItemVO.setQtyRequested(regItems[index].getQtyRequested());
								registryItemVO.setQtyWebPurchased(regItems[index].getQtyWebPurchased());
								registryItemVO.setQtyPurchased(regItems[index].getQtyPurchased());
								registryItemVO.setRegistrantMode(regItems[index].getRegistrantMode());
								registryItemVO.setRowID(regItems[index].getRowID());
								registryItemVO.setSku(regItems[index].getSKU());
								//registryItemVO.setsKUDetailVO();
								// Taking the price string of form of ($XX.XX) into (XX.XX) to be used later.
								registryItemVO.setPromoDescURL(regItems[index].getPromoDescURL());
								registryItemVO.setPromoDesc(regItems[index].getPromoDesc());
								
								registryItemsList.add(registryItemVO);
								// Setting Map<Skuid,RegistryItemVO>
								skuRegItemVOMap.put(String.valueOf(regItems[index].getSKU()), registryItemVO);
							}
						}
						regItemsListVO.setTotEntries(regItemListRes.
								getGetRegistryItemListResult().getTotEntries());
						//registryItemsListVO.setSortReponse(registryItemListResponse.getGetRegistryItemListResult().getSortSequ);
						//default sort seq - category
						//registryItemsListVO.setSortReponse("1");
						regItemsListVO.setRegistryItemList(registryItemsList);
						
						// Set the Map to hold Sku specific Registry Item VO.
						regItemsListVO.setSkuRegItemVOMap(skuRegItemVOMap);
					} else {

						regItemsListVO.getServiceErrorVO()
								.setErrorExists(true);
						regItemsListVO.getServiceErrorVO().setErrorId(
								regItemListRes.getGetRegistryItemListResult().getStatus().getID());
						regItemsListVO.getServiceErrorVO()
								.setErrorMessage(regItemListRes
												.getGetRegistryItemListResult().getStatus().getErrorMessage());
						regItemsListVO.setWebServiceError(true);
					}
				}
			} catch (Exception e) {
				BBBPerformanceMonitor.end("GetRegistryInfoServiceUnMarshaller-processResponse");
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1374,e.getMessage(), e);
			}			

			BBBPerformanceMonitor.end("GetRegistryInfoServiceUnMarshaller-processResponse");

			logDebug("GetRegistryInfoServiceUnMarshaller.processResponse() method ends");
		}
		return regItemsListVO;
	}
}
