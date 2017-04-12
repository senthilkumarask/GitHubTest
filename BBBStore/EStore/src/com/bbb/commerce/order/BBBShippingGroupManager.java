package com.bbb.commerce.order;

//Java Imports
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.DuplicateShippingGroupException;
import atg.commerce.order.EmailAddressContainer;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderTools;
import atg.commerce.order.RepositoryAddress;
import atg.commerce.order.RepositoryContactInfo;
import atg.commerce.order.ShippingAddressContainer;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.order.ShippingGroupManager;
import atg.commerce.order.purchase.CommerceItemShippingInfo;
import atg.commerce.order.purchase.CommerceItemShippingInfoContainer;
import atg.commerce.order.purchase.PurchaseProcessHelper;
import atg.commerce.order.purchase.ShippingGroupMapContainer;
import atg.commerce.pricing.PricingException;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.address.AddressTools;

import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.cms.tools.CmsTools;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.checkout.manager.BBBSameDayDeliveryManager;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.commerce.common.BBBAddressImpl;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.order.purchase.BBBCommerceItemShippingInfoTools;
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper;
import com.bbb.commerce.order.shipping.BBBShippingInfoBean;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrder.OrderType;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;


/**
 * This class Extends the default ShippingGroupManager for custom functionality.
 *
 * @author jpadhi
 * @version $Revision: #1 $
 */
public class BBBShippingGroupManager extends ShippingGroupManager {

	/*
	 * ===================================================== *
	 * ============ MEMBER VARIABLES ======================= *
	 * ===================================================== *
	 */
	/**
	 * StoreShippingGroupType property.
	 */
	private String mStoreShippingGroupType; 
	private String mHardGoodShippingGroupType;
	private BBBCatalogTools mCatalogUtil;
	private CmsTools mCmsTools;
	private BBBPricingTools mPricingTools;
	private BBBSameDayDeliveryManager sameDayDeliveryManager;
	private String sddShipMethodId;

	public CmsTools getCmsTools() {
		return mCmsTools;
	}

	public void setCmsTools(CmsTools mCmsTools) {
		this.mCmsTools = mCmsTools;
	}

	private BBBCommerceItemShippingInfoTools mCISITools;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private BBBPurchaseProcessHelper purchaseProcessHelper;
	
	// constants
	private static final String LBL_STORE_PICKUP_SHIP_METHOS = "lbl_store_pickup_ship_methos";
	private static final String TXT_AS_PREVIOUSLY_SELECTED = "txt_as_previously_selected";
	
	/*
	 * ===================================================== *
	 * ============== GETTERS and SETTERS ================== *
	 * ===================================================== *
	 */

	public String getStoreShippingGroupType() {
		return this.mStoreShippingGroupType;
	}

	public void setStoreShippingGroupType(final String pStoreShippingGroupType) {
		this.mStoreShippingGroupType = pStoreShippingGroupType;
	}

	public String getHardGoodShippingGroupType() {
		return this.mHardGoodShippingGroupType;
	}

	public void setHardGoodShippingGroupType(final String pHardGoodShippingGroupType) {
		this.mHardGoodShippingGroupType = pHardGoodShippingGroupType;
	}

	public BBBCommerceItemShippingInfoTools getCisiTools() {
		return this.mCISITools;
	}

	public void setCisiTools(final BBBCommerceItemShippingInfoTools cisiTools) {
		this.mCISITools = cisiTools;
	}

	public BBBCatalogTools getCatalogUtil() {
		return this.mCatalogUtil;
	}

	public void setCatalogUtil(final BBBCatalogTools catalogUtil) {
		this.mCatalogUtil = catalogUtil;
	}
	
