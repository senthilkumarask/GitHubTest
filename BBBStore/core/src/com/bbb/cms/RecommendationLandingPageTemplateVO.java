/**
 * 
 */
package com.bbb.cms;

import java.io.Serializable;
import java.util.List;

/**
 * @author Amit
 *
 */
public class RecommendationLandingPageTemplateVO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mSiteId;
	private PromoBoxVO mPromoBox;
	private List<PromoBoxVO> mPromoBoxList;
	private String mChannel;
	private String mRegistryType;
	private PromoBoxVO mPromoBoxBottom;
	/**
	 * @return the siteId
	 */
	public String getSiteId() {
		return mSiteId;
	}
	/**
	 * @param pSiteId the siteId to set
	 */
	public void setSiteId(String pSiteId) {
		mSiteId = pSiteId;
	}	

	public String toString(){
		StringBuffer toString=new StringBuffer(" Registrant Page VO for Site ");
		toString.append(mSiteId).append("\n");
		
		if(mPromoBox!=null){
			toString.append(mPromoBox.toString());
		}
		else{
			toString.append("Upper Promo Box is null for the Registrant Page \n");
		}

		if(mChannel!=null){
			toString.append(mChannel.toString());
		}
		else{
			toString.append("Channel is null for the Registrant Page \n");
		}
		
		if(mRegistryType!=null){
			toString.append(mRegistryType.toString());
		}
		else{
			toString.append("Registry Type is null for the Registrant Page \n");
		}
		if(mPromoBoxList!=null && !mPromoBoxList.isEmpty()){
			toString.append(" \n Middle Promo Box List Details  for the Registrant Page \n");
			for(PromoBoxVO PromoBoxVO:mPromoBoxList){
				toString.append(PromoBoxVO.toString());
			}
		}
		else{
			toString.append(" Promo Box List is null for the Registrant Page");
		}	
		if(mPromoBoxBottom!=null){
			toString.append(mPromoBoxBottom.toString());
		}
		else{
			toString.append("Bottom Promo Box is null for the Registrant Page \n");
		}
		return toString.toString();
	}
	
	public PromoBoxVO getPromoBox() {
		return mPromoBox;
	}
	public void setPromoBox(PromoBoxVO pPromoBox) {
		mPromoBox = pPromoBox;
	}
	public List<PromoBoxVO> getPromoBoxList() {
		return mPromoBoxList;
	}
	public void setPromoBoxList(List<PromoBoxVO> pPromoBoxList) {
		mPromoBoxList = pPromoBoxList;
	}
	public String getChannel() {
		return mChannel;
	}
	public void setChannel(String pChannel) {
		mChannel = pChannel;
	}
	public String getRegistryType() {
		return mRegistryType;
	}
	public void setRegistryType(String pRegistryType) {
		mRegistryType = pRegistryType;
	}
	public PromoBoxVO getPromoBoxBottom() {
		return mPromoBoxBottom;
	}
	public void setPromoBoxBottom(PromoBoxVO pPromoBoxBottom) {
		mPromoBoxBottom = pPromoBoxBottom;
	}

}
