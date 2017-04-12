package com.bbb.commerce.giftregistry.comparator;

import java.util.Comparator;
import java.util.Date;

import com.bbb.utils.BBBUtility;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.constants.BBBCoreConstants;

/**This class contains comparator for multiple objects used in registry search
 * @author 
 *
 */
public class RegistryComparable implements Comparable<RegistrySummaryVO> {
	
	
	public Comparator<RegistrySummaryVO> firstNameAsc = new Comparator<RegistrySummaryVO>() {

		public int compare(RegistrySummaryVO reg1, RegistrySummaryVO reg2) {
		   String regFirstName1 = reg1.getPrimaryRegistrantFirstName().toUpperCase();
		   String regFirstName2 = reg2.getPrimaryRegistrantFirstName().toUpperCase();

		   //ascending order
		   return regFirstName1.compareTo(regFirstName2);
	    }

	};
	
	public Comparator<RegistrySummaryVO> lastNameAsc = new Comparator<RegistrySummaryVO>() {

		public int compare(RegistrySummaryVO reg1, RegistrySummaryVO reg2) {
		   String regLastName1 = reg1.getPrimaryRegistrantLastName().toUpperCase();
		   String regLastName2 = reg2.getPrimaryRegistrantLastName().toUpperCase();
           
		   if(!BBBUtility.isEmpty(regLastName1) && !BBBUtility.isEmpty(regLastName2) ) {
			 //ascending order
			   return regLastName1.compareTo(regLastName2);
			 } else {
				return BBBCoreConstants.ZERO;
			}
		   
		  }

	};
	
	public Comparator<RegistrySummaryVO> eventTypeAsc = new Comparator<RegistrySummaryVO>() {

		public int compare(RegistrySummaryVO reg1, RegistrySummaryVO reg2) {
		   String regEventDescription1 = reg1.getEventDescription();
		   String regEventDescription2 = reg2.getEventDescription();

		   //ascending order
		   if (!BBBUtility.isEmpty(regEventDescription1) && !BBBUtility.isEmpty(regEventDescription2) ) {
			   return regEventDescription1.compareTo(regEventDescription2);
		   } else {
			   return BBBCoreConstants.ZERO;
		}
		   
	    }

	};
	
	public Comparator<RegistrySummaryVO> eventTypeDesc = new Comparator<RegistrySummaryVO>() {

		public int compare(RegistrySummaryVO reg1, RegistrySummaryVO reg2) {
		   String regEventDescription1 = reg1.getEventDescription();
		   String regEventDescription2 = reg2.getEventDescription();

		   //Descending order
		   if (!BBBUtility.isEmpty(regEventDescription1) && !BBBUtility.isEmpty(regEventDescription2) ) {
			   return regEventDescription2.compareTo(regEventDescription1);
		   } else {
			   return BBBCoreConstants.ZERO;
		}
		   
	    }

	};
	
	public Comparator<RegistrySummaryVO> eventDateAsc = new Comparator<RegistrySummaryVO>() {

		public int compare(RegistrySummaryVO reg1, RegistrySummaryVO reg2) {
		Date regEventDate1 = reg1.getEventDateObject();
		Date regEventDate2 = reg2.getEventDateObject();
              
		 if(regEventDate1 != null && regEventDate2 != null) {
			 //ascending order
			   return regEventDate1.compareTo(regEventDate2);
		   } else {
			   return BBBCoreConstants.ZERO;
		   }
		   
	    }

	};
	
	public Comparator<RegistrySummaryVO> stateAsc = new Comparator<RegistrySummaryVO>() {

		public int compare(RegistrySummaryVO reg1, RegistrySummaryVO reg2) {
		   String regState1 = reg1.getState().toUpperCase();
		   String regState2 = reg2.getState().toUpperCase();
           
		   if(!BBBUtility.isEmpty(regState1) && !BBBUtility.isEmpty(regState2) ) {
			 //ascending order
			   return regState1.compareTo(regState2);
			   } else {
			   return BBBCoreConstants.ZERO;
			   }
		   
	    }

	};
	
	public Comparator<RegistrySummaryVO> regIdAsc = new Comparator<RegistrySummaryVO>() {

		public int compare(RegistrySummaryVO reg1, RegistrySummaryVO reg2) {
		   String regId1 = reg1.getRegistryId();
		   String regId2 = reg2.getRegistryId();
            
		   if(!BBBUtility.isEmpty(regId1) && !BBBUtility.isEmpty(regId2) ) {
			 //ascending order
			   return regId1.compareTo(regId2);
		   } else {
			return BBBCoreConstants.ZERO;
		   }
		   
	    }

	};
	
