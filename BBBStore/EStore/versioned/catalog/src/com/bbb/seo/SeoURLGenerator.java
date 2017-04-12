package com.bbb.seo;

import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.seo.IndirectUrlTemplate;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.service.webappregistry.WebApp;

public class SeoURLGenerator extends GenericService {

IndirectUrlTemplate productTemplate;
Repository defaultRepository;
String defaultItemDescriptorName;

public IndirectUrlTemplate getProductTemplate() {
return productTemplate;
}
public void setProductTemplate(IndirectUrlTemplate productTemplate) {
this.productTemplate = productTemplate;
}
public Repository getDefaultRepository() {
return defaultRepository;
}
public void setDefaultRepository(Repository defaultRepository) {
this.defaultRepository = defaultRepository;
}
public String getDefaultItemDescriptorName() {
return defaultItemDescriptorName;
}
public void setDefaultItemDescriptorName(String defaultItemDescriptorName) {
this.defaultItemDescriptorName = defaultItemDescriptorName;
}



public String formatUrl(String productId, String displayName) {
String formattedURL = null;
if(isLoggingDebug()){
logDebug("formatUrl() starts : input parameters :  productId "+productId+", displayName "+displayName);
}
try{
WebApp pDefaultWebApp = null;
UrlParameter[] pUrlParams = getProductTemplate().cloneUrlParameters();
pUrlParams[0].setValue(displayName);
pUrlParams[1].setValue(productId);
formattedURL = getProductTemplate().formatUrl(pUrlParams, pDefaultWebApp);
if(isLoggingDebug()){
logDebug("formattedURL generated from IndirectUrlTemplate : "+formattedURL );
}
}catch (ItemLinkException e) {
if(isLoggingError()){
logError("Exception occourred while creating SEO URL for the product : "+displayName, e);
}
}catch (Exception e) {
if(isLoggingError()){
logError("Exception occourred while creating SEO URL for the product : "+displayName, e);
}
}
return formattedURL;
}

}
