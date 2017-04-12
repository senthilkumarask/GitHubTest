package com.bbb.commerce.catalog.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.search.bean.result.BBBDynamicPriceSkuVO;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

import atg.repository.RepositoryItem;
/**
 * 
 * @author njai13
 *
 */
public class SKUDetailVO  extends SKUVO {

	/**
	 * 
	 */
    private BBBDynamicPriceSkuVO dynamicSKUPriceVO;
	private static final long serialVersionUID = 1L;
	private Map<String, List<AttributeVO>> skuAttributes;
	private List<ShipMethodVO> freeShipMethods=new ArrayList<ShipMethodVO>();
	private List<StateVO> nonShippableStates=new ArrayList<StateVO>();
	private List<ShipMethodVO> eligibleShipMethods=new ArrayList<ShipMethodVO>();
	private List<RebateVO> eligibleRebates=new ArrayList<RebateVO>();
	private String commaSepNonShipableStates;
	private boolean hasRebate;
	private boolean hasSddAttribute;
	private boolean onSale;
	private boolean zoomAvailable;
	private String parentProdId;
	private boolean restrictedForIntShip;
	private boolean restrictedForBopusAllowed;
	private boolean intlRestricted;
	private boolean customizableRequired;
	private boolean customizationOffered;
	private String customizableCodes;
	private String personalizationType;
	private boolean shippingRestricted;
	private boolean shipMsgFlag;
	private String displayShipMsg;
	private String pricingLabelCode;
	private boolean inCartFlag;
	private boolean dynamicPriceSKU;
    private String displayURL;
    private String customizeCTAFlag;
	
	public String getCustomizeCTAFlag() {
		return customizeCTAFlag;
	}

	public void setCustomizeCTAFlag(String customizeCTAFlag) {
		this.customizeCTAFlag = customizeCTAFlag;
	} 

	/**
	 * @return the shippingRestricted
	 */
	public boolean isShippingRestricted() {
		return shippingRestricted;
	}

	/**
	 * @param shippingRestricted the shippingRestricted to set
	 */
	public void setShippingRestricted(boolean shippingRestricted) {
		this.shippingRestricted = shippingRestricted;
	}

	//BPSI-2440  | Set the VDC offset message
	private String vdcOffsetMessage;
	
	/**
	 * @return the String vdcOffsetMessage
	 */
	public String getVdcOffsetMessage() {
		return this.vdcOffsetMessage;
	}
	
	/**
	 * @param vdcOffsetMessage
	 */
	public void setVdcOffsetMessage(String vdcOffsetMessage) {
		this.vdcOffsetMessage = vdcOffsetMessage;
	}

	/**
	 * @return the customizationOffered
	 */
	public boolean isCustomizationOffered() {
		return this.customizationOffered;
	}

	/**
	 * @param customizationOffered the customizationOffered to set
	 */
	public void setCustomizationOffered(boolean customizationOffered) {
		this.customizationOffered = customizationOffered;
	}

	/**
	 * @return customizableCodes
	 */
	public String getCustomizableCodes() {
		return this.customizableCodes;
	}

	/**
	 * @param customizableCodes
	 */
	public void setCustomizableCodes(String customizableCodes) {
		this.customizableCodes = customizableCodes;
	}

	/**
	 * @return customizableRequired
	 */
	public boolean isCustomizableRequired() {
		return this.customizableRequired;
	}

	/**
	 * @param customizableRequired
	 */
	public void setCustomizableRequired(boolean customizableRequired) {
		this.customizableRequired = customizableRequired;
	}

	/**
	 * @return the intlRestricted
	 */
	public boolean isIntlRestricted() {
		return this.intlRestricted;
	}

	/**
	 * @param intlRestricted the intlRestricted to set
	 */
	public void setIntlRestricted(boolean intlRestricted) {
		this.intlRestricted = intlRestricted;
	}

	public boolean isRestrictedForBopusAllowed() {
		return this.restrictedForBopusAllowed;
	}

