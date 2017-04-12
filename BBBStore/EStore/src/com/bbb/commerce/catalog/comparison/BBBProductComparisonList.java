package com.bbb.commerce.catalog.comparison;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import atg.commerce.catalog.comparison.ComparisonList;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.util.TableInfo.Column;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.comparison.vo.CompareProductEntryVO;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

/**
 * @author magga3
 * 
 */
public class BBBProductComparisonList extends ComparisonList {

	private String url;
	private String lastSearchUrlCookie;
	private MutableRepository catalogRepository;
	private BBBCatalogTools catalogTools;
	private String cookieName;
	private String skuCookieName;
    private int colorFlagClear;
    private int attributesListFlagClear;
    private int customizationCodeFlagClear;
    
    public int getCustomizationCodeFlagClear() {
		return customizationCodeFlagClear;
	}

	public void setCustomizationCodeFlagClear(int customizationCodeFlagClear) {
		this.customizationCodeFlagClear = customizationCodeFlagClear;
	}

	/**
	 * BBBSessionBean
	 */
	private  BBBSessionBean sessionBean;
	/**
	 * @return the catalogRepository
	 */
	/**
	 * @return catalogRepository
	 */
	public MutableRepository getCatalogRepository() {
		return this.catalogRepository;
	}

	/**
	 * @param catalogRepository
	 *            the catalogRepository to set
	 */
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	/**
	 * @return the catalogTools
	 */
	/**
	 * @return catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return the sessionBean
	 */
	public BBBSessionBean getSessionBean() {
		return sessionBean;
	}

