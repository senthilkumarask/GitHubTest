package com.bbb.wishlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.commerce.gifts.GiftlistManager;
import atg.core.util.StringUtils;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSavedItemsSessionBean;
import com.bbb.rest.catalog.vo.SkuRestVO;
import com.bbb.vo.wishlist.LTLWishListDslVO;
import com.bbb.vo.wishlist.WishListProductVO;
import com.bbb.vo.wishlist.WishListSkuDetailVO;
import com.bbb.vo.wishlist.WishListVO;

public class BBBWishlistManager extends BBBGenericService{
	private BBBCatalogTools catalogTools;
	RepositoryItem mProfile;
	GiftlistManager mGiftlistManager;
	BBBSavedItemsSessionBean savedItemsSessionBean;
	
	/**
	 * @return the savedItemsSessionBean
	 */
	public BBBSavedItemsSessionBean getSavedItemsSessionBean() {
		return savedItemsSessionBean;
	}

	/**
	 * @param savedItemsSessionBean the savedItemsSessionBean to set
	 */
	public void setSavedItemsSessionBean(
			BBBSavedItemsSessionBean savedItemsSessionBean) {
		this.savedItemsSessionBean = savedItemsSessionBean;
	}

	/**
	 * Sets property profile.
	 * @param pProfile  The property to store the profile of the current customer.
	 * @beaninfo description:  The property to store the profile of the current customer.
	 **/
	public void setProfile(RepositoryItem pProfile) {
		mProfile = pProfile;
	}

	/**
	 * Returns property Profile
	 * @return The value of the property profile.
	 **/
	public RepositoryItem getProfile() {
		return mProfile;
	}


	/**
	 * Sets property giftlistManager.
	 * @param pGiftlistManager the giftlistManager class which provides a high level business layer interface to giftlists.
	 * @beaninfo description:  the giftlistManager class which provides a high level business layer interface to giftlists.
	 **/
	public void setGiftlistManager(GiftlistManager pGiftlistManager) {
		mGiftlistManager = pGiftlistManager;
	}

	/**
	 * Returns property giftlistManager.
	 * @return The value of the property GiftlistManager.
	 **/
	public GiftlistManager getGiftlistManager() {
		return mGiftlistManager;
	}

	protected static final String WISH_LIST = "wishlist";
	private SiteContext mSiteContext;
	/**
	 * @return the siteContext
	 */
	public SiteContext getSiteContext() {
		return mSiteContext;
	}

	/**
	 * @param pSiteContext the siteContext to set
	 */
	public void setSiteContext(SiteContext pSiteContext) {
		mSiteContext = pSiteContext;
	}
	String mSiteId;

	/**
	 * Sets property siteId.
	 * @param pSiteId  The property to store the site Id for the giftlist.
	 * @beaninfo description:  The property to store the site Id for the giftlist.
	 */
	public void setSiteId(String pSiteId){
		mSiteId = pSiteId;
	}

	/**
	 * Returns property siteId.
	 * @return The value of the property siteId.
	 */
	public String getSiteId(){
		return mSiteId;
	}


	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * This method gets the wish list items in the logged  in user's profile for rest
	 * The method will throw exception if it is accessed by non logged in user
	 * This is taken care by AccessControllerServlet .
	 * The items are filtered for current site id
	 * @return
	 */

