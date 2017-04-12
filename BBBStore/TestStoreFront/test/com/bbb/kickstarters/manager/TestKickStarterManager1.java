package com.bbb.kickstarters.manager;

import java.util.List;

import atg.nucleus.Nucleus;
import atg.repository.MutableRepository;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.PropertyManager;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.account.BBBProfileTools;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.framework.BBBSiteContext;
import com.bbb.profile.session.BBBSessionBean;
import com.sapient.common.tests.BaseTestCase;

public class TestKickStarterManager1 extends BaseTestCase {
	public void testPopularItems() throws Exception {
		 KickStarterManager kickStarterMnger = (KickStarterManager) getObject("kickStarterManager");
		 String site = (String) getObject("site");
		 String registryType = null;
				 
				 //(String)getObject("registryType");				 
		 List<ProductVO> prdList =  kickStarterMnger.getPopularItemsDetails( registryType,site);
		 boolean isTrue = false;	 
		 if(prdList!=null && prdList.size()>1){
			 isTrue =true;
		  }		 
		  assertTrue(isTrue);
		 }
	
}
