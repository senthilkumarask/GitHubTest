package com.bbb.cms.droplet;


import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.RecommenderLandingPageTemplateVO;
import com.sapient.common.tests.BaseTestCase;

public class TestRecommenderLandingPageTemplateDroplet extends BaseTestCase{
public void testRecommenderLandingPageTemplateDroplet() throws Exception {
	DynamoHttpServletRequest pRequest = getRequest();
	DynamoHttpServletResponse pResponse = getResponse();
		RecommenderLandingPageTemplateDroplet objRecommenderLandingPageTemplateDroplet = (RecommenderLandingPageTemplateDroplet) getObject("recommenderLandingPageTemplateDroplet");
    	String siteId= (String) this.getObject("siteId");
    	pRequest.setParameter("siteId", siteId);
    	String channel= (String) getObject("channel");
    	pRequest.setParameter("channel", channel);
    	String registryType= (String) getObject("registryType");
    	pRequest.setParameter("registryType", registryType);
    	String tokenError= (String) this.getObject("tokenError");
    	pRequest.setParameter("tokenError", tokenError);
    	String recommenderURL= (String) this.getObject("recommenderURL");
    	pRequest.setParameter("recommenderURL", recommenderURL);
    	String visibilityFlag= (String) this.getObject("visibilityFlag");
    	pRequest.setParameter("visibilityFlag", visibilityFlag);
    	String logInURL= (String) this.getObject("logInURL");
    	pRequest.setParameter("logInURL", logInURL);
    	String tokenErrorLogin= (String) this.getObject("tokenErrorLogin");
    	pRequest.setParameter("tokenErrorLogin", tokenErrorLogin);

    	objRecommenderLandingPageTemplateDroplet.service(pRequest, pResponse);
    	RecommenderLandingPageTemplateVO recommenderLandingPageTemplateVO =  (RecommenderLandingPageTemplateVO)getRequest().getObjectParameter("RecommenderTemplateVO");
		System.out.println(recommenderLandingPageTemplateVO.toString());
    	assertNotNull(recommenderLandingPageTemplateVO);
    	assertNotNull(recommenderLandingPageTemplateVO.getPromoBox());
    	assertNotNull(recommenderLandingPageTemplateVO.getPromoBoxList());

    }
}
