package com.bbb.cmo.processor;


import java.util.List;

import noNamespace.LineItemsDocument;
import noNamespace.LineItemsDocument.LineItems;
import noNamespace.LineItemsDocument.LineItems.LineItem;
import noNamespace.LineItemsDocument.LineItems.LineItem.ConfigurationModel;

import org.apache.xmlbeans.XmlException;

import atg.core.util.StringUtils;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;

import com.bbb.cmo.vo.CMOLineItemsRespVO;
import com.bbb.cmo.vo.LineItemVO;
import com.bbb.common.BBBGenericService;
import com.bbb.framework.httpquery.parser.ResultParserIF;
import com.bbb.framework.httpquery.vo.HTTPServiceResponseIF;
import com.bbb.kirsch.processor.GetItemsFromWorkbookResponseProcessor;

/**
 * This is class is used to parse the response from the CMO service and populate the response into VO objects.
 */
public class GetCMOLineItemsResponseProcessor implements ResultParserIF {
	
	
	/** The Logging. */
	ApplicationLogging mLogging =
		    ClassLoggingFactory.getFactory().getLoggerForClass(GetCMOLineItemsResponseProcessor.class);

	
    /* (non-Javadoc)
     * @see com.bbb.framework.httpquery.parser.ResultParserIF#parse(java.lang.String)
     */
    @Override
    public HTTPServiceResponseIF parse(String responseObject) {
        CMOLineItemsRespVO lRespVO = new CMOLineItemsRespVO();
        List<LineItemVO> lLineItemsList = lRespVO.getLineItems();
        
        try {
        	if(!StringUtils.isBlank(responseObject)){
        		if(mLogging.isLoggingDebug()){
        			mLogging.logDebug("Parsing the response from CMO Service \n" + responseObject);
        		}

        		LineItemsDocument lDocument = LineItemsDocument.Factory.parse(responseObject);
        		//fetching the line items from the response document
        		if(lDocument != null) {
        			LineItems lLineItems = lDocument.getLineItems();
        			if(lLineItems != null){
        				LineItem[] lLineItemArray = lLineItems.getLineItemArray();
        				if(lLineItemArray != null){
        					LineItemVO lLineItemVO;
        					ConfigurationModel lModel;
        					//looping through the lineItemsArray to populate into VO objects 

        					for (int i = 0; i < lLineItemArray.length; i++) {
        						LineItem lLineItem = lLineItemArray[i];
        						lModel = lLineItem.getConfigurationModel();
        						lLineItemVO = new LineItemVO();
        						lLineItemVO.setConfigId(lModel.getConfigId());
        						lLineItemVO.setCost(lModel.getCost());
        						lLineItemVO.setProductDescription(lModel.getProductDescription());
        						lLineItemVO.setQuantity(lModel.getQuantity());
        						lLineItemVO.setRetailPrice(lModel.getRetailPrice());
        						lLineItemVO.setSku(String.valueOf(lModel.getSku()));
        						lLineItemVO.setVendorLeadTime(lModel.getVendorLeadTime());
        						lLineItemVO.setDeliveryFee(lModel.getDeliveryFee());
        						lLineItemsList.add(lLineItemVO);
        					}
        				}
        			}
        		}
        	}
        } catch (XmlException e) {
            lRespVO.setError("ERR_GET_CMO_LINE_ITEMS_RESPONSE_PARSING");
            if(mLogging.isLoggingError()){
				mLogging.logError(e);
			}
        }

        return lRespVO;
    }
}
