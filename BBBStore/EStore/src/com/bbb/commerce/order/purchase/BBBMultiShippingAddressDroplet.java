package com.bbb.commerce.order.purchase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.commerce.order.OrderHolder;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.CommerceItemShippingInfo;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.BBBAddressTools;
import com.bbb.account.api.BBBAddressVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.commerce.vo.BBBAddressSelectionVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

/**
 * @author jpadhi
 *
 */
public class BBBMultiShippingAddressDroplet extends BBBDynamoServlet {

	private static final String DEFAULT_ADD_ID = "defaultAddId";
	private static final String SHIPPING_GROUP_NAME = "shippingGroupName";
	private static final String ADDRESS_CONTAINER = "addressContainer";
	private static final String KEYS = "keys";
	private static final String PROFILE_STRING = "PROFILE";
	private BBBCheckoutManager mCheckoutMgr;
	private String mShippingGroupMapContainerPath;
	private BBBAddressContainer addressContainer;
	private Profile profile;
	private OrderHolder cart;
	private BBBMultiShippingManager multiShippingManager;
	
	private BBBCatalogTools catalogTools;
	
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	public String getShippingGroupMapContainerPath() {
		return mShippingGroupMapContainerPath;
	}

	public void setShippingGroupMapContainerPath(
			String pShippingGroupMapContainerPath) {
		this.mShippingGroupMapContainerPath = pShippingGroupMapContainerPath;
	}

	public final BBBCheckoutManager getCheckoutMgr() {
		return mCheckoutMgr;
	}

	public final void setCheckoutMgr(final BBBCheckoutManager pCheckoutMgr) {
		this.mCheckoutMgr = pCheckoutMgr;
	}

