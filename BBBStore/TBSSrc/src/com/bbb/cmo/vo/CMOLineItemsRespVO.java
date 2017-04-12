package com.bbb.cmo.vo;

import java.util.ArrayList;
import java.util.List;

import com.bbb.framework.httpquery.vo.HTTPServiceResponseIF;

/**
 * Created by acer on 8/26/2014.
 */
public class CMOLineItemsRespVO implements HTTPServiceResponseIF {


    private String mError;
    
    private List<LineItemVO> mLineItems;


    public String getError() {
        return mError;
    }

    @Override
    public void setError(String pError) {
        mError = pError;
    }

	public List<LineItemVO> getLineItems() {
		if(mLineItems == null){
			mLineItems = new ArrayList<LineItemVO>();
		}
		return mLineItems;
	}

	public void setLineItems(List<LineItemVO> pLineItems) {
		mLineItems = pLineItems;
	}
	
	public String toString()
	{
		return "CMOLineItemsRespVO [mLineItems=" + mLineItems + ", mError="
				+ mError + "]";
	}
}
