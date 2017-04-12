
package com.bbb.account;

import java.util.Map;
import java.util.Set;

import com.bbb.commerce.common.BBBAddress;
import com.bbb.utils.BBBUtility;

import atg.core.util.Address;
import atg.userprofiling.address.AddressTools;

/**
 * Contains useful functions for Address manipulation
 * 
 * @author 
 */
public class BBBAddressTools extends AddressTools{

  //-------------------------------------
  /** Class version string */

  /**
   * Compares the properties of two addresses equality. If all properties are
   * equal then the addresses are equal.
   * 
   * @param pAddressA An Address
   * @param pAddressB An Address
   * @return A boolean indicating whether or not pAddressA and pAddressB 
   * represent the same address.
   */
  public static boolean compare(final Address pAddressA, final Address pAddressB){
    
    if((pAddressA == null) && (pAddressB != null)){
      return false;
    }
    if((pAddressA != null) && (pAddressB == null)){
      return false;
    }
    if((pAddressA == null)){
      return true;
    }

    return compareAddressValues(pAddressA, pAddressB);
  }

  
  /**
   * Compares the properties of two addresses equality. If all properties are
   * equal then the addresses are equal for LTL items shipping Address.
   * 
   * @param pAddressA An Address
   * @param pAddressB An Address
   * @return A boolean indicating whether or not pAddressA and pAddressB 
   * represent the same address.
   */
  public static boolean compareLTLAddressProperties(final BBBAddress pAddressA, final BBBAddress pAddressB){
    
    if((pAddressA == null) && (pAddressB != null)){
      return false;
    }
    if((pAddressA != null) && (pAddressB == null)){
      return false;
    }
    if((pAddressA == null)){
      return true;
    }

    return compareLTLAddressValues(pAddressA, pAddressB);
  }

private static boolean compareAddressValues(final Address pAddressA,
        final Address pAddressB) {
    final String aFirstName = pAddressA.getFirstName();
    final String bFirstName = pAddressB.getFirstName();

    final String aLastName = pAddressA.getLastName();
    final String bLastName = pAddressB.getLastName();

    final String aAddress1 = pAddressA.getAddress1();
    final String bAddress1 = pAddressB.getAddress1();

    final String aAddress2 = pAddressA.getAddress2();
    final String bAddress2 = pAddressB.getAddress2();

    final String aCity = pAddressA.getCity();
    final String bCity = pAddressB.getCity();

    final String aState = pAddressA.getState();
    final String bState = pAddressB.getState();

    final String aPostalCode = pAddressA.getPostalCode();
    final String bPostalCode = pAddressB.getPostalCode();

    final String aCountry = pAddressA.getCountry();
    final String bCountry = pAddressB.getCountry();
    
    return (
       areStringsEqual(aFirstName != null ? aFirstName.trim() : aFirstName, bFirstName != null ? bFirstName.trim() : bFirstName)
       &&
       areStringsEqual(aLastName != null ? aLastName.trim() : aLastName, bLastName != null ? bLastName.trim() : bLastName)
       &&
       areStringsEqual(aAddress1 != null ? aAddress1.trim() : aAddress1, bAddress1 != null ? bAddress1.trim() : bAddress1)
       &&
       areStringsEqual(aAddress2 != null ? aAddress2.trim() : aAddress2, bAddress2 != null ? bAddress2.trim() : bAddress2)
       &&
       areStringsEqual(aCity != null ? aCity.trim() : aCity, bCity != null ? bCity.trim() : bCity )
       &&
       areStringsEqual(aState != null ? aState.trim() : aState, bState != null ? bState.trim() : bState)
       &&
       areStringsEqual(aPostalCode != null ? aPostalCode.trim() : aPostalCode, bPostalCode != null ? bPostalCode.trim() : bPostalCode)
       &&
       areStringsEqual(aCountry != null ? aCountry.trim() : aCountry, bCountry != null ? bCountry.trim() : bCountry)
       );
}

  
  private static boolean compareLTLAddressValues(final BBBAddress pAddressA, final BBBAddress pAddressB) {

	    final String aPhonenumber = pAddressA.getPhoneNumber();
	    final String bPhonenumber = pAddressB.getPhoneNumber();
	    
	    final String aEmail = pAddressA.getEmail();
	    final String bEmail = pAddressB.getEmail();
	    
	    final String aAltPhoneNumber = pAddressA.getAlternatePhoneNumber();
	    final String bAltPhoneNumber = pAddressB.getAlternatePhoneNumber();
	    return (
	       areStringsEqual(aPhonenumber != null ? aPhonenumber.trim() : aPhonenumber, bPhonenumber != null ? bPhonenumber.trim() : bPhonenumber)
	       &&
	       areStringsEqual(aEmail != null ? aEmail.trim() : aEmail, bEmail != null ? bEmail.trim() : bEmail)
	       &&
	       areStringsEqual(aAltPhoneNumber != null ? aAltPhoneNumber.trim() : aAltPhoneNumber, bAltPhoneNumber != null ? bAltPhoneNumber.trim() : bAltPhoneNumber)
	       );
	}
  
public static BBBAddress copyBBBAddress(BBBAddress srcAddress, BBBAddress destAddress) {

	//BBBSL-1976. Adding trimmed address values to merge shipping group if difference is due to trailing spaces.
	if (!BBBUtility.isEmpty(srcAddress.getFirstName())) {
		destAddress.setFirstName(srcAddress.getFirstName().trim());
	}

	if (!BBBUtility.isEmpty(srcAddress.getLastName())) {
		destAddress.setLastName(srcAddress.getLastName().trim());
	}
	
	if (!BBBUtility.isEmpty(srcAddress.getMiddleName())) {
		destAddress.setMiddleName(srcAddress.getMiddleName().trim());
	}

	if (!BBBUtility.isEmpty(srcAddress.getAddress1())) {
		destAddress.setAddress1(srcAddress.getAddress1().trim());
	}

	if (!BBBUtility.isEmpty(srcAddress.getAddress2())) {
		destAddress.setAddress2(srcAddress.getAddress2().trim());
	}

	if (!BBBUtility.isEmpty(srcAddress.getCity())) {
		destAddress.setCity(srcAddress.getCity().trim());
	}

	if (!BBBUtility.isEmpty(srcAddress.getState())) {
		destAddress.setState(srcAddress.getState().trim());
	}

	if (!BBBUtility.isEmpty(srcAddress.getPostalCode())) {
		destAddress.setPostalCode(srcAddress.getPostalCode().trim());
	}
	
	if (!BBBUtility.isEmpty(((BBBAddress)srcAddress).getCompanyName())) {
		destAddress.setCompanyName(((BBBAddress)srcAddress).getCompanyName().trim());
	}
	
	if (!BBBUtility.isEmpty(srcAddress.getCountry())) {
		destAddress.setCountry(srcAddress.getCountry().trim());
	}
	
	if (!BBBUtility.isEmpty(srcAddress.getEmail())) {
		destAddress.setEmail(srcAddress.getEmail().trim());
	}
	
	if (!BBBUtility.isEmpty(srcAddress.getAddress3())) {
		destAddress.setAddress3(srcAddress.getAddress3().trim());
	}
	
	if (!BBBUtility.isEmpty(srcAddress.getAlternatePhoneNumber())) {
		destAddress.setAlternatePhoneNumber(srcAddress.getAlternatePhoneNumber().trim());
	}
	
	if (!BBBUtility.isEmpty(srcAddress.getPhoneNumber())) {
		destAddress.setPhoneNumber(srcAddress.getPhoneNumber().trim());
	}
	
	destAddress.setPoBoxAddress(srcAddress.isPoBoxAddress());
	destAddress.setQasValidated(srcAddress.isQasValidated());
	
	return destAddress;
}


private static boolean areStringsEqual(final String firstString, final String secondString) {
    return ((BBBUtility.isEmpty(firstString) && BBBUtility.isEmpty(secondString)) ||(!BBBUtility.isEmpty(firstString) && firstString.equalsIgnoreCase(secondString)));
}

public static boolean duplicateFound(Map<String, BBBAddress> addressMap, BBBAddress bbbAddress){
	boolean duplicateFound = false;
	Set<String> addressKeySet = addressMap.keySet();
	BBBAddress tempAddress = null;
	for (String key : addressKeySet) {
		tempAddress = addressMap.get(key);
		if(bbbAddress instanceof Address && tempAddress instanceof Address && (tempAddress.getRegistryId()==null || tempAddress.getRegistryId().isEmpty())){
			duplicateFound = compare((Address)tempAddress, (Address)bbbAddress);
			if(duplicateFound){
				return duplicateFound;
			}
		}
	}
	
	return duplicateFound;
}

}
