package com.bbb.kickstarters;

import java.io.Serializable;
import java.util.List;

import com.bbb.cms.PromoBoxVO;

public class KickStarterItemsVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<KickStarterVO> kickStarterItems;
	private List<PromoBoxVO> promoBoxVOList;

	public List<KickStarterVO> getKickStarterItems() {
		return kickStarterItems;
	}

	public void setKickStarterItems(List<KickStarterVO> kickStarterItems) {
		this.kickStarterItems = kickStarterItems;
	}

	public List<PromoBoxVO> getPromoBoxVOList() {
		return promoBoxVOList;
	}

	public void setPromoBoxVOList(List<PromoBoxVO> promoBoxVOList) {
		this.promoBoxVOList = promoBoxVOList;
	}
	
	
}