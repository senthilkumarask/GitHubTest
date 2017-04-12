package com.bbb.rest;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.bbb.rest.account.TestAddNewAddressInProfile;
import com.bbb.rest.account.TestCanadaStoreDetails;
import com.bbb.rest.account.TestCreateUserAfterCheckOut;
import com.bbb.rest.account.TestGetCouponService;
import com.bbb.rest.account.TestMakeDefaultBillingAddress;
import com.bbb.rest.account.TestMakeDefaultCreditCard;
import com.bbb.rest.account.TestMakeDefaultShippingAddress;
import com.bbb.rest.account.TestProfileInfo;
import com.bbb.rest.account.TestRemoveAddressInProfile;
import com.bbb.rest.account.TestRestHandleContactUsRequest;
import com.bbb.rest.account.TestUpdateAddressInProfile;
import com.bbb.rest.browse.TestGetBreadcrumb;
import com.bbb.rest.cart.TestAddToCartOrder;
import com.bbb.rest.cart.TestEmailCart;
import com.bbb.rest.cart.TestMoveAllWishListItemsToCart;
import com.bbb.rest.cart.TestMoveToPayment;
import com.bbb.rest.cart.TestMoveWishListItemToCart;
import com.bbb.rest.cart.TestPriceItem;
import com.bbb.rest.cart.TestUpdateCartOrder;
import com.bbb.rest.catalog.TestCollegeDetails;
import com.bbb.rest.catalog.TestGetClearanceProducts;
import com.bbb.rest.catalog.TestGetDormRoomCollections;
import com.bbb.rest.catalog.TestGetEcoFeeSKUDetailForState;
import com.bbb.rest.catalog.TestGetEcoFeeSKUDetailForStore;
import com.bbb.rest.catalog.TestGetStoreDetails;
import com.bbb.rest.catalog.TestProductDetail;
import com.bbb.rest.catalog.TestProductListDetails;
import com.bbb.rest.catalog.TestSkuDetail;
import com.bbb.rest.certona.TestCertonaDroplet;
import com.bbb.rest.cms.TestNavigationLinks;
import com.bbb.rest.cms.TestRestCategoryPageContent;
import com.bbb.rest.cms.TestRestHomePageContent;
import com.bbb.rest.cms.TestRestStaticContent;
import com.bbb.rest.cms.TestStaticContent;
import com.bbb.rest.commerce.TestGetAllAddressForCheckout;
import com.bbb.rest.commerce.TestGetAllCreditCards;
import com.bbb.rest.commerce.TestGetShippingPolicies;
import com.bbb.rest.commerce.TestMultiShippingToOrder;
import com.bbb.rest.commerce.TestPlaceCurrentOrder;
import com.bbb.rest.commerce.TestPopulateState;
import com.bbb.rest.commerce.TestProductBreadCrumb;
import com.bbb.rest.commerce.TestRestAddBillingAddress;
import com.bbb.rest.commerce.TestRestAddCreditCardToOrder;
import com.bbb.rest.commerce.TestRestAddRemoveGiftMessage;
import com.bbb.rest.commerce.TestRestAddShippingAddress;
import com.bbb.rest.commerce.TestRestCanPerformExpressCheckout;
import com.bbb.rest.commerce.TestRestCartAndCheckout;
import com.bbb.rest.commerce.TestRestChangeShippingGroup;
import com.bbb.rest.commerce.TestRestCheckOrderPackNHoldEligibility;
import com.bbb.rest.commerce.TestRestCheckShippingMethodRestrictionForOrder;
import com.bbb.rest.commerce.TestRestCheckShippingRestrictionForOrder;
import com.bbb.rest.commerce.TestRestClosenessQualifierService;
import com.bbb.rest.commerce.TestRestEstimatedShippingDates;
import com.bbb.rest.commerce.TestRestExpressCheckoutCondition;
import com.bbb.rest.commerce.TestRestExpressCheckoutForOrder;
import com.bbb.rest.commerce.TestRestGetCurrentOrderDetails;
import com.bbb.rest.commerce.TestRestGetItemInventory;
import com.bbb.rest.commerce.TestRestGetLegacyOrderDetailForUser;
import com.bbb.rest.commerce.TestRestGetShippingType;
import com.bbb.rest.commerce.TestRestRemoveItemsFromCart;
import com.bbb.rest.commerce.TestRestVerifiedByVisaLookup;
import com.bbb.rest.commerce.TestShipToMultiplePeople;
import com.bbb.rest.email.droplet.TestBBBEmailFetchDroplet;
import com.bbb.rest.giftregistry.TestAddItemToRegistry;
import com.bbb.rest.giftregistry.TestBridalShowAPI;
import com.bbb.rest.giftregistry.TestCreateBabyRegistryClient;
import com.bbb.rest.giftregistry.TestEmailRegistry;
import com.bbb.rest.giftregistry.TestForgotRegistryPassword;
import com.bbb.rest.giftregistry.TestGetBridalToolkitRegistriesAPI;
import com.bbb.rest.giftregistry.TestGetProfileRegistryList;
import com.bbb.rest.giftregistry.TestGetRegistryDetailAPI;
import com.bbb.rest.giftregistry.TestGetRegistryInfoForProfileId;
import com.bbb.rest.giftregistry.TestGetRegistryListForProfile;
import com.bbb.rest.giftregistry.TestGiftRegistryRecommendation;
import com.bbb.rest.giftregistry.TestHandleBabyBook;
import com.bbb.rest.giftregistry.TestHandleBridalBook;
import com.bbb.rest.giftregistry.TestImportRegistry;
import com.bbb.rest.giftregistry.TestPersistRecommenderReln;
import com.bbb.rest.giftregistry.TestRemoveItemFromRegistry;
import com.bbb.rest.giftregistry.TestRestGetRegistryTypes;
import com.bbb.rest.giftregistry.TestSearchRegistryByCriteria;
import com.bbb.rest.giftregistry.TestUpdateItemInRegistry;
import com.bbb.rest.giftregistry.TestUpdateRegistryAPI;
import com.bbb.rest.giftregistry.TestValidateToken;
import com.bbb.rest.internationalShipping.formhandler.TestInternationalShipFormHandler;
import com.bbb.rest.internationalShipping.utils.TestBuildContext;
import com.bbb.rest.internationalShipping.utils.TestCheckoutHelper;
import com.bbb.rest.mapquest.TestSearchInStore;
import com.bbb.rest.mapquest.TestSearchStore;
import com.bbb.rest.payment.TestAddRemoveGiftCard;
import com.bbb.rest.payment.TestGiftCardBalance;
import com.bbb.rest.profile.TestAddCreditCard;
import com.bbb.rest.profile.TestCheckForRegistration;
import com.bbb.rest.profile.TestCreateAccount;
import com.bbb.rest.profile.TestForgotPassword;
import com.bbb.rest.profile.TestOrderTrackingDetails;
import com.bbb.rest.profile.TestProfileAccount;
import com.bbb.rest.profile.TestProfileLogout;
import com.bbb.rest.profile.TestRemoveCreditCard;
import com.bbb.rest.profile.TestUnsubscribeOOSEmail;
import com.bbb.rest.profile.TestUpdateAccount;
import com.bbb.rest.profile.TestUpdateCreditCard;
import com.bbb.rest.search.TestRestSearchManager;
import com.bbb.rest.selfservice.TestEmailAFriend;
import com.bbb.rest.selfservice.TestGetAllSubject;
import com.bbb.rest.selfservice.TestGetCommonGreeting;
import com.bbb.rest.selfservice.TestSetFavoriteStore;
import com.bbb.rest.selfservice.TestSurvey;
import com.bbb.rest.seo.TestSEOTag;
import com.bbb.rest.wishlist.TestAddItemToWishlist;
import com.bbb.rest.wishlist.TestMoveAllWishListItemsToRegistry;
import com.bbb.rest.wishlist.TestMoveWishListItemToRegistry;
import com.bbb.rest.wishlist.TestRemoveAllWishListItems;
import com.bbb.rest.wishlist.TestWishListService;



