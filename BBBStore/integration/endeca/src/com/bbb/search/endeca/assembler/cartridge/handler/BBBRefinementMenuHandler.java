package com.bbb.search.endeca.assembler.cartridge.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bbb.search.endeca.assembler.cartridge.config.CustomRefinementMenu;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.cartridge.RefinementMenu;
import com.endeca.infront.cartridge.RefinementMenuConfig;
import com.endeca.infront.cartridge.RefinementMenuConfig.RefinementsShown;
import com.endeca.infront.cartridge.RefinementMenuHandler;
import com.endeca.infront.navigation.model.DvalSpec;
import com.endeca.infront.navigation.request.RefinementMdexQuery;

/**
 * This handler converts refinements into CustomRefinementMenu and also processes CustomDimensionId 
 * 
 * @author sc0054
 *
 */
public class BBBRefinementMenuHandler extends RefinementMenuHandler {
	
	private String mCustomDimensionId;
	
	private String defaultRefinementsShown;
	private int defaultMaxNumRefinements;
	
	private static final String ALL= "ALL";
	private static final String SOME= "SOME";
	
	
//	@Override
//	public void preprocess(RefinementMenuConfig config) throws CartridgeHandlerException {
		//below fix does not work - probably issue in out of box code
		//DEFAULT sort is changed to DISABLED in out of the box code instead of DEFAULT
		/*if (config.getSort() != null) {
			String configuredSort = config.getSort().toUpperCase();
			if ("DEFAULT".equalsIgnoreCase(configuredSort)) {
				config.setSort(RefinementMdexQuery.RefinementSortType.DEFAULT.toString());
			}
		} else {
			config.setSort(RefinementMdexQuery.RefinementSortType.DEFAULT.toString());
		}*/
		
//		super.preprocess(config);
//	}
	
	@Override
	public RefinementMenu process(RefinementMenuConfig config) throws CartridgeHandlerException {
		//out of the box code restricting to only 10 max refinement values 
		//if refinements are not controlled using refinementmenu in expmgr
		if(config.getTypedProperty(BBBEndecaConstants.REFINEMENTS_SHOWN) == null) {
			if(getDefaultRefinementsShown().equalsIgnoreCase(ALL)) {
				config.setRefinementsShown(RefinementsShown.ALL.toString());				
			} else if (getDefaultRefinementsShown().equalsIgnoreCase(SOME)) {
				config.setRefinementsShown(RefinementsShown.SOME.toString());
			} else {
				config.setRefinementsShown(RefinementsShown.NONE.toString());
			}
			
		}
		if(config.getTypedProperty(BBBEndecaConstants.MAX_NUM_REFINEMENTS) == null) {
			config.setMaxNumRefinements(getDefaultMaxNumRefinements());
		}
		
		RefinementMenu refinementMenu = super.process(config);
		
		if(null == refinementMenu) {
			return refinementMenu;
		}
		
		CustomRefinementMenu bbbRefinementMenu = new CustomRefinementMenu(config);
		bbbRefinementMenu.setAncestors(refinementMenu.getAncestors());
		bbbRefinementMenu.setCollapseAction(refinementMenu.getCollapseAction());
		bbbRefinementMenu.setDimensionName(refinementMenu.getDimensionName());
		bbbRefinementMenu.setDisplayName(refinementMenu.getDisplayName());
		bbbRefinementMenu.setExpandAction(refinementMenu.getExpandAction());
		bbbRefinementMenu.setLessLink(refinementMenu.getLessLink());
		bbbRefinementMenu.setMoreLink(refinementMenu.getMoreLink());
		bbbRefinementMenu.setMultiSelect(refinementMenu.isMultiSelect());
		bbbRefinementMenu.setRefinements(refinementMenu.getRefinements());
		bbbRefinementMenu.setWhyPrecedenceRuleFired(refinementMenu.getWhyPrecedenceRuleFired());
		
		List<String> boostedDvalIDs = new ArrayList<>();
		List<String> buriedDvalIDs = new ArrayList<>();
		boolean isCustomDimension = false;
		if(null != config.get(getCustomDimensionId())) {
			Boolean customDimensionIdBoolean = (Boolean)config.get(getCustomDimensionId());	
			isCustomDimension = customDimensionIdBoolean.booleanValue(); 
		}
		
		boolean hasCusotmBoostDims = false;
		boolean hasCustomBuryDims = false;
		
		if (isCustomDimension) {
			if(null != config.getBoostRefinements()) {
				hasCusotmBoostDims = config.getBoostRefinements().size() > 0 ? true:false;				
			}
			if(null != config.getBuryRefinements()) {
				hasCustomBuryDims = config.getBuryRefinements().size() > 0 ? true:false;
			}
		}
		
		if(hasCusotmBoostDims) {
			List<DvalSpec> boostedDvalList = config.getBoostRefinements();
			Iterator<DvalSpec> boostedDvalIter = boostedDvalList.iterator();
			while(boostedDvalIter.hasNext()) {
				DvalSpec dvalSpec = boostedDvalIter.next();
				boostedDvalIDs.add(dvalSpec.getId());
			}
		} else if(hasCustomBuryDims) {
			List<DvalSpec> buriedDvalList = config.getBuryRefinements();
			Iterator<DvalSpec> buriedDvalIter = buriedDvalList.iterator();
			while(buriedDvalIter.hasNext()) {
				DvalSpec dvalSpec = buriedDvalIter.next();
				buriedDvalIDs.add(dvalSpec.getId());
			}
		} 
		
		bbbRefinementMenu.setCustomBoostedDvals(boostedDvalIDs);
		bbbRefinementMenu.setCustomBuriedDvals(buriedDvalIDs);
		
		return bbbRefinementMenu;
	}
	

	/**
	 * @return the mCustomDimensionId
	 */
	public String getCustomDimensionId() {
		return mCustomDimensionId;
	}

	/**
	 * @param mCustomDimensionId the mCustomDimensionId to set
	 */
	public void setCustomDimensionId(String mCustomDimensionId) {
		this.mCustomDimensionId = mCustomDimensionId;
	}

	/**
	 * @return the defaultMaxNumRefinements
	 */
	public int getDefaultMaxNumRefinements() {
		return defaultMaxNumRefinements;
	}

	/**
	 * @param defaultMaxNumRefinements the defaultMaxNumRefinements to set
	 */
	public void setDefaultMaxNumRefinements(int defaultMaxNumRefinements) {
		this.defaultMaxNumRefinements = defaultMaxNumRefinements;
	}

	/**
	 * @return the defaultRefinementsShown
	 */
	public String getDefaultRefinementsShown() {
		return defaultRefinementsShown;
	}

	/**
	 * @param defaultRefinementsShown the defaultRefinementsShown to set
	 */
	public void setDefaultRefinementsShown(String defaultRefinementsShown) {
		this.defaultRefinementsShown = defaultRefinementsShown;
	}
	
}

