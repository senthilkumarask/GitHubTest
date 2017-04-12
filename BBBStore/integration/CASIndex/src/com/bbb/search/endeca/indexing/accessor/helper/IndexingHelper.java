package com.bbb.search.endeca.indexing.accessor.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

public class IndexingHelper extends GenericService {
	
	BBBCatalogTools catalogTools;
	MutableRepository catalogRepository;
	
	public String getAttributeProperty(final RepositoryItem pItem){
		
		return getAttribute(pItem);
	}
	
    public String getAttributeJsonProperty(final RepositoryItem pItem){
		
		return getAttributeJson(pItem);
	}

	public String getChildProduct(final RepositoryItem pItem){
		
		return getChildProperty(pItem);
	}
	
	public String getChildProductDesc(final RepositoryItem pItem){
		
		return getChildDescProperty(pItem);
	}
	
	@SuppressWarnings("unchecked")
	public String getChildDescProperty(RepositoryItem pItem) {

		//JSONObject obj = new JSONObject();
		String value = null;
		List<RepositoryItem> childSKUS = null;
		
		if (pItem != null){
			childSKUS = (List<RepositoryItem>) pItem.getPropertyValue(BBBCatalogConstants.CHILD_SKUS);
			 if (isLoggingDebug()){logDebug("child SKUS********* \t:"+childSKUS);} //test log
			 
			 if(childSKUS != null){
				  for(RepositoryItem sku : childSKUS){
					  String skuId = sku.getRepositoryId();
			  if (isLoggingDebug()){logDebug("sku Id********* \t:"+skuId);} //test log
			  
			  try {
					RepositoryItem skuRepositoryItem = getCatalogRepository().getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
					
					if (isLoggingDebug()){logDebug("sku RepositoryItem********* \t:"+skuRepositoryItem);}
					
					if (skuRepositoryItem != null) {
						if(null != skuId){
							if (isLoggingDebug()){logDebug("Sku Id********* \t:"+skuId);}
							value=(skuId);
							//obj.put("SKU_ID", skuId);
						}
						if(null != skuRepositoryItem.getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME)){
							String color = (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME);
							if (isLoggingDebug()){logDebug("color********* \t:"+color);}
							value=value+color;
							//obj.put("COLOR", color);
						}
						if(null != skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SIZE_SKU_PROPERTY_NAME)){
							String size = (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SIZE_SKU_PROPERTY_NAME);
							if (isLoggingDebug()){logDebug("size********* \t:"+size);}
							value=value+size;
							//obj.put("SKU_SIZE", size);
						}
						if(null != skuRepositoryItem.getPropertyValue(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME)){
							String upc = (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME);
							if (isLoggingDebug()){logDebug("upc********* \t:"+upc);}
							value=value+upc;
							//obj.put("UPC", upc);
						}
						}
						
					//obj.put("SKU_ID:", pItem.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED));
					} catch (RepositoryException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
				}
			}
			}
						}
		return value;
			  
	}

	@SuppressWarnings("unchecked")
	private String getChildProperty(RepositoryItem pItem) {

		
		JSONObject obj = new JSONObject();
		List<RepositoryItem> childSKUS = null;
		
		if (pItem != null){
			childSKUS = (List<RepositoryItem>) pItem.getPropertyValue(BBBCatalogConstants.CHILD_SKUS);
			 if (isLoggingDebug()){logDebug("child SKUS********* \t:"+childSKUS);} //test log
			 
			 if(childSKUS != null){
				  for(RepositoryItem sku : childSKUS){
					  String skuId = sku.getRepositoryId();
			  if (isLoggingDebug()){logDebug("sku Id********* \t:"+skuId);} //test log
			  
			  try {
					RepositoryItem skuRepositoryItem = getCatalogRepository().getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
					
					if (isLoggingDebug()){logDebug("sku RepositoryItem********* \t:"+skuRepositoryItem);}
					
					if (skuRepositoryItem != null) {
						if(null != skuId){
							if (isLoggingDebug()){logDebug("Sku Id********* \t:"+skuId);}
							obj.put("SKU_ID", skuId);
						}
						if(null != skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME)){
							String swatchImage = (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME);
							if (isLoggingDebug()){logDebug("swatchImage********* \t:"+swatchImage);}
							obj.put("SWATCH_IMAGE_ID", swatchImage);
						}
						if(null != skuRepositoryItem.getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME)){
							String color = (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME);
							if (isLoggingDebug()){logDebug("color********* \t:"+color);}
							obj.put("COLOR", color);
						}
						if(null != skuRepositoryItem.getPropertyValue("colorGroup")){
							String colorGroup = (String) skuRepositoryItem.getPropertyValue("colorGroup");
							if (isLoggingDebug()){logDebug("color********* \t:"+colorGroup);}
							obj.put("COLOR_GROUP", colorGroup);
						}
						if(null != skuRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME)){
							String mediumImage = (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME);
							if (isLoggingDebug()){logDebug("mediumImage********* \t:"+mediumImage);}
							obj.put("MEDIUM_IMAGE_ID", mediumImage);
						}
						if(null != skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SIZE_SKU_PROPERTY_NAME)){
							String size = (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SIZE_SKU_PROPERTY_NAME);
							if (isLoggingDebug()){logDebug("size********* \t:"+size);}
							obj.put("SKU_SIZE", size);
						}
						if(null != skuRepositoryItem.getPropertyValue(BBBCatalogConstants.LTL_ITEM_ASSEMBLY_TIME)){
							String assemblyTime = (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.LTL_ITEM_ASSEMBLY_TIME);
							if (isLoggingDebug()){logDebug("assemblyTime********* \t:"+assemblyTime);}
							obj.put("ASSEMBLY_TIME", assemblyTime);
						}
						if(null != skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED)){
							Boolean isAssemblyOffered = (Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED);
							if (isLoggingDebug()){logDebug("isAssemblyOffered********* \t:"+isAssemblyOffered);}
							obj.put("IS_ASSEMBLY_OFFERED", isAssemblyOffered);
						}
						if(null != skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SKU_WEIGHT)){
							String caseweight = (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SKU_WEIGHT);
							if (isLoggingDebug()){logDebug("caseweight********* \t:"+caseweight);}
							obj.put("CASE_WEIGHT", caseweight);
						}
						if(null != skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ORDER_TO_SHIP_SLA)){
							String orderToShipSla = (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ORDER_TO_SHIP_SLA);
							if (isLoggingDebug()){logDebug("orderToShipSla********* \t:"+orderToShipSla);}
							obj.put("ORDER_TO_SHIP_SLA", orderToShipSla);
						}
						if(null != skuRepositoryItem.getPropertyValue(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME)){
							String upc = (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME);
							if (isLoggingDebug()){logDebug("upc********* \t:"+upc);}
							obj.put("UPC", upc);
						}
						}
						
					//obj.put("SKU_ID:", pItem.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED));
					} catch (RepositoryException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
				} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 

			}
			}
						}
		return obj.toString();
	}
	
	
	public String getAttributeJson(RepositoryItem pItem) {

		String skuAttrRelnId = null;
		RepositoryItem[] attributeInfoItems = null;
		//String displayDescription = null;
		JSONObject obj = new JSONObject();
		
		if (pItem != null) {
			@SuppressWarnings("unchecked")
			Set<RepositoryItem> skuAttrRelnIdsSet = (Set<RepositoryItem>) pItem
					.getPropertyValue("skuAttributeRelns");
			if (skuAttrRelnIdsSet != null) {
				for (RepositoryItem skuAttrRelnIds : skuAttrRelnIdsSet) {
					skuAttrRelnId = (String) skuAttrRelnIds.getRepositoryId();
					int index = skuAttrRelnId
							.indexOf(BBBCoreConstants.UNDERSCORE);
					String attributeId = skuAttrRelnId.substring(index + 1);

					if (isLoggingDebug()) {
						logDebug("attributeId (Expecting product repository item)\t:"
								+ attributeId);
					}
					try {
						if (attributeId != null) {
							attributeInfoItems = getCatalogTools()
									.getAttributeInfoRepositoryItems(
											attributeId);
						}
						if (attributeInfoItems != null
								&& attributeInfoItems.length > 0) {
							for (RepositoryItem item : attributeInfoItems) {
								if (item != null) {
									if (null != item
											.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME)) {
										String displayDescription = String
												.valueOf(item
														.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME));
										obj.put("DISPLAY_DESCRIP", displayDescription);
										if (isLoggingDebug()) {logDebug("displayDescription\t:"+ displayDescription);}
										if (isLoggingDebug()) {logDebug("Object\t:"+ obj);}
									}
									if (null != item
											.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME)) {
										String imageURL = String
												.valueOf(item
														.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME));
										obj.put("IMAGE_URL", imageURL);
										if (isLoggingDebug()) {logDebug("displayDescription\t:"+ imageURL);}
										if (isLoggingDebug()) {logDebug("Object\t:"+ obj);}
									}
									if (null != item
											.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME)) {
										String actionURL = String
												.valueOf(item
														.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME));
										obj.put("ACTION_URL", actionURL);
										if (isLoggingDebug()) {logDebug("displayDescription\t:"+ actionURL);}
										if (isLoggingDebug()) {logDebug("Object\t:"+ obj);}
									}
									if (null != item
											.getPropertyValue(BBBCatalogConstants.PLACE_HOLDER_ATTRIBUTE_PROPERTY_NAME)) {
										String placeHolder = String
												.valueOf(item
														.getPropertyValue(BBBCatalogConstants.PLACE_HOLDER_ATTRIBUTE_PROPERTY_NAME));
										obj.put("PLACE_HOLDER", placeHolder);
										if (isLoggingDebug()) {logDebug("displayDescription\t:"+ placeHolder);}
										if (isLoggingDebug()) {logDebug("Object\t:"+ obj);}
									}
									if (null != item
											.getPropertyValue(BBBCatalogConstants.PRIORITY_ATTRIBUTE_PROPERTY_NAME)) {
										String priority = String
												.valueOf(item
														.getPropertyValue(BBBCatalogConstants.PRIORITY_ATTRIBUTE_PROPERTY_NAME));
										obj.put("PRIORITY", priority);
										if (isLoggingDebug()) {logDebug("displayDescription\t:"+ priority);}
										if (isLoggingDebug()) {logDebug("Object\t:"+ obj);}
									}
									if (null != item
											.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_FLAG)) {
										String intlFlag = String
												.valueOf(item
														.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_FLAG));
										obj.put("PRIORITY", intlFlag);
										if (isLoggingDebug()) {logDebug("displayDescription\t:"+ intlFlag);}
										if (isLoggingDebug()) {logDebug("Object\t:"+ obj);}
									}
								}
							}
						}
					} catch (BBBBusinessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BBBSystemException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			// return attributeId;
		}
		return obj.toString();
    }

	
	public String getAttribute(final RepositoryItem pItem) {
		
		final String MM_DD_YYYY = "MM/dd/yyyy";
		String skuAttrRelnId = null;
		RepositoryItem[] attributeInfoItems = null;
		String displayDescription = null;

		if (pItem != null) {
			@SuppressWarnings("unchecked")
			Set<RepositoryItem> skuAttrRelnIdsSet = (Set<RepositoryItem>) pItem
					.getPropertyValue("skuAttributeRelns");
			if (skuAttrRelnIdsSet != null) {	
				for (RepositoryItem skuAttrRelnIds : skuAttrRelnIdsSet) {
					skuAttrRelnId = (String) skuAttrRelnIds.getRepositoryId();
					final SimpleDateFormat dateFormat = new SimpleDateFormat(
							MM_DD_YYYY);
					try {
					final Date previewDate = dateFormat
								.parse(dateFormat.format(new Date()));
					//if (isLoggingDebug()) {logDebug("previewDate :****************"+ previewDate);}
					final Date startDateOfSku = (Date) skuAttrRelnIds
							.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_RELATION_PROPERTY_NAME);
					//if (isLoggingDebug()) {logDebug("startDateOfSku :**************"+ startDateOfSku);}
					final Date endDateOfSku = (Date) skuAttrRelnIds
							.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_RELATION_PROPERTY_NAME);
					//if (isLoggingDebug()) {logDebug("endDateOfSku :*****************"+ endDateOfSku);}
					int index = skuAttrRelnId
							.indexOf(BBBCoreConstants.UNDERSCORE);
					String attributeId = skuAttrRelnId.substring(index + 1);
					//if (isLoggingDebug()) {logDebug("attributeId *******:"+ attributeId);}
						if (attributeId != null) {
							attributeInfoItems = getCatalogTools()
									.getAttributeInfoRepositoryItems(
											attributeId);
						}
						if (attributeInfoItems != null
								&& attributeInfoItems.length > 0) {
							for (RepositoryItem item : attributeInfoItems) {
								if ((item != null)
										&& (item.getPropertyValue(BBBCatalogConstants.PLACE_HOLDER_ATTRIBUTE_PROPERTY_NAME) != null)
										&& (BBBUtility.isAttributeApplicable(
												previewDate, startDateOfSku,
												endDateOfSku))) {
									AttributeVO attributeVO = new AttributeVO();
									String attributeName = item
											.getRepositoryId();
									attributeVO.setAttributeName(attributeName);
									if (null != item
											.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME)) {
										displayDescription = String
												.valueOf(item
														.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME));
										// attributeVO.setAttributeDescrip(displayDescription);
										if (isLoggingDebug()) {
											logDebug("displayDescription*********:"
													+ displayDescription);
										}
									}
								}
							}
						}
					} catch (BBBBusinessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BBBSystemException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
		return displayDescription;
	}
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.catalogTools = pCatalogTools;
	}
	/**
	 * @return the catalogRepository
	 */
	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	/**
	 * @param pCatalogRepository the catalogRepository to set
	 */
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}
}