	public void setRestrictedForBopusAllowed(boolean restrictedForBopusAllowed) {
		this.restrictedForBopusAllowed = restrictedForBopusAllowed;
	}

	private boolean ltlItem;
	private boolean isAssemblyOffered;
	/**
	 * @return the ltlItem
	 */
	public boolean isLtlItem() {
		return this.ltlItem;
	}

	/**
	 * @param ltlItem
	 */
	public void setLtlItem(boolean ltlItem) {
		this.ltlItem = ltlItem;
	} 

	/**
	 * @return the parentProdId
	 */
	public String getParentProdId() {
		return this.parentProdId;
	}

	/**
	 * @param parentProdId the parentProdId to set
	 */
	public void setParentProdId(String parentProdId) {
		this.parentProdId = parentProdId;
	}

	 

	public SKUDetailVO() {
		// TODO Auto-generated constructor stub
	}

	public SKUDetailVO(RepositoryItem skuRepositoryItem) {
		super(skuRepositoryItem);

	}



	/**
	 * @return the skuAttributes
	 */
	public Map<String, List<AttributeVO>> getSkuAttributes() {
		return this.skuAttributes;
	}

	/**
	 * @param skuAttributes the skuAttributes to set
	 */
	public void setSkuAttributes(Map<String, List<AttributeVO>> skuAttributes) {
		this.skuAttributes = skuAttributes;
	}

	/**
	 * @return the freeShipMethods
	 */
	public List<ShipMethodVO> getFreeShipMethods() {
		return this.freeShipMethods;
	}

	/**
	 * @param freeShipMethods the freeShipMethods to set
	 */
	public void setFreeShipMethods(List<ShipMethodVO> freeShipMethods) {
		this.freeShipMethods = freeShipMethods;
	}

	/**
	 * @return the nonShippableStates
	 */
	public List<StateVO> getNonShippableStates() {
		return this.nonShippableStates;
	}

	/**
	 * @param nonShippableStates the nonShippableStates to set
	 */
	public void setNonShippableStates(List<StateVO> nonShippableStates) {
		this.nonShippableStates = nonShippableStates;
	}

	/**
	 * @return the eligibleShipMethods
	 */
	public List<ShipMethodVO> getEligibleShipMethods() {
		return this.eligibleShipMethods;
	}

	/**
	 * @param eligibleShipMethods the eligibleShipMethods to set
	 */
	public void setEligibleShipMethods(List<ShipMethodVO> eligibleShipMethods) {
		this.eligibleShipMethods = eligibleShipMethods;
	}

	/**
	 * @return the eligibleRebates
	 */
	public List<RebateVO> getEligibleRebates() {

		return this.eligibleRebates;
	}

	/**
	 * @param eligibleRebates the eligibleRebates to set
	 */
	public void setEligibleRebates(List<RebateVO> eligibleRebates) {
		this.eligibleRebates = eligibleRebates;
	}
	/**
	 * @return the commaSepNonShipableStates
	 */
	public String getCommaSepNonShipableStates() {
		return this.commaSepNonShipableStates;
	}

	/**
	 * @param commaSepNonShipableStates the commaSepNonShipableStates to set
	 */
	public void setCommaSepNonShipableStates(String commaSepNonShipableStates) {
		this.commaSepNonShipableStates = commaSepNonShipableStates;
	}

	public boolean isHasRebate() {
		return this.hasRebate;
	}

	public void setHasRebate(boolean hasRebate) {
		this.hasRebate = hasRebate;
	}
	
	public boolean isHasSddAttribute() {
		return this.hasSddAttribute;
	}

	public void setHasSddAttribute(boolean hasSddAttribute) {
		this.hasSddAttribute = hasSddAttribute;
	}
 	
	public boolean isOnSale() {
		return this.onSale;
	}

	public void setOnSale(boolean onSale) {
		this.onSale = onSale;
	}
	
	public boolean isZoomAvailable() {
		return this.zoomAvailable;
	}

