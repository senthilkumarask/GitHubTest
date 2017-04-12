
package com.bbb.commerce.order.droplet;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import atg.multisite.SiteContextManager;


import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.rest.checkout.vo.AppliedCouponListVO;

/**
 * @author 
 *
 */
public class UserCouponsComparator implements Comparator<AppliedCouponListVO>{

	/**
	 * This method is used to compare delivery charge in ShipMethodVO
	 */
	@Override
	public int compare(final AppliedCouponListVO obj1,
			final AppliedCouponListVO obj2) {

		DateFormat formatter ;
		Date date1=null;
		Date date2=null;
		String expDate1 = obj1.getExpiryDate();
		String expDate2 = obj2.getExpiryDate();
		String siteId = getSiteId();
		if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)){
			formatter = new SimpleDateFormat("dd/MM/yyyy");
		}else{
			formatter = new SimpleDateFormat("MM/dd/yyyy");
		}
		if(expDate1!=null && expDate2!=null){
			  try {
				 date1 = formatter.parse(expDate1);
				 date2 = formatter.parse(expDate2);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(date1 != null && date2 != null){
				if ((date1.compareTo(date2) == -1)) {
					return -1;
				} else if((date1.compareTo(date2) == 1)){
					return 1;
				}
			  }
		}
		 else{
			return 0;
		}
		return 0;

	}
/**
 * use to get the site id
 * @return - siteId
 */
	protected String getSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

}