/**
 *
 */
package com.bbb.commerce.catalog;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bbb.account.profile.vo.ProfileEDWInfoVO;
import com.bbb.cms.PromoBoxVO;
import com.bbb.commerce.browse.vo.RecommendedSkuVO;
import com.bbb.commerce.catalog.comparison.vo.CompareProductEntryVO;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.CollectionProductVO;
import com.bbb.commerce.catalog.vo.CollegeVO;
import com.bbb.commerce.catalog.vo.CreditCardTypeVO;
import com.bbb.commerce.catalog.vo.EcoFeeSKUVO;
import com.bbb.commerce.catalog.vo.GiftWrapVO;
import com.bbb.commerce.catalog.vo.ImageVO;
import com.bbb.commerce.catalog.vo.LTLAssemblyFeeVO;
import com.bbb.commerce.catalog.vo.LTLDeliveryChargeVO;
import com.bbb.commerce.catalog.vo.MediaVO;
import com.bbb.commerce.catalog.vo.PDPAttributesVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.RecommendedCategoryVO;
import com.bbb.commerce.catalog.vo.RegionVO;
import com.bbb.commerce.catalog.vo.RegistryCategoryMapVO;
import com.bbb.commerce.catalog.vo.RegistryTypeVO;
import com.bbb.commerce.catalog.vo.RollupTypeVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.catalog.vo.SiteChatAttributesVO;
import com.bbb.commerce.catalog.vo.SiteVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.commerce.catalog.vo.StoreSpecialityVO;
import com.bbb.commerce.catalog.vo.StoreVO;
import com.bbb.commerce.catalog.vo.ThresholdVO;
import com.bbb.commerce.catalog.vo.VendorInfoVO;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.search.bean.result.BBBDynamicPriceSkuVO;
import com.bbb.search.bean.result.BBBDynamicPriceVO;
import com.bbb.search.bean.result.BBBProduct;
import com.bbb.search.bean.result.CategoryRefinementVO;
import com.bbb.search.bean.result.FacetRefinementVO;
import com.bbb.search.bean.result.SortOptionVO;
import com.bbb.selfservice.vo.BeddingShipAddrVO;
import com.bbb.selfservice.vo.SchoolVO;

import atg.commerce.order.HardgoodShippingGroup;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;

/**
 * @author njai13
 *
 */
public interface BBBCatalogTools {

	public SKUDetailVO getSKUDetails (String siteId, String skuId, boolean calculateAboveBelowLine,String ... intlUSer)
			throws BBBSystemException, BBBBusinessException;
	public SKUDetailVO getSKUDetails(String siteId, String skuId, boolean calculateAboveBelowLine, boolean pAddException)
			throws BBBBusinessException, BBBSystemException;
	public  RepositoryItem getParentProductItemForSku(final String skuId) throws BBBBusinessException, BBBSystemException;
	public SKUDetailVO getSKUDetails (String siteId, String skuId)
			throws BBBSystemException, BBBBusinessException;
	public String getPromotionId(String couponId)
			throws BBBSystemException, BBBBusinessException;
	
	public SKUDetailVO getEverLivingSKUDetails(String siteId, String skuId,
             boolean calculateAboveBelowLine) throws BBBSystemException, BBBBusinessException;

	public boolean isSKUBelowLine (String siteId, String skuId)
			throws BBBSystemException, BBBBusinessException;
	
	public boolean isAssemblyFeeOffered (String skuId)
			throws BBBSystemException, BBBBusinessException;			

	public List<ShipMethodVO> getShippingMethodsForSku (String siteId, String skuId, boolean sameDayDeliveryFlag)
			throws BBBSystemException, BBBBusinessException;
	
	public Double getCaseWeightForSku (String skuId)
			throws BBBSystemException, BBBBusinessException;

	public List<StateVO> getNonShippableStatesForSku (String siteId, String skuId)
			throws BBBSystemException, BBBBusinessException;

	public ShipMethodVO getDefaultShippingMethod (String siteId)
			throws BBBSystemException, BBBBusinessException;

	public boolean isNexusState (String siteId, String stateId)
			throws BBBSystemException, BBBBusinessException;

