package com.bbb.commerce.giftregistry.manager;

import atg.multisite.SiteContextManager;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.giftregistry.bean.AddItemsBean;
import com.bbb.commerce.giftregistry.bean.GiftRegistryViewBean;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.commerce.giftregistry.vo.ValidateAddItemsResVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

/**
 * This class is the business layer object for the management of gift registry
 * (RegistryVO and RegistryListVO) object manipulation. It provides the high
 * level access to gift registry. It makes calls to lower level utilities in
 * GiftlistTools which makes the web service call.
 * 
 * @author sku134
 */
public class GSGiftRegistryManager extends GiftRegistryManager {

	private String addItemsToRegServiceName;
	private GiftRegistryTools giftRegistryTools;
	private BBBCatalogTools bbbCatalogTools;

	public BBBCatalogTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	public GiftRegistryTools getGiftRegistryTools() {
		return giftRegistryTools;
	}

	public void setGiftRegistryTools(GiftRegistryTools giftRegistryTools) {
		this.giftRegistryTools = giftRegistryTools;
	}

	/**
	 * @return the addItemsToRegServiceName
	 */
	public String getAddItemsToRegServiceName() {
		return addItemsToRegServiceName;
	}

	/**
	 * @param addItemsToRegServiceName the addItemsToRegServiceName to set
	 */
	public void setAddItemsToRegServiceName(String addItemsToRegServiceName) {
		this.addItemsToRegServiceName = addItemsToRegServiceName;
	}

	/**
	 * Method for adding guided selling items to the registry
	 * Input parameter will be a json string with the list containg combination of skuId,prodId,registryId and quantity
	 * @param jsonCollectionObj
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public boolean addStoreItemsToGiftRegistry(String jsonCollectionObj)
			throws BBBBusinessException, BBBSystemException {

		logDebug("GiftRegistryManager.addStoreItemsToGiftRegistry() method starts");
		boolean addStoreItems = false;

		try {
			GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
			getGiftRegistryTools().addItemJSONObjectParser(
					giftRegistryViewBean, jsonCollectionObj);
			String siteId = SiteContextManager.getCurrentSiteId();
			giftRegistryViewBean.setSiteFlag(getBbbCatalogTools()
					.getAllValuesForKey(
							BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
							siteId).get(0));
			giftRegistryViewBean.setUserToken(getBbbCatalogTools()
					.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
							BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
			giftRegistryViewBean.setServiceName(getAddItemsToRegServiceName());

			logDebug("siteFlag: " + giftRegistryViewBean.getSiteFlag());
			logDebug("userToken: " + giftRegistryViewBean.getUserToken());
			logDebug("ServiceName: " + getAddItemsToRegServiceName());

			preAddStoreItemsToGiftRegistry(giftRegistryViewBean);
			ValidateAddItemsResVO addItemsResVO = addItemToGiftRegistry(giftRegistryViewBean);
			if (!addItemsResVO.getServiceErrorVO().isErrorExists()) {
				addStoreItems = true;
			} else {
				errorAdditem(addItemsResVO);
				addStoreItems = false;
			}

			logDebug("get addStoreItemsToGiftRegistry value from request");


		} catch (BBBBusinessException e) {
			logDebug("BBBBusinessException from addStoreItemsToGiftRegistry of GiftRegistryManager");
			addStoreItems = false;
			throw (e);

		} catch (BBBSystemException es) {
			logDebug("BBBSystemException from addStoreItemsToGiftRegistry of GiftRegistryManager");
			addStoreItems = true;
			throw new BBBSystemException(
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1006,
					BBBGiftRegistryConstants.SYSTEM_EXCEPTION);
		} catch (NumberFormatException es) {
			logDebug("NumberFormatException from addStoreItemsToGiftRegistry of GiftRegistryManager");
			addStoreItems = false;
			throw new BBBBusinessException(
					BBBGiftRegistryConstants.INVALID_QUANTITY_EXCEPTION);

		}
		logDebug("GiftRegistryManager.addStoreItemsToGiftRegistry() method ends");
		return addStoreItems;
	}

	/**
	 * Method created to throw BBBException according to the errors received through the addItemToRegistry web service call
	 * @param addItemsResVO
	 * @throws BBBBusinessException
	 */
	private void errorAdditem(ValidateAddItemsResVO addItemsResVO) throws BBBBusinessException {



		if (!BBBUtility.isEmpty(addItemsResVO.getServiceErrorVO().getErrorMessage())
				&& addItemsResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR)// Technical
			// Error
		{
			logDebug("Fatal error from errorAdditem of GiftRegistryManager | webservice code ="+ addItemsResVO.getServiceErrorVO().getErrorId());
			throw new BBBBusinessException(BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR);
		}
		else if (!BBBUtility.isEmpty(addItemsResVO.getServiceErrorVO()
				.getErrorMessage())
				&& addItemsResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN)// Technical
			// Error
		{
			logDebug("Either user token or site flag invalid from errorAdditem of GiftRegistryManager | webservice code ="+ addItemsResVO.getServiceErrorVO().getErrorId());
			throw new BBBBusinessException(BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR);
		}
		else if (!BBBUtility.isEmpty(addItemsResVO.getServiceErrorVO()
				.getErrorMessage())
				&& addItemsResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT)// Technical
			// Error
		{
			logDebug("GiftRegistry input fields format error from addStoreItemsToGiftRegistry() of " +
					"GiftRegistryManager | webservice error code=" + addItemsResVO.getServiceErrorVO().getErrorId());			

			throw new BBBBusinessException(BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT);
		}
		else{

			throw new BBBBusinessException(BBBGiftRegistryConstants.SYSTEM_EXCEPTION);
		}

	}

	/**
	 * Check the quantity format is valid or not
	 * @param giftRegistryViewBean
	 */
	private void preAddStoreItemsToGiftRegistry(
			GiftRegistryViewBean giftRegistryViewBean) {

		logDebug("GiftRegistryManager.preAddStoreItemsToGiftRegistry() method starts");

		for (int i = 0; i < giftRegistryViewBean.getAdditem().size(); i++) {
			AddItemsBean addItemsBean = giftRegistryViewBean.getAdditem()
					.get(i);
			if (addItemsBean.getQuantity() != null) {
				Integer.parseInt(addItemsBean.getQuantity());

				if (!BBBUtility.isValidNumber(addItemsBean.getQuantity())) {

					logDebug(BBBGiftRegistryConstants.INVALID_QUANTITY_MESSAGE);
					throw new NumberFormatException(
							BBBGiftRegistryConstants.INVALID_QUANTITY_EXCEPTION);
				}

			} else {

				logDebug(BBBGiftRegistryConstants.INVALID_QUANTITY_MESSAGE);
				throw new NumberFormatException(
						BBBGiftRegistryConstants.QUANTITY_EXCEPTION);
			}
		}
		logDebug("GiftRegistryManager.preAddStoreItemsToGiftRegistry() method ends");

	}
}
