package com.bbb.internationalshipping.utils;

import java.beans.IntrospectionException;
import java.util.List;
import java.util.Map;

import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupManager;
import atg.core.util.ContactInfo;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;

import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.order.BBBCreditCard;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.framework.jaxb.internationalshipping.pofile.Address;
import com.bbb.framework.jaxb.internationalshipping.pofile.DomesticProfile;
import com.bbb.framework.jaxb.internationalshipping.pofile.OrderFeed;
import com.bbb.order.bean.BBBCommerceItem;
import java.util.regex.Pattern;
import com.bbb.utils.BBBConfigRepoUtils;


/**
 * The Class BBBInternationalOrderUpdateServiceHelper 
 * populate addresses and credit card info for international shipping order.
 */
public class BBBInternationalOrderUpdateServiceHelper extends BBBGenericService {

	
	private String nameStringBodyRegexPattern;
	
	private static final String BORDER_FNAME = "BorderFree";
	private static final String BORDER_LNAME = "Inc";
	//private static final String ENCODING_TYPE = "UTF-8";
	
	/** The credit card type map. */
	private Map<String, String> creditCardTypeMap;

	/** The order manager. */
	private BBBOrderManager orderManager;
	
	/**
	 * Gets the order manager.
	 *
	 * @return the order manager
	 */
	public BBBOrderManager getOrderManager() {
		return orderManager;
	}

