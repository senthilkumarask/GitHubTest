package com.bbb.commerce.giftregistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlObject;

import atg.core.util.StringUtils;

import com.bbb.commerce.giftregistry.vo.RegistryItemVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemsListVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.GetRegistryItemList2ResponseDocument;
import com.bedbathandbeyond.www.GetRegistryItemList2ResponseDocument.GetRegistryItemList2Response;
import com.bedbathandbeyond.www.Item2;

/**
 * 
 * @author asi162 Amandeep Singh Dhammu
 *
 */
public class GetRegistryItemList2ServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2669549708216180808L;

	@Override
	public ServiceResponseIF processResponse(XmlObject responseDocument)
			throws BBBSystemException, BBBBusinessException {
		
		
		
		
		RegistryItemsListVO itemsListVO = new RegistryItemsListVO();
		
		if(responseDocument !=null){
			
			GetRegistryItemList2Response getRegistryItemList2Response = ((GetRegistryItemList2ResponseDocument) responseDocument).getGetRegistryItemList2Response();
			
			
			if(getRegistryItemList2Response!=null){
				
				if(!getRegistryItemList2Response.getGetRegistryItemList2Result().getStatus().getErrorExists()){
					
					Item2[] items = null;
					if(getRegistryItemList2Response.getGetRegistryItemList2Result().getRegItems()!=null){
						
						
						items = getRegistryItemList2Response.getGetRegistryItemList2Result().getRegItems().getItem2Array();
					}
					
					List<RegistryItemVO> registryItemsListVO = new ArrayList<RegistryItemVO>();
					Map<String, RegistryItemVO> skuRegistryItemMap = new HashMap<String, RegistryItemVO>();
					
					RegistryItemVO listVO = null;
					
					if(items !=null){
						for(int i =0;i<items.length;i++){
							
							listVO = new RegistryItemVO();
							listVO.setJdaRetailPrice(items[i].getJDARetailPrice());
							if(items[i].getJDARetailPrice() > 0)
							listVO.setProductDescrip(items[i].getProductDescrip());									
							listVO.setGiftGiver(items[i].getGiftGiver());
							listVO.setQtyFulfilled(items[i].getQtyFulfilled());
							listVO.setQtyRequested(items[i].getQtyRequested());
							listVO.setQtyWebPurchased(items[i].getQtyWebPurchased());
							listVO.setQtyPurchased(items[i].getQtyPurchased());
							listVO.setRegistrantMode(items[i].getRegistrantMode()); 
							listVO.setRowID(items[i].getRowID());
							listVO.setSku(items[i].getSKU());
							listVO.setPromoDescURL(items[i].getPromoDescURL());
							listVO.setPromoDesc(items[i].getPromoDesc());
							listVO.setCustomizedPrice(items[i].getCustomizationPrice());
							listVO.setPersonlisedPrice(items[i].getPersonalizationPrice());
							listVO.setPersonalisedCode(items[i].getPersonalizationCode());
							listVO.setCustomizationDetails(items[i].getPersonalizationDescrip());
							listVO.setPersonalizedImageUrls(items[i].getPersonalizedImageUrl());
							listVO.setPersonalizedImageUrlThumbs(items[i].getPersonalizedImageUrlThumb());
							listVO.setPersonalizedMobImageUrls(items[i].getPersonalizedImageUrlThumb());
							listVO.setPersonalizedMobImageUrlThumbs(items[i].getPersonalizedImageUrlThumb());
							listVO.setRefNum(items[i].getReferenceID());
							listVO.setItemType(items[i].getItemType());
							listVO.setLtlDeliveryServices(items[i].getLtlDeliveryService());
							listVO.setAssemblySelected(items[i].getAssemblySelection());
							registryItemsListVO.add(listVO);
							if(!StringUtils.isEmpty(String.valueOf(items[i].getReferenceID()))){
							StringBuilder str = new StringBuilder(String.valueOf(items[i].getSKU()));
							str.append("_");
							String key= str.append(String.valueOf(items[i].getReferenceID())).toString();
							skuRegistryItemMap.put(key,listVO);							
							}
							else if(BBBCoreConstants.LTL.equalsIgnoreCase(items[i].getItemType()))
							{
								StringBuilder str = new StringBuilder(String.valueOf(items[i].getSKU()));
								str.append("_");
								String key= str.append(String.valueOf(items[i].getLtlDeliveryService())).toString();
								skuRegistryItemMap.put(key,listVO);	
							}
							else{
								skuRegistryItemMap.put(String.valueOf(items[i].getSKU()), listVO);								
							}
							
							
						}						
					}
					
					
					
					itemsListVO.setTotEntries(getRegistryItemList2Response.getGetRegistryItemList2Result().getTotEntries());
					
					itemsListVO.setRegistryItemList(registryItemsListVO);
					itemsListVO.setSkuRegItemVOMap(skuRegistryItemMap);
					
				}else{
					
					
					itemsListVO.getServiceErrorVO().setErrorExists(true);
					itemsListVO.getServiceErrorVO().setErrorId(getRegistryItemList2Response.getGetRegistryItemList2Result().getStatus().getID());
					itemsListVO.getServiceErrorVO().setErrorMessage(getRegistryItemList2Response.getGetRegistryItemList2Result().getStatus().getErrorMessage());
					itemsListVO.setWebServiceError(true);
				}
				
			}
			
		}
		
		return itemsListVO;
		
	
	}	
}