	/**
	 * @return the lblTxtTemplateManager
	 */
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	/**
	 * @param lblTxtTemplateManager the lblTxtTemplateManager to set
	 */
	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}
	
	/**
	 * @return the purchaseProcessHelper
	 */
	public BBBPurchaseProcessHelper getPurchaseProcessHelper() {
		return purchaseProcessHelper;
	}

	/**
	 * @param purchaseProcessHelper the purchaseProcessHelper to set
	 */
	public void setPurchaseProcessHelper(
			BBBPurchaseProcessHelper purchaseProcessHelper) {
		this.purchaseProcessHelper = purchaseProcessHelper;
	}
	
	/**
	 * Pricing tools component
	 * @return BBBPricingTools
	 */
	public BBBPricingTools getPricingTools() {
		return mPricingTools;
	}
	/**
	 * Set pricing tools component
	 * @param mPricingTools
	 */
	public void setPricingTools(final BBBPricingTools mPricingTools) {
		this.mPricingTools = mPricingTools;
	}
	
	/*
	 * ===================================================== *
	 * ================ STANDARD METHODS =================== *
	 * ===================================================== *
	 */

	/**
	 * This method iterates over all the shipping groups of the order and find
	 * matching BBBStoreShippingGroup for the input StoreId
	 *
	 * @param pStoreId
	 * @param pOrder
	 *
	 */
	public ShippingGroup getStorePickupShippingGroup(final String pStoreId,
			final Order pOrder) throws CommerceException {

		if (this.isLoggingDebug()) {
			this.logDebug(new StringBuilder()
					.append("getStorePickupShippingGroup() input storeId :  ")
					.append(pStoreId).toString());
		}

		BBBStoreShippingGroup storeShippingGroup = null;
		@SuppressWarnings("unchecked")
		final
		List<ShippingGroup> shippingGroupList = pOrder.getShippingGroups();
		for (final ShippingGroup sg : shippingGroupList) {
			if (this.getStoreShippingGroupType().equalsIgnoreCase(
					sg.getShippingGroupClassType())
					&& pStoreId.equals(((BBBStoreShippingGroup) sg)
							.getStoreId())) {
				if (this.isLoggingDebug()) {
					this.logDebug(new StringBuilder()
							.append("getStorePickupShippingGroup() found shipping group in order for matching storeId : ")
							.append(pStoreId).toString());
				}
				return sg;
			}
		}
		if (null != this.getStoreShippingGroupType()) {
			if (this.isLoggingDebug()) {
				this.logDebug("getStorePickupShippingGroup() could not find shipping group in order for matching storeId. Creating new Store Shipping group.");
			}
			storeShippingGroup = (BBBStoreShippingGroup) this.createShippingGroup(this.getStoreShippingGroupType());
			storeShippingGroup.setStoreId(pStoreId);
			this.addShippingGroupToOrder(pOrder, storeShippingGroup);
			/*
			 * try{ getShippingGroupContainerService().addShippingGroup(
			 * storeShippingGroup.getId(), storeShippingGroup); }catch
			 * (Exception e) { // TODO: handle exception }
			 */
		}
		return storeShippingGroup;
	}

	/**
	 * This method iterates over all the shipping groups of the order and find
	 * matching registryHardgood shipping group for the input RegistryId
	 *
	 * @param registryId
	 * @param pOrder
	 * @throws BBBBusinessException 
	 *
	 */
	public ShippingGroup getRegistryShippingGroup(final String registryId,
			final Order pOrder) throws CommerceException, BBBBusinessException {

		BBBHardGoodShippingGroup registryHGShippingGroup = null;
		List<String> nonLTLSG = new ArrayList<String>();
		if ((pOrder != null) && (registryId != null)) {
			try {
				RepositoryItem[] applicableShipMethods = this.getCmsTools().getShippingMethods(pOrder.getSiteId());
				nonLTLSG.add(BBBCoreConstants.HARD_GOODS_SHIP_GROUP_ITEM_DESCRIPTOR);
				for (final RepositoryItem repositoryItem : applicableShipMethods) {
					if (null != repositoryItem.getPropertyValue("isLTLShippingMethod")
							&& !(Boolean) repositoryItem.getPropertyValue("isLTLShippingMethod"))
						nonLTLSG.add((String) repositoryItem.getPropertyValue("id"));
				}
			} catch (RepositoryException e) {
				vlogError(
						"BBBShippingGroupManager.getRegistryShippingGroup: No shipping method available for site {0}, RepositoryException occured {1} ",
						pOrder.getSiteId(), e);
				throw new BBBBusinessException(
						BBBCatalogErrorCodes.SHIPPING_METHOD_NOT_CONFIGURED_FOR_SITE_IN_REPOSITORY,
						"No shipping method available for site:" + pOrder.getSiteId());
			}
			@SuppressWarnings("unchecked")
			final
			List<ShippingGroup> shippingGroupList = pOrder.getShippingGroups();
			for (final ShippingGroup sg : shippingGroupList) {
				if (sg instanceof BBBHardGoodShippingGroup && nonLTLSG.contains(sg.getShippingMethod())) {
					registryHGShippingGroup = (BBBHardGoodShippingGroup)sg;
					final List<CommerceItemRelationship> commerceItemRelationships  = sg.getCommerceItemRelationships();
					for (final CommerceItemRelationship relationship : commerceItemRelationships) {
						final CommerceItem item = relationship.getCommerceItem();
						if ((item instanceof BBBCommerceItem) && registryId.equalsIgnoreCase(((BBBCommerceItem) item).getRegistryId())){
							return registryHGShippingGroup;
						}
					}
				}
			}
			// LTL-1285 | Registry Item should be added to NON LTL SG
			HardgoodShippingGroup shippingGroup = this.getFirstNonLTLHardgoodShippingGroupWithRels(pOrder);
			if (null != shippingGroup) {
				return shippingGroup;
			}
			shippingGroup = this.getFirstNonGiftHardgoodShippingGroupWithoutRels(pOrder);
			if (null != shippingGroup) {
				return shippingGroup;
			}
			registryHGShippingGroup = (BBBHardGoodShippingGroup) this.createShippingGroup(this.getOrderTools().getDefaultShippingGroupType());
			this.addShippingGroupToOrder(pOrder, registryHGShippingGroup);
			return registryHGShippingGroup;
		} else {
			return registryHGShippingGroup;
		}
	}

	/**
	 *
	 *
	 *
	 * @param pRegistryId
	 * @param pOrder
	 * @return
	 * @throws CommerceException
	 */
	public ShippingGroup getRegistryPickupShippingGroup(
			final String pRegistryId, final Order pOrder)
			throws CommerceException {
		if ((pOrder != null) && (pRegistryId != null)) {
			@SuppressWarnings("unchecked")
			final
			List<ShippingGroup> shippingGroupList = pOrder.getShippingGroups();
			for (final ShippingGroup sg : shippingGroupList) {
				if ((sg instanceof BBBHardGoodShippingGroup)
						&& pRegistryId.equals(((BBBHardGoodShippingGroup) sg)
								.getRegistryId())) {
					if (this.isLoggingDebug()) {
						this.logDebug("Found BBBHardGoodShipping group with id"
								+ ((BBBHardGoodShippingGroup) sg)
										.getRegistryId());
					}
					return sg;
				}
			}
		}
		return null;
	}

	/**
	 * This method creates ShippingGroup in case of BBBStoreShippingGroup else
	 * it calls super method for HardGoodShippingGroup and
	 * ElectronicShippingGroup
	 *
	 * @param pSrcOrder
	 * @param pDstOrder
	 * @param pGroup
	 * @return ShippingGroup
	 * @throws CommerceException
	 */
	@Override
	protected ShippingGroup mergeOrdersCopyShippingGroup(final Order pSrcOrder,
			final Order pDstOrder, final ShippingGroup pGroup) throws CommerceException {

		if (pGroup instanceof BBBStoreShippingGroup) {
			return this.mergeOrdersCopyStoreShippingGroup(pSrcOrder, pDstOrder,
					(BBBStoreShippingGroup) pGroup);
		} else {
			// LTL change | setting previous shipping method in new shipping group for merging cart
			ShippingGroup sg = (BBBHardGoodShippingGroup)super.mergeOrdersCopyShippingGroup(pSrcOrder, pDstOrder,
					pGroup);
			if(sg != null)
				sg.setShippingMethod(pGroup.getShippingMethod());
			return sg;
		}
	}
	
	/**
	 * This method creates new ShippingGroup if Source Shipping Group doesn't has address
	 * @param pSrcOrder
	 * @param pDstOrder
	 * @param pGroup
	 * @return HardgoodShippingGroup
	 * @throws CommerceException
	 */
	@Override
	protected HardgoodShippingGroup mergeOrdersCopyHardgoodShippingGroup(Order pSrcOrder, Order pDstOrder,
			HardgoodShippingGroup pGroup) throws CommerceException {
			
			HardgoodShippingGroup hsg = super.mergeOrdersCopyHardgoodShippingGroup(pSrcOrder, pDstOrder, pGroup);
			if (hsg == null) {
				
				/* If Source Shipping group doesn't has address, create a new shipping group and return
				This fix was added for RMT-42013 */
				
				logInfo("Destination Order doesn't has shipping group. Returning clone of source shipping group :: " + pGroup);
				hsg = (HardgoodShippingGroup)createShippingGroup(pGroup.getShippingGroupClassType());
//				hsg.setShippingMethod(pGroup.getShippingMethod());
			}
			return hsg;
		}
	/**
	 * This method iterates over all the shipping groups of the order and find
	 * matching BBBStoreShippingGroup for the input StoreId If matching storeId
	 * is not found it creates a new BBBStoreShippingGroup
	 *
	 * @param pStoreId
	 * @param pOrder
	 * @return ShippingGroup
	 *
	 */
	public ShippingGroup mergeOrdersCopyStoreShippingGroup(final Order pSrcOrder,
			final Order pDstOrder, final BBBStoreShippingGroup pGroup)
			throws CommerceException {

		BBBStoreShippingGroup storeShippingGroup = null;
		@SuppressWarnings("unchecked")
		final
		List<ShippingGroup> shippingGroupList = pDstOrder.getShippingGroups();
		for (final ShippingGroup sg : shippingGroupList) {
			if (this.getStoreShippingGroupType().equalsIgnoreCase(
					sg.getShippingGroupClassType())
					&& pGroup.getStoreId().equals(
							((BBBStoreShippingGroup) sg).getStoreId())) {
				if (this.isLoggingDebug()) {
					this.logDebug(new StringBuilder()
							.append("mergeOrdersCopyStoreShippingGroup() found shipping group in order for matching storeId : ")
							.append(pGroup.getStoreId()).toString());
				}
				return sg;
			}
		}
		if (null != this.getStoreShippingGroupType()) {
			if (this.isLoggingDebug()) {
				this.logDebug("mergeOrdersCopyStoreShippingGroup() could not find shipping group in order for matching storeId. Creating new Store Shipping group.");
			}
			storeShippingGroup = (BBBStoreShippingGroup) this.createShippingGroup(this.getStoreShippingGroupType());
			storeShippingGroup.setStoreId(pGroup.getStoreId());

		}
		return storeShippingGroup;
	}

	/**
	 * The method takes the order object and returns the hardgoodshipping group
	 * in the order. If hardgood shipping group is not found in the order
	 * creates a new one and returns
	 */
	public ShippingGroup getHardGoodShippingGroup(final Order pOrder)
			throws CommerceException {
		if (this.isLoggingDebug()) {
			this.logDebug("Entry getHardGoodShippingGroup");
		}
		ShippingGroup hrdShpGrp = null;
		@SuppressWarnings("unchecked")
		final
		List<ShippingGroup> shippingGroupList = pOrder.getShippingGroups();
		if (shippingGroupList.size() > 0) {
			for (int i = 0; i < shippingGroupList.size(); i++) {
				if (shippingGroupList.get(i) instanceof HardgoodShippingGroup) {
					hrdShpGrp = shippingGroupList.get(i);
					return hrdShpGrp;
				}
			}
		}

		/**
		 * If no HardgoodShippingGroup found then creating new.
		 */
		hrdShpGrp = this.createShippingGroup(this.getHardGoodShippingGroupType());
		pOrder.addShippingGroup(hrdShpGrp);

		if (this.isLoggingDebug()) {
			this.logDebug("Exit getHardGoodShippingGroup");
		}

		return hrdShpGrp;
	}

	/**
	 * Creates a new HardGood Shipping Group with Address and Shipping method
	 * and adds to Order
	 *
	 * After calling this method one has to add the shipping group into container map
	 *
	 * @param pOrder
	 * @param pAddress
	 * @param shippingMethod
	 * @param pBBBShippingGroupContainerService
	 * @return
	 * @throws CommerceException
	 */

	public BBBHardGoodShippingGroup createHardGoodShippingGroup(final Order pOrder,
			final Address pAddress, final String shippingMethod)
			throws CommerceException {
		if (this.isLoggingDebug()) {
			this.logDebug("createNewHardGoodShippingGroup() starts : ");
		}
		BBBHardGoodShippingGroup hgSG = null;
		final ShippingGroup sg = this.createShippingGroup(this.getHardGoodShippingGroupType());
		if ((sg != null) && (sg instanceof BBBHardGoodShippingGroup)) {
			hgSG = (BBBHardGoodShippingGroup) sg;
			hgSG.setPoBoxAddress(((BBBAddress)pAddress).isPoBoxAddress());
			hgSG.setQASValidated(((BBBAddress)pAddress).isQasValidated());
			hgSG.setShippingAddress(pAddress);
			hgSG.setShippingMethod(shippingMethod);
			this.addShippingGroupToOrder(pOrder, hgSG);
		}
		if (this.isLoggingDebug()) {
			this.logDebug("createNewHardGoodShippingGroup() returns ");
		}

		return hgSG;
	}

	/**
	 * Returns list of all HardgoodShippingGroup address in the order
	 *
	 * @param order
	 * @return listOfBBBAddress
	 */
	public List<BBBAddress> getAllShippingGroupAddress(final Order order) {
		final List<BBBAddress> addressList = new ArrayList<BBBAddress>();
		// get addresses of all hard good shipping groups

		@SuppressWarnings("unchecked")
		final
		List<ShippingGroup> sgs = order.getShippingGroups();
		if (null != sgs) {
			for (final ShippingGroup shippingGroup : sgs) {
				if (shippingGroup instanceof HardgoodShippingGroup) {
					final HardgoodShippingGroup hgsg = (HardgoodShippingGroup) shippingGroup;
					final Address shippingAddress = hgsg.getShippingAddress();
					if ((null != shippingAddress)
							&& !StringUtils.isEmpty(shippingAddress
									.getFirstName())) {

						final BBBAddress bbbSshippingAddress = (BBBAddress) shippingAddress;
						bbbSshippingAddress.setId(hgsg.getId());
						addressList.add(bbbSshippingAddress);
					}
				}
			}

		}

		return addressList;
	}

	/**
	 *
	 * @param shippingInfo
	 * @param shippingGroup
	 * @throws IntrospectionException
	 * @throws RepositoryException
	 */
	public void shipToAddress(final BBBShippingInfoBean shippingInfo,
			final BBBHardGoodShippingGroup shippingGroup) throws RepositoryException,
			IntrospectionException {
		shippingGroup.setChanged(true);

		if (shippingInfo.getPackAndHoldDate() != null) {
			shippingGroup.setShipOnDate(shippingInfo.getPackAndHoldDate());
		}else{
			shippingGroup.setShipOnDate(null);
		}
		if ((shippingInfo.getShippingConfirmationEmail() != null) && shippingInfo.isSendShippingConfirmation()) {
			shippingGroup.setShippingConfirmationEmail(shippingInfo.getShippingConfirmationEmail());
			shippingGroup.setSendShippingConfirmation(shippingInfo.isSendShippingConfirmation());
		}else {
			shippingGroup
			.getSpecialInstructions().remove(BBBCheckoutConstants.CONFIRMATION_EMAIL_ID);

		}
		if (shippingInfo.getAddress() != null) {
			shippingGroup.setPoBoxAddress(shippingInfo.getAddress().isPoBoxAddress());
			shippingGroup.setQASValidated(shippingInfo.getAddress().isQasValidated());			
			AddressTools.copyAddress((Address) shippingInfo.getAddress(),
					shippingGroup.getShippingAddress());
		}
		if (shippingInfo.getShippingMethod() != null) {
			shippingGroup.setShippingMethod(shippingInfo.getShippingMethod());
		}
		shippingGroup.setRegistryId(shippingInfo.getRegistryId());
		shippingGroup.setRegistryInfo(shippingInfo.getRegistryInfo());

	}

	/**
	 *
	 * @param order
	 * @return
	 */
	public List<ShipMethodVO> getShippingMethodsForOrder(final Order order) {

		if (this.isLoggingDebug()) {
			this.logDebug("Inside getShippingMethodsForOrder , order - " + order);
		}

		final List<ShipMethodVO> results = new ArrayList<ShipMethodVO>();
		final List<ShipMethodVO> removalResults = new ArrayList<ShipMethodVO>();
		final List<ShipMethodVO> removableResults = new ArrayList<ShipMethodVO>();
		int counter = 0;
		String catalogRefId = null;
		String siteId = null;
		boolean bbbCommerceItemFlag = false;
		boolean giftCardItemFlag = false;
		boolean sameDayDeliveryFlag = false;
 		String sddEligibleOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
 		if(null != sddEligibleOn){
 			sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
 		}
		//Added R2.1 COde requirment
		synchronized (order) {

				@SuppressWarnings("unchecked")
				final
			List<CommerceItem> items = order.getCommerceItems();
			try {
				for (final CommerceItem commerceItem : items) {

					siteId = order.getSiteId();
					// siteId = SiteContextManager.getCurrentSiteId();
					catalogRefId = commerceItem.getCatalogRefId();

					/***
					 * BBB-99 |BBBSystemException| Catalog API Method
					 * [isGiftCardItem]: RepositoryException : Ids cannot be
					 * null
					 */
					if (BBBUtility.isNotEmpty(catalogRefId)) {
						if (this.getCatalogUtil().isGiftCardItem(siteId, catalogRefId)) {
							giftCardItemFlag = true;
						}else if(commerceItem instanceof BBBCommerceItem){
							bbbCommerceItemFlag = true;
						}
						 List<ShipMethodVO> shippingMethodsForSku=new ArrayList<ShipMethodVO>();
						if(!(commerceItem instanceof LTLDeliveryChargeCommerceItem) && !(commerceItem instanceof LTLAssemblyFeeCommerceItem)){
						shippingMethodsForSku = this.getCatalogUtil()
								.getShippingMethodsForSku(siteId, catalogRefId, sameDayDeliveryFlag);
						}
	
						if (counter == 0) {// first time fill whatever is returned for
											// the first sku
							results.addAll(shippingMethodsForSku);
						} else {// compare with whatever is available in the results
							if(!shippingMethodsForSku.isEmpty() && shippingMethodsForSku.size()>0){
							for (final ShipMethodVO shipMethodVO : results) {
								if (!shippingMethodsForSku.contains(shipMethodVO)) {
									removalResults.add(shipMethodVO);
								}
							}
						}
						}
						final PurchaseProcessHelper pp =(PurchaseProcessHelper) ServletUtil.getCurrentRequest().resolveGlobalName("/atg/commerce/order/purchase/PurchaseProcessHelper");
						try {
							pp.adjustItemRelationshipsForQuantityChange(order, commerceItem, commerceItem.getQuantity());
						} catch (final CommerceException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
								logError(e.getMessage(),e);
						}
						counter++;
					}
				}

			if (!results.isEmpty() && !removalResults.isEmpty()) {
				results.removeAll(removalResults);
			}

			// exclude express shipping method for single gift cart item
			if ((null != items)
					&& !bbbCommerceItemFlag
					&& giftCardItemFlag) {
				for (final ShipMethodVO shipMethodVOItem : results) {

					if (BBBCoreConstants.SHIP_METHOD_EXPRESS_ID.equalsIgnoreCase(shipMethodVOItem.getShipMethodId())) {
						removableResults.add(shipMethodVOItem);
					}
				}

				results.removeAll(removableResults);

			}

		} catch (final BBBSystemException e) {
			if(this.isLoggingError()) {
			    this.logError(LogMessageFormatter.formatMessage(null, "SystemException"),
					e);
			}
		} catch (final BBBBusinessException e) {
		    if(this.isLoggingError()) {
		        this.logError(LogMessageFormatter.formatMessage(null,
					"BusinessException"), e);
		    }
		}
		}
		return results;
	}


	/**
	 * gives non registry shipping addresses for the order
	 *
	 * @param pOrder
	 * @return addressList
	 * @story UC_Checkout_Billing
	 */
	public List<com.bbb.commerce.common.BBBAddress> getNonRegistryShippingAddress(
			final BBBOrder pOrder) {
		final List<com.bbb.commerce.common.BBBAddress> addressList = new ArrayList<com.bbb.commerce.common.BBBAddress>();
		@SuppressWarnings("unchecked")
		final
		List<HardgoodShippingGroup> hgsgs = this.getHardgoodShippingGroups(pOrder);
		if ((null != hgsgs) && !hgsgs.isEmpty()) {
			for (final HardgoodShippingGroup hgsg : hgsgs) {
				final com.bbb.commerce.common.BBBAddress address = (com.bbb.commerce.common.BBBAddress) hgsg
						.getShippingAddress();
				if ((null != address) && (null == address.getRegistryInfo())) {
					addressList.add(address);
					address.setPoBoxAddress(((BBBHardGoodShippingGroup)hgsg).getIsPoBoxAddress());
					address.setQasValidated(((BBBHardGoodShippingGroup)hgsg).getqasValidated());
				}
			}
		}
		return addressList;
	}

	/**
	 * This methods takes the skuId and siteId and returns the list of shipping
	 * methods
	 *
	 * @param skuId
	 *            skuId of the commerce item
	 * @param siteId
	 *            siteId
	 * @return list of ShipMethodVO
	 */
	public List<ShipMethodVO> getShippingMethodsForSku(final String skuId,
			final String siteId) throws BBBSystemException, BBBBusinessException {

		if (this.isLoggingDebug()) {
			this.logDebug("Inside getShippingMethodsForSku , skuId - " + skuId
					+ ", Site Id -" + siteId);
		}
		boolean sameDayDeliveryFlag = false;
 		String sddEligibleOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
 		if(null != sddEligibleOn){
 			sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
 		}
		final List<ShipMethodVO> shippingMethodsForSku = this.getCatalogUtil().getShippingMethodsForSku(
					siteId, skuId, sameDayDeliveryFlag);

		if(this.getCatalogUtil().isGiftCardItem(siteId,
				skuId)) {
			for (final Iterator<ShipMethodVO> iterator = shippingMethodsForSku.iterator(); iterator.hasNext();) {
				final ShipMethodVO shipMethodVOItem = iterator.next();
				if (BBBCoreConstants.SHIP_METHOD_EXPRESS_ID.equalsIgnoreCase(shipMethodVOItem.getShipMethodId())) {
					iterator.remove();
				}

			}

		}
		return shippingMethodsForSku;
	}

	/**
	 * This method returns ShippingMethodVO which contains shipping method
	 * details based on passed shippingID.
	 *
	 * @param pShippingID
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public ShipMethodVO getShippingMethodForID(final String pShippingID)
			throws BBBBusinessException, BBBSystemException {

		ShipMethodVO shipMethodVO = null;
		if (this.isLoggingDebug()) {
			this.logDebug("[START] getShippingMethodForID , pShippingID - "
					+ pShippingID);
		}

		if (pShippingID != null) {
			shipMethodVO = new ShipMethodVO(this.getCatalogUtil().getShippingMethod(
					pShippingID));
		}

		if (this.isLoggingDebug()) {
			this.logDebug("[END] getShippingMethodForID , shipMethodVO - "
					+ shipMethodVO);
		}
		return shipMethodVO;
	}

	/**
	 * This methods splits input CommerceItemShippingInfo and
	 * creates multiple CommerceItemShippingInfo
	 *
	 * @param skuId
	 *            skuId of the commerce item
	 * @param siteId
	 *            siteId
	 * @return list of ShipMethodVO
	 * @throws CommerceException
	 * @throws RepositoryException 
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public void splitCommerceItemShippingInfo(
			final CommerceItemShippingInfo pCurrentCISI, final Order pOrder,
			final CommerceItemShippingInfoContainer pCommerceItemShippingInfoContainer)
			throws CommerceException, BBBSystemException, BBBBusinessException{
		if (this.isLoggingDebug()) {
			this.logDebug("Inside splitCommerceItemShippingInfo , pCurrentCISI - "
					+ pCurrentCISI + ", pOrder -" + pOrder
					+ ", pCommerceItemShippingInfoContainer -"
					+ pCommerceItemShippingInfoContainer);
		}
		final long pSplitQuantity = 1;
		long existingQty = 0;
		while (pCurrentCISI.getQuantity() > 1) {
			existingQty = pCurrentCISI.getQuantity();
			if(((BBBCommerceItem)pCurrentCISI.getCommerceItem()).isLtlItem()){
				this.getCisiTools().splitCommerceItemShippingInfoForLTLByQuantity(
						pCommerceItemShippingInfoContainer, pCurrentCISI,
						pSplitQuantity, pOrder);
			} else{
				this.getCisiTools().splitCommerceItemShippingInfoByQuantity(
						pCommerceItemShippingInfoContainer, pCurrentCISI,
						pSplitQuantity);
			}
			pCurrentCISI.setQuantity(existingQty - 1);
		}

	}

	/**
	 * Copy ShippingAddress from pFromShippingGroup to pToShippingGroup
	 *
	 * @ pFromShippingGroup
	 * @ pToShippingGroup
	 */

	@Override
	public void copyShippingAddress(final ShippingGroup pFromShippingGroup,
			final ShippingGroupImpl pToShippingGroup) throws CommerceException {
		if (pToShippingGroup instanceof ShippingAddressContainer) {
			final ShippingAddressContainer newHg = (ShippingAddressContainer) pToShippingGroup;
			final ShippingAddressContainer oldHg = (ShippingAddressContainer) pFromShippingGroup;
			final Address oldAddress = oldHg.getShippingAddress();
			Address newAddress;
			if (oldAddress instanceof BBBRepositoryContactInfo) {
				newAddress = new BBBRepositoryContactInfo(
						pToShippingGroup.getRepositoryItem());
			} else if (oldAddress instanceof RepositoryContactInfo) {
				newAddress = new RepositoryContactInfo(
						pToShippingGroup.getRepositoryItem());
			} else if (oldAddress instanceof RepositoryAddress) {
				newAddress = new RepositoryAddress(
						pToShippingGroup.getRepositoryItem());
			} else if (oldAddress instanceof ContactInfo) {
				newAddress = new ContactInfo();
			} else {
				newAddress = new Address();
				OrderTools.copyAddress(oldAddress, newAddress);
				newHg.setShippingAddress(newAddress);
			}
			if (pToShippingGroup instanceof EmailAddressContainer) {
				final EmailAddressContainer eac = (EmailAddressContainer) pFromShippingGroup;
				final String addr = eac.getEmailAddress();
				if (addr != null) {
					((EmailAddressContainer) pToShippingGroup)
							.setEmailAddress(addr);
				}
			}
		}

	}


	public BBBHardGoodShippingGroup getMatchingSGForIdAndMethod(final Order pOrder, final BBBAddressContainer pContainer,
					final String pSourceId, final String pShippingMethod)
					throws CommerceException{

		BBBHardGoodShippingGroup bbbHGSG = null;
		final List<BBBHardGoodShippingGroup> bbbSGList = new ArrayList<BBBHardGoodShippingGroup>();

		//Add all SG with matching sourceId into the list
	    for (final Object obj : pOrder.getShippingGroups()) {
			if(obj instanceof BBBHardGoodShippingGroup){
				final BBBHardGoodShippingGroup tempSG = (BBBHardGoodShippingGroup)obj;
				if((tempSG.getSourceId() != null) && pSourceId.equalsIgnoreCase(tempSG.getSourceId()) ){
					bbbSGList.add((BBBHardGoodShippingGroup)obj);
				}
			}
		}

	    if(bbbSGList.size() > 0){
	    	//If any SG with matching shipping method is found return it
		    for (final BBBHardGoodShippingGroup bbbHardGoodShippingGroup : bbbSGList) {
				if(pShippingMethod.equalsIgnoreCase(bbbHardGoodShippingGroup.getShippingMethod())){
					return bbbHardGoodShippingGroup;
				}
			}
		    final BBBAddress pAddress = (BBBAddress)pContainer.getAddressFromContainer(pSourceId);
		    final BBBAddress pDestAddress = new BBBAddressImpl();
		    try {
				AddressTools.copyAddress((Address)pAddress, (Address)pDestAddress);
			} catch (IntrospectionException e) {
            	this.logError("BBBShippingGroupManager:getMatchingSGForIdAndMethod IntrospectionException" + e.getMessage(),e);
            	throw new RuntimeException(e.getMessage()); //NOPMD
			}
		    pContainer.addAddressToContainer(pSourceId, pDestAddress);
 		    // Clone the 1st SG which matches to the sourceID, set Shipping Method and return
		    final BBBHardGoodShippingGroup clonedShipGroup = (BBBHardGoodShippingGroup)this.cloneShippingGroup(bbbSGList.get(0));
		    clonedShipGroup.setShippingAddress((Address)pDestAddress);
		    clonedShipGroup.setPoBoxAddress(((BBBAddress) pAddress).isPoBoxAddress());
		    clonedShipGroup.setQASValidated(((BBBAddress) pAddress).isQasValidated());			
			clonedShipGroup.setShippingMethod(pShippingMethod);
			clonedShipGroup.setSourceId(pSourceId);
			if( BBBCheckoutConstants.REGISTRY_SOURCE.equalsIgnoreCase( ((BBBAddress)pAddress).getSource()) ){
				final String registryId = ((BBBAddress)pAddress).getId();
				clonedShipGroup.setRegistryId(registryId);
				clonedShipGroup.setRegistryInfo(this.getRegistryInfo(pOrder, registryId));



			}
			this.addShippingGroupToOrder(pOrder, clonedShipGroup);
			return clonedShipGroup;
	    }else{
	    	//If there is no SG which matches to the source Id then create a new SG with the address, shippingMethod & sourceId
	    	bbbHGSG = (BBBHardGoodShippingGroup)this.createShippingGroup(this.getHardGoodShippingGroupType());
	    	final Address pAddress = (Address)pContainer.getAddressFromContainer(pSourceId);
	    	bbbHGSG.setShippingAddress(pAddress);
	    	bbbHGSG.setPoBoxAddress(((BBBAddress) pAddress).isPoBoxAddress());
	    	bbbHGSG.setQASValidated(((BBBAddress) pAddress).isQasValidated());
	    	bbbHGSG.setShippingMethod(pShippingMethod);
	    	bbbHGSG.setSourceId(pSourceId);
	    	if( BBBCheckoutConstants.REGISTRY_SOURCE.equalsIgnoreCase( ((BBBAddress)pAddress).getSource()) ){
	    		bbbHGSG.setRegistryId(((BBBAddress)pAddress).getId());
	    		bbbHGSG.setRegistryInfo(this.getRegistryInfo(pOrder, ((BBBAddress)pAddress).getId()));
			}
			this.addShippingGroupToOrder(pOrder, bbbHGSG);
	    }

		return bbbHGSG;
	}

	
	/**
	 * This method creates assembly and delivery items and CISI for commerce items present in sgciMap.
	 * @param sgciMap
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws CommerceException
	 * @throws RepositoryException
	 */
	public void createDelAssItemForSGCIMap(Map<String, List<String>> sgciMap, Order order, ShippingGroupMapContainer shippingGroupMapContainer,
			CommerceItemShippingInfoContainer commerceItemShippingInfoContainer)
			throws BBBBusinessException, BBBSystemException, CommerceException, RepositoryException {
		for(String sgid : sgciMap.keySet()){
			//create delivery item
			BBBHardGoodShippingGroup shipGrp = (BBBHardGoodShippingGroup) order.getShippingGroup(sgid);
			if(!sgciMap.get(sgid).isEmpty()){
				for(String commerceItemId : sgciMap.get(sgid)){
					BBBCommerceItem comitem = (BBBCommerceItem) order.getCommerceItem(commerceItemId);
					/* When, an LTL item is added to the cart from registry as a first item, all the items are being assigned delivery and assembly commerce item ids. */
					if (comitem.isLtlItem()) {
						String deliveryId = ((BBBCommerceItemManager)getCommerceItemManager()).addLTLDeliveryChargeSku(order, shipGrp,order.getSiteId(), comitem);
						comitem.setDeliveryItemId(deliveryId);
						String assemblyId = "";
						String isAssemblySelected = comitem.getWhiteGloveAssembly();
						if(BBBUtility.isNotEmpty(isAssemblySelected) && isAssemblySelected.equalsIgnoreCase(BBBCatalogConstants.TRUE)){
							assemblyId = ((BBBCommerceItemManager)getCommerceItemManager()).addLTLAssemblyFeeSku(order, shipGrp, order.getSiteId(), comitem);
							comitem.setAssemblyItemId(assemblyId);
						}
						//create cisi for delivery and assembly items.
						CommerceItemShippingInfo delcisi = new CommerceItemShippingInfo();
						CommerceItemShippingInfo asscisi = new CommerceItemShippingInfo();
						delcisi.setCommerceItem(order.getCommerceItem(deliveryId));
						delcisi.setRelationshipType("SHIPPINGQUANTITY");
						delcisi.setShippingGroupName(shippingGroupMapContainer.getDefaultShippingGroupName());
						delcisi.setQuantity(comitem.getQuantity());
						String shippingMethod = ((CommerceItemShippingInfo)commerceItemShippingInfoContainer.getCommerceItemShippingInfos(commerceItemId).get(0)).getShippingMethod();
						delcisi.setShippingMethod(shippingMethod);
						commerceItemShippingInfoContainer.addCommerceItemShippingInfo(deliveryId, delcisi);
						if(BBBUtility.isNotEmpty(assemblyId)){
							asscisi.setCommerceItem(order.getCommerceItem(assemblyId));
							asscisi.setRelationshipType("SHIPPINGQUANTITY");
							asscisi.setShippingGroupName(shippingGroupMapContainer.getDefaultShippingGroupName());
							asscisi.setQuantity(comitem.getQuantity());
							asscisi.setShippingMethod(shippingMethod);
							commerceItemShippingInfoContainer.addCommerceItemShippingInfo(assemblyId, asscisi);
						}
					}
					//((BBBCommerceItemManager)getCommerceItemManager()).getltlItemsAssoc(deliveryId, assemblyId, shipGrp,comitem.getId());
				}
			}
		}
	}
	
	
	/**
	 * This method adds removes assembly, if applicable, for commerceIdListWithAss and commerceIdListWithoutAss
	 * @param sgciMapForWgWithAss
	 * @param sgciMapForWgWithoutAss
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws CommerceException
	 * @throws RepositoryException
	 */
	public void addRemoveAssForCommerceItems(
			Map<String, List<String>> sgciMapForWgWithAss, Map<String, List<String>> sgciMapForWgWithoutAss,
			Order order, ShippingGroupMapContainer shippingGroupMapContainer,
			CommerceItemShippingInfoContainer commerceItemShippingInfoContainer)
			throws BBBBusinessException, BBBSystemException, CommerceException, RepositoryException {
		
		//add assembly for commerceIdListWithAss
		if(!sgciMapForWgWithAss.isEmpty()){
			for(String sgid : sgciMapForWgWithAss.keySet()){
				BBBHardGoodShippingGroup shipGrp = (BBBHardGoodShippingGroup) order.getShippingGroup(sgid);
				if(!sgciMapForWgWithAss.get(sgid).isEmpty()){
					for(String commerceItemId : sgciMapForWgWithAss.get(sgid)){
						//String assemblyCommItemId = (String) ((RepositoryItem)shipGrp.getLTLItemMap().get(commerceItemId)).getPropertyValue("assemblyItemId");
						String assemblyCommItemId = ((BBBCommerceItem)order.getCommerceItem(commerceItemId)).getAssemblyItemId();
						BBBCommerceItem comitemWithAss = (BBBCommerceItem) order.getCommerceItem(commerceItemId);
						if(BBBUtility.isEmpty(assemblyCommItemId)){
							CommerceItemShippingInfo asscisi = new CommerceItemShippingInfo();
							assemblyCommItemId = ((BBBCommerceItemManager)getCommerceItemManager()).addLTLAssemblyFeeSku(order, shipGrp, order.getSiteId(), comitemWithAss);
							comitemWithAss.setAssemblyItemId(assemblyCommItemId);
							//((MutableRepositoryItem)shipGrp.getLTLItemMap().get(commerceItemId)).setPropertyValue("assemblyItemId", assemblyCommItemId);
							asscisi.setCommerceItem(order.getCommerceItem(assemblyCommItemId));
							asscisi.setRelationshipType("SHIPPINGQUANTITY");
							asscisi.setShippingGroupName(shippingGroupMapContainer.getDefaultShippingGroupName());
							asscisi.setQuantity(comitemWithAss.getQuantity());
							asscisi.setShippingMethod(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD);
							commerceItemShippingInfoContainer.addCommerceItemShippingInfo(assemblyCommItemId, asscisi);
						}
					}
				}
			}
		}
		//remove assembly for commerceIdListWithoutAss
		if(!sgciMapForWgWithoutAss.isEmpty()){
			for(String sgid : sgciMapForWgWithoutAss.keySet()){
				BBBHardGoodShippingGroup shipGrp = (BBBHardGoodShippingGroup) order.getShippingGroup(sgid);
				if(!sgciMapForWgWithoutAss.get(sgid).isEmpty()){
					for(String commerceItemId : sgciMapForWgWithoutAss.get(sgid)){
						//String assemblyCommItemId = (String) ((RepositoryItem)shipGrp.getLTLItemMap().get(commerceItemId)).getPropertyValue("assemblyItemId");
						String assemblyCommItemId = ((BBBCommerceItem)order.getCommerceItem(commerceItemId)).getAssemblyItemId();
						if(BBBUtility.isNotEmpty(assemblyCommItemId)){
							this.getCommerceItemManager().removeAllRelationshipsFromCommerceItem(order, assemblyCommItemId);
							this.getCommerceItemManager().removeItemFromOrder(order, assemblyCommItemId);
							//((MutableRepositoryItem)shipGrp.getLTLItemMap().get(commerceItemId)).setPropertyValue("assemblyItemId", "");
							((BBBCommerceItem)order.getCommerceItem(commerceItemId)).setAssemblyItemId(null);
							commerceItemShippingInfoContainer.removeCommerceItemShippingInfos(assemblyCommItemId);
						}
					}
				}
			}
		}
	}
	
	/**
	 * This method is used to apply LTLItemAssocMap to newly created shipping groups for ltl items.
	 * @param sgLtlItemAssocList
	 * @throws CommerceException
	 * @throws RepositoryException
	 */
	public void applyLTLItemAssocMap(List<ShippingGroup> sglist,
			Order order)
			throws CommerceException, RepositoryException {
		Map <String, List<BBBCommerceItem>> shipgrpToCIMap = new HashMap<String, List<BBBCommerceItem>>();
		for(ShippingGroup shipGroup : sglist){
        	if(shipGroup instanceof BBBHardGoodShippingGroup){
        		List <BBBShippingGroupCommerceItemRelationship> sgciRelList= shipGroup.getCommerceItemRelationships();
        		List<BBBCommerceItem> commitemList = new ArrayList<BBBCommerceItem>();
        		for(BBBShippingGroupCommerceItemRelationship sgciRel: sgciRelList){
        			if(sgciRel.getCommerceItem() instanceof BBBCommerceItem){
        				if(((BBBCommerceItem)sgciRel.getCommerceItem()).isLtlItem()){
        					commitemList.add((BBBCommerceItem)sgciRel.getCommerceItem());
    					} else{
    						break;
    					}
        			}
        		}
        		if(!commitemList.isEmpty()){
    				shipgrpToCIMap.put(shipGroup.getId(), commitemList);
    			}
        	}
        }
		for(String shipGrpId : shipgrpToCIMap.keySet()){
			for(BBBCommerceItem commerceItem : shipgrpToCIMap.get(shipGrpId)){
				this.getCommerceItemManager().removeAllRelationshipsFromCommerceItem(order, commerceItem.getDeliveryItemId());
				this.getCommerceItemManager().addItemQuantityToShippingGroup(order, commerceItem.getDeliveryItemId(), shipGrpId, commerceItem.getQuantity());
				if(BBBUtility.isNotEmpty(commerceItem.getAssemblyItemId())){
					this.getCommerceItemManager().removeAllRelationshipsFromCommerceItem(order, commerceItem.getAssemblyItemId());
					this.getCommerceItemManager().addItemQuantityToShippingGroup(order, commerceItem.getAssemblyItemId(), shipGrpId, commerceItem.getQuantity());
				}
			}
		}
	}
	
	/**
	 * This method is used to merge cisi when shipping method is same for ltl items
	 * @param sgAfterApply
	 * @param skuCommItemListMap
	 * @throws CommerceItemNotFoundException
	 * @throws InvalidParameterException
	 * @throws CommerceException
	 */
	public void mergeCISIforSameLTLShipMethod(ShippingGroup sgAfterApply,
			Map<String, List<String>> skuCommItemListMap, Order order, PurchaseProcessHelper purchaseProcessHelper, CommerceItemShippingInfoContainer commerceItemShippingInfoContainer)
			throws CommerceException {
		if(!skuCommItemListMap.keySet().isEmpty()){
			if(ServletUtil.getCurrentRequest().getContextPath().equalsIgnoreCase(BBBCoreConstants.CONTEXT_TBS)){
				for(String skuid : skuCommItemListMap.keySet()){
					ListIterator<String> iter= skuCommItemListMap.get(skuid).listIterator();
					while(iter.hasNext())
					{
						String commItemid =(String)iter.next();
						if(order.getCommerceItem(commItemid)!=null && ((TBSCommerceItem)order.getCommerceItem(commItemid)).getTBSItemInfo()!=null &&((TBSCommerceItem)order.getCommerceItem(commItemid)).getTBSItemInfo().getOverrideQuantity()>0)
						{
							iter.remove();
						}
					}
				}
			}			
		    for(String skuId : skuCommItemListMap.keySet()){
		    	if(skuCommItemListMap.get(skuId).size() > 1){
		    		int qty = 0;
		    		for(String commItemId : skuCommItemListMap.get(skuId)){
		    			qty += order.getCommerceItem(commItemId).getQuantity();
		    		}
		    		purchaseProcessHelper.adjustItemRelationshipsForQuantityChange(order,order.getCommerceItem(skuCommItemListMap.get(skuId).get(0)),qty);
		    		order.getCommerceItem(skuCommItemListMap.get(skuId).get(0)).setQuantity(qty);
		    		if(sgAfterApply instanceof BBBHardGoodShippingGroup){
		    			String deliveryItemId = ((BBBCommerceItem)order.getCommerceItem(skuCommItemListMap.get(skuId).get(0))).getDeliveryItemId();
		    			String assemblyItemId = ((BBBCommerceItem)order.getCommerceItem(skuCommItemListMap.get(skuId).get(0))).getAssemblyItemId();
		    			//RepositoryItem ltlItemsAssoc = (RepositoryItem) ((BBBHardGoodShippingGroup)sgAfterApply).getLTLItemMap().get(order.getCommerceItem(skuCommItemListMap.get(skuId).get(0)).getId());
		    			//String deliveryItemId = (String) ltlItemsAssoc.getPropertyValue("deliveryItemId");
		    			//String assemblyItemId = (String)ltlItemsAssoc.getPropertyValue("assemblyItemId");
		    			purchaseProcessHelper.adjustItemRelationshipsForQuantityChange(order,order.getCommerceItem(deliveryItemId),qty);
		    			order.getCommerceItem(deliveryItemId).setQuantity(qty);
		    			((CommerceItemShippingInfo)commerceItemShippingInfoContainer.getCommerceItemShippingInfos(skuCommItemListMap.get(skuId).get(0)).get(0)).setQuantity(qty);
		    			((CommerceItemShippingInfo)commerceItemShippingInfoContainer.getCommerceItemShippingInfos(deliveryItemId).get(0)).setQuantity(qty);
		    			if(BBBUtility.isNotEmpty(assemblyItemId)){
		    				purchaseProcessHelper.adjustItemRelationshipsForQuantityChange(order,order.getCommerceItem(assemblyItemId),qty);
		    				order.getCommerceItem(assemblyItemId).setQuantity(qty);
		    				((CommerceItemShippingInfo)commerceItemShippingInfoContainer.getCommerceItemShippingInfos(assemblyItemId).get(0)).setQuantity(qty);
		    			}
		        		for (int commItemCount = 1 ; commItemCount < skuCommItemListMap.get(skuId).size() ; commItemCount++){
		        			CommerceItem commItem = order.getCommerceItem(skuCommItemListMap.get(skuId).get(commItemCount));
		        			String deliveryItemId1 = ((BBBCommerceItem)commItem).getDeliveryItemId();
		        			String assemblyItemId1 = ((BBBCommerceItem)commItem).getAssemblyItemId();
		        			//RepositoryItem ltlItemsAssoc1 = (RepositoryItem) ((BBBHardGoodShippingGroup)sgAfterApply).getLTLItemMap().get(commItem.getId());
		        			//String deliveryItemId1 = (String) ltlItemsAssoc1.getPropertyValue("deliveryItemId");
		        			//String assemblyItemId1 = (String)ltlItemsAssoc1.getPropertyValue("assemblyItemId");
		        			((BBBCommerceItemManager)this.getCommerceItemManager()).removeDeliveryAssemblyCIFromOrderByCISg(commItem.getId(), order);
		        			((BBBCommerceItem)commItem).setDeliveryItemId(null);
		        			((BBBCommerceItem)commItem).setAssemblyItemId(null);
		        			this.getCommerceItemManager().removeAllRelationshipsFromCommerceItem(order, commItem.getId());
		        			this.getCommerceItemManager().removeItemFromOrder(order, commItem.getId());
		        			commerceItemShippingInfoContainer.removeCommerceItemShippingInfos(commItem.getId());
		        			commerceItemShippingInfoContainer.removeCommerceItemShippingInfos(deliveryItemId1);
		        			if(BBBUtility.isNotEmpty(assemblyItemId1)){
		        				commerceItemShippingInfoContainer.removeCommerceItemShippingInfos(assemblyItemId1);
		        			}
		        		}
		        	}
		    	}
		    }
		}
	}
	
	
	/**
	 * @param pOrder
	 * @param registryId
	 */
	public String getRegistryInfo(final Order pOrder, final String registryId) {
		final Map registryMap = ((BBBOrderImpl) pOrder).getRegistryMap();
		final RegistrySummaryVO registry = (RegistrySummaryVO) registryMap.get(registryId);
		final StringBuffer registryInfo = new StringBuffer("<strong>");
		registryInfo.append(registry.getPrimaryRegistrantFirstName()).append(" ").append(registry.getPrimaryRegistrantLastName());
		if(!StringUtils.isEmpty(registry.getCoRegistrantFirstName())) {
		    registryInfo.append(" & ").append(registry.getCoRegistrantFirstName()).append(" ").append(registry.getCoRegistrantLastName());
		}
		registryInfo.append("</strong> (Registry #").append(registryId).append(")");

		return registryInfo.toString();
	}


	@Override
	public void addShippingGroupToOrder(final Order pOrder,
			final ShippingGroup pShippingGroup) throws CommerceException {
		if (pOrder == null) {
			throw new InvalidParameterException(ResourceUtils.getMsgResource(
					"InvalidOrderParameter",
					"atg.commerce.order.OrderResources", sResourceBundle));
		}
		if (pShippingGroup == null) {
			throw new InvalidParameterException(ResourceUtils.getMsgResource(
					"InvalidShippingGroupParameter",
					"atg.commerce.order.OrderResources", sResourceBundle));
		}
		/*if (pShippingGroup.getCommerceItemRelationshipCount() > 0)
			throw new InvalidParameterException(ResourceUtils.getMsgResource(
					"CannotAddShippingGroupWithRelationships",
					"atg.commerce.order.OrderResources", sResourceBundle));*/
		try {
			pOrder.addShippingGroup(pShippingGroup);
		} catch (final DuplicateShippingGroupException e) {
			final String msgArgs[] = { pShippingGroup.getId(), pOrder.getId() };
			throw new CommerceException(ResourceUtils.getMsgResource(
					"DuplicateShippingGroupIdDetected",
					"atg.commerce.order.OrderResources", sResourceBundle,
					msgArgs), e);
		}
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getAllHardgoodShippingGroups(final Order pOrder) {
		final List sgs = new ArrayList();
        final Collection shippingGroups = pOrder.getShippingGroups();
        final Iterator shippingGrouperator = shippingGroups.iterator();
        ShippingGroup sg = null;
        do
        {
            if(!shippingGrouperator.hasNext()) {
                break;
            }
            sg = (ShippingGroup)shippingGrouperator.next();
            if(sg instanceof HardgoodShippingGroup)
            {
            	sgs.add(sg);
            }
        } while(true);
        return sgs;
	}

	/**
	 * This method removes address from shipping group if cart is empty and only
	 * one Shipping group of type BBBHardGoodShippingGroup is left.
	 */
	@Override
	public void removeEmptyShippingGroups(final Order pOrder)
			throws CommerceException {
		if (this.isLoggingDebug()) {
			this.logDebug("[START] BBBShippingGroupManager.removeEmptyShippingGroups");
		}
		BBBSessionBean sessionBean = null;
		BBBAddressContainer addressContainer = null;
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		if (pRequest !=null) {
			sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
			addressContainer = (BBBAddressContainer) pRequest.resolveName("/com/bbb/commerce/common/BBBAddressContainer");
		}

		final List<ShippingGroup> shippingGroups = pOrder.getShippingGroups();
		if((null != shippingGroups) && !shippingGroups.isEmpty()){
			final ArrayList<String> removalList = new ArrayList(shippingGroups.size());
			for (final Object element : shippingGroups) {
				final ShippingGroup sg = (ShippingGroup) element;
				if(sg instanceof BBBStoreShippingGroup){
					if(sg.getCommerceItemRelationshipCount() == 0){
						removalList.add(sg.getId());
					}
				}
			}
			if(!removalList.isEmpty()){
				for (final String shippingGroupId : removalList) {
					super.removeShippingGroupFromOrder(pOrder, shippingGroupId);
				}
			}
		}

		if ((pOrder.getCommerceItemCount() == 0)
				&& (pOrder.getShippingGroupCount() == 1)) {

			final Object sGrp = pOrder.getShippingGroups().get(0);
			if (sGrp instanceof BBBHardGoodShippingGroup) {
				// PS-34092 || Removing Shipping Group from Order as it was creating problem in Multi Shipping
				if (this.isLoggingDebug()) {
					this.logDebug("Removing Last Shipping Group from Order");
				}
				
				if(sessionBean !=null && sessionBean.isSinglePageCheckout()){
					if(!addressContainer.getAddressMap().isEmpty()){
						addressContainer.getAddressMap().clear();	
					}
					if(!addressContainer.getDuplicate().isEmpty()){
						addressContainer.getDuplicate().clear();	
					}
	        	}
				super.removeShippingGroupFromOrder(pOrder, ((BBBHardGoodShippingGroup)sGrp).getId());
			}
		} else {
			
			if (addressContainer != null) {
				this.removeAddFromContainer(pOrder, addressContainer, BBBCoreConstants.BLANK);
			
				if(pOrder.getCommerceItemCount() == 0 && sessionBean!=null && sessionBean.isSinglePageCheckout()){
					if(!addressContainer.getAddressMap().isEmpty()){
						addressContainer.getAddressMap().clear();	
					}
					if(!addressContainer.getDuplicate().isEmpty()){
						addressContainer.getDuplicate().clear();	
					}
										
	        	}
			}
			super.removeEmptyShippingGroups(pOrder);
		}

		if (this.isLoggingDebug()) {
			this.logDebug("[END] BBBShippingGroupManager.removeEmptyShippingGroups");
		}
	}


	/**
	 * This method iterates through all the sku-ids and checks whether all the skus are eligible to be shipped via particular method or not
	 * @param skus : List of sku-ids
	 * @param shippingMethod : shipping method against which shipping eligibility of the skus will be checked
	 * @return true: if all the skus are eligible to be shipped via given shipping method
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	public boolean checkShippingMethodForSkus(final String skus, final String shippingMethod) throws BBBBusinessException, BBBSystemException{
		if(((skus == null) || skus.isEmpty()) ||((shippingMethod == null) || shippingMethod.trim().equals(""))){
			throw new BBBBusinessException(BBBCoreErrorConstants.REQUIRED_PARA_NULL, "required Parameter is null or empty");
		}
		final String[] skuIds = skus.split(",");
		boolean isValidShippingMethod = false;
		final String siteId = SiteContextManager.getCurrentSiteId();
		boolean sameDayDeliveryFlag = false;
 		String sddEligibleOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
 		if(null != sddEligibleOn){
 			sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
 		}
		for(final String skuId : skuIds ){
			isValidShippingMethod = false;
			if((skuId == null) || skuId.trim().equals("")){
				throw new BBBBusinessException(BBBCoreErrorConstants.REQUIRED_PARA_NULL, "required Parameter is null or empty");
			}
			if(((BBBCoreConstants.SHIP_METHOD_EXPRESS_ID).equalsIgnoreCase(shippingMethod)) && ((this.getCatalogUtil().isGiftCardItem(siteId,
					skuId)))){
				return false;
			}
			final List<ShipMethodVO> skuShippingMethodsVo = this.getCatalogUtil().getShippingMethodsForSku(siteId, skuId, sameDayDeliveryFlag);
			for(final ShipMethodVO shipMethodVo:skuShippingMethodsVo){
				if(shippingMethod.equalsIgnoreCase(shipMethodVo.getShipMethodId())){
					isValidShippingMethod = true;
					break;
				}
			}
			if(!isValidShippingMethod){
				break;
			}
		}
		return isValidShippingMethod;
	}

	/**
	 * The method takes the order object and returns the first hardgoodshipping group
	 * which has zero relationship in the order. If hardgood shipping group is
	 * not found in the order then it returns null
	 * @throws BBBBusinessException 
	 */
	public HardgoodShippingGroup getFirstNonGiftHardgoodShippingGroupWithoutRels(final Order pOrder) throws BBBBusinessException {

		if(this.isLoggingDebug()){
			this.logDebug("Entry getFirstNonGiftHardgoodShippingGroupWithoutRels");
		}

		@SuppressWarnings("unchecked")
		final
		List<ShippingGroup> shippingGroupList = pOrder.getShippingGroups();
		try {
		// LTL-1285 | Registry Item should be added to NON LTL SG | START
		final RepositoryItem[] applicableShipMethods = this.getCmsTools().getShippingMethods(pOrder.getSiteId());
		List<String> nonLTLSG = new ArrayList<String>();
		nonLTLSG.add(BBBCoreConstants.HARD_GOODS_SHIP_GROUP_ITEM_DESCRIPTOR);
		for(final RepositoryItem repositoryItem : applicableShipMethods) {
			if(null != repositoryItem.getPropertyValue("isLTLShippingMethod") && !(Boolean)repositoryItem.getPropertyValue("isLTLShippingMethod")) 
				nonLTLSG.add((String)repositoryItem.getPropertyValue("id"));
		}
		// LTL-1285 | Registry Item should be added to NON LTL SG | END
		if (null != shippingGroupList) {

			for (final ShippingGroup shippingGroup : shippingGroupList) {
				// LTL-1285 Added extra check for non LTL SG
				if (shippingGroup instanceof BBBHardGoodShippingGroup && nonLTLSG.contains(shippingGroup.getShippingMethod())) {

					@SuppressWarnings("unchecked")
					final
					List<CommerceItemRelationship> relationships  = shippingGroup.getCommerceItemRelationships();
					if(relationships != null && relationships.size() == 0 ) {
						if(this.isLoggingDebug()){
							this.logDebug("returning first HardGoodShippingGroup without Rels - " + shippingGroup.getId());
						}
						return (BBBHardGoodShippingGroup)shippingGroup;
					}
				}
			}
		}
		} catch (RepositoryException e) {
				logError(e);
			
			throw new BBBBusinessException(BBBCatalogErrorCodes.SHIPPING_METHOD_NOT_CONFIGURED_FOR_SITE_IN_REPOSITORY, "No shipping method available for site:"+pOrder.getSiteId());
		}
		if(this.isLoggingDebug()){
			this.logDebug("Exit getFirstNonGiftHardgoodShippingGroupWithoutRels");
		}
		return null;
	}
	
	/** LTL-1285 | Registry Item should be added to NON LTL SG
	 * The method takes the order object and returns the first hardgoodshipping group
	 * which has at least one relationship in the order. If hard good shipping group is
	 * not found in the order then it returns null
	 * @throws BBBBusinessException 
	 */
	public HardgoodShippingGroup getFirstNonLTLHardgoodShippingGroupWithRels(final Order pOrder) throws BBBBusinessException {

		if(this.isLoggingDebug()){
			this.logDebug("Entry getFirstNonGiftHardgoodShippingGroupWithoutRels");
		}

		@SuppressWarnings("unchecked")
		final
		List<ShippingGroup> shippingGroupList = pOrder.getShippingGroups();
		try {
			
		final RepositoryItem[] applicableShipMethods = this.getCmsTools().getShippingMethods(pOrder.getSiteId());
		List<String> nonLTLSG = new ArrayList<String>();
		nonLTLSG.add(BBBCoreConstants.HARD_GOODS_SHIP_GROUP_ITEM_DESCRIPTOR);
		for(final RepositoryItem repositoryItem : applicableShipMethods) {
			if(null != repositoryItem.getPropertyValue("isLTLShippingMethod") && !(Boolean)repositoryItem.getPropertyValue("isLTLShippingMethod")) 
				nonLTLSG.add((String)repositoryItem.getPropertyValue("id"));
		}

		if (null != shippingGroupList) {

			for (final ShippingGroup shippingGroup : shippingGroupList) {
				if (shippingGroup instanceof BBBHardGoodShippingGroup && nonLTLSG.contains(shippingGroup.getShippingMethod())) {

					@SuppressWarnings("unchecked")
					final
					List<CommerceItemRelationship> relationships  = shippingGroup.getCommerceItemRelationships();
					if((relationships != null) && (relationships.size() > 0) ) {
						if(this.isLoggingDebug()){
							this.logDebug("returning first HardGoodShippingGroup without Rels - " + shippingGroup.getId());
						}
						return (BBBHardGoodShippingGroup)shippingGroup;
					}
				}
			}
		}
		} catch (RepositoryException e) {
				logError(e);
			throw new BBBBusinessException(BBBCatalogErrorCodes.SHIPPING_METHOD_NOT_CONFIGURED_FOR_SITE_IN_REPOSITORY, "No shipping method available for site:"+pOrder.getSiteId());
		}
		if(this.isLoggingDebug()){
			this.logDebug("Exit getFirstNonGiftHardgoodShippingGroupWithoutRels");
		}
		return null;
	}
	
	/**
	 * LTL Finds First Non LTL HardgoodShippingGroup
	 * @param pOrder
	 * @return
	 * @throws BBBBusinessException
	 */
	public HardgoodShippingGroup getFirstNonLTLHardgoodShippingGroup(final Order pOrder) throws BBBBusinessException {
		if(this.isLoggingDebug()){
			this.logDebug("Entry getFirstNonGiftHardgoodShippingGroupWithoutRels");
		}

		@SuppressWarnings("unchecked")
		final List<ShippingGroup> shippingGroupList = pOrder.getShippingGroups();
		BBBHardGoodShippingGroup bbbHardGoodSG = null;
		try {
			final RepositoryItem[] applicableShipMethods = this.getCmsTools().getShippingMethods(pOrder.getSiteId());
		
		if (null != shippingGroupList) {
			boolean breaker = false;
			for (final ShippingGroup shippingGroup : shippingGroupList) {
				if (shippingGroup instanceof BBBHardGoodShippingGroup) {
					if(shippingGroup.getShippingMethod() == null) {
						//Here shipping group will Never be Null as we already did a check above of "instanceof". So only shippingMethod will be null. 
            			//So instead of returning Null set the shipping method.
						shippingGroup.setShippingMethod(BBBCoreConstants.HARD_GOODS_SHIP_GROUP_ITEM_DESCRIPTOR);
            			return (BBBHardGoodShippingGroup)shippingGroup;
					} else if(shippingGroup.getShippingMethod().equalsIgnoreCase(BBBCoreConstants.HARD_GOODS_SHIP_GROUP_ITEM_DESCRIPTOR)) {
						return (BBBHardGoodShippingGroup)shippingGroup;
					}
					for(final RepositoryItem repositoryItem : applicableShipMethods) {
						if(null != repositoryItem.getPropertyValue("isLTLShippingMethod") && !(Boolean)repositoryItem.getPropertyValue("isLTLShippingMethod") 
							&& null != repositoryItem.getPropertyValue("id") 
							&& shippingGroup.getShippingMethod().equalsIgnoreCase((String)repositoryItem.getPropertyValue("id")) ) {
							bbbHardGoodSG = (BBBHardGoodShippingGroup) shippingGroup;
							breaker = true;
							break;
						}
					}
				}
				if(breaker) {
					break;
				}
			}
		}
	} catch (RepositoryException e) {
			logError(e);
		throw new BBBBusinessException(BBBCatalogErrorCodes.SHIPPING_METHOD_NOT_CONFIGURED_FOR_SITE_IN_REPOSITORY, "No shipping method available for site:"+pOrder.getSiteId());
	}
		return bbbHardGoodSG;
	}
	
	/**
	 * This method returns shipping method description in case of multi
	 * shipping.
	 * 
	 * @param req
	 * @param order
	 * @param description
	 * @return
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	public String getMultiShippingDescription(DynamoHttpServletRequest req,
			BBBOrder order) throws BBBBusinessException, BBBSystemException {
		if (isLoggingDebug()) {
			logDebug("[START] getMultiShippingDescription");
		}
		String description = null;

		if (OrderType.BOPUS.equals(order.getOrderType())) {
			description = getLblTxtTemplateManager().getPageLabel(
					LBL_STORE_PICKUP_SHIP_METHOS,
					req.getLocale().getLanguage(), null);
		}

		if (OrderType.HYBRID.equals(order.getOrderType())) {
			description = getLblTxtTemplateManager().getPageLabel(
					TXT_AS_PREVIOUSLY_SELECTED, req.getLocale().getLanguage(),
					null);
		}

		if (OrderType.ONLINE.equals(order.getOrderType())) {
			String shipMethodName = order.isCommonShippingMethodAmongHGS();
			if (shipMethodName != null) {
				description = this.getShippingMethodForID(shipMethodName).getShipMethodDescription();
			} else {
				description = getLblTxtTemplateManager().getPageLabel(
						TXT_AS_PREVIOUSLY_SELECTED,
						req.getLocale().getLanguage(), null);
			}

		}

		if (isLoggingDebug()) {
			logDebug("[END] getMultiShippingDescription - description:"
					+ description);
		}

		return description;
	}

	/**
	 * This method returns shipping method description in case of single
	 * shipping.
	 * 
	 * @param req
	 * @param order
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public String getSingleShippingDescription(DynamoHttpServletRequest req,
			BBBOrder order) throws BBBBusinessException, BBBSystemException {
		if (isLoggingDebug()) {
			logDebug("[START] getSingleShippingDescription");
		}
		String description = null;

		if (OrderType.BOPUS.equals(order.getOrderType())) {
			description = getLblTxtTemplateManager().getPageLabel(
					LBL_STORE_PICKUP_SHIP_METHOS,
					req.getLocale().getLanguage(), null);
		} else {
			String shipMethod = "";
			//LTL RM-27709 | LTL item removed on cart and normal item added from last minute item then also LTL SG remains in order and is 
			// dispayed as Estimated Shipping method on Cart and Shipping Page.
			if(order.getCommerceItems().size() > 0)
			{
				CommerceItem item=(CommerceItem)order.getCommerceItems().get(0);
				if(item instanceof BBBCommerceItem)
				{
					List list=((BBBCommerceItem)item).getShippingGroupRelationships();				
					ShippingGroup sg = ((BBBShippingGroupCommerceItemRelationship)(list.get(0))).getShippingGroup();
					shipMethod=sg.getShippingMethod();
				}
				if(!BBBUtility.isEmpty(shipMethod))
				{
					description = this.getShippingMethodForID(shipMethod).getShipMethodDescription();
				}
				if (isLoggingDebug()) {
					logDebug("ShipMethod: "+shipMethod+", Description: "+ description+", SkuId:"+item.getCatalogRefId());
				}
			}
		}

		if (isLoggingDebug()) {
			logDebug("[END] getSingleShippingDescription - description: "
					+ description);
		}
		return description;
	}
	
	
	   /**
	    * To calculate the shipping cost for shipping method
	    * @param shipMethodVOs 
	    * @param order
	    */
		public void calculateShippingCost(List<ShipMethodVO> shipMethodVOs, final BBBOrder order) {
			
				@SuppressWarnings("unchecked")
				List<HardgoodShippingGroup> hardgoodShippingGroupList = this.getHardgoodShippingGroups(order);
				// calculate cost for each shipping group in order for each method
				for (ShipMethodVO shipMethodVOItem : shipMethodVOs) {
					double shippingCharges = 0.0;
					for (HardgoodShippingGroup hardgoodShippingGroupItem : hardgoodShippingGroupList) {
						List commerceItemRelationShips = hardgoodShippingGroupItem.getCommerceItemRelationships(); 
						try {
							if (!(commerceItemRelationShips == null || commerceItemRelationShips.isEmpty())) {
								if (shipMethodVOItem.getShipMethodId().equals(BBBCoreConstants.SHIP_METHOD_STANDARD_ID)) {
									// iterates through the PricingAdjustments and populate the discounted shipping cost for the
									// Free Standard Shipping case.
									shippingCharges += ((BBBPricingTools) getPricingTools()).fillAdjustmentsForShipMethod(hardgoodShippingGroupItem,order, shipMethodVOItem.getShipMethodId());
								}else if(shipMethodVOItem.getShipMethodId().equals(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD)){
									shippingCharges += ((BBBPricingTools) getPricingTools()).calculateShippingCost(order.getSiteId(),BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD,(HardgoodShippingGroup) hardgoodShippingGroupItem, null);
								}
								else {
									shippingCharges += ((BBBPricingTools) getPricingTools()).calculateShippingCost(order.getSiteId(),shipMethodVOItem.getShipMethodId(),(HardgoodShippingGroup) hardgoodShippingGroupItem, null);
								}
							}

						} catch (PricingException exp) {
							if (isLoggingError()) {
								logError("Pricing exception occured while calculating shipping cost", exp);
							}
						}
					}
					shipMethodVOItem.setShippingCharge(shippingCharges);
					if(shipMethodVOItem.getShipMethodId().equals(getSddShipMethodId())){
						String sddShipCharge = getSameDayDeliveryManager().getBbbCatalogTools().getSddShipMethodCharge();
						//Adding a very high value for sddShipCharge
						if(!StringUtils.isBlank(sddShipCharge)){
							shipMethodVOItem.setSortShippingCharge(Double.valueOf(sddShipCharge));
						}
					}else{
						shipMethodVOItem.setSortShippingCharge(shippingCharges);
					}
					
				}
				
				
				Collections.sort(shipMethodVOs, new Comparator<ShipMethodVO>() {
					@Override
					public int compare(ShipMethodVO shipMethod1,ShipMethodVO shipMethod2) {
						Double shipCharge1 = ((ShipMethodVO) shipMethod1).getShippingCharge();
						Double shipCharge2 = ((ShipMethodVO) shipMethod2).getShippingCharge();
		 				// ascending order
						 return shipCharge1.compareTo(shipCharge2);
					}

				});


		}

		//LTL-1235 expectedDeliveryDateForLTLItem at Item level
	/***
	 * Returns true if shipping method is ltl
	 * 
	 * @param shippingMethod
	 * @return
	 */
	public boolean isLTLSg(String shippingMethod, String siteId) {
		try {
			final RepositoryItem[] applicableShipMethods = this.getCmsTools().getShippingMethods(siteId);
			List<String> allNonLTLSG = new ArrayList<String>();
			allNonLTLSG.add(BBBCoreConstants.HARD_GOODS_SHIP_GROUP_ITEM_DESCRIPTOR);
			for(final RepositoryItem repositoryItem : applicableShipMethods) {
				if(null != repositoryItem.getPropertyValue("isLTLShippingMethod") && !(Boolean)repositoryItem.getPropertyValue("isLTLShippingMethod")) {
					allNonLTLSG.add((String)repositoryItem.getPropertyValue("id"));
				}
			}
			return allNonLTLSG.contains(shippingMethod)?false:true;
		} catch (RepositoryException e) {
			
				logError("No shipping method available for site "+siteId);
			
			return false;
		}
	}
	
	
	/**
	 * This method calculates shipping cost.
	 *
	 * @param shipMethodVOList the ship method vo list
	 * @param order the order
	 * @param hardgoodShippingGroupList the hardgood shipping group list
	 * @param sddShippingZip 
	 */
	
	public void calculateShippingCost(List<ShipMethodVO> shipMethodVOList,
			final BBBOrder order,
			List<HardgoodShippingGroup> hardgoodShippingGroupList, String sddShippingZip) {
		
		// calculate cost for each shipping group in order for each method
		for (ShipMethodVO shipMethodVOItem : shipMethodVOList) {
			double shippingCharges = 0.0;
			for (HardgoodShippingGroup hardgoodShippingGroupItem : hardgoodShippingGroupList) {
				shippingCharges = 0.0;
				try {
					if(!(hardgoodShippingGroupItem.getCommerceItemRelationships() == null ||
							hardgoodShippingGroupItem.getCommerceItemRelationships().isEmpty())){
						
						if(shipMethodVOItem.getShipMethodId().equals(BBBCoreConstants.SHIP_METHOD_STANDARD_ID)){
							//iterates through the PricingAdjustments and populate the discounted shipping cost for the Free Standard Shipping case.	
							shippingCharges += ((BBBPricingTools)getPricingTools()).fillAdjustmentsForShipMethod(hardgoodShippingGroupItem,order,shipMethodVOItem.getShipMethodId());
						}else {
							shippingCharges += ((BBBPricingTools) getPricingTools()).calculateShippingCost(order.getSiteId(),shipMethodVOItem.getShipMethodId(),(HardgoodShippingGroup) hardgoodShippingGroupItem, sddShippingZip);
						}
				}
					
				} catch (PricingException exp) {
					logError("Exception occured while fetching shipping methods " +
								"for perOrder flow - ", exp);
				}
			}
			if(isLoggingDebug())
				vlogDebug("BBBShippingGroupManager::calculateShippingCost() Shipping Method :: {0} Shipping Charge:: {1}",shipMethodVOItem.getShipMethodId(), shippingCharges);
			shipMethodVOItem.setShippingCharge(shippingCharges);
			shipMethodVOItem.setSortShippingCharge(shippingCharges);

		}
	}

	public BBBSameDayDeliveryManager getSameDayDeliveryManager() {
		return sameDayDeliveryManager;
	}

	public void setSameDayDeliveryManager(BBBSameDayDeliveryManager sameDayDeliveryManager) {
		this.sameDayDeliveryManager = sameDayDeliveryManager;
	}

	public String getSddShipMethodId() {
		return sddShipMethodId;
	}

	public void setSddShipMethodId(String sddShipMethodId) {
		this.sddShipMethodId = sddShipMethodId;
	}
	
		
	@Override
	public void removeShippingGroupFromOrder(Order pOrder, String pShippingGroupId){
		//Nucleus nucleus = Nucleus.getGlobalNucleus();
		
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		if (pRequest != null) {
			BBBAddressContainer addressContainer = (BBBAddressContainer) pRequest.resolveName("/com/bbb/commerce/common/BBBAddressContainer");
			this.removeAddFromContainer(pOrder,addressContainer,pShippingGroupId);
		}
		try {
			super.removeShippingGroupFromOrder(pOrder, pShippingGroupId);
		} catch (CommerceException e) {
			logError("Exception occurred while removing shipping group from Order", e);
		}
	}
	
	public void removeAddFromContainer(Order pOrder, BBBAddressContainer addressContainer, String shippingGroupId){
		List<ShippingGroup> allshippingGroups = pOrder.getShippingGroups();
		ArrayList<String> removalList = new ArrayList(allshippingGroups.size());
		if(BBBUtility.isEmpty(shippingGroupId)){
			Iterator iter = allshippingGroups.iterator();
			while (iter.hasNext()) {
				ShippingGroup group = (ShippingGroup)iter.next();
				if (group.getCommerceItemRelationshipCount() == 0) removalList.add(group.getId());
			}
		}
		List<BBBAddress> removeAddresses = new ArrayList<BBBAddress>();
		List<BBBAddress> addresses = addressContainer.getDuplicate();
		if(addresses != null){
			removeAddresses.addAll(addresses);
			int size = (null != addressContainer.getDuplicate()) ? addressContainer.getDuplicate().size() : 0;
			for(int i=0; i < size ; i++){
				if(removeAddresses.get(i) instanceof BBBRepositoryContactInfo){
					String addressId = ((BBBRepositoryContactInfo)removeAddresses.get(i)).getId();
					if(removalList.contains(addressId) || (!BBBUtility.isEmpty(shippingGroupId) && addressId.equalsIgnoreCase(shippingGroupId))){
						addressContainer.getDuplicate().remove(addressContainer.getDuplicate().get(i));
					}
				}
			}
		}
	}
}