	public Comparator<RegistrySummaryVO> maidenAsc = new Comparator<RegistrySummaryVO>() {

		public int compare(RegistrySummaryVO reg1, RegistrySummaryVO reg2) {
		   String regMaiden1 = reg1.getPrimaryRegistrantMaidenName().toUpperCase();
		   String regMaiden2 = reg2.getPrimaryRegistrantMaidenName().toUpperCase();
           
		   
		   if(!BBBUtility.isEmpty(regMaiden1) && !BBBUtility.isEmpty(regMaiden2) ) {
			   //ascending order
			   return regMaiden1.compareTo(regMaiden2);
			 } else {
				return BBBCoreConstants.ZERO;
			}
		   
		  
	    }

	};
	
	public Comparator<RegistrySummaryVO> firstNameDesc = new Comparator<RegistrySummaryVO>() {

		public int compare(RegistrySummaryVO reg1, RegistrySummaryVO reg2) {
		   String regFirstName1 = reg1.getPrimaryRegistrantFirstName().toUpperCase();
		   String regFirstName2 = reg2.getPrimaryRegistrantFirstName().toUpperCase();
           
		   if(!BBBUtility.isEmpty(regFirstName1) && !BBBUtility.isEmpty(regFirstName2) ) {
			 //descending order
			   return regFirstName2.compareTo(regFirstName1);
			 } else {
				return BBBCoreConstants.ZERO;
			}
		   
		   

	    }

	};
	
	public Comparator<RegistrySummaryVO> lastNameDesc = new Comparator<RegistrySummaryVO>() {

		public int compare(RegistrySummaryVO reg1, RegistrySummaryVO reg2) {
		   String regLastName1 = reg1.getPrimaryRegistrantLastName().toUpperCase();
		   String regLastName2 = reg2.getPrimaryRegistrantLastName().toUpperCase();
           
		   if(!BBBUtility.isEmpty(regLastName1) && !BBBUtility.isEmpty(regLastName2) ) {
			   //descending order
			   return regLastName2.compareTo(regLastName1);
				 } else {
					return BBBCoreConstants.ZERO;
				}
			   
			}

	};
	
	public Comparator<RegistrySummaryVO> eventDateDesc = new Comparator<RegistrySummaryVO>() {

		public int compare(RegistrySummaryVO reg1, RegistrySummaryVO reg2) {
		   Date regEventDate1 = reg1.getEventDateObject();
		   Date regEventDate2 = reg2.getEventDateObject();
           
		   if(regEventDate1 != null && regEventDate2 != null) {
			   //descending order
			        return regEventDate2.compareTo(regEventDate1);
				 } else {
					return BBBCoreConstants.ZERO;
				}
			
	    }

	};
	
	public Comparator<RegistrySummaryVO> stateDesc = new Comparator<RegistrySummaryVO>() {

		public int compare(RegistrySummaryVO reg1, RegistrySummaryVO reg2) {
		   String regState1 = reg1.getState().toUpperCase();
		   String regState2 = reg2.getState().toUpperCase();
           
		   if(!BBBUtility.isEmpty(regState1) && !BBBUtility.isEmpty(regState2) ) {
                 //descending order
			   return regState2.compareTo(regState1);
				 } else {
					return BBBCoreConstants.ZERO;
				}
		   
	    }

	};
	
	public Comparator<RegistrySummaryVO> regIdDesc = new Comparator<RegistrySummaryVO>() {

		public int compare(RegistrySummaryVO reg1, RegistrySummaryVO reg2) {
		   String regId1 = reg1.getRegistryId();
		   String regId2 = reg2.getRegistryId();
           
		   
		   if(!BBBUtility.isEmpty(regId1) && !BBBUtility.isEmpty(regId2) ) {
			 //descending order
			   return regId2.compareTo(regId1);
				 } else {
					return BBBCoreConstants.ZERO;
				}
		   
		   
	    }

	};
	
	public Comparator<RegistrySummaryVO> maidenDesc = new Comparator<RegistrySummaryVO>() {

		public int compare(RegistrySummaryVO reg1, RegistrySummaryVO reg2) {
		   String regMaiden1 = reg1.getPrimaryRegistrantMaidenName().toUpperCase();
		   String regMaiden2 = reg2.getPrimaryRegistrantMaidenName().toUpperCase();
           
		   if(!BBBUtility.isEmpty(regMaiden1) && !BBBUtility.isEmpty(regMaiden2) ) {
			 //descending order
			   return regMaiden2.compareTo(regMaiden1);
			} else {
				return BBBCoreConstants.ZERO;
			}
		   
		   
	    }

	};

	@Override
	public int compareTo(RegistrySummaryVO o) {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