public class AllTests extends TestSuite {

	public static void main (String[] args) {
		junit.textui.TestRunner.run (suite());
	}
	public static Test suite() {

		TestSuite suite= new TestSuite();
		//suite.addTest(TestStaticContent.suite());
		suite.addTest(new TestStaticContent("testStaticContent"));
		suite.addTest(new TestRestStaticContent("testRestStaticContent"));
		suite.addTest(new TestRestStaticContent("testRestStaticContentError"));
		suite.addTest(new TestStaticContent("testRegistryContent"));
		suite.addTest(new TestStaticContent("getAllGuide"));
		suite.addTest(new TestStaticContent("getGuide"));
		suite.addTest(new TestStaticContent("getConfigType"));
		suite.addTest(new TestStaticContent("getConfigKeys"));
		suite.addTest(new TestStaticContent("getLabel"));
		suite.addTest(new TestStaticContent("testStaticContent"));
		suite.addTest(new TestStaticContent("getMultipleLabel"));
		suite.addTest(new TestStaticContent("getMultipleLabelException"));


		suite.addTest(new TestProfileAccount("testUnAuthorisedLogin"));
		suite.addTest(new TestProfileAccount("testAuthorisedLogin"));
		suite.addTest(new TestProfileAccount("testExistingSessionLogin"));
		suite.addTest(new TestAddToCartOrder("testAddSingleItemToCart"));
		suite.addTest(new TestAddToCartOrder("testAddSingleItemToCartInvalidSKU"));
		suite.addTest(new TestAddToCartOrder("testAddMultipleItemToCart"));
		suite.addTest(new TestAddToCartOrder("testMergeItemToCart"));
		suite.addTest(new TestRestSearchManager("testRestSearchManager"));
		suite.addTest(new TestRestSearchManager("testRestSearchManagerError"));
		suite.addTest(new TestRestSearchManager("testRSMPerformTypeAheadSearch"));
		suite.addTest(new TestRestSearchManager("testRSMPerformTypeAheadSearchError"));
		suite.addTest(new TestRestSearchManager("testRSMGetAllNavigation"));
		suite.addTest(new TestRestSearchManager("testRSMGetAllNavigationErr"));
		suite.addTest(new TestRestSearchManager("testRSMGetCollegesByState"));
		suite.addTest(new TestRestSearchManager("testRSMGetCollegeMerchandize"));
		suite.addTest(new TestRestCartAndCheckout("testShippingMethodsSingleShippingSuccess"));
		suite.addTest(new TestRestCartAndCheckout("testShippingMethodsMultiShippingSuccess"));
		suite.addTest(new TestRestCartAndCheckout("testShippingMethodsFailure"));
		suite.addTest(new TestRestCartAndCheckout("testGetStatesList"));
		suite.addTest(new TestRestCartAndCheckout("testGetCollegeStatesList"));
		suite.addTest(new TestRestCartAndCheckout("testGetCollegesByState"));
		suite.addTest(new TestCreateAccount("testCreateAccount"));
		suite.addTest(new TestCreateAccount("testCreateAccountError"));
		suite.addTest(new TestCreateBabyRegistryClient("testCreateBabyReg"));
		suite.addTest(new TestCreateBabyRegistryClient("testCreateBabyRegError"));
		suite.addTest(new TestGetRegistryInfoForProfileId("testGetRegistryInfoForProfile"));
		suite.addTest(new TestGetRegistryInfoForProfileId("testGetRegistryInfoForProfileError"));
		suite.addTest(new TestGetRegistryInfoForProfileId("testGetRegRecommItemsForDeclinedTab"));
		suite.addTest(new TestGetRegistryInfoForProfileId("testGetRegRecommItemsForDeclinedTabErr"));
		suite.addTest(new TestGetRegistryListForProfile("testGetRegistryListForProfile"));

	//	 This JUnit method name has changed
		suite.addTest(new TestGetRegistryListForProfile("testGetRegistryListForProfileError"));

		suite.addTest(new TestCollegeDetails("testCollegeDetail"));
		suite.addTest(new TestCollegeDetails("testCollegeDetailError"));
		suite.addTest(new TestProductDetail("testProductDetails"));
		suite.addTest(new TestProductDetail("testProductDetailsErr"));
		suite.addTest(new TestProductListDetails("testProductListDetails"));
		suite.addTest(new TestProductListDetails("testProductListDetailsErr"));
		suite.addTest(new TestGetCouponService("testGetCoupons"));
		suite.addTest(new TestGetCouponService("testGetCouponsBySchoolId"));
		suite.addTest(new TestGetCouponService("testGetCouponsAllInputNull"));
		suite.addTest(new TestGetCouponService("testGetCouponsByEmailOnly"));
		suite.addTest(new TestAddItemToWishlist("testTransientUser"));
		suite.addTest(new TestAddItemToWishlist("testGetWishList"));
		suite.addTest(new TestAddItemToWishlist("testGetWishListNonLoggedInUser"));
		suite.addTest(new TestAddItemToWishlist("testAddSingleSkuToWishlist"));
		suite.addTest(new TestUpdateAccount("testUpdateAccount"));
		suite.addTest(new TestUpdateAccount("testUpdateAccountError"));
		suite.addTest(new TestRestGetCurrentOrderDetails("testGetCurrentOrderDetailsTransientUser"));
		suite.addTest(new TestRestGetCurrentOrderDetails("testGetCurrentOrderDetailsLoggedInUser"));
		suite.addTest(new TestRestGetCurrentOrderDetails("testGetATGOrderDetails"));
		suite.addTest(new TestRestGetCurrentOrderDetails("testGetATGOrderDetailsFailure"));
		suite.addTest(new TestForgotPassword("testForgotPassword"));
		suite.addTest(new TestForgotPassword("testForgotPasswordError"));
		suite.addTest(new TestOrderTrackingDetails("testOrderTrackingDetails"));
		suite.addTest(new TestOrderTrackingDetails("testOrderTrackingDetailsError"));
		suite.addTest(new TestRestRemoveItemsFromCart("testRemoveItemFromOrderLoggedInUser"));
		suite.addTest(new TestRestRemoveItemsFromCart("testRemoveItemFromOrderTransientUser"));
		suite.addTest(new TestRestRemoveItemsFromCart("testRemoveItemFromOrderFailure"));
		suite.addTest(new TestSkuDetail("testSkuDetails"));
		suite.addTest(new TestSkuDetail("testSkuDetailsErr"));
		suite.addTest(new TestAddItemToRegistry("testAddItemToRegistry"));
		suite.addTest(new TestAddItemToRegistry("testAddItemToRegistryErr"));
		suite.addTest(new TestAddItemToRegistry("testAddItemToRegistryRecommender"));
		suite.addTest(new TestAddItemToRegistry("testAcceptRecommendationRegistry"));
		suite.addTest(new TestAddItemToRegistry("testAcceptRecommendationFromDeclinedTab"));
		suite.addTest(new TestAddItemToRegistry("testAcceptRecommendationFromDeclinedTabErr"));
		suite.addTest(new TestAddItemToRegistry("testDeclineRecommendationFromPendingTab"));
		suite.addTest(new TestAddItemToRegistry("testDeclineRecommendationFromPendingTabErr"));
		suite.addTest(new TestProfileLogout("testCheckLogout"));
		suite.addTest(new TestProfileLogout("testCheckLogoutError"));
		suite.addTest(new TestGetRegistryDetailAPI("testGetRegistryDetail"));
		suite.addTest(new TestGetRegistryDetailAPI("testGetRegistryDetailError"));
		suite.addTest(new TestGetRegistryDetailAPI("testGetRegistryDetailForMobileApp"));
		suite.addTest(new TestAddItemToWishlist("testAddSingleSkuToWishlist"));
		suite.addTest(new TestWishListService("testUpdateGiftlistItemsForQtyZero"));
		suite.addTest(new TestWishListService("testUpdateGiftlistItems"));
		suite.addTest(new TestWishListService("testRemoveWishListNonLoggedInUser"));
		suite.addTest(new TestAddItemToWishlist("testAddSingleSkuToWishlist"));
		suite.addTest(new TestWishListService("testRemoveGiftlistItems"));
		suite.addTest(new TestMakeDefaultCreditCard("testMakeDefaultCreditCard"));
		suite.addTest(new TestMakeDefaultCreditCard("testMakeDefaultCreditCardError"));
		suite.addTest(new TestPriceItem("testPriceItem"));
		suite.addTest(new TestPriceItem("testPriceItemError"));
		suite.addTest(new TestProfileInfo("testProfileInforUser"));
		suite.addTest(new TestProfileInfo("testProfileInfoforUserError"));
		suite.addTest(new TestUpdateCartOrder("testUpdateItemsToCart"));
		suite.addTest(new TestUpdateCartOrder("testUpdateItemsToCartError"));
		suite.addTest(new TestRestHandleContactUsRequest("testContactUsViaEmailRequestSuccess"));
		suite.addTest(new TestRestHandleContactUsRequest("testContactUsViaPhoneRequestSuccess"));
		suite.addTest(new TestRestHandleContactUsRequest("testContactUsRequestFailure"));
		suite.addTest(new TestRestAddShippingAddress("testAddNewShippingAddress"));
		suite.addTest(new TestRestAddShippingAddress("testAddNewShippingAddressLoggedInFlow"));
		suite.addTest(new TestRestAddShippingAddress("testAddNewShippingAddressFailure"));
		suite.addTest(new TestSearchRegistryByCriteria("testSearchRegistryByName"));
		suite.addTest(new TestSearchRegistryByCriteria("testSearchRegistryByRegistryId"));
		suite.addTest(new TestSearchRegistryByCriteria("testSearchRegistryError"));
		suite.addTest(new TestCanadaStoreDetails("testGetCanadaStoreListDetails"));
		suite.addTest(new TestAddCreditCard("testAddCreditCard"));
		suite.addTest(new TestAddCreditCard("testAddCreditCardError"));
		suite.addTest(new TestUpdateCreditCard("testUpdateCreditCard"));
		suite.addTest(new TestUpdateCreditCard("testUpdateCreditCardError"));
		suite.addTest(new TestGiftCardBalance("testGetGiftCardBalance"));
		suite.addTest(new TestGiftCardBalance("testGetGiftCardBalanceError"));
		suite.addTest(new TestGiftCardBalance("testGetGiftCardBalanceMaxAttempt"));
		suite.addTest(new TestRestAddCreditCardToOrder("testAddNewCreditCartToOrderLoggedIn"));
		suite.addTest(new TestRestAddCreditCardToOrder("testAddNewCreditCartToOrder"));
		suite.addTest(new TestRestAddCreditCardToOrder("testAddNewCreditCartToOrderError"));
		suite.addTest(new TestRestGetLegacyOrderDetailForUser("testGetLegacyOrderDetail"));
		suite.addTest(new TestRestGetLegacyOrderDetailForUser("testGetLegacyOrderDetailErr"));
		suite.addTest(new TestRestAddBillingAddress("testAddNewBillingAddress"));
		suite.addTest(new TestRestAddBillingAddress("testAddNewBillingAddressLoggedInFlow"));
		suite.addTest(new TestRestAddBillingAddress("testAddNewBillingAddressFailure"));
		suite.addTest(new TestMakeDefaultShippingAddress("testMakeDefaultShippingAddress"));
		suite.addTest(new TestMakeDefaultShippingAddress("testMakeDefaultShippingAddressError"));
		suite.addTest(new TestMakeDefaultBillingAddress("testMakeDefaultBillingAddress"));
		suite.addTest(new TestMakeDefaultBillingAddress("testMakeDefaultBillingAddressError"));
		suite.addTest(new TestAddNewAddressInProfile("testAddNewAddress"));
		suite.addTest(new TestAddNewAddressInProfile("testAddNewAddressError"));
		suite.addTest(new TestAddNewAddressInProfile("testAddNewAddressWithOutLoginError"));
		suite.addTest(new TestUpdateAddressInProfile("testUpdateAddress"));
		suite.addTest(new TestUpdateAddressInProfile("testUpdateAddressError"));
		suite.addTest(new TestUpdateAddressInProfile("testUpdateAddressWithOutLoginError"));
		suite.addTest(new TestRemoveCreditCard("testRemoveCreditCard"));
		suite.addTest(new TestRemoveCreditCard("testRemoveCreditCardError"));
		suite.addTest(new TestUnsubscribeOOSEmail("testUnsubscribeOOSEmail"));
		suite.addTest(new TestUnsubscribeOOSEmail("testUnsubscribeOOSEmailError"));
		suite.addTest(new TestRestCanPerformExpressCheckout("testCanPerformExpressCheckout"));
		suite.addTest(new TestRestGetShippingType("testGetShippingType"));
		suite.addTest(new TestRemoveAddressInProfile("testRemoveAddress"));
		suite.addTest(new TestRemoveAddressInProfile("testRemoveAddressWithOutLoginError"));
		suite.addTest(new TestCheckForRegistration("testCheckForRegistration"));
		suite.addTest(new TestCheckForRegistration("testCheckForRegistrationError"));
		suite.addTest(new TestAddRemoveGiftCard("testAddGiftCardForPayment"));
		suite.addTest(new TestAddRemoveGiftCard("testAddGiftCardForPaymentError"));
		suite.addTest(new TestAddRemoveGiftCard("testIsOrderAmountCoveredByGC"));
		suite.addTest(new TestAddRemoveGiftCard("testMaxGiftCardReached"));
		suite.addTest(new TestAddRemoveGiftCard("testIsGiftCardAlreadyExist"));
		suite.addTest(new TestRestAddRemoveGiftMessage("testRemoveGiftMessagesLoggedInFlow"));
		suite.addTest(new TestRestAddRemoveGiftMessage("testAddGiftMessagesLoggedInFlow"));
		suite.addTest(new TestRestAddRemoveGiftMessage("testAddGiftMessageTransientUser"));
		suite.addTest(new TestRestAddRemoveGiftMessage("testAddRemoveGiftMessagesFailure"));
		suite.addTest(new TestRestAddRemoveGiftMessage("testGiftWrapSKUTransientUser"));
		suite.addTest(new TestSetFavoriteStore("testSetFavoriteStore"));
		suite.addTest(new TestSetFavoriteStore("testSetFavoriteStoreError"));
		suite.addTest(new TestRestCheckShippingRestrictionForOrder("testGetSkusShippingStatus"));

		suite.addTest(new TestMoveWishListItemToCart("testMoveWishListItemToCart"));
		suite.addTest(new TestMoveWishListItemToCart("testMoveWishListItemToCartError"));
		suite.addTest(new TestMoveWishListItemToCart("testMoveWishListItemToCartNonLoggedInUser"));
		suite.addTest(new TestMoveAllWishListItemsToCart("testMoveAllWishListItemsToCart"));
		suite.addTest(new TestMoveAllWishListItemsToCart("testMoveAllWishListItemsToCartError"));
		suite.addTest(new TestMoveAllWishListItemsToCart("testMoveAllWishListItemsToCartNonLoggedInUser"));
		suite.addTest(new TestRemoveAllWishListItems("testRemoveAllWishListItems"));
		suite.addTest(new TestRemoveAllWishListItems("testRemoveAllWishListItemsNonLoggedInUser"));
		suite.addTest(new TestMoveAllWishListItemsToRegistry("testMoveAllWishListItemsToRegistry"));
		suite.addTest(new TestMoveAllWishListItemsToRegistry("testMoveAllWishListItemsToRegistryError"));
		suite.addTest(new TestMoveAllWishListItemsToRegistry("testMoveAllWishListItemsToRegistryNonLoggedInUser"));
		//Survey
		suite.addTest(new TestSurvey("testRequestInfo"));
		suite.addTest(new TestSurvey("testRequestInfoError"));

		//Change Shipping Group from Bopus to Online & Online to Bopus
		suite.addTest(new TestRestChangeShippingGroup("testOnlineToBopusAndBopusToOnlineSuccess"));
		suite.addTest(new TestRestChangeShippingGroup("testOnlineToBopusAndBopusToOnlineFailure"));
		suite.addTest(new TestRestCheckOrderPackNHoldEligibility("testOrderPackNHoldEligibility"));
		suite.addTest(new TestUpdateRegistryAPI("testUpdateRegistry"));
		suite.addTest(new TestUpdateRegistryAPI("testUpdateRegistryException"));
		suite.addTest(new TestUpdateRegistryAPI("testUpdateRegistryNonLogin"));
		suite.addTest(new TestUpdateItemInRegistry("testUpdateItemInRegistry"));
		suite.addTest(new TestUpdateItemInRegistry("testUpdateItemInRegistryNonLogin"));
		suite.addTest(new TestImportRegistry("testImportRegistry"));
		suite.addTest(new TestImportRegistry("testImportRegistryNonLogin"));
		suite.addTest(new TestRemoveItemFromRegistry("testRemoveItemFromRegistry"));
		suite.addTest(new TestRemoveItemFromRegistry("testRemoveItemFromRegistryNonLogin"));
		suite.addTest(new TestForgotRegistryPassword("testForgotRegistryPassword"));
		suite.addTest(new TestForgotRegistryPassword("testForgotRegistryPasswordException"));
		suite.addTest(new TestForgotRegistryPassword("testForgotRegistryPasswordNonLogin"));
		suite.addTest(new TestRestCheckShippingMethodRestrictionForOrder("testCheckShippingMethodForSkus"));

		suite.addTest(new TestRestEstimatedShippingDates("testGetExpectedShippingDate"));
		suite.addTest(new TestRestEstimatedShippingDates("testGetExpectedShippingDateError"));
		suite.addTest(new TestGetClearanceProducts("testGetClearanceProducts"));
		suite.addTest(new TestGetDormRoomCollections("testGetDormRoomCollections"));
		suite.addTest(new TestHandleBabyBook("testhandleBabyBook"));
		suite.addTest(new TestHandleBabyBook("testhandleBabyBookError"));
		suite.addTest(new TestHandleBridalBook("testhandleBridalBook"));
		suite.addTest(new TestHandleBridalBook("testhandleBridalBookError"));

		suite.addTest(new TestRestClosenessQualifierService("testGetClosenessQualifiersForLoggedinUser"));
		suite.addTest(new TestRestClosenessQualifierService("testGetClosenessQualifiersForAnonymousUser"));
		suite.addTest(new TestRestClosenessQualifierService("testGetClosenessQualifiersError"));
		//suite.addTest(new TestCertonaDroplet("testCertonaDroplet_college_landing"));
		suite.addTest(new TestCertonaDroplet("testCertonaDroplet_error"));
		//suite.addTest(new TestCertonaDroplet("testCertonaDroplet_baby_landing"));
		//suite.addTest(new TestCertonaDroplet("testCertonaDroplet_bridal_landing"));
		suite.addTest(new TestCertonaDroplet("testCertonaDroplet_product_detail"));
		suite.addTest(new TestCertonaDroplet("testCertonaDroplet_category_landing"));
		suite.addTest(new TestCertonaDroplet("testCertonaDroplet_cart_slot"));
		suite.addTest(new TestCertonaDroplet("testCertonaDroplet_home_page"));
		suite.addTest(new TestCertonaDroplet("testCertonaDroplet_top_reg_item"));
		suite.addTest(new TestBridalShowAPI("testBridalShow"));
		suite.addTest(new TestBridalShowAPI("testBridalShowError"));
		suite.addTest(new TestGetEcoFeeSKUDetailForState("testGetEcoFeeSKUForState"));
		suite.addTest(new TestGetEcoFeeSKUDetailForState("testGetEcoFeeSKUForStateError"));
		suite.addTest(new TestGetEcoFeeSKUDetailForStore("testGetEcoFeeSKUForStore"));
		suite.addTest(new TestGetEcoFeeSKUDetailForStore("testGetEcoFeeSKUForStoreError"));
		suite.addTest(new TestGetStoreDetails("testGetStoreDetails"));
		suite.addTest(new TestGetAllSubject("testGetAllSubjects"));
		suite.addTest(new TestGetAllAddressForCheckout("testGetAllAddressForCheckout"));
		suite.addTest(new TestGetAllAddressForCheckout("testGetAllAddressForCheckoutLoggedInFlow"));
		suite.addTest(new TestGetShippingPolicies("testGetShippingMethodDetails"));
		suite.addTest(new TestGetShippingPolicies("testGetShippingPriceTableDetail"));

		suite.addTest(new TestGetCommonGreeting("testGetGiftWrapMsg"));

		suite.addTest(new TestEmailAFriend("testEmailAFriend"));
		suite.addTest(new TestEmailAFriend("testEmailAFriendException"));

		suite.addTest(new TestEmailRegistry("testEmailRegistry"));
		suite.addTest(new TestEmailRegistry("testEmailRegistryException"));

		suite.addTest(new TestEmailRegistry("testEmailGiftRegistryRecommendation"));
		suite.addTest(new TestEmailRegistry("testEmailGiftRegistryRecommendationException"));

		suite.addTest(new TestRestGetRegistryTypes("testGetRegistryTypes"));
		//move item from wishlist to registry
		suite.addTest(new TestMoveWishListItemToRegistry("testMoveWishListItemToRegistry"));
		suite.addTest(new TestMoveWishListItemToRegistry("testInvalidRegistryId"));
		suite.addTest(new TestMoveWishListItemToRegistry("testMoveWishListItemToRegistryException"));
		suite.addTest(new TestMoveWishListItemToRegistry("testMoveWishListItemToRegistryNonLoggedInUser"));
		//Test Create User after Guest Checkout
		suite.addTest(new TestCreateUserAfterCheckOut("testCreateUserWhenEmailIsAlreadyRegistered"));
		suite.addTest(new TestCreateUserAfterCheckOut("testCreateUserAfterCheckOut"));
		suite.addTest(new TestCreateUserAfterCheckOut("testCreateUserInvalidOrderCheckout"));
		suite.addTest(new TestCreateUserAfterCheckOut("testCreateUserInvalidPassword"));

		suite.addTest(new TestGetAllCreditCards("testGetAllCreditCard"));
		suite.addTest(new TestGetAllCreditCards("testGetAllCreditCardLoggedIn"));
		//suite.addTest(new TestGetAllAppliedCoupons("testGetApplyCoupon"));
		//suite.addTest(new TestGetAllAppliedCoupons("testGetApplyCouponError"));
		suite.addTest(new TestPlaceCurrentOrder("testPlaceCurrentOrderLoggedIn"));
		suite.addTest(new TestPlaceCurrentOrder("testPlaceCurrentOrderForAnonymousUser"));
		//suite.addTest(new TestPlaceCurrentOrder("testPlaceCurrentOrderError"));
		suite.addTest(new TestGetBridalToolkitRegistriesAPI("testGetBridalToolkitRegistries"));
		suite.addTest(new TestGetBridalToolkitRegistriesAPI("testGetBridalToolkitRegistriesError"));

		suite.addTest(new TestRestGetItemInventory("testGetItemsInventoryLoggedinUser"));
		suite.addTest(new TestRestGetItemInventory("testGetItemsInventoryAnonymousUser"));
		suite.addTest(new TestRestGetItemInventory("testGetItemsInventoryError"));


		suite.addTest(new TestSearchInStore("testSearchInStorePostal"));
		suite.addTest(new TestSearchInStore("testSearchStorePageKey"));
		suite.addTest(new TestSearchInStore("testSearchInStoreByError"));


		suite.addTest(new TestSearchStore("testSearchStorePostal"));
		suite.addTest(new TestSearchStore("testSearchStorePageKey"));
		suite.addTest(new TestSearchStore("testSearchStoreError"));

		suite.addTest(new TestGetProfileRegistryList("testGetRegistryProfileListLoggedInFlow"));
		suite.addTest(new TestGetProfileRegistryList("testGetRegistryProfileList"));

		suite.addTest(new TestRestHomePageContent("testRestHomePageContent"));
		suite.addTest(new TestNavigationLinks("testNavigationLinks"));

		suite.addTest(new TestRestCategoryPageContent("testCategoryPageContent"));
		suite.addTest(new TestGetBreadcrumb("testGetBreadcrumb"));
		suite.addTest(new TestBBBEmailFetchDroplet("testEmailFetchContent"));

		suite.addTest(new TestSEOTag("testSeoTag"));

		suite.addTest(new TestProductBreadCrumb("testProductBreadCrumbForProduct"));
		suite.addTest(new TestProductBreadCrumb("testProductBreadCrumbForCategory"));

		suite.addTest(new TestEmailCart("testEmailCart"));

		suite.addTest(new TestMoveToPayment("testMoveToPayment"));
		suite.addTest(new TestPopulateState("testGetState"));
		suite.addTest(new TestRestExpressCheckoutCondition("testGetExpressCheckoutCondition"));
		suite.addTest(new TestRestExpressCheckoutForOrder("testGetExpressCheckoutforOrder"));
		suite.addTest(new TestRestVerifiedByVisaLookup("testRestVerifiedByVisaLookup"));

		//International Shipping

		suite.addTest(new TestInternationalShipFormHandler("testCartValidate"));
		suite.addTest(new TestInternationalShipFormHandler("testEnvoyCartValidate"));
		suite.addTest(new TestCheckoutHelper("testCheckoutHelper"));
		suite.addTest(new TestBuildContext("testBuildContextFromIPSuccess"));
		suite.addTest(new TestBuildContext("testBuildContextFromIPError"));
		suite.addTest(new TestBuildContext("testBuildContextOnCountryCodeSuccess"));
		suite.addTest(new TestBuildContext("testBuildContextOnCountryCodeError"));
		suite.addTest(new TestBuildContext("testBuildContextAllSuccess"));

		suite.addTest(new TestMultiShippingToOrder("testAddMultiShippingToOrder"));
		suite.addTest(new TestMultiShippingToOrder("testAddMultiShippingToOrderError"));

		suite.addTest(new TestShipToMultiplePeople("testSplitCurrentItem"));
		suite.addTest(new TestShipToMultiplePeople("testSplitCurrentItemError"));
		suite.addTest(new TestValidateToken("testValidateToken"));
		suite.addTest(new TestPersistRecommenderReln("testPersistRecommenderReln"));

		suite.addTest(new TestGiftRegistryRecommendation("testGiftRegistryRecommendation"));
		suite.addTest(new TestGiftRegistryRecommendation("testGiftRegistryRecommendationException"));
		return suite;

	}

}
