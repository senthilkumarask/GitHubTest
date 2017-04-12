package com.bbb.browse;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.bbb.certona.droplet.TestCertonaDroplet;
import com.bbb.cms.droplet.TestCustomLandingTemplateDroplet;
import com.bbb.commerce.browse.droplet.TestBrandDetailDroplet;
import com.bbb.commerce.browse.droplet.TestBrandsDroplet;
import com.bbb.commerce.browse.droplet.TestCategoryLandingDroplet;
import com.bbb.commerce.browse.droplet.TestExitemIdDroplet;
import com.bbb.commerce.browse.droplet.TestMinimalProductDetailDroplet;
import com.bbb.commerce.browse.droplet.TestProdToutDroplet;
import com.bbb.commerce.browse.droplet.TestProductViewedDroplet;
import com.bbb.commerce.browse.droplet.TestSKUDetailDroplet;
import com.bbb.commerce.giftregistry.TestAddItemToGiftRegistryDroplet;
import com.bbb.commerce.giftregistry.TestBridalToolkitLinkDroplet;
import com.bbb.commerce.giftregistry.TestGetRegistryVODroplet;
import com.bbb.commerce.giftregistry.TestGiftRegistryAnnounceCardDroplet;
import com.bbb.commerce.giftregistry.TestGiftRegistryFormHandler;
import com.bbb.commerce.giftregistry.TestGiftRegistryRecommendations;
import com.bbb.commerce.giftregistry.TestGiftRegistryTypesDroplet;
import com.bbb.commerce.giftregistry.TestGiftRegistryUpdateProfile;
import com.bbb.commerce.giftregistry.TestProfileExistCheckDroplet;
import com.bbb.commerce.giftregistry.TestRegistryInfoDisplayDroplet;
import com.bbb.commerce.giftregistry.TestRegistryItemsDisplayDroplet;
import com.bbb.commerce.giftregistry.TestSelectHeaderDroplet;
import com.bbb.commerce.giftregistry.droplet.TestDateCalculationDroplet;
import com.bbb.commerce.giftregistry.droplet.TestGetGenderKeyDroplet;
import com.bbb.commerce.giftregistry.droplet.TestGetRegistryTypeNameDroplet;
import com.bbb.commerce.giftregistry.droplet.TestGiftRegistryFetchProductIdDroplet;
import com.bbb.commerce.giftregistry.droplet.TestGiftRegistryFlyoutDroplet;
import com.bbb.commerce.giftregistry.droplet.TestGiftRegistryPaginationDroplet;
import com.bbb.commerce.giftregistry.droplet.TestMyRegistriesDisplayDroplet;
import com.bbb.commerce.giftregistry.droplet.TestPOBoxValidateDroplet;
import com.bbb.commerce.giftregistry.droplet.TestRegistryAddressOrderDroplet;
import com.bbb.commerce.giftregistry.droplet.TestValidateRegistryDroplet;
import com.bbb.commerce.giftregistry.formhandler.TestGiftRegistryFormHandler1;
import com.bbb.commerce.giftregistry.formhandler.TestGiftRegistryFormHandler10;
import com.bbb.commerce.giftregistry.formhandler.TestGiftRegistryFormHandler11;
import com.bbb.commerce.giftregistry.formhandler.TestGiftRegistryFormHandler12;
import com.bbb.commerce.giftregistry.formhandler.TestGiftRegistryFormHandler2;
import com.bbb.commerce.giftregistry.formhandler.TestGiftRegistryFormHandler3;
import com.bbb.commerce.giftregistry.formhandler.TestGiftRegistryFormHandler4;
import com.bbb.commerce.giftregistry.formhandler.TestGiftRegistryFormHandler5;
import com.bbb.commerce.giftregistry.formhandler.TestGiftRegistryFormHandler6;
import com.bbb.commerce.giftregistry.formhandler.TestGiftRegistryFormHandler7;
import com.bbb.commerce.giftregistry.formhandler.TestGiftRegistryFormHandler8;
import com.bbb.commerce.giftregistry.formhandler.TestGiftRegistryFormHandler9;
import com.bbb.commerce.giftregistry.formhandler.TestGiftRegistryFormHandlerDoColink;
import com.bbb.commerce.giftregistry.formhandler.TestGiftRegistryManageItems;
import com.bbb.commerce.giftregistry.webservices.test.WSTestAddItemsToGiftRegistry;
import com.bbb.commerce.giftregistry.webservices.test.WSTestForgetRegistryPassword;
import com.bbb.commerce.giftregistry.webservices.test.WSTestGiftRegistryAnnounceCardFormHandler;
import com.bbb.commerce.giftregistry.webservices.test.WSTestImportRegistry;
import com.bbb.personalstore.droplet.TestContextCookieDroplet;
import com.bbb.personalstore.droplet.TestPersonalStoreDroplet;
import com.bbb.personalstore.manager.TestPersonalStoreManager;
import com.bbb.personalstore.targeter.TestCertonaTargeter;
import com.bbb.search.droplet.TestResultListDroplet;
import com.bbb.search.droplet.TestSearchDroplet;
import com.bbb.search.droplet.TestTypeAheadDroplet;
import com.bbb.search.handler.TestNavigationSearchFormHandler;