	/**
	 * initialize the addressMap of BBBAddressContainer by inserting address 
	 * from profile, registry and shipping group of order
	 * 
	 * @param req
	 * @param res
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override   
	public void service(final DynamoHttpServletRequest req,
			final DynamoHttpServletResponse res) throws ServletException, IOException {

	    BBBPerformanceMonitor.start("BBBMultiShippingAddressDroplet", "service");
		BBBAddressContainer addressContainer = (BBBAddressContainer) req.getObjectParameter(ADDRESS_CONTAINER);
		
		//final Map<String, String> sourceMap = addressContainer.getSourceMap();
		final Profile profile = (Profile) req.getObjectParameter(BBBCoreConstants.PROFILE);
		final BBBOrder order = (BBBOrder) req.getObjectParameter(BBBCoreConstants.ORDER);
		final String siteId = req.getParameter(BBBCoreConstants.SITE_ID);
		final String shippingGroupName = req.getParameter(SHIPPING_GROUP_NAME);
		BBBHardGoodShippingGroup sg = null;
		
		 
		//Moved the block down- LTL
		
		LinkedHashMap<String, BBBAddress> addressMap = null;
		List<String> addressKeys = new ArrayList<String>();
		String errorMsg = null;
		try{
			addressMap = addAddressToMap(profile, order, siteId, addressKeys);
			logDebug("Total address count in BBBAddressMap : " + addressMap.size());
			addressContainer.setAddressMap(addressMap);

		}catch (BBBBusinessException e) {
			logError("Business Exception Occoured while updting addressMap in BBBAddressContainer ",e);
			errorMsg = "Business Exception Occoured while updting addressMap in BBBAddressContainer";
		}catch (BBBSystemException e) {
			logError("System Exception Occoured while updting addressMap in BBBAddressContainer ",e);
			errorMsg = "System Exception Occoured while updting addressMap in BBBAddressContainer";
		}catch (Exception e) {
			logError("Exception Occoured while updting addressMap in BBBAddressContainer ",e);
			errorMsg = "Exception Occoured while Updting addressMap in BBBAddressContainer";
		}
		
		if(errorMsg != null){
			req.serviceLocalParameter( BBBCoreConstants.ERROR_OPARAM, req, res);
			req.setParameter(BBBCoreConstants.ERROR, errorMsg);
		}else{
		    //Add new addresses to the addressContainer
			if(addressContainer.getNewAddressMap() != null){
				Map<String, BBBAddress> newAddressMap = addressContainer.getNewAddressMap();
				Set<String> newAddressKeys = newAddressMap.keySet();
				List<String> newAddressKeysList=new ArrayList<String>();
				newAddressKeysList.addAll(newAddressKeys);
				Collections.sort(newAddressKeysList);
				for (String newAddKey : newAddressKeysList) {
			
					if(BBBAddressTools.duplicateFound(addressMap, addressContainer.getNewAddressMap().get(newAddKey))){
						String key=keyForDuplicate(addressMap, addressContainer.getNewAddressMap().get(newAddKey));
						if(BBBUtility.isNotEmpty(key)){
							if(null != addressContainer.getNewAddressMap().get(newAddKey).getAlternatePhoneNumber()) {
								addressMap.get(key).setAlternatePhoneNumber(addressContainer.getNewAddressMap().get(newAddKey).getAlternatePhoneNumber());
							}
							if(null != addressContainer.getNewAddressMap().get(newAddKey).getPhoneNumber()) {
								addressMap.get(key).setPhoneNumber(addressContainer.getNewAddressMap().get(newAddKey).getPhoneNumber());
							}
							if(null != addressContainer.getNewAddressMap().get(newAddKey).getEmail()) {
								addressMap.get(key).setEmail(addressContainer.getNewAddressMap().get(newAddKey).getEmail());
							}
						}
					}else if( !addressKeys.contains(newAddKey) && !BBBAddressTools.duplicateFound(addressMap, addressContainer.getNewAddressMap().get(newAddKey))){
						addressContainer.getAddressMap().put(newAddKey, addressContainer.getNewAddressMap().get(newAddKey));
						addressKeys.add(newAddKey);
					}
				}
			}
			//Start: Moved the block - LTL
			String defaultAddId = "";
			BBBShippingGroupContainerService containerService = (BBBShippingGroupContainerService)req.resolveName(getShippingGroupMapContainerPath()) ;
				if( containerService.getShippingGroup(shippingGroupName) instanceof BBBHardGoodShippingGroup ){
					sg = (BBBHardGoodShippingGroup)( containerService.getShippingGroup(shippingGroupName));
					defaultAddId = sg.getSourceId();
				} else {
					defaultAddId = shippingGroupName;
				}
				
				if(BBBUtility.isNotEmpty(defaultAddId) && (!addressKeys.contains(defaultAddId))){
					String matchKeyInAddressMap = defaultAddId;
					if(!BBBUtility.isMapNullOrEmpty(addressContainer.getNewAddressMap())){
						matchKeyInAddressMap = keyForDuplicate(addressMap, addressContainer.getNewAddressMap().get(defaultAddId));					
							if(BBBUtility.isNotEmpty(matchKeyInAddressMap)){
								if(null != addressContainer.getNewAddressMap().get(defaultAddId).getAlternatePhoneNumber()) {
									addressMap.get(matchKeyInAddressMap).setAlternatePhoneNumber(addressContainer.getNewAddressMap().get(defaultAddId).getAlternatePhoneNumber());
								}
								if(null != addressContainer.getNewAddressMap().get(defaultAddId).getPhoneNumber()) {
									addressMap.get(matchKeyInAddressMap).setPhoneNumber(addressContainer.getNewAddressMap().get(defaultAddId).getPhoneNumber());
								}
								if(null != addressContainer.getNewAddressMap().get(defaultAddId).getEmail()) {
									addressMap.get(matchKeyInAddressMap).setEmail(addressContainer.getNewAddressMap().get(defaultAddId).getEmail());
								}
								defaultAddId = matchKeyInAddressMap;
							}
					}
				}
			//End: Moved the block - LTL
			
			//Re arrange the addressKeys so that new address comes at the top
			//BBBSL-1976. Merge shipping group if difference is due to cases or trailing spaces.
			if(addressContainer.getNewAddressKey() != null &&  addressKeys.contains(addressContainer.getNewAddressKey())){
				addressKeys.remove(addressContainer.getNewAddressKey());
				int TOP_ADDRESS_INDEX = 0;
				addressKeys.add(TOP_ADDRESS_INDEX, addressContainer.getNewAddressKey());
			}
			
			req.setParameter(ADDRESS_CONTAINER, addressContainer);
			req.setParameter("newAddress", addressContainer.getNewAddressKey() );
			req.setParameter(KEYS, addressKeys);
			req.setParameter(DEFAULT_ADD_ID, defaultAddId);		
			req.serviceLocalParameter( BBBCoreConstants.OUTPUT, req, res);
		} 
		BBBPerformanceMonitor.end("BBBMultiShippingAddressDroplet", "service");
	}

	/**
	 *  Populates the addressMap with Profile, registry and Order's shipping group address
	 * 
	 * @param profile
	 * @param order
	 * @param siteId
	 * @return BBBAdressMap<String, BBBAddress>
	 * 
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public LinkedHashMap<String, BBBAddress> addAddressToMap(
			final Profile profile, final BBBOrder order, final String pSiteId, List<String> addressKeys) throws
			BBBBusinessException, BBBSystemException{


		Address tempAddress;
		final LinkedHashMap<String, BBBAddress> addressMap = new LinkedHashMap<String, BBBAddress>();
		
		List<BBBAddressVO> profileShippingAddresses = getCheckoutMgr().getProfileShippingAddress(profile, pSiteId);
		logDebug("profile adddress count is " + profileShippingAddresses.size());
		
		// sort the profile Shipping Addresses list
		Collections.sort(profileShippingAddresses, new Comparator<BBBAddressVO>() {
			@Override
			public int compare(BBBAddressVO obj1, BBBAddressVO obj2) {
				int returnValue = 0;

				if (null != obj1 && null != obj2 && null != obj1.getIdentifier() && obj1.getIdentifier().compareTo(obj2.getIdentifier()) > 0) {
					returnValue = 1;
				} else {
					returnValue = -1;
				}

				return returnValue;
			}
		});

		@SuppressWarnings("unchecked")
		final List<ShippingGroup> allShippingGroups = order.getShippingGroups();
		for (ShippingGroup sg : allShippingGroups) {
			if (sg instanceof BBBHardGoodShippingGroup) {
				tempAddress = ((BBBHardGoodShippingGroup) sg).getShippingAddress();
				if (!StringUtils.isEmpty(tempAddress.getAddress1())) {

					String sgId = ((BBBHardGoodShippingGroup) sg).getSourceId();
					if (!StringUtils.isBlank(sgId)
							&& !(sgId.contains(BBBCheckoutConstants.REGISTRY_SOURCE) || sgId.contains(PROFILE_STRING))
							&& !addressMap.containsKey(sgId)
							&& !org.apache.commons.lang.StringUtils.equalsIgnoreCase(PROFILE_STRING, ((BBBAddress) tempAddress).getSource())
							&& !org.apache.commons.lang.StringUtils.equalsIgnoreCase(BBBCheckoutConstants.REGISTRY_SOURCE, ((BBBAddress) tempAddress)
									.getSource())) {
						addressMap.put(sgId, (BBBAddress) tempAddress);
						addressKeys.add(sgId);
					}
				}
			}
		}
		logDebug("Shipping group adddress count : " + addressMap.size());
		
		//ship to po box changes
		List<String> shiptoPOBoxOn;
		boolean shiptoPOFlag =false;
		try {
			shiptoPOBoxOn = getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON);
			shiptoPOFlag = Boolean.parseBoolean(shiptoPOBoxOn.get(0));
		
		// get default profile address
		BBBAddress defaultProfileAddress = getCheckoutMgr().getProfileAddressTool().getDefaultShippingAddress(profile, pSiteId);
		if (null != defaultProfileAddress && !BBBAddressTools.isNullAddress((Address) defaultProfileAddress)) {
			if(shiptoPOFlag ||(!shiptoPOFlag && BBBUtility.isNonPOBoxAddress(defaultProfileAddress.getAddress1(), defaultProfileAddress.getAddress2()))){
			Integer defaultProfileIndex = profileShippingAddresses.indexOf(defaultProfileAddress);
			addressMap.put((profileShippingAddresses.get(defaultProfileIndex)).getIdentifier(), profileShippingAddresses.get(defaultProfileIndex));
			addressKeys.add((profileShippingAddresses.get(defaultProfileIndex)).getIdentifier());
			profileShippingAddresses.remove(defaultProfileAddress);
			} 
		}
		}catch (BBBSystemException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logError(e.getMessage(),e);
		} catch (BBBBusinessException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logError(e.getMessage(),e);
		}
		
		//add all profile address to the address map
		for (BBBAddressVO bbbAddressVO : profileShippingAddresses) {	
			addressMap.put(bbbAddressVO.getIdentifier(), bbbAddressVO);	
			addressKeys.add(bbbAddressVO.getIdentifier());
		}
		
		final List<BBBAddress> registryAddresses = getCheckoutMgr().getRegistryShippingAddress(pSiteId, order);
		logDebug("Registry adddress count is " + registryAddresses.size());
		
		for (BBBAddress bbbAddress : registryAddresses) {
			// This will replace the record if any
				addressMap.put(bbbAddress.getIdentifier(), bbbAddress);
				addressKeys.add(bbbAddress.getIdentifier());
		}

		return addressMap;
	} 
	
	
	/**
	 * LTL 
	 * This method is used to expose the logic of calculating multiple addresses eligible for an order by taking into account all the 
	 * address from various sources such as profile addresses, registry items addresses, addresses already present in Shipping container
	 * @return
	 * @throws BBBSystemException
	 * @throws ServletException
	 * @throws IOException
	 */
	public BBBAddressSelectionVO getMultishippingAddresses() throws BBBSystemException, ServletException, IOException {
	
	  final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
      final DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
      
      request.setParameter(BBBCoreConstants.PROFILE, this.getProfile());
      request.setParameter(BBBCoreConstants.ORDER, this.getCart().getCurrent());
      request.setParameter(BBBCoreConstants.SITE_ID, SiteContextManager.getCurrentSiteId());
      request.setParameter(ADDRESS_CONTAINER, this.getAddressContainer());
      
      Map<String, String> defaultAddIdMap = new HashMap<String, String>();
      Map<String, CommerceItemShippingInfo> cisiMap = getMultiShippingManager().getCommerceItemShippingInfos(false);
      for (String key: cisiMap.keySet()) {
    	  String shippingGroupName = cisiMap.get(key).getShippingGroupName();
          if (StringUtils.isBlank(shippingGroupName)) {
              request.setParameter(SHIPPING_GROUP_NAME, BBBCoreConstants.BLANK);
          } else {
        	  request.setParameter(SHIPPING_GROUP_NAME, shippingGroupName);
          }
          this.service(request, response);
          defaultAddIdMap.put(key, (String) request.getObjectParameter(DEFAULT_ADD_ID));
      }
      BBBAddressSelectionVO addressSelectionVO = new BBBAddressSelectionVO();
      addressSelectionVO.setDefaultAddIdMap(defaultAddIdMap);
      addressSelectionVO.setNewAddrContainer((BBBAddressContainer)request.getObjectParameter(ADDRESS_CONTAINER));
      
      return addressSelectionVO;
	}

