package com.bbb.commerce.checkout.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import atg.core.util.Address;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.api.BBBCreditCardAPIImpl;
import com.bbb.commerce.checkout.BBBCreditCardContainer;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.common.BasicBBBCreditCardInfo;
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBAccountConstants;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.rest.checkout.vo.CreditCardInfoVO;
import com.bbb.rest.output.BBBRestDozerBeanProvider;
import com.bbb.utils.BBBUtility;

/**
 * To populate credit cards from the profile and from the order.
 *
 * @author sdandriyal
 * @story UC_Checkout_Payment (CreditCard)
 * @version 1.0
 */
/**
 * @author sdandr
 *
 */
public class GetCreditCardsForPayment extends BBBDynamoServlet{

	/**
	 * Constant for BBBCreditCardAPIImpl
	 */
	private BBBCreditCardAPIImpl creditCardAPIImpl;

	/**
	 * Constant for BBBCreditCardContainer
	 */
	private BBBCreditCardContainer creditCardContainer;

	/**
	 * Constant for BBBPurchaseProcessHelper
	 */
	private BBBPurchaseProcessHelper purchaseProcessHelper;


	private BBBRestDozerBeanProvider dozerBean;
	public BBBRestDozerBeanProvider getDozerBean() {
		return dozerBean;
	}

	public void setDozerBean(BBBRestDozerBeanProvider dozerBean) {
		this.dozerBean = dozerBean;
	}

	public static final String OUTPUT_ERROR_MSG = "errorMsg";