	@SuppressWarnings("rawtypes")
	public WishListVO getWishListItems(){
		
		logDebug("BBBWishlistManager.getWishListItems : start");
		List<GiftListVO> items = getSavedItemsSessionBean().getItems();
		List<GiftListVO> wishListItemList = new ArrayList<GiftListVO>();
		String siteId = getSiteContext().getSite().getId();
		WishListVO wishListVO = new WishListVO();
		
		if(items != null && !items.isEmpty()){
			Map<String,WishListProductVO> prodPropMap = new HashMap<String,WishListProductVO>();
			Map<String,WishListSkuDetailVO> skuPropMap = new HashMap<String,WishListSkuDetailVO>();
			String skuId = null;
			String productId = null;
			String itemSiteId = null;
			WishListProductVO wishListProductVO = null;
			WishListSkuDetailVO wishListSkuDetailVO = null;
			LTLWishListDslVO ltlSavedDslOptions = null;
			List<String> dslOptionSaved = null;
			for(GiftListVO giftList : items){
				
					try{
						
						skuId = giftList.getSkuID();
						productId = giftList.getProdID();
						
						wishListProductVO = getWishListProductVO(siteId, productId);
						wishListSkuDetailVO = getWishListSkuDetailVO(siteId, skuId, productId);
						prodPropMap.put(productId, wishListProductVO);
						skuPropMap.put(skuId, wishListSkuDetailVO);	
						wishListItemList.add(giftList);
						
						logDebug(" adding sku id "+skuId +" in wish List site id applicable for the sku id is "+itemSiteId);
						
					} catch (BBBSystemException e) {
						logError("BBBWishlistManager BBBSystemException "+e.getMessage()+" not adding sku Id in list "+skuId);
					} catch (BBBBusinessException e) {
						logError("BBBWishlistManager BBBSystemException "+e.getMessage()+" not adding sku Id in list "+skuId);
					}
					
				
			}
			
			//Setting DSL options for wishlist
			/*List<GiftListVO> lGiftListVo = (List<GiftListVO>) getSavedItemsSessionBean().getSaveItems(true);*/
			List<GiftListVO> lGiftListVo = ((BBBSavedItemsSessionBean) ServletUtil.getCurrentRequest().resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean")).getSaveItems(true);
			if (lGiftListVo !=null) {
				for(GiftListVO giftListVo:lGiftListVo){
					if(null != wishListVO && null != wishListVO.getSavedDsl() && null != wishListVO.getSavedDsl().getSavedDslOptions() && 
							(wishListVO.getSavedDsl().getSavedDslOptions().size() != 0) && wishListVO.getSavedDsl().getSkuId().equalsIgnoreCase(giftListVo.getSkuID()) && 
							giftListVo.getLtlShipMethodDesc()!= null && !StringUtils.isEmpty(giftListVo.getLtlShipMethodDesc())){
						wishListVO.getSavedDsl().getSavedDslOptions().add(giftListVo.getLtlShipMethod());
					}
					else if(giftListVo.getLtlShipMethodDesc()!= null && !StringUtils.isEmpty(giftListVo.getLtlShipMethodDesc())){
						dslOptionSaved = new ArrayList<String>();
						dslOptionSaved.add(giftListVo.getLtlShipMethod());
						ltlSavedDslOptions = new LTLWishListDslVO();
						ltlSavedDslOptions.setSavedDslOptions(dslOptionSaved);
						ltlSavedDslOptions.setSkuId(giftListVo.getSkuID());
						wishListVO.setSavedDsl(ltlSavedDslOptions);
					}
				}
			}

			wishListVO.setProdPropMap(prodPropMap);
			wishListVO.setSkuPropMap(skuPropMap);
			wishListVO.setWishListItems(wishListItemList);
			
		}
		
		logDebug(" BBBWishlistManager.getWishListItems : end");
		return wishListVO;
	}
	/**
	 * Set sub set values from ProductVO into WishListProductVO that are required to display wishlist items
	 * @param siteId
	 * @param productId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */

	private WishListProductVO getWishListProductVO(String siteId, String productId) throws BBBSystemException, BBBBusinessException{
		WishListProductVO wishListProductVO=new WishListProductVO ();

		ProductVO productVO=getCatalogTools().getProductDetails(siteId, productId);
		if(productVO != null){
			wishListProductVO.setAttributesList(productVO.getAttributesList());
			wishListProductVO.setCollection(productVO.isCollection());
			wishListProductVO.setName(productVO.getName());
			wishListProductVO.setProductId(productVO.getProductId());
			wishListProductVO.setProductImages(productVO.getProductImages());
		}

		return wishListProductVO;
	}

	/**
	 * Set sub set values from SKUDetailVO into WishListSkuDetailVO that are required to display wishlist items
	 * @param siteId
	 * @param skuId
	 * @param productId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private WishListSkuDetailVO getWishListSkuDetailVO(String siteId, String skuId,String productId) throws BBBSystemException, BBBBusinessException{
		
		WishListSkuDetailVO wishListSkuDetailVO=new WishListSkuDetailVO();
		SKUDetailVO sKUDetailVO=getCatalogTools().getSKUDetails(siteId, skuId, false);
		
		String salePrice = String.valueOf(getCatalogTools().getSalePrice(productId,skuId));
		String listPrice = String.valueOf(getCatalogTools().getListPrice(productId,skuId));
		wishListSkuDetailVO.setListPrice(listPrice);
		wishListSkuDetailVO.setSalePrice(salePrice);
		
		if(sKUDetailVO != null){
			
			wishListSkuDetailVO.setColor(sKUDetailVO.getColor());
			wishListSkuDetailVO.setDescription(sKUDetailVO.getDescription());
			wishListSkuDetailVO.setDisplayName(sKUDetailVO.getDisplayName());
			wishListSkuDetailVO.setSize(sKUDetailVO.getSize());
			wishListSkuDetailVO.setSkuId(skuId);
			wishListSkuDetailVO.setUpc(sKUDetailVO.getUpc());
			
		}
		
		return wishListSkuDetailVO;

	}
	/**
	 * Method to get all wishlist items added for a profile
	 * @return
	 */
	public List getWishListItemsFromProfile(){
		logDebug("BBBWishlistManager.getWishListItemsFromProfile : start");
		List items = null;
		
		logDebug(" getting wish list for profile with email "+getProfile().getPropertyValue("email"));
		String giftid=((RepositoryItem) getProfile().getPropertyValue(WISH_LIST)).getRepositoryId();
		logDebug(" Wish List id associated with the profile "+giftid);
		
		items = getGiftlistManager().getGiftlistItems(giftid);
		logDebug("No of items in the wishlist  "+((items!=null && !items.isEmpty())?items.size():" Zero"));
		
		logDebug("BBBWishlistManager.getWishListItemsFromProfile : End");
		return items;
	}
	
	/**Fetches the Product Ids of saved items on basis of transient/non-transient users.
	 * 
	 * @return String List of product Ids
	 */
	@SuppressWarnings("unchecked")
	public String getProdIDsSavedItems() {
		String prodIdsList = null;
		String productId= null;
		if (getProfile().isTransient()) {
			final StringBuilder fetchProductIdsList = new StringBuilder("");
			List<GiftListVO> giftLisVos = this.getSavedItemsSessionBean().getGiftListVO();
			if (giftLisVos != null) {
				for(GiftListVO giftVo:giftLisVos){
					productId = giftVo.getProdID();
					fetchProductIdsList.append(productId).append(";");
				}
			}
			return fetchProductIdsList.toString();
		} else {
			// if logged in user
			RepositoryItem wishList = ((RepositoryItem) getProfile()
					.getPropertyValue(WISH_LIST));
			if (wishList != null) {
				List<RepositoryItem> wishtListItems = (List<RepositoryItem>) (wishList
						.getPropertyValue(BBBCoreConstants.GIFT_LIST_ITEMS));
				prodIdsList = convertSavedItemsToGiftListVO(wishtListItems);
			}
		}
		return prodIdsList;
	}
	
	/**
	 * 
	 * @return String prodIdsList
	 */
	private String convertSavedItemsToGiftListVO(
			List<RepositoryItem> wishtListItems) {

		final StringBuilder prodIdsList = new StringBuilder("");
		String prodId;
		for (RepositoryItem item : wishtListItems) {
			String sisterSiteId = SiteContextManager.getCurrentSiteId();

			String itemSiteId = (String) item.getPropertyValue("siteId");

			if (null != itemSiteId
					&& (itemSiteId
							.equals(SiteContextManager.getCurrentSiteId()) || itemSiteId
							.equals(sisterSiteId))) {
				prodId = (String) item.getPropertyValue(BBBCoreConstants.PRODUCTID);

				prodIdsList.append(prodId).append(";");
			}
		}
		return prodIdsList.toString();
	}


}
