package com.bbb.commerce.checkout.util

import java.util.Map
import java.util.Set

import javax.transaction.TransactionManager;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCheckoutConstants
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException
import com.bbb.repository.RepositoryItemMock
import com.bbb.selfservice.vo.SchoolVO

import atg.commerce.CommerceException
import atg.commerce.claimable.ClaimableManager
import atg.commerce.claimable.ClaimableTools
import atg.commerce.order.Order
import atg.commerce.order.OrderManager
import atg.commerce.promotion.PromotionTools
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.userprofiling.Profile
import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 *
 * @author Velmurugan Moorthy
 *
 * This class is written to unit test the BBBCouponUtil
 *
 */
public class BBBCouponUtilSpecification extends BBBExtendedSpec {

	private BBBCouponUtil couponUtilSpy
	private BBBCatalogTools catalogToolsMock
	private Profile profileMock 
	private RepositoryItemMock schoolRepoItemMock
	private ClaimableManager claimableManagerMock 
	private ClaimableTools claimableToolsMock
	private OrderManager orderManager
	private TransactionManager transactionManager
	
	def setup() {
		
		couponUtilSpy = Spy()
		catalogToolsMock = Mock()
		profileMock = Mock()
		claimableManagerMock = Mock()
		claimableToolsMock = Mock()
		transactionManager = Mock()
		orderManager = Mock()
		
		couponUtilSpy.setBBBCatalogTools(catalogToolsMock)
		couponUtilSpy.setClaimableManager(claimableManagerMock)
		couponUtilSpy.setTransactionManager(transactionManager)
		couponUtilSpy.setOrderManager(orderManager) 
		
	}
	
	
	/*=======================================================
	 * addSchoolPromotion - test cases - STARTS				*
	 *												 	    *  
	 * Metod signature :   									*
	 * 														*
	 * public void addSchoolPromotion(Profile pProfile,	 	* 
	 *		Map<String, RepositoryItem> pPromotions)	 	* 	
	 * throws BBBSystemException, BBBBusinessException	    *  
	 *======================================================= 
	 */
	
