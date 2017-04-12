package com.bbb.commerce.catalog;

/**
 * This class is responsible for setting the value of mOverrideEnabledFromComponent property
 * to true or false as configured in .properties file
 * mOverrideEnabledFromComponent will be used to set mOverrideEnabledFromComponent property 
 * of BBBCatalogTools. 
 * This is done to ensure that no one touches this component unless absolutely required
 * and asked for.
 */
public class ConfigureKeyFromComponentFlag {
	
	private boolean mOverrideEnabledFromComponent;

	public boolean isOverrideEnabledFromComponent() {
		return mOverrideEnabledFromComponent;
	}

	public void setOverrideEnabledFromComponent(
			boolean pOverrideEnabledFromComponent) {
		mOverrideEnabledFromComponent = pOverrideEnabledFromComponent;
	}

}