	/**
	 * Sets the order manager.
	 *
	 * @param orderManager the new order manager
	 */
	public void setOrderManager(final BBBOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	/**
	 * Gets the encryptor tools.
	 *
	 * @return the encryptor tools
	 */
	/**
	 * Gets the credit card type map.
	 *
	 * @return the credit card type map
	 */
	public Map<String, String> getCreditCardTypeMap() {
		return creditCardTypeMap;
	}

	/**
	 * Sets the credit card type map.
	 *
	 * @param creditCardTypeMap the credit card type map
	 */
	public void setCreditCardTypeMap(final Map<String, String> creditCardTypeMap) {
		this.creditCardTypeMap = creditCardTypeMap;
	}

	/**
	 * @return the nameStringBodyRegexPattern
	 */
	public String getNameStringBodyRegexPattern() {
		return nameStringBodyRegexPattern;
	}

	/**
	 * @param nameStringBodyRegexPattern the nameStringBodyRegexPattern to set
	 */
	public void setNameStringBodyRegexPattern(String nameStringBodyRegexPattern) {
		this.nameStringBodyRegexPattern = nameStringBodyRegexPattern;
	}

	/**
	 * Update shipping info.
	 *
	 * @param bbbOrder the bbb order
	 * @param shippingAddress the shipping address
	 * @return true, if successful
	 * @throws CommerceException 
	 */
	public boolean updateShippingInfo(final BBBOrder bbbOrder,final OrderFeed orderFeed) throws CommerceException
	{
		if(bbbOrder.getShippingGroupCount()==1)
		{
			vlogDebug("Order found Shipping group for order id :order {0}.id ", bbbOrder);
			final ShippingGroup shipGroup =(ShippingGroup) bbbOrder.getShippingGroups().get(0);
			final BBBHardGoodShippingGroup hardgoodShippingGroup = (BBBHardGoodShippingGroup) (shipGroup);
			final BBBRepositoryContactInfo atgShippingAddress = (BBBRepositoryContactInfo) hardgoodShippingGroup.getShippingAddress();
			updateAddress(orderFeed, atgShippingAddress,"Shipping");
			hardgoodShippingGroup.setShippingAddress(atgShippingAddress);
			//hardgoodShippingGroup.getPriceInfo().setCurrencyCode("USD");
		}
		else
		{
			vlogDebug("Order not found Shipping group for order id :order {0}.id ", bbbOrder);
			final ShippingGroupManager shippingGroupManager = getOrderManager().getShippingGroupManager();
			shippingGroupManager.removeAllShippingGroupsFromOrder(bbbOrder);
			/* add a default shipping group to the order */
			ShippingGroup defaultShippingGroup = shippingGroupManager.createShippingGroup();
			shippingGroupManager.addShippingGroupToOrder(bbbOrder, defaultShippingGroup);
			final BBBHardGoodShippingGroup hardgoodShippingGroup = (BBBHardGoodShippingGroup) (defaultShippingGroup);
			final BBBRepositoryContactInfo atgShippingAddress = (BBBRepositoryContactInfo) hardgoodShippingGroup.getShippingAddress();
			updateAddress(orderFeed, atgShippingAddress,"Shipping");
			hardgoodShippingGroup.setShippingAddress(atgShippingAddress);

			for (BBBCommerceItem ci : (List<BBBCommerceItem>) bbbOrder.getCommerceItems()) {
				if (ci.getShippingGroupRelationshipCount() < 1) {
					getOrderManager().getCommerceItemManager().addItemQuantityToShippingGroup(bbbOrder, ci.getId(),
							defaultShippingGroup.getId(),ci.getQuantity());
				}
			}
		}
		return true;
	}

	/**
	 * Update payment info.
	 *
	 * @param bbbOrder the bbb order
	 * @param billingAddress the billing address
	 * @param creditCardInfo the credit card info
	 * @return true, if successful
	 * @throws IntrospectionException the introspection exception
	 * @throws CommerceException the commerce exception
	 * @throws RepositoryException 
	 */
	public boolean updatePaymentInfo(final BBBOrder bbbOrder,final OrderFeed orderFeed) throws CommerceException, RepositoryException  
	{
		PaymentGroup paymentGroup = null;
		double total=0;
		boolean isRequiredNewPaymentGroup=false;

		if(bbbOrder.getPaymentGroupCount()==1 )
		{
			vlogDebug("Order found payment group for order id :order {0}.id ", bbbOrder);
			paymentGroup = (PaymentGroup)bbbOrder.getPaymentGroups().get(0);
			total = bbbOrder.getPriceInfo().getTotal();
			if(paymentGroup instanceof BBBCreditCard)
			{
				vlogDebug("Adding payment to orderManager :order {0}.id ", bbbOrder);
				//orderManager.addOrderAmountToPaymentGroup(bbbOrder, paymentGroup.getId(), total);
				orderManager.addRemainingOrderAmountToPaymentGroup(bbbOrder, paymentGroup.getId());
				vlogDebug("Adding paymentGroup to orderManager  :order {0}.id ", bbbOrder);
				
			}
			else
			{
				isRequiredNewPaymentGroup=true;
			}
		}
		else
		{
			isRequiredNewPaymentGroup=true;
		}
		vlogDebug("New Payment Group Required ["+isRequiredNewPaymentGroup+"]");
		if(isRequiredNewPaymentGroup)
		{
			vlogDebug("Removing existing Payment group for order id :order {0}.id ", bbbOrder);
			orderManager.getPaymentGroupManager().removeAllPaymentGroupsFromOrder(bbbOrder);
			paymentGroup = orderManager.getPaymentGroupManager().createPaymentGroup();
			paymentGroup.setCurrencyCode(BBBInternationalShippingConstants.CURRENCY_USD);
			total = bbbOrder.getPriceInfo().getTotal();
			paymentGroup.setAmount(total);
			orderManager.getPaymentGroupManager().addPaymentGroupToOrder(bbbOrder, paymentGroup);
			orderManager.addOrderAmountToPaymentGroup(bbbOrder, paymentGroup.getId(), total);
			vlogDebug("Added new Payment group for order id :order {0}.id ", bbbOrder);
		}
		/*for (final Object pg : bbbOrder.getPaymentGroupRelationships()) {
			if (pg instanceof PaymentGroupOrderRelationship) {
				final PaymentGroupOrderRelationship payGrpOrdRel = (PaymentGroupOrderRelationship) pg;
				payGrpOrdRel.setRelationshipType(404);
			}
		}*/
		final CreditCard creditCard = (CreditCard) paymentGroup;
		copyCreditCardInfo(creditCard,orderFeed,total);
		populateBillingAddress(orderFeed,bbbOrder);
		return true;
	}

	private void populateBillingAddress(final OrderFeed orderFeed,BBBOrder bbbOrder) throws RepositoryException {

		MutableRepository orderRepo = (MutableRepository) getOrderManager().getOrderTools().getOrderRepository();
		MutableRepositoryItem addrItem = orderRepo.createItem("bbbAddress");
		BBBRepositoryContactInfo atgAddress = new BBBRepositoryContactInfo(addrItem);
		 final DomesticProfile domesticProfile = orderFeed.getOrder().get(0).getDomesticProfile();
         final List<Address> add=domesticProfile.getAddress();
          for(final Address address:add){
	            if ("Billing".equals(((Address) address).getType()))
	            {   
					atgAddress.setAddress1(address.getAddressLine1()==null ? "" : trimStringToLength(address.getAddressLine1(), 85));
					atgAddress.setAddress2(address.getAddressLine2()==null ? "" : trimStringToLength(address.getAddressLine2(),80));
					atgAddress.setAddress3(address.getAddressLine3()==null ? "" : trimStringToLength(address.getAddressLine3(),50));
			
					atgAddress.setCity(address.getCity()==null ? "" : trimStringToLength(address.getCity(),40));
					atgAddress.setFirstName(address.getFirstName()==null ? "" : trimStringToLength(address.getFirstName(),40));
					atgAddress.setLastName(address.getLastName()==null ? "" : trimStringToLength(address.getLastName(),40));
					atgAddress.setMiddleName(address.getMiddleInitials()==null ? "" : trimStringToLength(address.getMiddleInitials(),40));
					atgAddress.setCountry(address.getCountry()==null ? "" : trimStringToLength(address.getCountry(),40));
					atgAddress.setPostalCode(address.getPostalCode()==null ? "" : trimStringToLength(address.getPostalCode(),10));
					
					atgAddress.setState(address.getRegion()==null ? "" : trimStringToLength(address.getRegion(),40));
					((ContactInfo) atgAddress).setEmail(address.getEmail()==null ? "" : trimStringToLength(address.getEmail(),255));
					((ContactInfo) atgAddress).setPhoneNumber(address.getPrimaryPhone()==null ? "" : trimStringToLength(address.getPrimaryPhone(),40));
	            }
          }
		orderRepo.addItem(addrItem);
		bbbOrder.setBillingAddress(atgAddress);

	}

	/**
	 * Update address.
	 *
	 * @param address the address
	 * @param atgAddress the atg address
	 */
	private void updateAddress(final OrderFeed orderFeed,final  BBBRepositoryContactInfo atgAddress,String shippingType) {

		boolean isShippingAddress=false;
		final DomesticProfile domesticProfile = orderFeed.getOrder().get(0).getDomesticProfile();
        final  List<Address> add=domesticProfile.getAddress();
       
        for(final Address address:add){
            	            
            if ( shippingType.equals(address.getType()))
            {
            	isShippingAddress = "Shipping".equalsIgnoreCase(address.getType());
           
		if (address.getAddressLine1()!= null) {
			final String address1 = address.getAddressLine1();
			atgAddress.setAddress1(trimStringToLength(address1, 85));
		} else {
				logDebug("address.getAddress1() is null");
		}
		if (address.getAddressLine2()!= null) {
			final String address2 = address.getAddressLine2();
			atgAddress.setAddress2(trimStringToLength(address2,80));
		} else {
				logDebug("address.getAddress2() is null");
		}

		if (address.getAddressLine3() != null) {
			final 	String address3 = address.getAddressLine3();
			atgAddress.setAddress3(trimStringToLength(address3,50));
		} else {
			logDebug("address.getAddress3() is null");
		}
		if (address.getCity()!= null) {
			final String city = address.getCity();
			atgAddress.setCity(trimStringToLength(city,40));
		} else {
			logDebug("address.getCity() is null");
		}

		String borderFName = BBBConfigRepoUtils.getStringValue(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, BBBInternationalShippingConstants.BORDER_FNAME);
		if (borderFName == null) borderFName = BORDER_FNAME;
		String borderLName = BBBConfigRepoUtils.getStringValue(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, BBBInternationalShippingConstants.BORDER_LNAME);
		if (borderLName == null) borderLName = BORDER_LNAME;
		
		if (address.getFirstName()!= null) {
			final 	String firstName = address.getFirstName();
			if (isShippingAddress && !validateNamePattern(firstName)) {
				atgAddress.setFirstName(trimStringToLength(borderFName, 40));
				atgAddress.setLastName(trimStringToLength(borderLName, 40));
			} else {
				atgAddress.setFirstName(trimStringToLength(firstName, 40));
				} 
			} else {
				logDebug("address.getFirstName() is null");
			}

		if (address.getLastName()!= null) {
			final 	String lastName = address.getLastName();
			if (isShippingAddress && !validateNamePattern(lastName)) {
				atgAddress.setFirstName(trimStringToLength(borderFName, 40));
				atgAddress.setLastName(trimStringToLength(borderLName, 40));
			} else if (!(borderFName.equalsIgnoreCase(atgAddress.getFirstName()) && borderLName.equalsIgnoreCase(atgAddress.getLastName()))){
				atgAddress.setLastName(trimStringToLength(lastName, 40));
				} 
			} else {
				logDebug("address.getLastName() is null");
			}

		if (address.getMiddleInitials()!= null) {
			final 	String middleName = address.getMiddleInitials();
			atgAddress.setMiddleName(trimStringToLength(middleName, 40));
		} else {
			logDebug("address.getMiddleName() is null");
			}

		if (address.getCountry()!= null) {
			final String country = address.getCountry();
			atgAddress.setCountry(trimStringToLength(country,40));
		} else {
			logDebug("address.getCountry() is null");
		}
		if (address.getPostalCode()!= null) {
			final String postalCode = address.getPostalCode();
			atgAddress.setPostalCode(trimStringToLength(postalCode, 10));
		} else {
				logDebug("address.getPostalCode() is null");
		}
		if (address.getRegion()!= null) {
			final String region = address.getRegion();
			atgAddress.setState(trimStringToLength(region,40));
		} else {
			logDebug("address.getState() is null");
		}
		if (address.getEmail()!= null) {
			final String email = address.getEmail();
			((ContactInfo) atgAddress).setEmail(trimStringToLength(email,255));
		} else {
				logDebug("address.getEmail() is null");
		}
		if (address.getPrimaryPhone()!= null) {
			final String primaryPhone = address.getPrimaryPhone();
			((ContactInfo) atgAddress).setPhoneNumber(trimStringToLength(primaryPhone,40));
		} else {
				logDebug("address.getPhoneNumber() is null");
		}
            }
        }
	}

	/**
	 * Trim string to length.
	 *
	 * @param pString the string
	 * @param pLength the length
	 * @return the string
	 */
	public static String trimStringToLength(final String pString,final  int pLength){
		return pString.length()>pLength?pString.substring(0, pLength):pString;
	}

	/**
	 * Copy credit card info.
	 *
	 * @param creditCard the credit card
	 * @param creditCardInfo the credit card info
	 * @param order the order
	 * @param billingAddress the billing address
	 * @param orderTotal the order total
	 * @throws IntrospectionException the introspection exception
	 */
	private void copyCreditCardInfo(final CreditCard creditCard, OrderFeed orderFeed,final double orderTotal) {

		if (creditCard != null && orderFeed.getOrder().get(0).getCreditCard() != null) {
			final BBBRepositoryContactInfo atgBillingAddress = (BBBRepositoryContactInfo) creditCard.getBillingAddress();
			com.bbb.framework.jaxb.internationalshipping.pofile.CreditCard creditCardInfo=(com.bbb.framework.jaxb.internationalshipping.pofile.CreditCard) orderFeed.getOrder().get(0).getCreditCard();
			//update billing address
			updateAddress(orderFeed, atgBillingAddress,"Billing");
			// bbbOrder.setBillingAddress(atgBillingAddress);
			creditCard.setCardVerficationNumber(creditCardInfo.getCVN());
			creditCard.setBillingAddress(atgBillingAddress);
			creditCard.setCreditCardNumber(creditCardInfo.getNumber());
			creditCard.setCreditCardType(getCreditCardTypeMap().get(creditCardInfo.getType()));
			//creditCard.setExpirationDayOfMonth("01");
			creditCard.setExpirationMonth(creditCardInfo.getExpiry().getMonth());
			creditCard.setExpirationYear(creditCardInfo.getExpiry().getYear());
			creditCard.setAmount(orderTotal);
			((BBBCreditCard) creditCard).setNameOnCard(creditCardInfo.getNameOnCard());
		}

	}

	/**
	 * validationNamePattern.
	 *
	 * @param nameString the name value
	 * @return boolean true if special character found, or false when not found.
	 */
	private boolean validateNamePattern(String nameString) {

		boolean isValidName = true;

		// Checks special characters in nameString
		if (!Pattern.compile(nameStringBodyRegexPattern).matcher(nameString).matches()) {
			logDebug("Special character found & will be discarded : "+ nameString);
			isValidName = false;
		}

		return isValidName;
	}

}