	/**
	 * This method performs the following tasks;
	 * 1.	If user is authenticated, get the list of credit card from Profile. Add the list to BBBCreditCardContainer map
	 * 2. 	Get the Credit Card from Order, if user has entered before and coming back to Payment Page
	 * 3. 	Check the container (sourceKey) to see if Order Credit Card is a copy of Profile Credit Card. If yes,
	 *  	then remove the Profile Credit Card from container and add the Credit Card from Order to Container
	 *
	 * @param DynamoHttpServletRequest, DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException, IOException
	 */
	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response) throws ServletException, IOException {

	    BBBPerformanceMonitor.start("GetCreditCardsForPayment", "service");
		logDebug("Entry GetCreditCardsForPayment.service");
		//String selectedId = null;
		Profile profile = (Profile) request.getObjectParameter("Profile");
		BBBOrderImpl order = (BBBOrderImpl) request.getObjectParameter("Order");
		//BBBCreditCardContainer creditCardContainer = getCreditCardContainer();
		BBBCreditCardContainer creditCardContainer = (BBBCreditCardContainer) request.getObjectParameter("CreditCardContainer");
		try {
			processCrediCardContainer(request, profile, order, creditCardContainer);
			request.serviceParameter(BBBAccountConstants.OPARAM_OUTPUT, request, response);
			
		} catch (BBBSystemException exception) {
			 if (isLoggingError()) {
				 logError(LogMessageFormatter.formatMessage(request, "Error Occured display saved credit cards:"), exception);
	
			 }
			 request.setParameter(OUTPUT_ERROR_MSG, exception.getMessage());
		} catch (BBBBusinessException exception) {
			 if (isLoggingError()) {
				 logError(LogMessageFormatter.formatMessage(request, "Error Occured display saved credit cards:"), exception);
			 }
			 request.setParameter(OUTPUT_ERROR_MSG, exception.getMessage());
		}
		BBBPerformanceMonitor.end("GetCreditCardsForPayment", "service");
	}
	

	protected void processCrediCardContainer(final DynamoHttpServletRequest request,
			Profile profile, BBBOrderImpl order,
			BBBCreditCardContainer creditCardContainer) throws ServletException, IOException, BBBSystemException, BBBBusinessException {
		String selectedId = null;
		

			

			List<BasicBBBCreditCardInfo> creditCardInfoList = extractGetCreditCardFromOrder(order);
			//commiting if condition as duplicate card is being displayed at checkout during placing order with same card.
			//if(!creditCardInfoList.isEmpty()) {
				creditCardContainer.initialize();
			//}
			for (BasicBBBCreditCardInfo bbbCreditCardInfo : creditCardInfoList) {
				creditCardContainer.addCreditCardToContainer(bbbCreditCardInfo.getPaymentId(), bbbCreditCardInfo);
				if(! bbbCreditCardInfo.getExpired()){
					selectedId = bbbCreditCardInfo.getPaymentId();
				}
			}

			List<BasicBBBCreditCardInfo> basicBBBCreditCardInfoList = getCreditCardAPIImpl().getUserCreditCardWallet(profile, SiteContextManager.getCurrentSiteId());
			for (BasicBBBCreditCardInfo bbbCreditCardInfo : basicBBBCreditCardInfoList) {

				if (!creditCardInfoList.isEmpty() && compareDuplicateCreditCard(bbbCreditCardInfo, creditCardInfoList.get(0))) {
					logDebug("Duplicate Credit Card found No Action Required");
				} else {
					if (StringUtils.isBlank(selectedId) && bbbCreditCardInfo.isDefault()) {
						selectedId = bbbCreditCardInfo.getPaymentId();
					}

					String ccKey = bbbCreditCardInfo.getPaymentId();
					creditCardContainer.addCreditCardToContainer(ccKey, bbbCreditCardInfo);
				}
			}

			//Removing redundant code getOrderBanalace(order). Content in this method
			//getOrderBanalace(order) was commented out and was always returning  0.0
			request.setParameter(BBBCheckoutConstants.CREDIT_CARD_AMOUNT, 0.0);
			request.setParameter(BBBCheckoutConstants.CREDIT_CARD_INFO,basicBBBCreditCardInfoList);
			request.setParameter(BBBCheckoutConstants.SELECTED_ID, selectedId);
			

		
	}

	protected List<BasicBBBCreditCardInfo> extractGetCreditCardFromOrder(BBBOrderImpl order) {
		return getPurchaseProcessHelper().getCreditCardFromOrder(order);
	}

	/**
	 * This method performs the following tasks for API
	 * 1.	If user is authenticated, get the list of credit card from Profile. Add the list to BBBCreditCardContainer map
	 * 2. 	Get the Credit Card from Order, if user has entered before and coming back to Payment Page
	 * 3. 	Check the container (sourceKey) to see if Order Credit Card is a copy of Profile Credit Card. If yes,
	 *  	then remove the Profile Credit Card from container and add the Credit Card from Order to Container
	 *
	 * @param DynamoHttpServletRequest, DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException, IOException
	 * @throws BBBSystemException
	 */

	public CreditCardInfoVO getAllCreditCard(BBBOrder bbbOrder, Profile profile) throws ServletException, IOException, BBBSystemException {

		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
		RepositoryView view = null;
		RqlStatement statement = null;
		Object params[] = new Object[1];
		RepositoryItem[] items = null;
		Map<String,BasicBBBCreditCardInfo> allCardInfoList=new HashMap<String, BasicBBBCreditCardInfo>();
		logDebug("Entry GetCreditCardsForPayment.allCrediCardAPT");
		BBBCreditCardContainer creditCardContainer = new BBBCreditCardContainer();
		request.setParameter("Profile", profile);
		//retrieve credit-card view from profile repository to get billing address
		try {
			view = profile.getRepository().getView("credit-card");
			statement = RqlStatement.parseRqlStatement("id=?0");
		} catch (RepositoryException e) {
			logError("Repository Exception while retrieving credit-card view from profile");
		}
		//request.setParameter("Order", bbbOrder);
		//request.setParameter("CreditCardContainer", creditCardContainer);

			//service(request,response);
		CreditCardInfoVO cardInfoVO=new CreditCardInfoVO();
		try {
			processCrediCardContainer(request, profile, (BBBOrderImpl) bbbOrder, creditCardContainer);
			String orderBalance=request.getParameter("creditCardAmount");
			String selectedId=request.getParameter("selectedId");
			logDebug("End GetCreditCardsForPayment.allCrediCardAPT");
			// populate credit card list
			Map<String,BasicBBBCreditCardInfo> creditCardInfoList=creditCardContainer.getCreditCardMap();
	
			Iterator<Map.Entry<String, BasicBBBCreditCardInfo>> entries = creditCardInfoList.entrySet().iterator();
			while (entries.hasNext()) {
				BasicBBBCreditCardInfo basicBBBCreditCardInfo=new BasicBBBCreditCardInfo();
				Map.Entry<String, BasicBBBCreditCardInfo> entry = entries.next();
				basicBBBCreditCardInfo=entry.getValue();
				params[0] = basicBBBCreditCardInfo.getPaymentId();
				BBBRepositoryContactInfo contactInfo = null;
				Address billAddr = new Address();
				//get billing address for given credit card number on the basis of credit card id
				try {
					if(null != view){
						items = extractExecuteQuery(view, statement, params);
						if(items != null && items[0] != null){
							RepositoryItem item = (RepositoryItem)items[0].getPropertyValue(BBBCoreConstants.BILLING_ADDRESS);
							contactInfo = new BBBRepositoryContactInfo((MutableRepositoryItem) item);
							billAddr.setAddress1(contactInfo.getAddress1());
							billAddr.setAddress2(contactInfo.getAddress2());
							billAddr.setAddress3(contactInfo.getAddress3());
							billAddr.setCity(contactInfo.getCity());
							billAddr.setState(contactInfo.getState());
							billAddr.setCountry(contactInfo.getCountry());
							billAddr.setCounty(contactInfo.getCounty());
							billAddr.setPostalCode(contactInfo.getPostalCode());
							billAddr.setOwnerId(contactInfo.getOwnerId());
							billAddr.setFirstName(contactInfo.getFirstName());
							billAddr.setMiddleName(contactInfo.getMiddleName());
							billAddr.setLastName(contactInfo.getLastName());
							billAddr.setPrefix(contactInfo.getPrefix());
							billAddr.setSuffix(contactInfo.getSuffix());
						}
						else{
							logDebug("getAllCreditCard :: No credit card exist with credit card number: " + basicBBBCreditCardInfo.getCreditCardNumber() + "credit-card Id: " + basicBBBCreditCardInfo.getPaymentId());}
					}
				} catch (RepositoryException e) {
					logError("getAllCreditCard :: Repository Exception while retrieving billing Address for credit card number: " + basicBBBCreditCardInfo.getCreditCardNumber() + "credit-card Id: " + basicBBBCreditCardInfo.getPaymentId());
				}
	
				basicBBBCreditCardInfo.setBillingAddress(billAddr);
				basicBBBCreditCardInfo.setCreditCardNumber(BBBUtility.maskCrediCardNumber(basicBBBCreditCardInfo.getCreditCardNumber()));
				basicBBBCreditCardInfo.setCardVerificationNumber(null);
				basicBBBCreditCardInfo.setExpirationMonth(BBBUtility.stripExpirationMonth(basicBBBCreditCardInfo.getExpirationMonth()));
				basicBBBCreditCardInfo.setExpirationYear(BBBUtility.stripExpirationYear(basicBBBCreditCardInfo.getExpirationYear()));
				allCardInfoList.put(entry.getKey(), basicBBBCreditCardInfo);
			}
			
			cardInfoVO.setCreditCardInfoList(allCardInfoList);
			cardInfoVO.setSelectedId(selectedId);
			cardInfoVO.setOrderBalance(null);

			
		} catch (BBBSystemException exception) {
			 if (isLoggingError()) {
				 logError(LogMessageFormatter.formatMessage(request, "Error Occured display saved credit cards:"), exception);
	
			 }
			 throw new BBBSystemException("getCreditCardError:1073","Error Occurred while fetching saved credit cards details");
		} catch (BBBBusinessException exception) {
			 if (isLoggingError()) {
				 logError(LogMessageFormatter.formatMessage(request, "Error Occured display saved credit cards:"), exception);
			 }
			 throw new BBBSystemException("getCreditCardError:1073","Error Occurred while fetching saved credit cards details");
		}
			
			

		return cardInfoVO;
	}

	protected RepositoryItem[] extractExecuteQuery(RepositoryView view, RqlStatement statement, Object[] params)
			throws RepositoryException {
		return statement.executeQuery(view, params);
	}


	/** This method is to compare the given two BasicBBBCreditCardInfo objects.
	 * It compares the card number, expiration month and expiration year.
	 * @param basicBBBCreditCardInfoList
	 * @param creditCardInfo
	 * @return
	 */
	private boolean compareDuplicateCreditCard(BasicBBBCreditCardInfo basicBBBCreditCardInfoList, BasicBBBCreditCardInfo creditCardInfo) {

		Boolean flag = Boolean.FALSE;
		if (basicBBBCreditCardInfoList != null && creditCardInfo != null
		        && StringUtils.equals(basicBBBCreditCardInfoList.getCreditCardNumber(), creditCardInfo.getCreditCardNumber())
			        && StringUtils.equals(basicBBBCreditCardInfoList.getExpirationYear(), creditCardInfo.getExpirationYear())
			        && StringUtils.equals(basicBBBCreditCardInfoList.getExpirationMonth(), creditCardInfo.getExpirationMonth())){
						flag = Boolean.TRUE;
		}
		return flag;
	}

	/**
	 * @return the creditCardAPIImpl
	 */
	public BBBCreditCardAPIImpl getCreditCardAPIImpl() {
		return creditCardAPIImpl;
	}



	/**
	 * @param creditCardAPIImpl the creditCardAPIImpl to set
	 */
	public void setCreditCardAPIImpl(BBBCreditCardAPIImpl creditCardAPIImpl) {
		this.creditCardAPIImpl = creditCardAPIImpl;
	}

	/**
	 * @return the creditCardContainer
	 */
	public BBBCreditCardContainer getCreditCardContainer() {
		return creditCardContainer;
	}


	/**
	 * @param creditCardContainer the creditCardContainer to set
	 */
	public void setCreditCardContainer(BBBCreditCardContainer creditCardContainer) {
		this.creditCardContainer = creditCardContainer;
	}

	/**
	 * @return the purchaseProcessHelper
	 */
	public BBBPurchaseProcessHelper getPurchaseProcessHelper() {
		return purchaseProcessHelper;
	}

	/**
	 * @param purchaseProcessHelper the purchaseProcessHelper to set
	 */
	public void setPurchaseProcessHelper(
			BBBPurchaseProcessHelper purchaseProcessHelper) {
		this.purchaseProcessHelper = purchaseProcessHelper;
	}
}
