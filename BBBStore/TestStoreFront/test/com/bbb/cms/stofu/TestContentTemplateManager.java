package com.bbb.cms.stofu;

import java.util.Locale;

import atg.servlet.RequestLocale;

import com.bbb.cms.RecommendationLandingPageTemplateVO;
import com.bbb.cms.manager.ContentTemplateManager;
import com.bbb.cms.vo.CMSResponseVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.sapient.common.tests.BaseTestCase;

public class TestContentTemplateManager extends BaseTestCase{
	
	public void testContentTemplateDataSuccess() throws Exception
	{
		ContentTemplateManager contentTemplate =(ContentTemplateManager) getObject("contentTemplate");
		String templateName = (String) getObject("templateName");
		String requestJSON = (String) getObject("requestJSON");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		CMSResponseVO cmsResponseVO= contentTemplate.getContent(templateName, requestJSON);
		assertNotNull(cmsResponseVO);
		
	}
	
	public void testContentTemplateNotExistErr() throws Exception
	{
		ContentTemplateManager contentTemplate =(ContentTemplateManager) getObject("contentTemplate");
		String templateName = (String) getObject("templateName");
		String requestJSON = (String) getObject("requestJSON");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		try{
			contentTemplate.getContent(templateName, requestJSON);
			
		}
		catch (BBBSystemException e) {
			assertNotNull(e);
		}
		catch (BBBBusinessException e) {
			if (e.getMessage().contains("template_not_exist")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}
		
	}
	
	public void testContentTemplateRegistryDataSuccess() throws Exception
	{
		ContentTemplateManager contentTemplate =(ContentTemplateManager) getObject("contentTemplate");
		String templateName = (String) getObject("templateName");
		String requestJSON = (String) getObject("requestJSON");
		String registryURL= "/store/_includes/modals/inviteFriendsRegistry.jsp?eventType=Baby&registryId=203522339";
		String quote = "\"";
		requestJSON = requestJSON + registryURL + quote + "}";
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		RecommendationLandingPageTemplateVO cmsResponseVO= contentTemplate.getRegistrantContent(templateName, requestJSON);
		assertNotNull(cmsResponseVO);
	}
	
	public void testContentTemplateRecommenderDataSuccess() throws Exception
	{
		ContentTemplateManager contentTemplate =(ContentTemplateManager) getObject("contentTemplate");
		String templateName = (String) getObject("templateName");
		String requestJSON = (String) getObject("requestJSON");		
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		RecommendationLandingPageTemplateVO cmsResponseVO= contentTemplate.getRegistrantContent(templateName, requestJSON);
		assertNotNull(cmsResponseVO);
	}
	
	public void testContentTemplateRegistryNotExistErr() throws Exception
	{
		ContentTemplateManager contentTemplate =(ContentTemplateManager) getObject("contentTemplate");
		String templateName = (String) getObject("templateName");
		String requestJSON = (String) getObject("requestJSON");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		try{
			contentTemplate.getRegistrantContent(templateName, requestJSON);
			
		}
		catch (BBBSystemException e) {
			assertNotNull(e);
		}
		catch (BBBBusinessException e) {
			if (e.getMessage().contains("template_not_exist")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}
		
	}
}
  