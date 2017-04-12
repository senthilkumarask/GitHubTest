package com.bbb.wishlist;

import java.io.Serializable;
import java.util.List;

public class GiftListVOWrapper implements Serializable {
	
	/** The giftlistvo list . */
	private List<GiftListVO> giftListVOlist;
	private boolean hasMoreItems ;
	private int size;
	private int fromSflIndex; 
	
	private static final long serialVersionUID = 1L;
	
	public boolean isHasMoreItems() {
		return hasMoreItems;
	}
	
	public void setHasMoreItems(boolean hasMoreItems) {
		this.hasMoreItems = hasMoreItems;
	}
	
	public List<GiftListVO> getGiftListVOlist() {
		return giftListVOlist;
	}

	public void setGiftListVOlist(List<GiftListVO> giftListVOlist) {
		this.giftListVOlist = giftListVOlist;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getFromSflIndex() {
		return fromSflIndex;
	}
	

	public void setFromSflIndex(int fromSflIndex) {
		this.fromSflIndex = fromSflIndex;
	}
	
}
	
	
	

	
	
	