public class BSGRTests {
	public static Test suite() {
        TestSuite suite = new TestSuite("Test for Browse, Search and GiftRegistry");
        
        //Personal Store
        suite.addTestSuite(TestPersonalStoreDroplet.class);

        //$JUnit-BEGIN$
//        suite.addTestSuite(TestGetRegistryVODroplet.class);
        suite.addTestSuite(TestRegistryAddressOrderDroplet.class);
        suite.addTestSuite(TestGiftCardListDroplet.class);
        
        suite.addTestSuite(TestGiftRegistryFormHandler.class);
        suite.addTestSuite(TestProfileExistCheckDroplet.class);
        suite.addTestSuite(TestBridalToolkitLinkDroplet.class);        
        suite.addTestSuite(TestSelectHeaderDroplet.class);
        
        suite.addTestSuite(TestGiftRegistryFormHandler1.class);
        suite.addTestSuite(TestGiftRegistryFormHandler2.class);
        suite.addTestSuite(TestGiftRegistryFormHandler3.class);
        suite.addTestSuite(TestGiftRegistryFormHandler4.class);
        suite.addTestSuite(TestGiftRegistryFormHandler5.class);
        suite.addTestSuite(TestGiftRegistryFormHandler6.class);
        suite.addTestSuite(TestGiftRegistryFormHandler7.class);
        suite.addTestSuite(TestGiftRegistryFormHandler8.class);
        suite.addTestSuite(TestGiftRegistryFormHandler9.class);
        suite.addTestSuite(TestGiftRegistryFormHandler10.class);
        suite.addTestSuite(TestSKURollUpListDroplet.class);
        suite.addTestSuite(TestRollUpListDroplet.class);
        suite.addTestSuite(TestGetRegistryVODroplet.class);
        suite.addTestSuite(TestGiftRegistryAnnounceCardDroplet.class);
        
        
        suite.addTestSuite(TestAddItemToGiftRegistryDroplet.class);
        suite.addTestSuite(TestGiftRegistryTypesDroplet.class);
        suite.addTestSuite(TestGiftRegistryUpdateProfile.class);
        suite.addTestSuite(TestDateCalculationDroplet.class);
        suite.addTestSuite(TestGiftRegistryManageItems.class);
        suite.addTestSuite(TestRegistryInfoDisplayDroplet.class);
        suite.addTestSuite(TestRegistryItemsDisplayDroplet.class);
        suite.addTestSuite(TestMyRegistriesDisplayDroplet.class);
        suite.addTestSuite(WSTestImportRegistry.class);
        suite.addTestSuite(WSTestGiftRegistryAnnounceCardFormHandler.class);
        suite.addTestSuite(WSTestAddItemsToGiftRegistry.class);
        suite.addTestSuite(TestGiftRegistryPaginationDroplet.class);
        suite.addTestSuite(TestBreadcrumbDroplet.class);
        suite.addTestSuite(WSTestForgetRegistryPassword.class);
        suite.addTestSuite(TestGiftRegistryFetchProductIdDroplet.class);
        suite.addTestSuite(TestGiftRegistryFlyoutDroplet.class);
        suite.addTestSuite(TestCertonaDroplet.class);
        suite.addTestSuite(TestPOBoxValidateDroplet.class);
        suite.addTestSuite(TestConfigURLDroplet.class);
        suite.addTestSuite(TestGiftRegistryFormHandlerDoColink.class);
        suite.addTestSuite(TestNavigationSearchFormHandler.class);
        suite.addTestSuite(TestGetRegistryTypeNameDroplet.class);
        suite.addTestSuite(TestGiftRegistryFormHandler11.class);
        suite.addTestSuite(TestEverLivingPDPDroplet.class);
        suite.addTestSuite(TestEverLivingDetailsDroplet.class);
        
        
        suite.addTestSuite(TestProductDetailDroplet.class);
        suite.addTestSuite(TestBrandsDroplet.class);
        suite.addTestSuite(TestBrandDetailDroplet.class);
        suite.addTestSuite(TestMinimalProductDetailDroplet.class);
        suite.addTestSuite(TestResultListDroplet.class);
        suite.addTestSuite(TestSearchDroplet.class);
        suite.addTestSuite(TestTypeAheadDroplet.class);
        suite.addTestSuite(TestEmailAFriendFormHandler.class);
        suite.addTestSuite(TestCategoryLandingDroplet.class);
        suite.addTestSuite(TestProdToutDroplet.class);
        suite.addTestSuite(TestBBBCaptchaServlet.class);
        
        //New Sapunit Created by Rajesh Saini
        suite.addTestSuite(TestBBBBackInStockFormHandler.class);
        //suite.addTestSuite(TestListSchedulerJobs.class);
        suite.addTestSuite(TestGetGenderKeyDroplet.class);
        suite.addTestSuite(TestExitemIdDroplet.class);
        suite.addTestSuite(TestProductViewedDroplet.class);
        suite.addTestSuite(TestSKUDetailDroplet.class);
        suite.addTestSuite(TestValidateRegistryDroplet.class);
        suite.addTestSuite(TestGiftRegistryFormHandler12.class);
        
        //CLP
        suite.addTestSuite(TestCustomLandingTemplateDroplet.class);
        
        //PersonalStore
        suite.addTestSuite(TestContextCookieDroplet.class);
        suite.addTestSuite(TestPersonalStoreManager.class);
        suite.addTestSuite(TestCertonaTargeter.class);
        
        //Registry Recommendations
        suite.addTestSuite(TestGiftRegistryRecommendations.class);
        
        
        //$JUnit-END$
        return suite;
    }
}