	public void setZoomAvailable(boolean zoomAvailable) {
		this.zoomAvailable = zoomAvailable;
	}
	
	public String toString(){

		StringBuffer toString=new StringBuffer();
		if(this.freeShipMethods!=null && !this.freeShipMethods.isEmpty()){
			toString.append(" \n Free Ship Methods \n");
			int i=0;
			for(ShipMethodVO shipVo:this.freeShipMethods){
				toString.append(++i+")").append(shipVo.getShipMethodDescription()).append("\n");
			}

		}
		else{
			toString.append(" \n No Free Ship Methods available \n");
		}
		if(this.eligibleShipMethods!=null && !this.eligibleShipMethods.isEmpty()){
			toString.append( "\n  Eligible Ship Methods \n");
			int i=0;
			for(ShipMethodVO shipVo:this.eligibleShipMethods){
				toString.append(++i+")").append(shipVo.getShipMethodDescription()).append("\n");
			}

		}
		else{
			toString.append(" \n No Eligible Ship Methods available  \n");
		}
		toString.append("Is sku eligible for rebates? "+this.hasRebate);
		toString.append("Is sku eligible for sale? "+this.onSale);
		toString.append("Is sku has zoom available? "+this.zoomAvailable);
		
		if(this.eligibleRebates!=null && !this.eligibleRebates.isEmpty()){
			toString.append(" \n Eligible Rebates \n");
			int i=0;
			for(RebateVO rebateVo:this.eligibleRebates){
		
				toString.append(++i+") Rebate Description ").append(rebateVo.getRebateDescription()).append(" Rebate Id ")
				.append(rebateVo.getRebateId()).append("\n");
			}
			
		}
		else{
			toString.append(" \n No Eligible Rebates available  \n");
		}
		 
		
		if(this.skuAttributes!=null && !this.skuAttributes.isEmpty()){
			Set<String >keySet=this.skuAttributes.keySet();
			for(String key:keySet){
				List<AttributeVO> attrList=this.skuAttributes.get(key);
				if(attrList!=null && !attrList.isEmpty()){
					toString.append("\n Sku Attributes For key ").append(key).append(" Total no "+attrList.size()+" \n");
					int i=0;
					for(AttributeVO attrVo:attrList){
						toString.append(++i+") Attribute Description::  "+attrVo.getAttributeDescrip())
						.append(" ::attribute Name  "+attrVo.getAttributeName()).append(" ::attribute PlaceHolder::  "+attrVo.getPlaceHolder()).append("\n");
					}
				}
				else{
					toString.append("\n No Sku attributes present for key ").append(key).append(" \n");
				}
			}
		}
		else{
			toString.append("No Attributes defined for product ").append(this.getSkuId());
		}

		return super.toString()+ toString.toString();
	}
	
	
	

	public boolean isRestrictedForIntShip() {
		return this.restrictedForIntShip;
	}

	public void setRestrictedForIntShip(boolean restrictedForIntShip) {
		this.restrictedForIntShip = restrictedForIntShip;
	}

	/* overridding the isVdcSku to make vdc flag site-specific
	 * (non-Javadoc)
	 * @see com.bbb.commerce.catalog.vo.SKUVO#isVdcSku()
	 */
	@Override
	public boolean isVdcSku() {
		boolean vdcSkuFlag = false;
		if (super.isVdcSku()) {
			List<String> vdcAttributesList = null;
			if (this.skuAttributes != null && !this.skuAttributes.isEmpty()) {
				vdcAttributesList = BBBConfigRepoUtils.getAllValues(
						BBBCmsConstants.CONTENT_CATALOG_KEYS,
						BBBCatalogConstants.VDC_ATTRIBUTES_LIST);
				Set<String> keySet = this.skuAttributes.keySet();
				for (String key : keySet) {
					List<AttributeVO> attrList = this.skuAttributes.get(key);
					if (attrList != null && !attrList.isEmpty()) {
						for (AttributeVO attrVo : attrList) {
							if (vdcAttributesList != null
									&& !vdcAttributesList.isEmpty()
									&& vdcAttributesList.contains(attrVo
											.getAttributeName())) {
								setVdcSku(true);
								vdcSkuFlag = true;
								break;
							}
						}
					}
				}
			}

		} else {
			setVdcSku(false);
			vdcSkuFlag = false;
		}
		return vdcSkuFlag;
	}