	public boolean isFreeShipping (String siteId, String skuId, String shippingMethodId)
			throws BBBSystemException, BBBBusinessException;

	public double getShippingFee (String siteId,String shippingMethodId, String stateId, double SubTotalAmount, String regionId)
			throws BBBSystemException, BBBBusinessException;
	
	public RegionVO getRegionDataFromZip(String zipCode) throws BBBBusinessException, BBBSystemException;

	public boolean isFixedPriceShipping (String siteId, String skuId)
			throws BBBSystemException, BBBBusinessException;

	public double shippingCostForGiftCard (String siteId, String ShippingMethodId)
			throws BBBSystemException, BBBBusinessException;

	public double getSKUSurcharge (String siteId, String skuId ,String shippingMethodId)
			throws BBBSystemException, BBBBusinessException;

	public List<StateVO> getStates (String siteId, boolean showMilitaryStates, String noShowPage)
	throws BBBSystemException, BBBBusinessException;

	public List<StateVO> getStateList ()
	throws BBBSystemException, BBBBusinessException;

	public List<StateVO> getCollegeStateList ()
			throws BBBSystemException, BBBBusinessException;
			
	public List<CollegeVO> getCollegesByState (String stateCode)
			throws BBBSystemException, BBBBusinessException;
	public boolean isSkuActive(RepositoryItem skuRepositoryItem,String ... intlUser);
	public Map<String,RepositoryItem>  getSkuAttributeList(RepositoryItem skuRepositoryItem,String siteId,	Map<String,RepositoryItem> attributeMap, String regionPromoAttr, boolean isZipSddEligible)
			throws BBBSystemException, BBBBusinessException;

	public CategoryVO getCategoryDetail (String siteId, String categoryId, boolean isProductListRequired)
			throws BBBSystemException, BBBBusinessException;

	public List<ProductVO> getClearanceProducts (String siteId, String categoryId)
			throws BBBSystemException, BBBBusinessException;
	public ProductVO getProductDetails (String siteId, String productId)
			throws BBBSystemException, BBBBusinessException;
	
    public ProductVO getProductVOMetaDetails(final String pSiteId, final String pProductId)
            throws BBBSystemException, BBBBusinessException;
	
