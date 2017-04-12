package com.bbb.search.endeca.assembler.cartridge.config;

import java.util.List;

import com.endeca.infront.cartridge.RefinementMenu;
import com.endeca.infront.cartridge.RefinementMenuConfig;

/**
 * This class is used to support custom dimension id functionality
 * 
 * @author sc0054
 *
 */
public class CustomRefinementMenu extends RefinementMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<String> customBoostedDvals;
	
	private List<String> customBuriedDvals;
	
	private boolean isCustomDimension;

	public CustomRefinementMenu(RefinementMenuConfig pConfig) {
		super(pConfig);
	}
	
	/**
	 * @return the isCustomDimension
	 */
	public boolean isCustomDimension() {
		return isCustomDimension;
	}

	/**
	 * @param isCustomDimension the isCustomDimension to set
	 */
	public void setCustomDimension(boolean isCustomDimension) {
		this.isCustomDimension = isCustomDimension;
	}

	/**
	 * @return the customBoostedDvals
	 */
	public List<String> getCustomBoostedDvals() {
		return customBoostedDvals;
	}

	/**
	 * @param customBoostedDvals the customBoostedDvals to set
	 */
	public void setCustomBoostedDvals(List<String> customBoostedDvals) {
		this.customBoostedDvals = customBoostedDvals;
	}

	/**
	 * @return the customBuriedDvals
	 */
	public List<String> getCustomBuriedDvals() {
		return customBuriedDvals;
	}

	/**
	 * @param customBuriedDvals the customBuriedDvals to set
	 */
	public void setCustomBuriedDvals(List<String> customBuriedDvals) {
		this.customBuriedDvals = customBuriedDvals;
	}
	

}
