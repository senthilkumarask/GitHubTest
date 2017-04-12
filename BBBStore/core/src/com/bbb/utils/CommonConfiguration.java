/**
 * 
 */
package com.bbb.utils;


/**
 * @author alakra
 *
 */
public class CommonConfiguration{

	private boolean mDisplayOrderXML = false;
	
	private boolean mPersistOrderXML = false;
	
	private boolean mLoggingDebugForRequestScopedComponents = false;
	
	private boolean shallowProfileChanges;
	
	private boolean logDebugEnableOnCartFormHandler= false;
	
	private boolean logDebugEnableOnCartFormHandlerForOrderDetail= false;
	
	private boolean logDebugEnableOnRepriceOrder= false;
	
	private boolean logDebugEnableOnRepriceOrderForOrderDetail= false;
	
	private String envTypeName;
	
		
	public boolean isShallowProfileChanges() {
		return shallowProfileChanges;
	}

	public void setShallowProfileChanges(boolean shallowProfileChanges) {
		this.shallowProfileChanges = shallowProfileChanges;
	}
		
	
	/**
	 * @return the persistOrderXML
	 */
	public final boolean isPersistOrderXML() {
		return mPersistOrderXML;
	}

	/**
	 * @param pPersistOrderXML the persistOrderXML to set
	 */
	public final void setPersistOrderXML(boolean pPersistOrderXML) {
		mPersistOrderXML = pPersistOrderXML;
	}

	/**
	 * @return the loggingDebugForRequestScopedComponents
	 */
	public boolean isLoggingDebugForRequestScopedComponents() {
		return mLoggingDebugForRequestScopedComponents;
	}

	/**
	 * @param pLoggingDebugForRequestScopedComponents the loggingDebugForRequestScopedComponents to set
	 */
	public final void setLoggingDebugForRequestScopedComponents(boolean pLoggingDebugForRequestScopedComponents) {
		mLoggingDebugForRequestScopedComponents = pLoggingDebugForRequestScopedComponents;
	}

	/**
	 * @return the displayOrderXML
	 */
	public final boolean isDisplayOrderXML() {
		return mDisplayOrderXML;
	}

	/**
	 * @param pDisplayOrderXML the displayOrderXML to set
	 */
	public final void setDisplayOrderXML(boolean pDisplayOrderXML) {
		mDisplayOrderXML = pDisplayOrderXML;
	}

	public boolean isLogDebugEnableOnCartFormHandler() {
		return logDebugEnableOnCartFormHandler;
	}

	public void setLogDebugEnableOnCartFormHandler(
			boolean logDebugEnableOnCartFormHandler) {
		this.logDebugEnableOnCartFormHandler = logDebugEnableOnCartFormHandler;
	}

	public boolean isLogDebugEnableOnCartFormHandlerForOrderDetail() {
		return logDebugEnableOnCartFormHandlerForOrderDetail;
	}

	public void setLogDebugEnableOnCartFormHandlerForOrderDetail(
			boolean logDebugEnableOnCartFormHandlerForOrderDetail) {
		this.logDebugEnableOnCartFormHandlerForOrderDetail = logDebugEnableOnCartFormHandlerForOrderDetail;
	}

	public boolean isLogDebugEnableOnRepriceOrder() {
		return logDebugEnableOnRepriceOrder;
	}

	public void setLogDebugEnableOnRepriceOrder(boolean logDebugEnableOnRepriceOrder) {
		this.logDebugEnableOnRepriceOrder = logDebugEnableOnRepriceOrder;
	}

	public boolean isLogDebugEnableOnRepriceOrderForOrderDetail() {
		return logDebugEnableOnRepriceOrderForOrderDetail;
	}

	public void setLogDebugEnableOnRepriceOrderForOrderDetail(
			boolean logDebugEnableOnRepriceOrderForOrderDetail) {
		this.logDebugEnableOnRepriceOrderForOrderDetail = logDebugEnableOnRepriceOrderForOrderDetail;
	}

	/**
	 * @return the envTypeName
	 */
	public String getEnvTypeName() {
		return envTypeName;
	}

	/**
	 * @param envTypeName the envTypeName to set
	 */
	public void setEnvTypeName(String envTypeName) {
		this.envTypeName = envTypeName;
	}
}