	/**
	 * @param sessionBean the sessionBean to set
	 */
	public void setSessionBean(BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	/**
	 * add method to add product to comparison List Check - 1. comparison List
	 * is less than 5(Max Product count that can be compared) 2. comparison List
	 * already contains that item or not
	 * 
	 * @param compareProductEntryVO
	 * @return true if product added successfully otherwise false
	 */
	public boolean add(CompareProductEntryVO compareProductEntryVO) {
		if (isLoggingDebug()) {
			logDebug("BBBProductComparisonList.add() : Start");
		}
		boolean productAdded = false;
		if (compareProductEntryVO != null
				&& super.getItems().size() < BBBCoreConstants.MAX_COMPARE_PRODUCT_COUNT) {
			productAdded = this.addItem(compareProductEntryVO);
		} else {
			logInfo("Cannot add item [" + ((compareProductEntryVO != null)?compareProductEntryVO.getProductId():"")
					+ "] in compare list, Maximum Items already added");
		}

		if (isLoggingDebug()) {
			logDebug("BBBProductComparisonList.add() : End");
		}
		return productAdded;
	}

	/**
	 * removeItem method to remove product from comparison List
	 * 
	 * @param compareProductEntryVO
	 * @param pRequest
	 * @param pResponse
	 * @return true if product found in comparison List and removed otherwise
	 *         false
	 * @throws IOException
	 * @throws ServletException
	 */
	@SuppressWarnings("rawtypes")
	public boolean removeItem(CompareProductEntryVO compareProductEntryVO,
			DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		if (isLoggingDebug()) {
			logDebug("BBBProductComparisonList.removeItem() : Start");
		}
		boolean productRemoved = false;
		List<CompareProductEntryVO> productInDrawer = super.getItems();
		synchronized (productInDrawer) {
			if (compareProductEntryVO != null && productInDrawer != null) {
				for (CompareProductEntryVO iter : productInDrawer) {
					if (iter != null
							&& iter.getProductId().equals(
									compareProductEntryVO.getProductId())) {
						productInDrawer.remove(iter);
						productRemoved = true;
						// save the comma separated product ids of the products
						// currently in the compare drawer in the cookie. The
						// cookie
						// is updated each time the product is removed from the
						// drawer.
						if (productInDrawer.size() == 0) {
							addListToCookie(pRequest, pResponse, "", "", true);
						} else {
							String productList = "";
							String skuList = "";
							for (CompareProductEntryVO compareProduct : productInDrawer) {
								productList = productList
										+ compareProduct.getProductId() + ",";
								skuList = skuList + compareProduct.getSkuId()
										+ ",";

							}
							if (!BBBUtility.isEmpty(productList)
									&& !BBBUtility.isEmpty(skuList)) {
								productList = productList.substring(0,
										productList.length() - 1);
								addListToCookie(pRequest, pResponse,
										productList, skuList, false);

							}
						}
						break;
					}
				}
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBProductComparisonList.removeItem() : End");
		}
		return productRemoved;
	}

	/**
	 * Override containsItem method to check if given product already exists in
	 * comparison List Check - Only productId to decide whether it exist or not
	 * 
	 * @param Object
	 * @return true if exists otherwise false
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean containsItem(Object obj) {
		if (isLoggingDebug()) {
			logDebug("BBBProductComparisonList.containsItem() : Start");
		}
		boolean containsItem = false;
		CompareProductEntryVO addProductVO = (CompareProductEntryVO) obj;
		List productInDrawer = super.getItems();
		synchronized (productInDrawer) {
			if (productInDrawer != null && productInDrawer.size() > 0) {
				for (Object drawerProduct : productInDrawer) {
					CompareProductEntryVO drawerProductVO = (CompareProductEntryVO) drawerProduct;
					if (addProductVO.getProductId().equals(
							drawerProductVO.getProductId())) {
						containsItem = true;
						break;
					}
				}
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBProductComparisonList.containsItem() : End");
		}
		return containsItem;
	}

	/**
	 * This method is overridden to load the compare drawer items list. If the
	 * drawer is empty, it checks for the cookie containing the drawer products
	 * list. When the user session is expired/ logged out, the drawer is again
	 * loaded from this cookie and the items are again added to the
	 * ProductComparisonList
	 * 
	 */
	@Override
	public List getItems() {

		if (!BBBUtility.isListEmpty(super.getItems())) {
			return super.getItems();
		}
		String productsList = "";
		String skuList = "";
		final Cookie[] allCookies = ServletUtil.getCurrentRequest()
				.getCookies();
		if (allCookies != null) {
			for (Cookie cookie : allCookies) {
				if (cookie.getName().equals(this.getCookieName())) {
					productsList = cookie.getValue();
				}
				if (cookie.getName().equals(this.getSkuCookieName())) {
					skuList = cookie.getValue();
				}
			}
		}
		List<String> items = new ArrayList<String>();
		List<String> skuItems = new ArrayList<String>();
		if (!BBBUtility.isEmpty(productsList)) {
			items = Arrays.asList(productsList.split("\\s*,\\s*"));
		}
		if (!BBBUtility.isEmpty(skuList)) {
			skuItems = Arrays.asList(skuList.split("\\s*,\\s*"));
		}
		// the products are again added to the ComparisonList
		int itemCount = 0;
		for (String productId : items) {
			String skuId = skuItems.get(itemCount);
			if (skuId.equals("null")) {
				skuId = null;
			}
			boolean result = false;
			CompareProductEntryVO compareProductVO = populateVO(productId,
					skuId);
			if (compareProductVO != null) {
				result = add(compareProductVO);
			}
			if (!result) {
				this.logError("BBBProductComparisonList.getItems() : Error while adding Product to comparison List from cookie");
			}
			itemCount++;
		}
		return super.getItems();
	}

	private CompareProductEntryVO populateVO(String productId, String skuId) {
		if (isLoggingDebug()) {
			logDebug("BBBCompareProductsCookieDroplet.populateVO() : Start");
		}

		CompareProductEntryVO compareProductEntryVO = new CompareProductEntryVO();
		if (skuId != null) {
			compareProductEntryVO.setSkuId(skuId);
			compareProductEntryVO.setProductId(productId);
			try {
				RepositoryItem skuRepositoryItem = getCatalogRepository()
						.getItem(skuId,
								BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
				if (skuRepositoryItem != null
						&& skuRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) != null) {

					String ImagePath = (String) skuRepositoryItem
							.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME);
					List<String> scene7KeysList = getCatalogTools()
							.getAllValuesForKey(
									BBBCoreConstants.THIRD_PARTY_URL,
									BBBCoreConstants.SCENE7_URL);
					String scene7Path = scene7KeysList.get(0);
					compareProductEntryVO.setImagePath(scene7Path
							+ BBBCoreConstants.SLASH + ImagePath);
					// setting LTL flag
					if(skuRepositoryItem.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) != null){
						compareProductEntryVO.setLtlProduct((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME));
					}
					
				}
			} catch (RepositoryException e) {
				logDebug("unable to retrieve item from Sku Repository for skuId"+skuId);

			} catch (BBBSystemException e) {
				logError("BBBCompareProductHandler.populateVO() : System Exception while fetching scene7 key "+ e);
			} catch (BBBBusinessException e) {
				logError("BBBCompareProductHandler.populateVO() : Business Exception while fetching scene7 key "+ e);
			}
			
		}
		else{
		compareProductEntryVO.setProductId(productId);
		RepositoryItem productRepositoryItem;
		try {
			productRepositoryItem = this.getCatalogRepository().getItem(
					productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
			if (productRepositoryItem != null
					&& productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.COLLECTION_THUMBNAIL_PRODUCT_PROPERTY_NAME) != null) {
				final String imagePath = (String) productRepositoryItem
						.getPropertyValue(BBBCatalogConstants.COLLECTION_THUMBNAIL_PRODUCT_PROPERTY_NAME);
				List<String> scene7KeysList = getCatalogTools()
						.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL,
								BBBCoreConstants.SCENE7_URL);
				String scene7Path = scene7KeysList.get(0);
				compareProductEntryVO.setImagePath(scene7Path
						+ BBBCoreConstants.SLASH + imagePath);
			}
		} catch (RepositoryException e) {
			logError("BBBCompareProductsCookieDroplet.populateVO() : Repository Exception while getting product Info product id "
					+ e);
			compareProductEntryVO = null;
		} catch (BBBSystemException e) {
			logError("BBBCompareProductsCookieDroplet.populateVO() : System Exception while fetching scene7 key "
					+ e);
			compareProductEntryVO = null;
		} catch (BBBBusinessException e) {
			logError("BBBCompareProductsCookieDroplet.populateVO() : Business Exception while fetching scene7 key "
					+ e);
			compareProductEntryVO = null;
		}
		if (isLoggingDebug()) {
			logDebug("BBBCompareProductsCookieDroplet.populateVO() : End");
		}
		}
		return compareProductEntryVO;
	}

	/**
	 * This method is used to create the browser session cookie containing comma
	 * separated product ids of the products currently in the drawer. The cookie
	 * is deleted if the drawer is cleared.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param productList
	 * @param killCookie
	 * @throws ServletException
	 * @throws IOException
	 */
	public void addListToCookie(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse, String productList,
			String skuList, boolean killCookie) throws ServletException,
			IOException {

		String domain = pRequest.getServerName();
		String path = "/";
		if(isLoggingDebug()){
			logDebug("path to set:" + path);
			logDebug("domain to set:" + domain);
		}	

		final Cookie cookie = new Cookie(this.getCookieName(), productList);
		final Cookie skuCookie = new Cookie(this.getSkuCookieName(), skuList);
		skuCookie.setDomain(domain);
		skuCookie.setPath(path);
		cookie.setDomain(domain);
		cookie.setPath(path);
		if (killCookie) {
			cookie.setMaxAge(0);
			skuCookie.setMaxAge(0);
		}
		BBBUtility.addCookie(pResponse, cookie, true);
		BBBUtility.addCookie(pResponse, skuCookie, true);
	}

	/**
	 * This method is used to fetch the last navigated PLP/ Search page url from
	 * session. If the session is expired/ logged out, then it will fetch the
	 * url from browser cookie. This url is used on product comparison page to
	 * add another item.
	 * 
	 * @return url
	 */
	public String getUrl() {

		if (BBBUtility.isEmpty(this.url)) {
			final Cookie[] allCookies = ServletUtil.getCurrentRequest()
					.getCookies();
			if (allCookies != null) {
				for (Cookie cookie : allCookies) {
					if (cookie.getName().equals(this.getLastSearchUrlCookie())) {
						String lastnavigatedUrl = cookie.getValue();
						if (!BBBUtility.isEmpty(lastnavigatedUrl)) {
							return lastnavigatedUrl;
						}
					}
				}
			}
		}
		return this.url;
	}

	/**
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return lastSearchUrlCookie
	 */
	public String getLastSearchUrlCookie() {
		return this.lastSearchUrlCookie;
	}

	/**
	 * @param lastSearchUrlCookie
	 */
	public void setLastSearchUrlCookie(String lastSearchUrlCookie) {
		this.lastSearchUrlCookie = lastSearchUrlCookie;
	}

	/**
	 * @return cookieName
	 */
	public String getCookieName() {
		return this.cookieName;
	}

	/**
	 * @param cookieName
	 */
	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

	public String getSkuCookieName() {
		return skuCookieName;
	}

	public void setSkuCookieName(String skuCookieName) {
		this.skuCookieName = skuCookieName;
	}

	public int getColorFlagClear() {
		return colorFlagClear;
	}

	public void setColorFlagClear(int colorFlagClear) {
		this.colorFlagClear = colorFlagClear;
	}

	public int getAttributesListFlagClear() {
		return attributesListFlagClear;
	}

	public void setAttributesListFlagClear(int attributesListFlagClear) {
		this.attributesListFlagClear = attributesListFlagClear;
	}
	
	
	/**
	 * Get the table columns and skips Free Standard Shipping and Clearance if these 
	 * two attributes are restricted for international context
	 * @return Column[]
	 */
	@Override
	
	public Column[] getTableColumns() {
		if (isLoggingDebug()) {
			logDebug("Entering - BBBProductComparisonList Method Name [getTableColumns]");
		}
		
		RepositoryItem fSSAttributeRepoItem = null;
		RepositoryItem clearanceAttributeRepoItem = null;
		boolean internationalShippingContext = false;
		if (this.getSessionBean() != null) {
			internationalShippingContext = this.getSessionBean().isInternationalShippingContext();
		}
		Column[] columns = super.getTableColumns();
		if (!internationalShippingContext) {
			if (isLoggingDebug()) {
				logDebug("Returning all the columns for Non Internatioanl(Domestic/US) context");
			}
			return columns;
		}
		List<Column> columnList = Arrays.asList(columns);
		if (isLoggingDebug()) {
			logDebug("Retrieved Columns list of size: " + columnList.size());
		}
		List<Column>columnListUpdated = new ArrayList<Column>();
		
		for (Column column : columnList) {
			try {
				if (column.getName().equalsIgnoreCase(BBBCatalogConstants.FREE_STANDARD_SHIPPING)) {
					List<String> freeShippingAttrIdList = BBBConfigRepoUtils.getAllValues(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.FREE_STANDARD_SHIPPING);
					if (!BBBUtility.isListEmpty(freeShippingAttrIdList)) {
						fSSAttributeRepoItem = getCatalogRepository().getItem(freeShippingAttrIdList.get(BBBCoreConstants.ZERO), BBBCatalogConstants.ATTRIBUTE_INFO);
						boolean fssIntlApplicable = checkAttributeIntlApplicability(fSSAttributeRepoItem, internationalShippingContext);
						if (fssIntlApplicable) {
							if (isLoggingDebug()) {
								logDebug("Adding Free Standard Shipping Attribute into the list as it is not internationally restricted");
							}
							columnListUpdated.add(column);
						}
					}
				} else if (column.getName().equalsIgnoreCase(BBBCatalogConstants.CLEARANCE)) {
					List<String> clearanceAttrIdList = BBBConfigRepoUtils.getAllValues(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.CLEARANCE_ATTRIBUTE);
					if (!BBBUtility.isListEmpty(clearanceAttrIdList)) {
						clearanceAttributeRepoItem = getCatalogRepository().getItem(clearanceAttrIdList.get(BBBCoreConstants.ZERO), BBBCatalogConstants.ATTRIBUTE_INFO);
						boolean clearanceIntlApplicable = checkAttributeIntlApplicability(clearanceAttributeRepoItem, internationalShippingContext);
						if (clearanceIntlApplicable) {
							if (isLoggingDebug()) {
								logDebug("Adding Clearance Attribute into the list as it is not internationally restricted");
							}
							columnListUpdated.add(column);
						}
					}
				}else if (!column.getName().equalsIgnoreCase(BBBCatalogConstants.SKU_GIFT_WRAPELIGIBLE)) {
					columnListUpdated.add(column);
			}
			} catch (RepositoryException e) {
				logError("unable to retrieve item from catalog Repository"+ e);
			}
		}
		if (isLoggingDebug()) {
			logDebug("Exit - BBBProductComparisonList " + " from method [getTableColumns]");
			logDebug("Returning updated columns list of size: " + columnListUpdated.size());
		}
		return columnListUpdated.toArray(new Column[columnListUpdated.size()]);
	}
	
	/**
	 * Checks if an Attribute is restricted for international context or not
	 * @param attributeInfo RepositoryItem
	 * @param internationalShippingContext boolean
	 * @return true, if not restricted else false
	 */
	
	public boolean checkAttributeIntlApplicability( RepositoryItem attributeInfo, boolean internationalShippingContext ) {
		boolean attributeIntlApplicable = true;
		if (attributeInfo != null) {
			String attrIntlFlag = (String) attributeInfo.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_FLAG);
            if (internationalShippingContext && !BBBUtility.isEmpty(attrIntlFlag)  && BBBCoreConstants.NO_CHAR.equalsIgnoreCase(attrIntlFlag)) {
            	if (isLoggingDebug()) {
					logDebug("Attribute with id: "+ attributeInfo.getRepositoryId() + " is Restricted for International Context" );
				}
            	attributeIntlApplicable = false;
			}
		}
		return attributeIntlApplicable;	
	}
}