	public ProductVO getProductDetails(String pSiteId, String pProductId,boolean populateRollUp, boolean isMinimalDetails, boolean isAddException)
			throws BBBSystemException, BBBBusinessException;
	public ProductVO getProductDetailsForLazyLoading(String pSiteId, String pProductId,boolean populateRollUp, boolean isMinimalDetails, int index, boolean isAddException)
			throws BBBSystemException, BBBBusinessException;
	/**
	 * This is method coded for getting product details for Main product only; Currently used for collection/accessories
	 * product. This is also used at places like Breadcrumb/Notify Me as for these place we don't need to fetch every child product of main product.
	 * @param pSiteId
	 * @param pProductId
	 * @param populateRollUp
	 * @param isMinimalDetails
	 * @param isAddException
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
    public ProductVO getMainProductDetails(String pSiteId, String pProductId,
             boolean populateRollUp,  boolean isMinimalDetails,  boolean isAddException)
            		 throws BBBSystemException, BBBBusinessException;
	
    public ProductVO getEverLivingMainProductDetails(String pSiteId, String pProductId,
            boolean populateRollUp,  boolean isMinimalDetails,  boolean isAddException)
           		 throws BBBSystemException, BBBBusinessException;
    
	public boolean isEverlivingProduct (String productId, String siteId)
	throws BBBSystemException, BBBBusinessException;
	
	public String getSkuByUPC(String upcCode) throws BBBBusinessException,
	BBBSystemException ;

	public List<CollectionProductVO> getCollegeCollections (String siteId, String categoryId)
		throws BBBSystemException, BBBBusinessException;
	public boolean isGiftCardItem (String siteId, String skuId)
			throws BBBSystemException, BBBBusinessException;

	public boolean isGiftWrapItem (String siteId, String skuId)
			throws BBBSystemException, BBBBusinessException;

	public List<RegistryTypeVO> getRegistryTypes (String siteId)
			throws BBBSystemException, BBBBusinessException;

	public boolean isSKUAvailable (String siteId, String skuId)
			throws BBBSystemException, BBBBusinessException;

	public SiteChatAttributesVO getSiteChatAttributes (String siteId)
			throws BBBSystemException;
	
	public String getGridListAttributes(String siteId)
	throws BBBSystemException;

	public List<String> getTimeZones (String siteId)
			throws BBBSystemException, BBBBusinessException;

	public String getcustomerCareEmailAddress(String siteId)
			throws BBBSystemException, BBBBusinessException;

	public List<CreditCardTypeVO> getCreditCardTypes(String siteId)
			throws BBBBusinessException, BBBSystemException;
	public Map<String, Map<String,String>> getAllConfigKeys() throws BBBSystemException, BBBBusinessException;
    public Map<String, String> getConfigValueByconfigType(String configType)
			throws BBBSystemException, BBBBusinessException;
	public List<String> getAllValuesForKey(String configType, String key)
			throws BBBSystemException, BBBBusinessException;
	public boolean getValueForConfigKey(String configType, String key, boolean defaultValue)
			throws BBBSystemException, BBBBusinessException;
	public String getValueForConfigKey(String configType, String key, String defaultValue)
			throws BBBSystemException, BBBBusinessException;
	public GiftWrapVO getWrapSkuDetails(String siteId) throws BBBBusinessException, BBBSystemException;
	public GiftWrapVO getGiftWrapSKUDetails() throws BBBBusinessException, BBBSystemException;
	public Map<String, String> getCommonGreetings(String siteId) throws BBBBusinessException, BBBSystemException;
	public BrandVO getBrandDetails(String brandId,String siteId) throws BBBBusinessException, BBBSystemException;
	public String getSiteTag(String siteId) throws BBBBusinessException, BBBSystemException;
	public RepositoryItem[] getPromotions(String couponId) throws BBBSystemException, BBBBusinessException;
	public boolean isPackNHoldWindow(String siteId,Date date) throws BBBSystemException, BBBBusinessException;
	public ThresholdVO getSkuThreshold(String siteId,String skuId) throws BBBSystemException,  BBBBusinessException;
	public SchoolVO getSchoolDetailsByName(String schoolName) throws BBBSystemException,  BBBBusinessException;
	public SchoolVO getSchoolDetailsById(String schoolId) throws BBBSystemException,  BBBBusinessException;

	public List<RollupTypeVO>getRollUpList(String productId,String firstRollUpValue,String firstRollUpType,String secondRollUpType)
			throws  BBBSystemException, BBBBusinessException;
	public String getSKUDetails(String siteId, String productId,
			Map<String,String> rollUpTypeValueMap) throws BBBSystemException,
			BBBBusinessException;
	public String getEverLivingSKUDetails(String siteId, String productId,
			Map<String,String> rollUpTypeValueMap) throws BBBSystemException,
			BBBBusinessException;
	public  SKUDetailVO getSKUDetails(String siteId, String skuId,
			boolean calculateAboveBelowLine,boolean isMinimal,boolean storeCheckReqd) throws BBBSystemException,
			BBBBusinessException;
	public List<StoreVO> getCanadaStoreLocatorInfo() throws  BBBSystemException, BBBBusinessException;
	public StoreVO getStoreDetails(String storeId) throws BBBBusinessException,BBBSystemException;
	public boolean isLeafCategory(String siteId, String categoryId) throws BBBSystemException, BBBBusinessException;
	public List<ProductVO> getGiftProducts(String siteId)throws BBBSystemException, BBBBusinessException;
	public List<ProductVO> getCollegeProduct(String collegeId,String siteId) throws BBBBusinessException, BBBSystemException;
	public String getDefaultCountryForSite(String siteId) throws BBBBusinessException, BBBSystemException;
	public List<String> getBopusEligibleStates(String siteId)throws BBBBusinessException, BBBSystemException;
	public List<String> getBopusDisabledStates()throws BBBBusinessException, BBBSystemException;
	public List<String> getBopusDisabledStores()throws BBBBusinessException, BBBSystemException;
	public String getExpectedDeliveryDate(String pShippingMethod, String pSiteId)throws BBBBusinessException, BBBSystemException;
	public RepositoryItem getShippingMethod(String shippingMethod) throws BBBBusinessException, BBBSystemException;
	public Map<String, CategoryVO> getParentCategoryForProduct(String productId, String siteId) throws BBBBusinessException,
	BBBSystemException;
	public String getImmediateParentCategoryForProduct(String productId, String siteId) throws BBBBusinessException,
	BBBSystemException;
	public Map<String, CategoryVO> getParentCategory(String categoryId, String siteId) throws BBBBusinessException,
	BBBSystemException;
	public String getParentProductForSku(String skuId) throws BBBBusinessException, BBBSystemException;
	public Boolean isFirstLevelCategory(String categoryId, String siteId)throws BBBBusinessException,
	BBBSystemException;
	public ImageVO getSkuImages(String skuId) throws BBBBusinessException, BBBSystemException;
	public String getRootCollegeIdFrmConfig(String siteId) throws BBBSystemException, BBBBusinessException;
	public List<String> getContentCatalogConfigration(String key) throws BBBSystemException, BBBBusinessException;
	public boolean isCategoryActive(RepositoryItem categoryRepositoryItem);
	public boolean isProductActive(RepositoryItem productRepositoryItem);
	//public boolean isEcoFeeEligibleForState(String stateId) throws BBBBusinessException,BBBSystemException;
	public EcoFeeSKUVO getEcoFeeSKUDetailForState(String stateId,String skuId)throws BBBBusinessException,BBBSystemException;
	public boolean isEcoFeeEligibleForStore(String storeId)throws BBBBusinessException,BBBSystemException;
	public EcoFeeSKUVO getEcoFeeSKUDetailForStore(String storeId,String skuId)throws BBBBusinessException,BBBSystemException;
	public String getFirstActiveParentProductForSKU(String skuId) throws BBBBusinessException,BBBSystemException;
	public boolean isSkuExcluded(String skuId,String promotionCode,boolean isAppliedCoupon) throws BBBSystemException, BBBBusinessException;
	public String getMaxInventorySKUForProduct(ProductVO productVO, String siteId) throws BBBBusinessException, BBBSystemException ;
	public String getRegistryTypeName(String registryCode,String siteId) throws BBBSystemException, BBBBusinessException;
	public SiteVO getSiteDetailFromSiteId(String siteId)throws BBBBusinessException,BBBSystemException;
	public Map<String,RegistryCategoryMapVO> getCategoryForRegistry(String registryTypeId) throws BBBSystemException, BBBBusinessException;
	public PromoBoxVO getPromoBoxForRegistry(String registryTypeId) throws BBBSystemException;
	public List<String> getPriceRanges(String registryCode,String country) throws BBBSystemException,BBBBusinessException;
	public List<String> getMxPriceRanges(String registryCode) throws BBBSystemException,BBBBusinessException;
	public Map<String,List<SKUDetailVO>> sortSkubyRegistry(List <String> skuIdList,Map <String,Double> skuIdPriceMap,String registryCode,String siteId,String sortType,String country) throws BBBBusinessException, BBBSystemException;
	public Map<String,List<SKUDetailVO>> sortMxSkubyRegistry(List <String> skuIdList,Map <String,Double> skuIdPriceMap,String registryCode,String siteId,String sortType, String mxConversionValue) throws BBBBusinessException, BBBSystemException;
	public Double getListPrice(String productId, String skuId) throws BBBSystemException;
    public Double getEffectivePrice(String productId, String skuId) throws BBBSystemException, BBBBusinessException;
    public Double getEffectivePrice(String productId, String skuId, String refNum, double pPrice) throws BBBSystemException, BBBBusinessException;
	public Double getSalePrice(String productId, String skuId) throws BBBSystemException;
	public String getParentProductForSku(String skuId,boolean activeParentNotReqd) throws BBBBusinessException, BBBSystemException;
	public String getThirdPartyTagStatus(String siteId, BBBCatalogTools catalogTools, String tagName) throws BBBBusinessException, BBBSystemException;
	public Map<String, String> getSkuPropFlagStatus(String SkuId) throws BBBBusinessException, BBBSystemException;
	public ProductVO getProductDetails(String pSiteId, String pProductId, boolean isMinimalDetails) throws BBBSystemException, BBBBusinessException;
	public RepositoryItem[] executeRQLQuery(String rqlQuery, String viewName)throws RepositoryException, BBBSystemException, BBBBusinessException;
	public List<MediaVO> getProductMedia(String productId,String siteId) throws BBBSystemException;
	public String getExpectedDeliveryDate(String shippingMethod,String state, String siteId) throws BBBBusinessException, BBBSystemException;
	public String getExpectedDeliveryDate(String shippingMethod,String state, String siteId, Date orderDate, boolean includeYearFlag) throws BBBBusinessException, BBBSystemException;
	public RepositoryItem[] getSkuClass(RepositoryItem sku) throws BBBSystemException;
	public boolean isClearanceProduct(String siteId, RepositoryItem product);
	public boolean isBeyondValueProduct(String siteId, RepositoryItem product);
	public boolean isSpecialPurchaseProduct(String siteId, RepositoryItem product);
	public List<CollectionProductVO> getDormRoomCollections(String siteId,
			String categoryId) throws BBBSystemException, BBBBusinessException;
	public String getJDADeptForSku(String siteId,String skuId) throws BBBBusinessException, BBBSystemException;
	public RepositoryItem getSkuRepositoryItem(String skuId) throws BBBBusinessException, BBBSystemException;
	public RepositoryItem[] getMultipleSkuRepositoryItems(final String[] skuIds) throws BBBBusinessException, BBBSystemException;
	public Object getRestStoreDetails(String storeId) throws BBBBusinessException,BBBSystemException,RepositoryException;
	public List<StoreSpecialityVO> getStoreSpecialityList(Set<RepositoryItem> specialityItemSet);
	public CategoryVO getCollegeCategories() throws BBBBusinessException,
			BBBSystemException;
	public List<CollectionProductVO> getDormRoomCollection() throws BBBSystemException, BBBBusinessException;
	public List<CollectionProductVO> getClearanceProduct() throws BBBSystemException, BBBBusinessException;

	public boolean isShippingZipCodeRestrictedForSku(String skuId,String siteId,String zipCode) throws BBBBusinessException, BBBSystemException;
	public boolean isSkuWithRestrictedShipping(String skuId,String siteId) throws BBBBusinessException, BBBSystemException;
	public String getRestrictedSkuDetails(String pSkuId,String siteId) throws BBBBusinessException, BBBSystemException;
	public List<StoreVO> getUSAStoreDetails() throws BBBBusinessException, BBBSystemException;
    public RepositoryItem getShippingDuration(String pShippingMethod, String pSiteId) throws BBBBusinessException, BBBSystemException;
	public RepositoryItem[] getAttributeInfoRepositoryItems(String id)throws BBBBusinessException, BBBSystemException;

	public List<String> getBopusInEligibleStores(String pStoreId, String pSiteId) throws BBBSystemException, BBBBusinessException;
	public BazaarVoiceProductVO getBazaarVoiceDetails(String pProductId);
	public BazaarVoiceProductVO getBazaarVoiceDetails(String pProductId,String siteId);
	public void getBccManagedCategory(CategoryVO pCategoryVO)throws BBBSystemException, BBBBusinessException;

	// R2 Scope Item -- API for returning the Page Tabs order for a particular page.
	public List<String> getTabNameList(String pageName) throws BBBSystemException, BBBBusinessException;

	 // added as part of release 2.1 scope#29.
	public String getPrimaryCategory(String productId) throws BBBSystemException, BBBBusinessException;

	public boolean isSkuOnSale(String productId,String skuId) throws BBBBusinessException, BBBSystemException;
	public boolean isSkuActive(String skuId) throws RepositoryException;
	public List<String> siteAttributeValues(final String pSiteId) throws BBBBusinessException, BBBSystemException;

	public boolean isProductActive(RepositoryItem prodRepoItem,String siteIdValue);
	public boolean getBrandDisplayFlag(String pBrandName) throws BBBBusinessException, BBBSystemException;
	public BeddingShipAddrVO getBeddingShipAddress(String schoolId) throws BBBSystemException,  BBBBusinessException;

	public boolean isPreviewEnabled();

	public Date getPreviewDate();
	public CategoryVO getSortedCollegeCategory(CategoryVO collegeCategory) throws BBBBusinessException, BBBSystemException;
	public boolean validateBeddingAttDate(String beddingShiptDate,String currentDate) throws ParseException;
	public BeddingShipAddrVO validateBedingKitAtt(List<HardgoodShippingGroup> shipGrpList, String collegeIdValue);
	public BeddingShipAddrVO getBeddingShipAddrVO(String collegeIdValue);
	public String getProductGuideId(RepositoryItem productRepositoryItem, String siteId) throws BBBSystemException;

	// Added as part of Ask and Answer and Chat
	public Map<String, CategoryVO> getAllParentCategoryForProduct(String productId, String siteId) throws BBBBusinessException,
	BBBSystemException;
	public PDPAttributesVO PDPAttributes (String pProductId, String pCategoryId, String pPoc, String pSiteId) throws BBBBusinessException, BBBSystemException;
	public boolean checkGlobalChat (String pSiteId)throws BBBSystemException;
	
	// Added as part of Shop Similar Item Link Changes
	public String getShopSimilarItemCategory(String pProductId, String pSiteId) throws BBBSystemException, BBBBusinessException;
	public Map<String,String> getSearchSortFieldMap() throws BBBSystemException;
	public boolean isProductActive(String pProductId,String siteIdValue) throws BBBBusinessException, BBBSystemException;
	public boolean isProductActive(String pProductId) throws BBBSystemException, BBBBusinessException;
	SKUDetailVO getSKUDetails(String siteId, boolean checkSKUActiveFlag,String skuId) throws BBBSystemException, BBBBusinessException;
	ProductVO getProductDetails(RepositoryItem productRepositoryItem,String pSiteId);
	public RepositoryItem getCategoryRepDetail(String currentSiteId, String catId);
	
	//R2.2 story 178-A4. Added for product comparison page
	public ProductVO getComparisonProductDetails(String productId, String siteId, CompareProductEntryVO compareProductVO) throws BBBBusinessException, BBBSystemException;
	public SKUDetailVO getComparisonSKUDetails(String siteId, String skuId, boolean calculateAboveBelowLine, CompareProductEntryVO compareProductEntryVO) throws BBBSystemException, BBBBusinessException;
	public void getCompareProductDetail(CompareProductEntryVO compareProductVO) throws BBBBusinessException, BBBSystemException;
	//R2.2 RM DEFECT- 23496 Start.Not able to see the changes made in Endeca in IST forSEO copy inbrand page
	public String getBrandName(String brandId) throws BBBSystemException;
	public String getBrandId(String brandName);
	//R2.2 RM DEFECT- 23496 END
	ProductVO getEverLivingProductDetails(String pSiteId, String pProductId,
			boolean populateRollUp, boolean isMinimalDetails,
			boolean isAddException) throws BBBSystemException,
			BBBBusinessException;
	public String getBazaarVoiceKey() throws BBBBusinessException, BBBSystemException;
	List<RollupTypeVO> getEverLivingRollUpList(String productId,
			String firstRollUpValue, String firstRollUpType,
			String secondRollUpType) throws BBBSystemException,
			BBBBusinessException;
	public RepositoryItem getCategoryForSite(final Set<RepositoryItem> parentCategorySet, final String siteId);
	public RepositoryItem[] executeRQLQuery(String couponRuleQuery, Object[] params, String couponRulesItemDescriptor,
			MutableRepository couponRepository) throws BBBSystemException;
	public boolean isSkuRestrictedForIntShip(SKUDetailVO skuVO) throws BBBBusinessException, BBBSystemException;
	public String getDefaultPLPView(String siteID) throws BBBSystemException;
	public boolean checkForDeptSubDeptClassInclusion(String ruleDeptId,
			String skuJdaDeptId, String ruleSubDeptId, String skuJdaSubDeptId,
			String ruleClass, String skuJdaClass);
	public boolean checkForVendorInclusion(String ruleVendorId,
			String skuVendorId, String ruleDeptId,
			boolean isDeptSubDeptClassInclusive);
	public  RecommendedSkuVO getRecommendedSKU (String siteId, BBBOrderImpl order);	
	
	//LTL Change
	public String getExpectedDeliveryDateForLTLItem(final String shippingMethod, final String siteId,final String skuId,final Date orderDate, boolean includeYearFlag)
            throws BBBBusinessException, BBBSystemException;
	//LTL Change
	public double getAssemblyCharge(final String siteId,final String skuId) throws BBBBusinessException, BBBSystemException;

	public List<ShipMethodVO> getLTLEligibleShippingMethods(String skuID,
			String siteId, String locale) throws BBBSystemException, BBBBusinessException;
	//LTL Change
	public LTLAssemblyFeeVO getLTLAssemblyFeeSkuDetails(String siteId)
			throws BBBSystemException, BBBBusinessException;
	public LTLDeliveryChargeVO getLTLDeliveryChargeSkuDetails(String siteId)
			throws BBBSystemException, BBBBusinessException;
			
	public  SKUDetailVO getMinimalSku(String skuId) throws BBBSystemException,BBBBusinessException;		
		
	public boolean isSkuLtl(String siteId, String skuId) throws BBBSystemException,BBBBusinessException;
	public Object resolveComponentFromNucleus(String componentPath);
    public Object resolveComponentFromRequest(String componentPath);

	/**
	 * This method is used to get the the ImageVo for the Product.
	 * This was added as a part of BBBSL-3022 performance fix
	 * 
	 * @param siteId
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public ImageVO getProductImages(String siteId) throws BBBBusinessException, BBBSystemException;
	/**
	 * This method is used to return the value of EComFulfillment property in thSKU repository Item.
	 * This was added as part of BBBSL-3018 performance fix
	 * 
	 * @param catalogRefId
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public String getSkuEComFulfillment(String skuId) throws BBBBusinessException, BBBSystemException;
    public StoreVO getStoreAppointmentDetails(final String storeId);	
    public double getStateTax(final String stateCode);
    public int getZoomIndex(String productId, String siteId);
	public boolean isInviteFriends();
    public List<String> getBccManagedCategoryList(int tmpDepth, String categoryId) throws BBBSystemException, BBBBusinessException;
    public double getDeliveryCharge(final String siteId, final String skuId, String shippingMethodCode) throws BBBBusinessException, BBBSystemException;
    public boolean isShippingMethodExistsForSku(final String siteId, final String skuId, final String shipMethodId, final boolean isAssemblyFeeOfferedForCI)
    		throws BBBBusinessException, BBBSystemException;
    public String getActualOffsetDate(final String siteId,final String skuId) throws BBBBusinessException, BBBSystemException;
    /**
     * 
     * 
     * @param shippingMethod
     * @param vdcSkuId
     * @param b
     * @param inputDate
     * @return
     * @throws BBBSystemException
     */
	public String getExpectedDeliveryTimeVDC(String shippingMethod,
			String vdcSkuId, boolean requireMsgInDate, Date inputDate, boolean includeYearFlag) throws BBBSystemException;
	public String getExpectedDeliveryTimeVDC(String shippingMethod,
			String vdcSkuId, boolean requireMsgInDate, Date inputDate, boolean includeYearFlag, boolean fromShippingPage) throws BBBSystemException;
	
