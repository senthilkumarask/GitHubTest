package com.bbb.commerce.browse.droplet;

import java.util.List;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;

import com.bbb.cms.LandingTemplateVO;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestCategoryLandingDroplet extends BaseTestCase{



	@SuppressWarnings("unchecked")
	public void testServiceSubCategory() throws Exception
	{
		CategoryLandingDroplet categoryLandingDroplet = (CategoryLandingDroplet) getObject("categoryLandingDroplet");
		categoryLandingDroplet.setLoggingDebug(true);

		String siteId= (String) getObject("siteId");
		String categoryId= (String) getObject("categoryId");
		getRequest().setParameter("id", categoryId);
		//categoryLandingDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		getRequest().setParameter("catFlg","true");
		getRequest().setParameter("siteId", siteId);

		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
				
		categoryLandingDroplet.service(getRequest(), getResponse());
		List<CategoryVO> subcategories=(List<CategoryVO>)getRequest().getObjectParameter("subcategoriesList");
		LandingTemplateVO  landingTemplateVO=(LandingTemplateVO)getRequest().getObjectParameter("landingTemplateVO");
		System.out.println("landingTemplateVO  "+landingTemplateVO);
		//System.out.println("landingTemplateVO  "+landingTemplateVO.getHeroImages());
		if(landingTemplateVO.getHeroImages()!=null)
		{
			for(int i=0;i<landingTemplateVO.getHeroImages().size();i++)
			{
				System.out.println("Image URL"+ landingTemplateVO.getHeroImages().get(i).getImageURL());
				assertNotNull(landingTemplateVO.getHeroImages().get(i).getImageURL());
			}

		}

		if(landingTemplateVO.getPromoTierLayout1()!=null)
		{
			for(int i=0;i<landingTemplateVO.getPromoTierLayout1().size();i++)
			{
				System.out.println("Promo Image URL"+ landingTemplateVO.getPromoTierLayout1().get(i).getPromoBoxFirstVOList().getImageURL());
				System.out.println("Promo Image URL"+ landingTemplateVO.getPromoTierLayout1().get(i).getPromoBoxSecondVOList().getImageURL());
				System.out.println("Promo Image URL"+ landingTemplateVO.getPromoTierLayout1().get(i).getPromoBoxThirdVOList().getImageURL());
				assertNotNull(landingTemplateVO.getPromoTierLayout1().get(i).getPromoBoxFirstVOList().getImageURL());
				assertNotNull(landingTemplateVO.getPromoTierLayout1().get(i).getPromoBoxSecondVOList().getImageURL());
				assertNotNull(landingTemplateVO.getPromoTierLayout1().get(i).getPromoBoxThirdVOList().getImageURL());
			}

		}
		assertNotNull(landingTemplateVO);



	}




}