	/**
	 * @return the isAssemblyOffered
	 */
	public boolean isAssemblyOffered() {
		return this.isAssemblyOffered;
	}

	/**
	 * @param isAssemblyOffered the isAssemblyOffered to set
	 */
	public void setAssemblyOffered(boolean isAssemblyOffered) {
		this.isAssemblyOffered = isAssemblyOffered;
	}

	/**
	 * @return the personalizationType
	 */
	public String getPersonalizationType() {
		return this.personalizationType;
	}

	/**
	 * @param personalizationType the personalizationType to set
	 */
	public void setPersonalizationType(String personalizationType) {
		this.personalizationType = personalizationType;
	}
	
	/**
	 * @return the shipMsgFlag
	 */
	
	public boolean isShipMsgFlag() {
		return shipMsgFlag;
	}
	
	/**
	 * @param shipMsgFlag the shipMsgFlag to set
	 */
	public void setShipMsgFlag(boolean shipMsgFlag) {
		this.shipMsgFlag = shipMsgFlag;
	}
	public String getDisplayShipMsg() {
		return displayShipMsg;
	}
	public void setDisplayShipMsg(String displayShipMsg) {
		this.displayShipMsg = displayShipMsg;
	}

	public String getPricingLabelCode() {
		//return pricingLabelCode;
		
		return getDynamicSKUPriceVO().getPricingLabelCode();
	}

	public void setPricingLabelCode(String pricingLabelCode) {
		this.pricingLabelCode = pricingLabelCode;
	}

	/**
	 * @return the inCartFlag
	 */
	public boolean isInCartFlag() {
		//return inCartFlag;
		
		return getDynamicSKUPriceVO().getInCartFlag();
	}

	/**
	 * @param inCartFlag the inCartFlag to set
	 */
	public final void setInCartFlag(boolean inCartFlag) {
		this.inCartFlag = inCartFlag;
	}

	/**
	 * @return the dynamicPriceSKU
	 */
	public final boolean isDynamicPriceSKU() {
		//return dynamicPriceSKU;
		return getDynamicSKUPriceVO().isDynamicPriceSKU();
	}

	/**
	 * @param dynamicPriceSKU the dynamicPriceSKU to set
	 */
	public final void setDynamicPriceSKU(boolean dynamicPriceSKU) {
		this.dynamicPriceSKU = dynamicPriceSKU;
	}

	public void setDynamicSKUPriceVO(BBBDynamicPriceSkuVO pDynamicSKUPriceVO) {
		dynamicSKUPriceVO = pDynamicSKUPriceVO;
	}
	
	public BBBDynamicPriceSkuVO getDynamicSKUPriceVO() {
		
		//if dynamice sku price info is null and info is not fetched once
		if(dynamicSKUPriceVO==null && ! isDynPriceInfoAlreadyFetched()){
			
			dynamicSKUPriceVO = BBBUtility.populateDynamicSKUPricingVO(getSkuId(), false); 
	
			//set it that information was fetched
			this.setDynPriceInfoAlreadyFetched(true);
		}
		
		return dynamicSKUPriceVO;
	}
	
	private boolean dynPriceInfoAlreadyFetched;

	public boolean isDynPriceInfoAlreadyFetched() {
		return dynPriceInfoAlreadyFetched;
	}
	public void setDynPriceInfoAlreadyFetched(boolean dynPriceInfoAlreadyFetched) {
		this.dynPriceInfoAlreadyFetched = dynPriceInfoAlreadyFetched;
	}

	public String getDisplayURL() {
		return displayURL;
	}

	public void setDisplayURL(String displayURL) {
		this.displayURL = displayURL;
	}

}
