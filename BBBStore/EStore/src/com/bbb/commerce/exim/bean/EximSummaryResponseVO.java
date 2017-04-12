package com.bbb.commerce.exim.bean;

import java.io.Serializable;
import java.util.List;

// 
/**
 * @author sanam jain
 * The Class EximSummaryResponseVO.
 */
public class EximSummaryResponseVO implements Serializable {
	
	/** The customizations. */
	private List<EximCustomizedAttributesVO> customizations;

	/**
	 * Gets the customizations.
	 *
	 * @return the customizations
	 */
	public List<EximCustomizedAttributesVO> getCustomizations() {
		return customizations;
	}

	/**
	 * Sets the customizations.
	 *
	 * @param customizations the new customizations
	 */
	public void setCustomizations(List<EximCustomizedAttributesVO> customizations) {
		this.customizations = customizations;
	}
	
	public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("EximSummaryResponseVO [");
        for(EximCustomizedAttributesVO eximCustomizedAttributesVO: getCustomizations()){
        	builder.append(eximCustomizedAttributesVO.toString());
        }
        builder.append("]");
        return builder.toString();
    }
}