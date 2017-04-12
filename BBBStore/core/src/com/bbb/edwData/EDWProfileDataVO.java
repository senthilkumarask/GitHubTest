/**
 * 
 */
package com.bbb.edwData;

import java.util.List;

import com.bbb.account.profile.vo.ProfileEDWInfoVO;
import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.framework.integration.ServiceRequestBase;

/**
 * @author vchan5
 *
 */
public class EDWProfileDataVO extends ServiceRequestBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2628433347669126856L;
	
	private String mServiceName = "inventoryJMSMessage";
	
    
	
	@Override
	public String getServiceName() {
		return mServiceName;
	}
	
	
	
	public void setServiceName(String pServiceName) {
		this.mServiceName = pServiceName;
	}
	
	public ProfileEDWInfoVO profileEDWData;



	public ProfileEDWInfoVO getProfileEDWData() {
		return profileEDWData;
	}



	public void setProfileEDWData(ProfileEDWInfoVO profileEDWData) {
		this.profileEDWData = profileEDWData;
	}
}