	boolean isCustomizationRequiredForSKU(RepositoryItem skuRepositoryItem, String siteId) ;
	boolean isCustomizationOfferedForSKU(RepositoryItem skuRepositoryItem, String siteId) ;
	public String getActualOffsetMessage(String skuId, String siteId)  throws BBBSystemException, BBBBusinessException;
	public String getVDCShipMessage(String skuId, boolean requireMsgInDate, String shippingMethod, Date inputDate, boolean fromPDP) throws BBBSystemException;
	public boolean isShippingRestrictionsExistsForSku(String pSkuId) throws BBBBusinessException, BBBSystemException;
	public Map<String,String> getZipCodesRestrictedForSku(final String pSkuId) throws BBBBusinessException, BBBSystemException;
	//public String getVendorNameById(String vendorId) throws BBBSystemException,BBBBusinessException;
	public VendorInfoVO getVendorInfo(String vendorId) throws BBBSystemException,BBBBusinessException;
	public String getParentCategoryIdForProduct(String productId, String siteId) throws BBBBusinessException, BBBSystemException;
	// TBS
	public double getOverrideThreshold(String siteId, String type) throws BBBBusinessException, BBBSystemException;
	public boolean isLogCouponDetails();
	public Set<String> getVendorConfigurationForPDP(String productId, String channel) throws BBBSystemException, BBBBusinessException;
	public boolean isChallengeQuestionOn();
	public RepositoryItem getSKUForUPCSearch(String skuOrUpcId);
	public BBBProduct getProductDetailsForUPCSearch(RepositoryItem productRepositoryItem, RepositoryItem skuItem) throws BBBBusinessException;
	public String getParentProductId(String pProductId, String siteId);
	public ProfileEDWInfoVO populateEDWProfileData(String profileId,ProfileEDWInfoVO edwDataVO) throws NumberFormatException, BBBSystemException, BBBBusinessException, RepositoryException;
	public void removePhantomCategory(List<FacetRefinementVO> facetRefinementVO,String pSiteId ) throws BBBSystemException, BBBBusinessException;
	//PS-61408 |Phantom Flag Failing to Control Dept. Facet - HOLIDAY Impacting
	public void removePhantomCategoryCLP(List<CategoryRefinementVO> categoryRefinementVO,String pSiteId) throws BBBSystemException, BBBBusinessException;
	public boolean isTBSProductActiveMIESearch(RepositoryItem productRepositoryItem);
	public List<CategoryVO> getRelatedCategories (String productId, String siteId) throws BBBSystemException, BBBBusinessException;
	public Object getRestStoreDetails(String storeId, String appointmentType)throws BBBBusinessException, BBBSystemException,RepositoryException;
    public void getDynamicPriceDetails(BBBDynamicPriceVO dynamicPriceVO,String productId);
    public RepositoryItem getDynamicPriceProductItem(String productId);
    public String returnCountryFromSession();
    public boolean getSkuIncartFlag(String skuId);
    public boolean getSkuIncartFlag(String skuId, boolean fetchFromDB);
    public Double getIncartPrice(String productId, String skuId) ;
    public void updateShippingMessageFlag(SKUDetailVO sKUDetailVO, boolean incartEligible) throws BBBBusinessException;
    public boolean hasSDDAttribute (String siteId, RepositoryItem skuRepositoryItem, String regionPromoAttribute, boolean isZipSddEligible) 
			throws BBBSystemException, BBBBusinessException;   
    public String packNHoldEndDate(final String siteId)
			throws RepositoryException;
    /**
	 * Fetch the config value for given type and config key combination
	 * In the case of no value or exception return the default value
	 * @param configType
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getConfigKeyValue(final String configType, final String key, final String defaultValue);
	
	/**
	 * Fetch the config value for given type and config key combination
	 * In the case of no value or exception return the default value
	 * @param configType
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getValueForConfigKey(final String configType, final String key, final int defaultValue);
	public void updateShippingMessageFlag(SKUDetailVO skuDetailVO,
			boolean isSkuPersonalized, double personalizePrice) throws BBBBusinessException;
	public SKUDetailVO getSKUDynamicPriceDetails(Map<Object,Object> parameterMap)
			throws BBBBusinessException, BBBSystemException;
	public BBBDynamicPriceSkuVO getDynamicPriceSKUVO(String skuId, boolean fromCart);
	public BBBDynamicPriceVO getDynamicProdPriceDescription(final String productId);
	public void evaluateDynamicPriceEligiblity(BBBDynamicPriceVO productPriceVo);
	public String getOmnitureVariable(DynamoHttpServletRequest pRequest);
	public boolean isLocalSKUDynamicCacheEnabled();
	public String getCustomizationCodeFromSKU(final RepositoryItem skuRepoItem);
	public String getCustomizationCodeFromSKU(final String sku);
	boolean isPhantomCategory(String pCategoryId) throws BBBSystemException, BBBBusinessException;
	public SortOptionVO getSortOptionsForSite();
	public List<RecommendedCategoryVO> getCategoryRecommendation(String categoryId, String productId);
	public List<ProductVO> getProductDetailsWithSiblings(String pProductId)throws BBBBusinessException, BBBSystemException;
	
}