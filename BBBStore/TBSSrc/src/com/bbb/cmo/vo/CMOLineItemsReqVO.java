package com.bbb.cmo.vo;

import com.bbb.framework.httpquery.vo.HTTPServiceRequestIF;

import java.util.Map;

/**
 * Created by acer on 8/26/2014.
 */
public class CMOLineItemsReqVO implements HTTPServiceRequestIF {

    private String mServiceName;
    private Map<String, String> mParamsValuesMap;
    private String mSiteId;
    private String mServiceType;

    public String getServiceName() {
        return mServiceName;
    }

    public void setServiceName(String pServiceName) {
        mServiceName = pServiceName;
    }

    public Map<String, String> getParamsValuesMap() {
        return mParamsValuesMap;
    }

    public void setParamsValuesMap(Map<String, String> pParamsValuesMap) {
        mParamsValuesMap = pParamsValuesMap;
    }

    public String getSiteId() {
        return mSiteId;
    }

    public void setSiteId(String pSiteId) {
        mSiteId = pSiteId;
    }

    public String getServiceType() {
        return mServiceType;
    }

    public void setServiceType(String pServiceType) {
        mServiceType = pServiceType;
    }
}
