package com.bbb.remote.promotion.template.translators;

//import static atg.remote.promotion.template.translators.TargetTranslator.sResourceBundle;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import atg.remote.promotion.template.model.ElementState;
import atg.remote.promotion.template.service.PromotionTemplateTools;
import atg.remote.promotion.template.translators.TargetTranslator;
import atg.repository.editingtemplate.TemplateException;
import atg.ui.commerce.pricing.DescriptionBuilder;
import atg.ui.commerce.pricing.DescriptionBuilderException;

public class BBBTargetTranslator extends TargetTranslator{
	
	

	@Override
	public Map<String, List<Object>> translateInputValues(
			Map<String, ElementState> pInputNameElementStateMap,
			Map<String, String> pOutputNameValueMap, Object pItemPropertyInfo,
			Map<String, String> pAttributes) throws TemplateException {

		
			if (isLoggingDebug()) {
				logDebug("BBBTargetTranslator:translateInputValues() Updating sort-by for Promotion");
			}
	
			if (pInputNameElementStateMap == null) {
				throw new TemplateException("Null input name value map.");
			}
	
			if (pOutputNameValueMap == null) {
				throw new TemplateException("Null output name value map.");
			}
	
			ElementState sortOrderElementState = (ElementState) pInputNameElementStateMap
					.get(getSortOrderInputName());
	
			String sortOrder = null;
	
			if (sortOrderElementState != null) {
				sortOrder = (String) sortOrderElementState.getValue();
			}
	
			if ((sortOrder == null) && (pAttributes != null)) {
				sortOrder = (String) pAttributes.get(getSortOrderInputName());
			}
	
			if (sortOrder != null) {
				sortOrder = convertSortOrder(sortOrder);
			}
	
			try {
				Document doc = DescriptionBuilder.createDocument();
				getPromotionExpressionService().getTargetDocument(doc);
	
				Element targetElem = null;
				NodeList targetElems = doc.getElementsByTagName("target");
				if ((null != targetElems) && (targetElems.getLength() > 0)) {
					targetElem = (Element) targetElems.item(0);
				}
	
				if (targetElem == null) {
					throw new TemplateException("No target expression.");
				}
	
				if (sortOrder != null) {
					NodeList upToElems = targetElem.getElementsByTagName("up-to-and-including");
					if (upToElems.getLength() > 0) {
						if(isLoggingDebug()){
							logDebug("Updating soty-by attribute of Target Element for PMDL to include priceInfo.currentPrice");
						}
						Element upToAndIncludingElem = (Element) upToElems.item(0);
						upToAndIncludingElem.setAttribute("sort-order", sortOrder);
						if(!(upToAndIncludingElem.getAttribute("sort-by").isEmpty()) && 
								((upToAndIncludingElem.getAttribute("sort-by").equals("priceInfo.salePrice")) || (upToAndIncludingElem.getAttribute("sort-by").equals("priceInfo.listPrice")))){
							upToAndIncludingElem.removeAttribute("sort-by");
							upToAndIncludingElem.setAttribute("sort-by", "priceInfo.currentPrice");
						}
					}
				}
	
				String pmdl = DescriptionBuilder.convertElementToString(targetElem);
	
				if (isLoggingDebug()) {
					logDebug("pmdl is:" + pmdl);
				}
	
				if (pOutputNameValueMap.containsKey(getEndTargetOutputName())) {
					String[] outputParts = PromotionTemplateTools.splitOutEndTag(pmdl);
					pOutputNameValueMap.put(getTargetOutputName(), outputParts[0]);
					pOutputNameValueMap.put(getEndTargetOutputName(), outputParts[1]);
				} else {
					pOutputNameValueMap.put(getTargetOutputName(), pmdl);
				}
				return null;
			} catch (Exception e) {
				if (e instanceof DescriptionBuilderException) {
					String errorMessage = MessageFormat.format("An Error Occurred while building the Promotion Properties :", new Object[] { e.getMessage() });
					Map errorMap = new HashMap();
					List errors = new ArrayList();
					errors.add(errorMessage);
					errorMap.put("general_error", errors);
					return errorMap;
				}
	
				throw new TemplateException("Failed to build the target PMDL from the expression", e);
			}
	}
}
