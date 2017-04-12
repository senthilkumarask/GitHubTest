/**
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import atg.core.util.StringUtils;

import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;


/**
 * The Class RegistryItemVO.
 *
 * @author skalr2
 */
public class RegistryItemVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The sku. */
	private long sku;
	
	/** The qty requested. */
	private int qtyRequested;
	
	/** The qty fulfilled. */
	private int qtyFulfilled;
	
	/** The qty web purchased. */
	private int qtyWebPurchased;
	
	/** The qty purchased. */
	private int qtyPurchased;
	
	/** The jda dept id. */
	private int jdaDeptId;
	
	/** The color cd. */
	private int colorCD;
	
	/** The dept sort seq. */
	private int deptSortSeq;
	
	/** The jda retail price. */
	private double jdaRetailPrice;
	
	/** The is gift giver. */
	private boolean isGiftGiver;
	
	/** The registrant mode. */
	private boolean registrantMode;
	private Map<String, List<AttributeVO>> skuAttributes;
	/** The is below line item. */
	private String isBelowLineItem;
	
	/** The s ku detail vo. */
	private SKUDetailVO sKUDetailVO;

	/** The row id. */
	private String rowID;
	
	/** The upc. */
	private String upc;
	
	/** The color desc. */
	private String colorDesc;
	
	/** The dept name. */
	private String deptName;
	
	/** The jda description. */
	private String jdaDescription;
	
	/** The price. */
	private String price;
	
	/** The price. */
	private String priceVal;
	
	/** The product descrip. */
	private String productDescrip;
	
	/** The image url. */
	private String imageURL;
	
	/** The product url. */
	private String productURL;
	
	/** The promo desc url. */
	private String promoDescURL;
	
	/** The promo desc. */
	private String promoDesc;
	
	/** The small desc. */
	private String smallDesc;
	
	private Timestamp lastMaintained;
	
	private Timestamp createTimestamp;
	
	private BigDecimal customizedPrice;
	private BigDecimal personlisedPrice;

	private String refNum;
	private String personalisedCode;
	private String customizationDetails;
	private String personalizedImageUrls;
	private String personalizedImageUrlThumbs;
	private String personalizedMobImageUrls;
	private String personalizedMobImageUrlThumbs;
	private Double personlisedDoublePrice;
	private Double customizedDoublePrice;
	/** The single digit personalization display code. */
	private String personalizationOptionsDisplay;
	private String ltlDeliveryServices;
	private String ltlShipMethodDesc;
	private String assemblySelected;
	private double deliverySurcharge;
	private double assemblyFees;
	private boolean shipMethodUnsupported;
	private double totalPrice;
	private double totalDeliveryCharges;
	
	private boolean dslUpdateable;
	private String displayNotifyRegistrantMsg;
	
	private String inCartPrice;
	
	private String inCartPriceVal;
	
	// For Eph Category id
	private String ephCatId;
    // For skipping or performing inventory checks 
	private boolean aboveLine;
	
	public boolean isAboveLine() {
		return aboveLine;
	}

	public void setAboveLine(boolean aboveLine) {
		this.aboveLine = aboveLine;
	}

	/**
	 * Gets the display notify registrant msg.
	 *
	 * @return the display notify registrant msg
	 */
	public String getDisplayNotifyRegistrantMsg() {
		return displayNotifyRegistrantMsg;
	}
	
	/**
	 * Sets the display notify registrant msg.
	 *
	 * @param displayNotifyRegistrantMsg the new display notify registrant msg
	 */
	public void setDisplayNotifyRegistrantMsg(String displayNotifyRegistrantMsg) {
		this.displayNotifyRegistrantMsg = displayNotifyRegistrantMsg;
	}
	public boolean isDSLUpdateable() {
		return dslUpdateable;
	}
	public void setDSLUpdateable(boolean isDSLUpdateable) {
		this.dslUpdateable = isDSLUpdateable;
	}
	public double getTotalDeliveryCharges() {
		return totalDeliveryCharges;
	}
	public void setTotalDeliveryCharges(double totalDeliveryCharges) {
		this.totalDeliveryCharges = totalDeliveryCharges;
	}
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	/**
	 * 
	 * @return assemblySelected
	 */
	public String getAssemblySelected() {
		return assemblySelected;
	}
	/**
	 * 
	 * @param assemblySelected
	 */
	public void setAssemblySelected(String assemblySelected) {
		this.assemblySelected = assemblySelected;
	}
	/**
	 * 
	 * @return shipMethodUnsupported
	 */
	public boolean isShipMethodUnsupported() {
		return shipMethodUnsupported;
	}

	public void setShipMethodUnsupported(boolean shipMethodUnsupported) {
		this.shipMethodUnsupported = shipMethodUnsupported;
	}

	/**
	 * @return the ltlDeliveryServices
	 */
	public String getLtlDeliveryServices() {
		return ltlDeliveryServices;
	}

	/**
	 * @param ltlDeliveryServices the ltlDeliveryServices to set
	 */
	public void setLtlDeliveryServices(String ltlDeliveryServices) {
		this.ltlDeliveryServices = ltlDeliveryServices;
	}
	/**
	 * @return the ltlDeliveryPrices
	 */
	public String getLtlShipMethodDesc() {
		return ltlShipMethodDesc;
	}

	/**
	 * @param ltlDeliveryPrices the ltlDeliveryPrices to set
	 */
	public void setLtlShipMethodDesc(String ltlShipMethodDesc) {
		this.ltlShipMethodDesc = ltlShipMethodDesc;
	}
	public String getPersonalizedImageUrls() {
		return personalizedImageUrls;
	}

	public void setPersonalizedImageUrls(String personalizedImageUrls) {
		this.personalizedImageUrls = personalizedImageUrls;
	}

	public String getPersonalizedImageUrlThumbs() {
		return personalizedImageUrlThumbs;
	}

	public void setPersonalizedImageUrlThumbs(String personalizedImageUrlThumbs) {
		this.personalizedImageUrlThumbs = personalizedImageUrlThumbs;
	}

	public String getPersonalizedMobImageUrls() {
		return personalizedMobImageUrls;
	}

	public void setPersonalizedMobImageUrls(String personalizedMobImageUrls) {
		this.personalizedMobImageUrls = personalizedMobImageUrls;
	}

	public String getPersonalizedMobImageUrlThumbs() {
		return personalizedMobImageUrlThumbs;
	}

	public void setPersonalizedMobImageUrlThumbs(
			String personalizedMobImageUrlThumbs) {
		this.personalizedMobImageUrlThumbs = personalizedMobImageUrlThumbs;
	}

	public String getCustomizationDetails() {
		return customizationDetails;
	}

	public void setCustomizationDetails(String customizationDetails) {
		this.customizationDetails = customizationDetails;
	}

	private String itemType;
	
	public String getRefNum() {
		return refNum;
	}

	public void setRefNum(String refNum) {
		this.refNum = refNum;
	}

	public String getPersonalisedCode() {
		return personalisedCode;
	}

	public void setPersonalisedCode(String personalisedCode) {
		this.personalisedCode = personalisedCode;
	}

	public BigDecimal getCustomizedPrice() {
		return customizedPrice;
	}

	public void setCustomizedPrice(BigDecimal customizedPrice) {
		this.customizedPrice = customizedPrice;
		if (customizedPrice != null){
			this.setCustomizedDoublePrice(customizedPrice.doubleValue());
		}
		else{
			this.setCustomizedDoublePrice(null);
		}
	}

	public Double getPersonlisedPrice() {
		if (personlisedPrice != null){
			return personlisedPrice.doubleValue();
		}
		else{
			return null;
		}
	}

	public void setPersonlisedPrice(BigDecimal personlisedPrice) {
		this.personlisedPrice = personlisedPrice;
		if (personlisedPrice != null){
			this.setPersonlisedDoublePrice(personlisedPrice.doubleValue());
		}
		else{
			this.setPersonlisedDoublePrice(null);
		}
	}

	/**
	 * @return the lastMaintained
	 */
	public Timestamp getLastMaintained() {
		return lastMaintained;
	}

	/**
	 * @param lastMaintained the lastMaintained to set
	 */
	public void setLastMaintained(Timestamp lastMaintained) {
		this.lastMaintained = lastMaintained;
	}

	/**
	 * @return the createTimestamp
	 */
	public Timestamp getCreateTimestamp() {
		return createTimestamp;
	}

	/**
	 * @param createTimestamp the createTimestamp to set
	 */
	public void setCreateTimestamp(Timestamp createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	/**
	 * Gets the sku.
	 *
	 * @return the sku
	 */
	public long getSku() {
		return sku;
	}
	
	/**
	 * Sets the sku.
	 *
	 * @param sku the sku to set
	 */
	public void setSku(long sku) {
		this.sku = sku;
	}
	
	/**
	 * Gets the qty requested.
	 *
	 * @return the qtyRequested
	 */
	public int getQtyRequested() {
		return qtyRequested;
	}
	
	/**
	 * Sets the qty requested.
	 *
	 * @param qtyRequested the qtyRequested to set
	 */
	public void setQtyRequested(int qtyRequested) {
		this.qtyRequested = qtyRequested;
	}
	
	/**
	 * Gets the qty fulfilled.
	 *
	 * @return the qtyFulfilled
	 */
	public int getQtyFulfilled() {
		return qtyFulfilled;
	}
	
	/**
	 * Sets the qty fulfilled.
	 *
	 * @param qtyFulfilled the qtyFulfilled to set
	 */
	public void setQtyFulfilled(int qtyFulfilled) {
		this.qtyFulfilled = qtyFulfilled;
	}
	
	/**
	 * Gets the qty web purchased.
	 *
	 * @return the qtyWebPurchased
	 */
	public int getQtyWebPurchased() {
		return qtyWebPurchased;
	}
	
	/**
	 * Sets the qty web purchased.
	 *
	 * @param qtyWebPurchased the qtyWebPurchased to set
	 */
	public void setQtyWebPurchased(int qtyWebPurchased) {
		this.qtyWebPurchased = qtyWebPurchased;
	}
	
	/**
	 * Gets the jda description.
	 *
	 * @return the jdaDescription
	 */
	public String getJdaDescription() {
		return jdaDescription;
	}
	
	/**
	 * Sets the jda description.
	 *
	 * @param jdaDescription the jdaDescription to set
	 */
	public void setJdaDescription(String jdaDescription) {
		this.jdaDescription = jdaDescription;
	}
	
	/**
	 * Gets the jda dept id.
	 *
	 * @return the jdaDeptId
	 */
	public int getJdaDeptId() {
		return jdaDeptId;
	}
	
	/**
	 * Sets the jda dept id.
	 *
	 * @param jdaDeptId the jdaDeptId to set
	 */
	public void setJdaDeptId(int jdaDeptId) {
		this.jdaDeptId = jdaDeptId;
	}
	
	/**
	 * Gets the dept name.
	 *
	 * @return the deptName
	 */
	public String getDeptName() {
		return deptName;
	}
	
	/**
	 * Sets the dept name.
	 *
	 * @param deptName the deptName to set
	 */
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	
	/**
	 * Gets the color cd.
	 *
	 * @return the colorCD
	 */
	public int getColorCD() {
		return colorCD;
	}
	
	/**
	 * Sets the color cd.
	 *
	 * @param colorCD the colorCD to set
	 */
	public void setColorCD(int colorCD) {
		this.colorCD = colorCD;
	}
	
	/**
	 * Gets the color desc.
	 *
	 * @return the colorDesc
	 */
	public String getColorDesc() {
		return colorDesc;
	}
	
	/**
	 * Sets the color desc.
	 *
	 * @param colorDesc the colorDesc to set
	 */
	public void setColorDesc(String colorDesc) {
		this.colorDesc = colorDesc;
	}
	
	/**
	 * Gets the dept sort seq.
	 *
	 * @return the deptSortSeq
	 */
	public int getDeptSortSeq() {
		return deptSortSeq;
	}
	
	/**
	 * Sets the dept sort seq.
	 *
	 * @param deptSortSeq the deptSortSeq to set
	 */
	public void setDeptSortSeq(int deptSortSeq) {
		this.deptSortSeq = deptSortSeq;
	}
	
	/**
	 * Gets the upc.
	 *
	 * @return the upc
	 */
	public String getUpc() {
		return upc;
	}
	
	/**
	 * Sets the upc.
	 *
	 * @param upc the upc to set
	 */
	public void setUpc(String upc) {
		this.upc = upc;
	}

	/**
	 * Gets the row id.
	 *
	 * @return the rowID
	 */
	public String getRowID() {
		return rowID;
	}
	
	/**
	 * Sets the row id.
	 *
	 * @param rowID the rowID to set
	 */
	public void setRowID(String rowID) {
		this.rowID = rowID;
	}
	
	/**
	 * Gets the registrant mode.
	 *
	 * @return the registrantModer
	 */
	public boolean getRegistrantMode() {
		return registrantMode;
	}
	
	/**
	 * Sets the registrant mode.
	 *
	 * @param registrantMode the new registrant mode
	 */
	public void setRegistrantMode(boolean registrantMode) {
		this.registrantMode = registrantMode;
	}
	
	/**
	 * Gets the s ku detail vo.
	 *
	 * @return the sKUDetailVO
	 */
	public SKUDetailVO getsKUDetailVO() {
		return sKUDetailVO;
	}
	
	/**
	 * Sets the s ku detail vo.
	 *
	 * @param sKUDetailVO the sKUDetailVO to set
	 */
	public void setsKUDetailVO(SKUDetailVO sKUDetailVO) {
		this.sKUDetailVO = sKUDetailVO;
	}
	
	/**
	 * Checks if is gift giver.
	 *
	 * @return the isGiftGiver
	 */
	public boolean isGiftGiver() {
		return isGiftGiver;
	}
	
	/**
	 * Sets the gift giver.
	 *
	 * @param isGiftGiver the isGiftGiver to set
	 */
	public void setGiftGiver(boolean isGiftGiver) {
		this.isGiftGiver = isGiftGiver;
	}
	
	/**
	 * Gets the qty purchased.
	 *
	 * @return the qtyPurchased
	 */
	public int getQtyPurchased() {
		return qtyPurchased;
	}
	
	/**
	 * Sets the qty purchased.
	 *
	 * @param qtyPurchased the qtyPurchased to set
	 */
	public void setQtyPurchased(int qtyPurchased) {
		this.qtyPurchased = qtyPurchased;
	}
	
	/**
	 * Gets the jda retail price.
	 *
	 * @return the jdaRetailPrice
	 */
	public double getJdaRetailPrice() {
		return jdaRetailPrice;
	}
	
	/**
	 * Sets the jda retail price.
	 *
	 * @param jdaRetailPrice the jdaRetailPrice to set
	 */
	public void setJdaRetailPrice(double jdaRetailPrice) {
		this.jdaRetailPrice = jdaRetailPrice;
	}
	
	/**
	 * Gets the price.
	 *
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}
	
	/**
	 * Sets the price.
	 *
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
	}
	
	/**
	 * Gets the product descrip.
	 *
	 * @return the productDescrip
	 */
	public String getProductDescrip() {
		return productDescrip;
	}
	
	/**
	 * Sets the product descrip.
	 *
	 * @param productDescrip the productDescrip to set
	 */
	public void setProductDescrip(String productDescrip) {
		this.productDescrip = productDescrip;
	}
	
	/**
	 * Gets the image url.
	 *
	 * @return the imageURL
	 */
	public String getImageURL() {
		return imageURL;
	}
	
	/**
	 * Sets the image url.
	 *
	 * @param imageURL the imageURL to set
	 */
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	
	/**
	 * Gets the product url.
	 *
	 * @return the productURL
	 */
	public String getProductURL() {
		return productURL;
	}
	
	/**
	 * Sets the product url.
	 *
	 * @param productURL the productURL to set
	 */
	public void setProductURL(String productURL) {
		this.productURL = productURL;
	}
	
	/**
	 * Gets the promo desc url.
	 *
	 * @return the promoDescURL
	 */
	public String getPromoDescURL() {
		return promoDescURL;
	}
	
	/**
	 * Sets the promo desc url.
	 *
	 * @param promoDescURL the promoDescURL to set
	 */
	public void setPromoDescURL(String promoDescURL) {
		this.promoDescURL = promoDescURL;
	}
	
	/**
	 * Gets the promo desc.
	 *
	 * @return the promoDesc
	 */
	public String getPromoDesc() {
		return promoDesc;
	}
	
	/**
	 * Sets the promo desc.
	 *
	 * @param promoDesc the promoDesc to set
	 */
	public void setPromoDesc(String promoDesc) {
		this.promoDesc = promoDesc;
	}
	
	/**
	 * Gets the small desc.
	 *
	 * @return the smallDesc
	 */
	public String getSmallDesc() {
		return smallDesc;
	}
	
	/**
	 * Sets the small desc.
	 *
	 * @param smallDesc the smallDesc to set
	 */
	public void setSmallDesc(String smallDesc) {
		this.smallDesc = smallDesc;
	}
	
	/**
	 * Gets the checks if is below line item.
	 *
	 * @return the checks if is below line item
	 */
	public String getIsBelowLineItem() {
		return isBelowLineItem;
	}
	
	/**
	 * Sets the checks if is below line item.
	 *
	 * @param isBelowLineItem the new checks if is below line item
	 */
	public void setIsBelowLineItem(String isBelowLineItem) {
		this.isBelowLineItem = isBelowLineItem;
	}

	
	public String getPriceVal() {
		if (!StringUtils.isEmpty(getPrice()))
		{
			DecimalFormat df = new DecimalFormat("#.00");
			priceVal = df.format(Double.valueOf(getPrice()));
		}
			
		return priceVal;
	}

	public void setPriceVal(String priceVal) {
		this.priceVal = priceVal;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public Double getPersonlisedDoublePrice() {
		return personlisedDoublePrice;
	}

	public void setPersonlisedDoublePrice(Double personlisedDoublePrice) {
		this.personlisedDoublePrice = personlisedDoublePrice;
	}

	public Double getCustomizedDoublePrice() {
		return customizedDoublePrice;
	}

	public void setCustomizedDoublePrice(Double customizedDoublePrice) {
		this.customizedDoublePrice = customizedDoublePrice;
	}
	/**
	 * Gets the deliverySurcharge for  item.
	 *
	 * @return the deliverySurcharge for item
	 */
	public double getDeliverySurcharge() {
		return deliverySurcharge;
	}
	/**
	 * Sets the deliverySurcharge.
	 *
	 * @param deliverySurcharge the deliverySurcharge to set
	 */
	public void setDeliverySurcharge(double deliverySurcharge) {
		this.deliverySurcharge = deliverySurcharge;
	}
	/**
	 * Gets the assemblyFees of the item.
	 *
	 * @return the assemblyFees of the  item
	 */
	public double getAssemblyFees() {
		return assemblyFees;
	}
	/**
	 * Sets assemblyFees.
	 *
	 * @param assemblyFees the assemblyFees to set
	 */
	public void setAssemblyFees(double assemblyFees) {
		this.assemblyFees = assemblyFees;
	}
	/**
	 * @return the personalizationOptionsDisplay
	 */
	public String getPersonalizationOptionsDisplay() {
		return personalizationOptionsDisplay;
	}

	/**
	 * @param personalizationOptionsDisplay the personalizationOptionsDisplay to set
	 */
	public void setPersonalizationOptionsDisplay(
			String personalizationOptionsDisplay) {
		this.personalizationOptionsDisplay = personalizationOptionsDisplay;
	}

	public String getInCartPrice() {
		return inCartPrice;
	}

	public void setInCartPrice(String inCartPrice) {
		this.inCartPrice = inCartPrice;
	}

	public String getInCartPriceVal() {
		if (!StringUtils.isEmpty(getInCartPrice()))
		{
			DecimalFormat df = new DecimalFormat("#.00");
			inCartPriceVal = df.format(Double.valueOf(getInCartPrice()));
		}
			
		return inCartPriceVal;
	}

	public void setInCartPriceVal(String inCartPriceVal) {
		this.inCartPriceVal = inCartPriceVal;
	}

	public String getEphCatId() {
		return ephCatId;
	}

	public void setEphCatId(String ephCatId) {
		this.ephCatId = ephCatId;
	}

	public Map<String, List<AttributeVO>> getSkuAttributes() {
		return skuAttributes;
	}

	public void setSkuAttributes(Map<String, List<AttributeVO>> skuAttributes) {
		this.skuAttributes = skuAttributes;
	}
	
}
