package com.bbb.commerce.giftregistry.droplet;

import atg.multisite.SiteContext;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * This Droplet is being used to set the request parameter if session timeout
 * 
 * @author Rajesh Saini
 * 
 */
public class GiftRegistrySessionExpiryDroplet extends BBBPresentationDroplet {

	private static final String FIRST_NAME_REG = "firstNameReg";
	private static final String LAST_NAME_REG = "lastNameReg";
	private static final String REGISTRY_NUMBER = "regNum";
	private static final String NAME = "NAME";

	private RegistrySearchVO mRegistrySearchVO;
	private BBBCatalogTools mCatalogTools;
	private SiteContext mSiteContext;
	private String mSearchRegistryServiceName;

	public RegistrySearchVO getRegistrySearchVO() {
		if (mRegistrySearchVO != null) {
			return mRegistrySearchVO;
		} else {
			mRegistrySearchVO = new RegistrySearchVO();
			return mRegistrySearchVO;
		}

	}

	public void setRegistrySearchVO(final RegistrySearchVO mRegistrySearchVO) {
		this.mRegistrySearchVO = mRegistrySearchVO;
	}

	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	public SiteContext getSiteContext() {
		return mSiteContext;
	}

	public void setSiteContext(SiteContext pSiteContext) {

		this.mSiteContext = pSiteContext;

	}

	public String getSearchRegistryServiceName() {
		return mSearchRegistryServiceName;
	}

	public void setSearchRegistryServiceName(String searchRegistryServiceName) {
		this.mSearchRegistryServiceName = searchRegistryServiceName;
	}

	public void service(final DynamoHttpServletRequest request,
			final DynamoHttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {
		logDebug(" GiftRegistrySessExpiry service - start");

		try {

			String siteId = getSiteId();
			GiftRegSessionBean registrySessionBean = (GiftRegSessionBean) request
					.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean");

			getRegistrySearchVO().setGiftGiver(true);

			getRegistrySearchVO().setFirstName(request.getParameter(FIRST_NAME_REG));
			getRegistrySearchVO().setLastName(request.getParameter(LAST_NAME_REG));
			getRegistrySearchVO().setRegistryId(request.getParameter(REGISTRY_NUMBER));

			getRegistrySearchVO().setSortSeq(NAME);

			getRegistrySearchVO().setSortSeqOrder(BBBGiftRegistryConstants.DEFAULT_SORT_ORDER);

			getRegistrySearchVO().setProfileId(null);
			getRegistrySearchVO().setFilterRegistriesInProfile(false);

			getRegistrySearchVO().setSiteId(getCatalogTools().getAllValuesForKey(
							BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
			getRegistrySearchVO().setUserToken(getCatalogTools().getAllValuesForKey(
							BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
			getRegistrySearchVO().setServiceName(getSearchRegistryServiceName());

			registrySessionBean.setRequestVO(getRegistrySearchVO());

			logDebug("GiftRegistrySessExpiry service - end");

		} catch (BBBSystemException e) {
			logError("Error while retrieving gift registryVO", e);
		} catch (BBBBusinessException e) {
			logError("Error while retrieving gift registryVO", e);
		}
	}

	protected String getSiteId() {
		return getSiteContext().getSite().getId();
	}

}
