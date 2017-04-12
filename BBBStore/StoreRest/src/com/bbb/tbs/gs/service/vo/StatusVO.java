package com.bbb.tbs.gs.service.vo;

import java.io.Serializable;

/**
 * Created by acer on 9/12/2014.
 */
public class StatusVO implements Serializable{

    private boolean mErrorExists;

    private String mOrderNum;

    private Integer mErrorId;

    private String mErrorMessage;

    private String mDisplayMessage;

    public boolean isErrorExists() {
        return mErrorExists;
    }

    public void setErrorExists(boolean pErrorExists) {
        mErrorExists = pErrorExists;
    }

    public String getOrderNum() {
        return mOrderNum;
    }

    public void setOrderNum(String pOrderNum) {
        mOrderNum = pOrderNum;
    }

    public Integer getErrorId() {
        return mErrorId;
    }

    public void setErrorId(Integer pErrorId) {
        mErrorId = pErrorId;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String pErrorMessage) {
        mErrorMessage = pErrorMessage;
    }

    public String getDisplayMessage() {
        return mDisplayMessage;
    }

    public void setDisplayMessage(String pDisplayMessage) {
        mDisplayMessage = pDisplayMessage;
    }
}