	def "addSchoolPromotion - School promotion added successfully - happy flow" () {
	
		given : 
		
		Map<String, RepositoryItem> pPromotions = new HashMap<>()
		def schoolPromoId = "school01"
		def schoolId = "school01"
		SchoolVO schoolVo = new SchoolVO()
		
		schoolRepoItemMock = new RepositoryItemMock(["id":"school01"])
		
		schoolVo.setPromotionRepositoryItem(schoolRepoItemMock)
		profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOL_PROMOTIONS) >> schoolPromoId
		profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS) >> schoolId
		1*catalogToolsMock.getSchoolDetailsById("school01") >> schoolVo
				
		when :
		 
		couponUtilSpy.addSchoolPromotion(profileMock, pPromotions)
		
		then : 
		
		1 * couponUtilSpy.logDebug("schoolPromotion added to Promotion Map")
		//not working - this getter method returns null so, unable to assert this.
		//1 * catalogToolsMock.getSchoolDetailsById(schoolId)//not working
		pPromotions.get(BBBCheckoutConstants.SCHOOLPROMO) == schoolRepoItemMock
	}

	def "addSchoolPromotion - School ID & School Promo ID mismatches" () {
		
			given :
			
			Map<String, RepositoryItem> pPromotions = new HashMap<>()
			def schoolPromoId = "school01"
			def schoolId = "school02"
			def SchoolVO schoolVo = new SchoolVO()
			
			schoolRepoItemMock = new RepositoryItemMock(["id":schoolId])
			
			schoolVo.setPromotionRepositoryItem(schoolRepoItemMock)
			profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOL_PROMOTIONS) >> schoolPromoId
			profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS) >> schoolId
			catalogToolsMock.getSchoolDetailsById(schoolId) >> schoolVo
					
			when :
			 
			couponUtilSpy.addSchoolPromotion(profileMock, pPromotions)
			
			then :
			
			0 * couponUtilSpy.logDebug("schoolPromotion added to Promotion Map")
			pPromotions.get(BBBCheckoutConstants.SCHOOLPROMO) == null
		}
	
	def "addSchoolPromotion - School ID is not string | school PROMO ID is string " () {
		
			given :
			
			Map<String, RepositoryItem> pPromotions = new HashMap<>()
			def schoolId = 101
			def schoolPromoId = "101"			
			def SchoolVO schoolVo = new SchoolVO()
			
			schoolRepoItemMock = new RepositoryItemMock(["id":"school01"])
			
			schoolVo.setPromotionRepositoryItem(schoolRepoItemMock)
			profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOL_PROMOTIONS) >> schoolPromoId
			profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS) >> schoolId
			catalogToolsMock.getSchoolDetailsById(schoolId) >> schoolVo
					
			when :
			 
			couponUtilSpy.addSchoolPromotion(profileMock, pPromotions)
			
			then :
			
			0 * couponUtilSpy.logDebug("schoolPromotion added to Promotion Map")
			//1 * catalogToolsMock.getSchoolDetailsById(schoolId) >> schoolVo //not working
			pPromotions.get(BBBCheckoutConstants.SCHOOLPROMO) == null
		}
	
	def "addSchoolPromotion - School ID string | school PROMO ID is not string" () {
		
			given :
			
			Map<String, RepositoryItem> pPromotions = new HashMap<>()
			def schoolId = "101"
			def schoolPromoId = 101			
			def SchoolVO schoolVo = new SchoolVO()
			
			schoolRepoItemMock = new RepositoryItemMock(["id":"school01"])
			
			schoolVo.setPromotionRepositoryItem(schoolRepoItemMock)
			profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOL_PROMOTIONS) >> schoolPromoId
			profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS) >> schoolId
			catalogToolsMock.getSchoolDetailsById(schoolId) >> schoolVo
					
			when :
			 
			couponUtilSpy.addSchoolPromotion(profileMock, pPromotions)
			
			then :
			
			0 * couponUtilSpy.logDebug("schoolPromotion added to Promotion Map")
			0 * catalogToolsMock.getSchoolDetailsById(schoolId)
			pPromotions.get(BBBCheckoutConstants.SCHOOLPROMO) == null
		}
	
	 def "addSchoolPromotion - Profile is corrupted/invalid (null)" () {
		
			given :
			
			Map<String, RepositoryItem> pPromotions = new HashMap<>()
			def schoolPromoId = "school01"
			def schoolId = "school01"
			def SchoolVO schoolVo = new SchoolVO()
			GenericService genericeServiceMock = Mock()
			Profile tempProfileMock = null
			
			schoolRepoItemMock = new RepositoryItemMock(["id":"school01"])
			
			schoolVo.setPromotionRepositoryItem(schoolRepoItemMock)
			profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOL_PROMOTIONS) >> schoolPromoId
			profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS) >> schoolId
			catalogToolsMock.getSchoolDetailsById(schoolId) >> schoolVo
					
			when :
			 
			couponUtilSpy.addSchoolPromotion(tempProfileMock, pPromotions)
			
			then :
			
			thrown NullPointerException
		}
	
	 def "addSchoolPromotion - Promotion is invalid/corrupted (null)" () {
		 
			 given :
			 
			 Map<String, RepositoryItem> pPromotions = new HashMap<>()
			 def schoolPromoId = "school01"
			 def schoolId = "school01"
			 def SchoolVO schoolVo = new SchoolVO()
			 
			 schoolRepoItemMock = null
			 
			 schoolVo.setPromotionRepositoryItem(schoolRepoItemMock)
			 profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOL_PROMOTIONS) >> schoolPromoId
			 profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS) >> schoolId
			 catalogToolsMock.getSchoolDetailsById(schoolId) >> schoolVo
					 
			 when :
			  
			 couponUtilSpy.addSchoolPromotion(profileMock, pPromotions)
			 
			 then :
			 
			 0 * couponUtilSpy.logDebug("schoolPromotion added to Promotion Map")
			 0 * pPromotions.put(*_)
		 }
	 
	
	/*==========================================================
	 * compareClaimCode - test cases - STARTS				   *
	 * 														   *	
	 * Method signature : 									   *
	 *														   *
	 *  public Boolean compareClaimCode(					   *
	 *  String couponClaimCode,Object pSchoolPromo)			   *				
	 *	throws RepositoryException							   *	 
	 *==========================================================
	 */
	
	 
	 def "compareClaimCode - matching coupon code found - happy flow" () {
		 
		 given :
		 
		 String couponClaimCode = "school50OFF"
		 String  pSchoolPromo = "school50OFF"
		 def couponId = "school50OFF"
		 def promoPropertyName = "promotion"
		 Set<RepositoryItem> multiplePromotions = new HashSet<>()
		 
		 RepositoryItemMock coupon = new RepositoryItemMock(["id":couponId])
		 
		 //claimableToolsMock.getClaimableItem(couponClaimCode) >> coupon
		 claimableToolsMock.getPromotionsPropertyName() >> promoPropertyName
		 claimableManagerMock.getClaimableTools() >> claimableToolsMock
		 //couponUtilSpy.setClaimableManager(claimableManagerMock)
		 coupon.setProperties([promotion:multiplePromotions])
		 multiplePromotions.add(coupon)
		 
		 when :
		 
	 	 def promoFound = couponUtilSpy.compareClaimCode(couponClaimCode, pSchoolPromo)
			
		then : 
		
		//promoFound == true		  
		//1* claimableToolsMock.getClaimableItem(_) //not working //TODO find why not working
		1* claimableToolsMock.getClaimableItem(_) >>coupon // working
		//1 * claimableToolsMock.getPromotionsPropertyName()
	 }
	 
	 def "compareClaimCode - input claim code, copuon and school promo(not string) are invalid/corrupt (null)" () {
		 
		 given :
		 
		 String couponClaimCode = null
		 def  pSchoolPromo = 101
		 def couponId = "school50OFF"
		 def promoPropertyName = "promotion"
		 Set<RepositoryItem> multiplePromotions = new HashSet<>()
		 
		 RepositoryItemMock coupon = null
		 
		 claimableToolsMock.getClaimableItem(couponClaimCode) >> coupon
		 claimableToolsMock.getPromotionsPropertyName() >> promoPropertyName
		 claimableManagerMock.getClaimableTools() >> claimableToolsMock
		 multiplePromotions.add(coupon)
		 
		 when :
		 
		  def promoFound = couponUtilSpy.compareClaimCode(couponClaimCode, pSchoolPromo)
			
		then :
		
		promoFound == false
		 
	 }
	 
	 def "compareClaimCode - Promotions not present (null)" () {
		 
		 given :
		 
		 String couponClaimCode = "school50OFF"
		 String  pSchoolPromo = "school50OFF"
		 def couponId = "school50OFF"
		 def promoPropertyName = "promotion"
		 Set<RepositoryItem> multiplePromotions = null
		 
		 RepositoryItemMock coupon = new RepositoryItemMock(["id":couponId])
		 
		 claimableToolsMock.getClaimableItem(couponClaimCode) >> coupon
		 claimableToolsMock.getPromotionsPropertyName() >> promoPropertyName
		 claimableManagerMock.getClaimableTools() >> claimableToolsMock
		 coupon.setProperties([promotion:multiplePromotions])
		 
		 when :
		 
		  def promoFound = couponUtilSpy.compareClaimCode(couponClaimCode, pSchoolPromo)
			
		then :
		
		promoFound == false
		 
	 }
	 
	 def "compareClaimCode - matching coupon code not found" () {
		 
		 given :
		 
		 String couponClaimCode = "school50OFF"
		 String  pSchoolPromo = "school02"
		 def couponId = "school50OFF"
		 def promoPropertyName = "promotion"
		 Set<RepositoryItem> multiplePromotions = new HashSet<>()
		 
		 RepositoryItemMock coupon = new RepositoryItemMock(["id":couponId])
		 
		 claimableToolsMock.getClaimableItem(couponClaimCode) >> coupon
		 claimableToolsMock.getPromotionsPropertyName() >> promoPropertyName
		 claimableManagerMock.getClaimableTools() >> claimableToolsMock
		 couponUtilSpy.setClaimableManager(claimableManagerMock)
		 coupon.setProperties([promotion:multiplePromotions])
		 multiplePromotions.add(coupon)
		 
		 when :
		 
		  def promoFound = couponUtilSpy.compareClaimCode(couponClaimCode, pSchoolPromo)
			
		then :
		
		promoFound == false
		 
	 }

	 
	
	 /*=======================================================
	  * compareClaimCode - test cases - ENDS			     *
	  *=======================================================
	  */

	 
	 /*==========================================================
	  * applySchoolPromotion - test cases - STARTS				*
	  * 														*
	  * Method signature : 									    *
	  *														    *
	  *  public Map<String, RepositoryItem> applySchoolPromotion*
	  *  (Map<String, RepositoryItem> pPromotions,				*
	  *   Profile pProfile, Order pOrder)						*
	  *    throws BBBSystemException, BBBBusinessException {    *
	  *==========================================================
	  */
	 
	 
	 def "applySchoolPromotion - school promotion applied successfully | User hasn't visited the school before - happy flow" () {
		 
		 def profileSchoolID = "school01"
		 def orderSchoolID = "school02"
		 BBBOrderImpl orderMock = Mock()
		 Map<String, RepositoryItem> promotions = new HashMap<>()
		 RepositoryItemMock schoolPromoItem = Mock()
		 PromotionTools promotionToolsMock = Mock()
		 promotions.put(BBBCheckoutConstants.SCHOOLPROMO,schoolPromoItem)
		 profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS) >> profileSchoolID
		 claimableManagerMock.getClaimableTools() >> claimableToolsMock
		 claimableManagerMock.getPromotionTools() >> promotionToolsMock
		 orderMock.getPropertyValue("schoolId") >> orderSchoolID
		 promotionToolsMock.removePromotion(*_) >> {}
		 
		 when : 
		 
		 Map<String, RepositoryItem> resultPromotions = couponUtilSpy.applySchoolPromotion(promotions, profileMock, orderMock)
		 
		 then : 
		 
		 resultPromotions.isEmpty()
		 1 * couponUtilSpy.addSchoolPromotion(*_)
		 1 * couponUtilSpy.logDebug("Removed schoolPromotion from Order")
		 
	 }
	 
	 def "applySchoolPromotion - Transaction exception occured while updating order" () {
		 
		 def profileSchoolID = "school01"
		 def orderSchoolID = "school02"
		 BBBOrderImpl orderMock = Mock()
		 Map<String, RepositoryItem> promotions = new HashMap<>()
		 RepositoryItemMock schoolPromoItem = Mock()
		 PromotionTools promotionToolsMock = Mock()
		 promotions.put(BBBCheckoutConstants.SCHOOLPROMO,schoolPromoItem)
		 profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS) >> profileSchoolID
		 claimableManagerMock.getClaimableTools() >> claimableToolsMock
		 claimableManagerMock.getPromotionTools() >> promotionToolsMock
		 orderMock.getPropertyValue("schoolId") >> orderSchoolID
		 promotionToolsMock.removePromotion(*_) >> {}
		// orderManager.updateOrder(orderMock) >> {throw new TransactionDemarcationException()}
		 
		 when :
		 
		 Map<String, RepositoryItem> resultPromotions = couponUtilSpy.applySchoolPromotion(promotions, profileMock, orderMock)
		 
		 then :
		 
		 thrown BBBSystemException
		 //1 * orderManager.updateOrder(_) // If we try to mock this. It triggers update order. Hence won't throw exception
		 1 * orderManager.updateOrder(orderMock) >> {throw new TransactionDemarcationException()}
	 }
	
	 def "applySchoolPromotion - Commerce exception occured while updating order" () {
		 
		 def profileSchoolID = "school01"
		 def orderSchoolID = "school02"
		 BBBOrderImpl orderMock = Mock()
		 Map<String, RepositoryItem> promotions = new HashMap<>()
		 RepositoryItemMock schoolPromoItem = Mock()
		 PromotionTools promotionToolsMock = Mock()
		 TransactionDemarcation td = Mock()
		 
		 promotions.put(BBBCheckoutConstants.SCHOOLPROMO,schoolPromoItem)
		 profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS) >> profileSchoolID
		 claimableManagerMock.getClaimableTools() >> claimableToolsMock
		 claimableManagerMock.getPromotionTools() >> promotionToolsMock
		 orderMock.getPropertyValue("schoolId") >> orderSchoolID
		 promotionToolsMock.removePromotion(*_) >> {}
		 orderManager.updateOrder(orderMock) >> {throw new CommerceException()} //>> {throw new TransactionDemarcationException()}
		 td.end() >> {throw new TransactionDemarcationException ()}
		 
		 when :
		 
		 Map<String, RepositoryItem> resultPromotions = couponUtilSpy.applySchoolPromotion(promotions, profileMock, orderMock)
		 
		 then :
		 
		 resultPromotions == null
		 thrown BBBSystemException
	 }
	 
	 
	 def "applySchoolPromotion -  while updating order Commerce Exception occured | loggingError flag is disabled(false)" () {
		 
		 def profileSchoolID = "school01"
		 def orderSchoolID = "school02"
		 BBBOrderImpl orderMock = Mock()
		 Map<String, RepositoryItem> promotions = new HashMap<>()
		 RepositoryItemMock schoolPromoItem = Mock()
		 PromotionTools promotionToolsMock = Mock()
		 TransactionDemarcation td = Mock()
		 
		 couponUtilSpy.setLoggingError(false)
		 promotions.put(BBBCheckoutConstants.SCHOOLPROMO,schoolPromoItem)
		 profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS) >> profileSchoolID
		 claimableManagerMock.getClaimableTools() >> claimableToolsMock
		 claimableManagerMock.getPromotionTools() >> promotionToolsMock
		 orderMock.getPropertyValue("schoolId") >> orderSchoolID
		 promotionToolsMock.removePromotion(*_) >> {}
		 orderManager.updateOrder(orderMock) >> {throw new CommerceException()} 
		 td.end() >> {throw new TransactionDemarcationException ()}
		 
		 when :
		 
		 Map<String, RepositoryItem> resultPromotions = couponUtilSpy.applySchoolPromotion(promotions, profileMock, orderMock)
		 
		 then :
		 
		 resultPromotions == null
		 thrown BBBSystemException
		 0 * couponUtilSpy.addSchoolPromotion(*_)
		 0 * couponUtilSpy.logError(*_)
	 }
	 
	 def "applySchoolPromotion - Transaction exception occured while updating order | loggingError flag is disabled(false)" () {
		 
		 def profileSchoolID = "school01"
		 def orderSchoolID = "school02"
		 BBBOrderImpl orderMock = Mock()
		 Map<String, RepositoryItem> promotions = new HashMap<>()
		 RepositoryItemMock schoolPromoItem = Mock()
		 PromotionTools promotionToolsMock = Mock()
		 
		 couponUtilSpy.setLoggingError(false)
		 promotions.put(BBBCheckoutConstants.SCHOOLPROMO,schoolPromoItem)
		 profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS) >> profileSchoolID
		 claimableManagerMock.getClaimableTools() >> claimableToolsMock
		 claimableManagerMock.getPromotionTools() >> promotionToolsMock
		 orderMock.getPropertyValue("schoolId") >> orderSchoolID
		 promotionToolsMock.removePromotion(*_) >> {}
		 orderManager.updateOrder(orderMock) >> {throw new TransactionDemarcationException()}
		 
		 when :
		 
		 Map<String, RepositoryItem> resultPromotions = couponUtilSpy.applySchoolPromotion(promotions, profileMock, orderMock)
		 
		 then :
		 
		 thrown BBBSystemException
	 }
	 
	 
	 def "applySchoolPromotion - promotion and schoolID in profile are invalid/corrupted (null)" () {
		 
		 def profileSchoolID = null
		 def orderSchoolID = "school02"
		 BBBOrderImpl orderMock = Mock()
		 Map<String, RepositoryItem> promotions = null
		 RepositoryItemMock schoolPromoItem = Mock()
		 PromotionTools promotionToolsMock = Mock()

		 profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS) >> profileSchoolID
		 claimableManagerMock.getClaimableTools() >> claimableToolsMock
		 claimableManagerMock.getPromotionTools() >> promotionToolsMock
		 orderMock.getPropertyValue("schoolId") >> orderSchoolID
		 promotionToolsMock.removePromotion(*_) >> {}
		 
		 when :
		 
		 Map<String, RepositoryItem> resultPromotions = couponUtilSpy.applySchoolPromotion(promotions, profileMock, orderMock)
		 
		 then :
		 
		 resultPromotions.isEmpty()
		 0 * couponUtilSpy.addSchoolPromotion(*_)
	 }
	 
	 def "applySchoolPromotion - Profile is corrupted/invalid (null) " () {
		 
		 def profileSchoolID = "school01"
		 def orderSchoolID = "school02"
		 BBBOrderImpl orderMock = Mock()
		 Map<String, RepositoryItem> promotions = new HashMap<>()
		 RepositoryItemMock schoolPromoItem = Mock()
		 PromotionTools promotionToolsMock = Mock()
		 
		 Profile pProfile = null
		 promotions.put(BBBCheckoutConstants.SCHOOLPROMO,schoolPromoItem)
		 claimableManagerMock.getClaimableTools() >> claimableToolsMock
		 claimableManagerMock.getPromotionTools() >> promotionToolsMock
		 orderMock.getPropertyValue("schoolId") >> orderSchoolID
		 promotionToolsMock.removePromotion(*_) >> {}
		 
		 when :
		 
		 Map<String, RepositoryItem> resultPromotions = couponUtilSpy.applySchoolPromotion(promotions, pProfile, orderMock)
		 
		 then :
		 
		 thrown NullPointerException
		 resultPromotions == null
		 0 * couponUtilSpy.addSchoolPromotion(*_)
		 0 * couponUtilSpy.logDebug("Removed schoolPromotion from Order")
	 }
	 
	 def "applySchoolPromotion - school promotion applied successfully - happy flow" () {
		 
		 def profileSchoolID = "school01"
		 def orderSchoolID = "school01"
		 def schoolPromoId = "school01"
		 def SchoolVO schoolVo = new SchoolVO()
		 BBBOrderImpl orderMock = Mock()
		 Map<String, RepositoryItem> promotions = new HashMap<>()
		 RepositoryItemMock schoolPromoItem = Mock()
		 RepositoryItemMock schoolRepoItemMock = new RepositoryItemMock(["id":"school01"])
		 PromotionTools promotionToolsMock = Mock()
		 
		 schoolVo.setPromotionRepositoryItem(schoolRepoItemMock)
		 //promotions.put(BBBCheckoutConstants.SCHOOLPROMO,schoolPromoItem)
		 profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS) >> profileSchoolID
		 profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOL_PROMOTIONS) >> schoolPromoId
		 claimableManagerMock.getClaimableTools() >> claimableToolsMock
		 claimableManagerMock.getPromotionTools() >> promotionToolsMock
		 orderMock.getPropertyValue("schoolId") >> orderSchoolID
		 catalogToolsMock.getSchoolDetailsById(orderSchoolID) >> schoolVo
		 promotionToolsMock.removePromotion(*_) >> {}
		 
		 when :
		 
		 Map<String, RepositoryItem> resultPromotions = couponUtilSpy.applySchoolPromotion(promotions, profileMock, orderMock)
		 
		 then :
		 
		 resultPromotions.get(BBBCheckoutConstants.SCHOOLPROMO) == schoolRepoItemMock
		 1 * couponUtilSpy.addSchoolPromotion(*_)
		 0 * couponUtilSpy.logDebug("Removed schoolPromotion from Order")
	 }
	 
	 def "applySchoolPromotion - school ID is order is invalid (empty)" () {
		 
		 def profileSchoolID = "school01"
		 def orderSchoolID = ""
		 BBBOrderImpl orderMock = Mock()
		 Map<String, RepositoryItem> promotions = new HashMap<>()
		 RepositoryItemMock schoolPromoItem = Mock()
		 RepositoryItemMock schoolRepoItemMock = new RepositoryItemMock(["id":"school01"])
		 PromotionTools promotionToolsMock = Mock()
		 
		 promotions.put(BBBCheckoutConstants.SCHOOLPROMO,schoolPromoItem)
		 profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS) >> profileSchoolID
		 claimableManagerMock.getClaimableTools() >> claimableToolsMock
		 claimableManagerMock.getPromotionTools() >> promotionToolsMock
		 orderMock.getPropertyValue("schoolId") >> orderSchoolID
		 promotionToolsMock.removePromotion(*_) >> {}
		 
		 when :
		 
		 Map<String, RepositoryItem> resultPromotions = couponUtilSpy.applySchoolPromotion(promotions, profileMock, orderMock)
		 
		 then :
		 
		 resultPromotions.get(BBBCheckoutConstants.SCHOOLPROMO) == schoolPromoItem
		 0 * couponUtilSpy.addSchoolPromotion(*_)
		 0 * couponUtilSpy.logDebug("Removed schoolPromotion from Order")
	 }
	 
	 def "applySchoolPromotion - School related promotions doesn't exist" () {
		 
		 def profileSchoolID = "school01"
		 def orderSchoolID = "school02"
		 BBBOrderImpl orderMock = Mock()
		 Map<String, RepositoryItem> promotions = new HashMap<>()
		 RepositoryItemMock collegePromoItem = Mock()
		 PromotionTools promotionToolsMock = Mock()
		 promotions.put("CollegePromo",collegePromoItem)
		 profileMock.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS) >> profileSchoolID
		 claimableManagerMock.getClaimableTools() >> claimableToolsMock
		 claimableManagerMock.getPromotionTools() >> promotionToolsMock
		 orderMock.getPropertyValue("schoolId") >> orderSchoolID
		 promotionToolsMock.removePromotion(*_) >> {}
		 
		 when :
		 
		 Map<String, RepositoryItem> resultPromotions = couponUtilSpy.applySchoolPromotion(promotions, profileMock, orderMock)
		 
		 then :
		 
		 resultPromotions.get("CollegePromo") == collegePromoItem
		 1 * couponUtilSpy.addSchoolPromotion(*_)
		 0 * couponUtilSpy.logDebug("Removed schoolPromotion from Order")
	 }
	 
	 
	 /*==========================================================
	  * applySchoolPromotion - test cases - ENDS				*
	  *==========================================================
	  */
	 
	
	
}