	/**
	 * @return the addressContainer
	 */
	public BBBAddressContainer getAddressContainer() {
		return addressContainer;
	}

	/**
	 * @param addressContainer the addressContainer to set
	 */
	public void setAddressContainer(BBBAddressContainer addressContainer) {
		this.addressContainer = addressContainer;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public OrderHolder getCart() {
		return cart;
	}

	public void setCart(OrderHolder cart) {
		this.cart = cart;
	}
	
	
	/**
	 * method to compare address and find key of duplicate address in addressMap
	 *  
	 * @param addressMap map of address to lookup for duplicate
	 * @param bbbAddress address whose duplicate needs to be matched
	 * @return String key for duplicate address found in address mep 
	 */
	public String keyForDuplicate(Map<String, BBBAddress> addressMap, BBBAddress bbbAddress){
		String matchKey = ""; 
		boolean duplicateFound = false;
		Set<String> addressKeySet = addressMap.keySet();
		BBBAddress tempAddress = null;
		for (String key : addressKeySet) {
			tempAddress = addressMap.get(key);
			if(bbbAddress instanceof Address && tempAddress instanceof Address && (tempAddress.getRegistryId()==null || tempAddress.getRegistryId().isEmpty())){
				duplicateFound = BBBAddressTools.compare((Address)tempAddress, (Address)bbbAddress);
				if(duplicateFound){
					matchKey = key;
					break;
				}
			}
		}
		
		return matchKey;
	}

	/**
	 * @return the multiShippingManager
	 */
	public BBBMultiShippingManager getMultiShippingManager() {
		return multiShippingManager;
	}

	/**
	 * @param multiShippingManager the multiShippingManager to set
	 */
	public void setMultiShippingManager(BBBMultiShippingManager multiShippingManager) {
		this.multiShippingManager = multiShippingManager;
	}
}
