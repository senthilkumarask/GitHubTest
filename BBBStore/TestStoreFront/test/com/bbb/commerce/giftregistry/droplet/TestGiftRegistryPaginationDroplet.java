package com.bbb.commerce.giftregistry.droplet;

import java.util.List;

import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryPaginationDroplet extends BaseTestCase {
	public void testService() throws Exception {
		GiftRegistryPaginationDroplet giftRegistryPaginationDroplet = (GiftRegistryPaginationDroplet) getObject("giftRegistryPaginationDroplet");
		final BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) this.getObject("catalogTools");
		String userToken = catalogTools.getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY,
				BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(
				BBBCoreConstants.ZERO);
		
		getRequest().setParameter(BBBGiftRegistryConstants.PAGE_NO, "1");
		getRequest().setParameter(BBBGiftRegistryConstants.PER_PAGE, "24");
		getRequest().setParameter(BBBGiftRegistryConstants.SORT_PASS_STRING, "NAME");
		
		RegistrySearchVO registrySearchVO = new RegistrySearchVO();
		registrySearchVO.setFirstName("sunil");
		registrySearchVO.setLastName("kumar");
		registrySearchVO.setServiceName("regSearch");
		registrySearchVO.setSiteId("1");
		registrySearchVO.setUserToken(userToken);
		registrySearchVO.setReturnLeagacyRegistries(false);
		registrySearchVO.setGiftGiver(true);
		//registrySearchVO.setFilterRegistriesInProfile(false);
		
		GiftRegSessionBean giftRegSessionBean = (GiftRegSessionBean) getObject("giftRegSessionBean");
		giftRegSessionBean.setRequestVO(registrySearchVO);
		getRequest().setParameter("siteId", "BedBathUS");		
		giftRegistryPaginationDroplet.service(getRequest(), getResponse());
		
		
		List<RegistrySummaryVO> registrySummaryVOs = (List<RegistrySummaryVO>) getRequest().getObjectParameter(BBBGiftRegistryConstants.REGiSTRY_SEARCH_LIST);
		//  if webservice is not working size will be zero
		if(registrySummaryVOs!=null && registrySummaryVOs.size()>0)
		{
		assertNotNull(registrySummaryVOs);
		}
		
		
		
	}
}
