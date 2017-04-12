package com.bbb.kirsch.vo;

import java.util.ArrayList;
import java.util.List;

import com.bbb.framework.httpquery.vo.HTTPServiceResponseIF;

public class ItemsFromWorkBookRespVO implements HTTPServiceResponseIF{
	
	private List<LineItem> mLineItems;
	
	private String mError;

	public List<LineItem> getLineItems() {
		if(mLineItems == null){
			mLineItems = new ArrayList<LineItem>();
		}
		return mLineItems;
	}

	public void setLineItems(List<LineItem> pLineItems) {
		mLineItems = pLineItems;
	}

	@Override
	public void setError(String pError) {
		this.mError = pError;
		
	}

	public String getError() {
		return mError;
	}

}
