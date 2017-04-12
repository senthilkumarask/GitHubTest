package com.bbb.kirsch.processor;

import java.util.List;

import noNamespace.GetItemsFromWorkBookResponseDocument;
import noNamespace.GetItemsFromWorkBookResponseDocument.GetItemsFromWorkBookResponse;
import noNamespace.GetItemsFromWorkBookResponseDocument.GetItemsFromWorkBookResponse.LineItems;
import noNamespace.GetItemsFromWorkBookResponseDocument.GetItemsFromWorkBookResponse.LineItems.ConfigurationModel;
import atg.core.util.StringUtils;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;

import com.bbb.framework.httpquery.parser.ResultParserIF;
import com.bbb.framework.httpquery.vo.HTTPServiceResponseIF;
import com.bbb.kirsch.vo.ItemsFromWorkBookRespVO;
import com.bbb.kirsch.vo.LineItem;

/**
 * This is class is used to parse the response from the Kirsch service and populate the response into VO objects.
 */
public class GetItemsFromWorkbookResponseProcessor implements ResultParserIF {
	
	ApplicationLogging mLogging =
		    ClassLoggingFactory.getFactory().getLoggerForClass(GetItemsFromWorkbookResponseProcessor.class);

	/** The Constant serialVersionUID. */
	static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see com.bbb.framework.httpquery.parser.ResultParserIF#parse(java.lang.String)
	 */
	public HTTPServiceResponseIF parse(String pResponse) {
		
		ItemsFromWorkBookRespVO itemsFormWorkbookVO = new ItemsFromWorkBookRespVO();
		GetItemsFromWorkBookResponseDocument doc;
		try {
			if(!StringUtils.isBlank(pResponse)){
				if(mLogging.isLoggingDebug()){
					mLogging.logDebug("Parsing the response from Kirsch Service \n" + pResponse);
				}
				doc = GetItemsFromWorkBookResponseDocument.Factory.parse(pResponse);
				
				GetItemsFromWorkBookResponse resp = doc.getGetItemsFromWorkBookResponse();
				if(resp != null){
					//fetching the line items from the response document
					LineItems[] items = resp.getLineItemsArray();
					if(items != null) {
						List<LineItem> lLineItemVOs = itemsFormWorkbookVO.getLineItems();
						LineItem itemVO;
						ConfigurationModel lConfigModel;
						//looping through the LineItems array to populate into VO objects 
						for (int i = 0; i < items.length; i++) {
							LineItems lLineItems = items[i];
							itemVO = new LineItem();
							lConfigModel = lLineItems.getConfigurationModel();

							itemVO.setConfigId(String.valueOf(lConfigModel.getConfigId()));
							itemVO.setCost(lConfigModel.getCost());
							itemVO.setEstimatedShipDate(lConfigModel.getEstimatedShipDate().getTime());
							itemVO.setHasInstallation(lConfigModel.getHasInstallation() > 0);
							itemVO.setProductDesc(lConfigModel.getProductDesc());
							itemVO.setProductImage(lConfigModel.getProductImage());
							itemVO.setQuantity(lConfigModel.getQuantity());
							itemVO.setRetailPrice(lConfigModel.getRetailPrice());
							itemVO.setSku(String.valueOf(lConfigModel.getSku()));

							lLineItemVOs.add(itemVO);
						}
					}
				}
			}
		} catch (org.apache.xmlbeans.XmlException e) {
			if(mLogging.isLoggingError()){
				mLogging.logError(e);
			}
		}

		return itemsFormWorkbookVO;
	}
}
