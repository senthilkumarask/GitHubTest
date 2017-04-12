package com.bbb.internationalshipping.droplet;



import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupManager;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBSystemException;
import com.bbb.internationalshipping.manager.InternationalCheckoutManager;
import com.bbb.internationalshipping.utils.InternationaShippingCheckoutHelper;
import com.bbb.internationalshipping.vo.BBBInternationalOrderSubmitVO;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.utils.BBBUtility;


/**
 * The Class InternationalOrderEnvoyDroplet.
 */
public class InternationalOrderEnvoyDroplet extends BBBDynamoServlet{

	private static final String OUTPUT = "output";

	/** The m user profile. */
	private String mUserProfile;
	
	/** The m shopping cart. */
	private String mShoppingCart;
	
	/** The m order manager. */
	private BBBOrderManager mOrderManager;
	private final String ORDERID="orderId";
	private final String MERCHANTORDERID="merchantOrderId";
	private final String FRAUDSTATE="fraudState";
	private final String COUNTRYCODE="countryCode";
	private final String CURRENCYCODE="currencyCode";
	private final String SUCCESS="success";
	private final String PENDING="pending";
	private final String OMNITUREPRODUCTSTRING = "omnitureProductString";
	private BBBPricingTools pricingTools;
	public BBBPricingTools getPricingTools() {
		return pricingTools;
	}

	public void setPricingTools(BBBPricingTools pricingTools) {
		this.pricingTools = pricingTools;
	}

	OrderHolder orderShoppingCart;
	
	public OrderHolder getOrderShoppingCart() {
		return orderShoppingCart;
	}

	public void setOrderShoppingCart(OrderHolder orderShoppingCart) {
		this.orderShoppingCart = orderShoppingCart;
	}

	/**
	 * Gets the user profile.
	 *
	 * @return the user profile
	 */
	public String getUserProfile() {
		return mUserProfile;
	}

	/**
	 * Sets the user profile.
	 *
	 * @param mUserProfile the new user profile
	 */
	public void setUserProfile(String mUserProfile) {
		this.mUserProfile = mUserProfile;
	}

	/**
	 * Gets the shopping cart.
	 *
	 * @return the shopping cart
	 */
	public String getShoppingCart() {
		return mShoppingCart;
	}

	/**
	 * Sets the shopping cart.
	 *
	 * @param mShoppingCart the new shopping cart
	 */
	public void setShoppingCart(String mShoppingCart) {
		this.mShoppingCart = mShoppingCart;
	}

	/**
	 * Gets the order manager.
	 *
	 * @return the order manager
	 */
	public BBBOrderManager getOrderManager() {
		return mOrderManager;
	}

	/**
	 * Sets the order manager.
	 *
	 * @param mOrderManager the new order manager
	 */
	public void setOrderManager(BBBOrderManager mOrderManager) {
		this.mOrderManager = mOrderManager;
	}

	private InternationaShippingCheckoutHelper helper;
	
	/**
	 * Gets the helper.
	 *
	 * @return the helper
	 */
	public InternationaShippingCheckoutHelper getHelper() {
		return helper;
	}

	/**
	 * Sets the helper.
	 *
	 * @param helper the new helper
	 */
	public void setHelper(InternationaShippingCheckoutHelper helper) {
		this.helper = helper;
	}

	/** The intl checkout manager. */
	private InternationalCheckoutManager intlCheckoutManager;

	
	public InternationalCheckoutManager getIntlCheckoutManager() {
		return intlCheckoutManager;
	}

	public void setIntlCheckoutManager(
			InternationalCheckoutManager intlCheckoutManager) {
		this.intlCheckoutManager = intlCheckoutManager;
	}

	/**
	 * 
	 */
	public void service(DynamoHttpServletRequest request,
			DynamoHttpServletResponse response)
	throws javax.servlet.ServletException, java.io.IOException {
		
		logDebug("Entering class: InternationalOrderEnvoyDroplet,  "
				+ "method : service");
			
		StringBuffer orderLogMessage = new StringBuffer();
		String message = null;
		
		String orderId = (String) request.getObjectParameter(ORDERID);
		String merchantOrderId = (String) request.getObjectParameter(MERCHANTORDERID);
		String fraudState = (String) request.getObjectParameter(FRAUDSTATE);
		String countrycode=(String) request.getObjectParameter(COUNTRYCODE);
		String currencyCode=(String) request.getObjectParameter(CURRENCYCODE);
		message = "Inside class: InternationalOrderEnvoyDroplet : Order ID ->" + orderId + "Merchant Order ID ->" + merchantOrderId + " FraudState ->"
		+ fraudState + "Country Code ->" + countrycode + "Currency Code" + currencyCode;
		orderLogMessage.append(message+ "\n");
		logDebug(message);
		Order order = getOrderShoppingCart().getCurrent();
		List<BBBCommerceItem> comItems=null;
		AddCommerceItemInfo[] atComInfo=null;
		if  (order != null) {
			 comItems = order.getCommerceItems();
			 atComInfo = new AddCommerceItemInfo[comItems
				.size()];
			int index = 0;
			for (BBBCommerceItem comItem : comItems) {
				atComInfo[index] = new AddCommerceItemInfo();
				atComInfo[index].setProductId(comItem.getAuxiliaryData()
						.getProductId());
				atComInfo[index].setCatalogRefId(comItem.getCatalogRefId());
				atComInfo[index].setQuantity(comItem.getQuantity());
				index++;
			}
		}
		
		if(BBBUtility.isNotEmpty(orderId)
				&& BBBUtility.isNotEmpty(merchantOrderId)
				&& BBBUtility.isNotEmpty(fraudState) 
				&& BBBUtility.isNotEmpty(countrycode)
				&& BBBUtility.isNotEmpty(currencyCode)) {
			try {
//				StringBuilder omnitureProductString = new StringBuilder();
				BBBInternationalOrderSubmitVO orderSubmitVO = new BBBInternationalOrderSubmitVO();
//				this.getHelper().persistOrderFromXml(orderId, merchantOrderId, fraudState, countrycode, currencyCode, omnitureProductString);
				this.getHelper().persistOrderFromXml(orderId, merchantOrderId, fraudState, countrycode, currencyCode, orderSubmitVO);
				String omnitureProductString = orderSubmitVO.getOmnitureProductString();
				if(!BBBUtility.isEmpty(omnitureProductString)){
					request.setParameter(OMNITUREPRODUCTSTRING, omnitureProductString);
				}
			if(fraudState.equalsIgnoreCase(SUCCESS) || fraudState.equalsIgnoreCase(PENDING))
			{
			
					intlCheckoutManager.clearCartCookies(request, response);
					final Profile profile = (Profile) request.resolveName(getUserProfile());
					OrderHolder cart = (OrderHolder) request.resolveName(getShoppingCart());
					BBBOrder newOrder;

					newOrder = (BBBOrder) getOrderManager().createOrder(profile.getRepositoryId(),
							getOrderManager().getOrderTools().getDefaultOrderType());


					
					logDebug(LogMessageFormatter.formatMessage(request, "New order created with id:" + newOrder));
					

					cart.setCurrent(newOrder);
				
				}

			else
			{
			  logInfo("New order id..."+getOrderShoppingCart().getCurrent().getId());
			  order = getOrderShoppingCart().getCurrent();
			  if(!orderId.equalsIgnoreCase(order.getId()) && order.getCommerceItemCount()==0)
			  {
				boolean shouldRollback = false;
				TransactionDemarcation td2 = new TransactionDemarcation();
				try {
					td2.begin(getOrderManager().getOrderTools().getTransactionManager());
					synchronized (order) {
						for (AddCommerceItemInfo basicComInfo : atComInfo) {
							BBBCommerceItem ci = (BBBCommerceItem)getOrderManager()
									.getCommerceItemManager().createCommerceItem(
											basicComInfo.getCatalogRefId(),
											basicComInfo.getProductId(),
											basicComInfo.getQuantity());
							getOrderManager().getCommerceItemManager().addItemToOrder(order, ci);
							//addAsSeparateItemToOrder(order, ci);
						}
						if(order.getCommerceItems() != null)
						{
							/* add a default payment group to the order */
							getOrderManager().getPaymentGroupManager().removeAllPaymentGroupsFromOrder(order);
							PaymentGroup defaultPaymentGroup =getOrderManager().getPaymentGroupManager().createPaymentGroup();
							getOrderManager().getPaymentGroupManager().addPaymentGroupToOrder(order, defaultPaymentGroup);

							List<BBBCommerceItem> commerceItems = order.getCommerceItems();

							final ShippingGroupManager shippingGroupManager = getOrderManager().getShippingGroupManager();
							shippingGroupManager.removeAllShippingGroupsFromOrder(order);
							/* add a default shipping group to the order */
							ShippingGroup defaultShippingGroup = shippingGroupManager.createShippingGroup();
							shippingGroupManager.addShippingGroupToOrder(order, defaultShippingGroup);

							for (BBBCommerceItem ci : (List<BBBCommerceItem>) order.getCommerceItems()) {
								if (ci.getShippingGroupRelationshipCount() < 1) {
									getOrderManager().getCommerceItemManager().addItemQuantityToShippingGroup(order, ci.getId(),
											defaultShippingGroup.getId(),ci.getQuantity());
								}
							}

						}
						
						this.getPricingTools().priceOrderTotal(order);
						getOrderManager().updateOrder(order);
					}
				} catch (CommerceException e) {
					shouldRollback = true;
					vlogError("Inside Exception: Exception occured while updating order" + e);
					
				} catch (TransactionDemarcationException e) {
					shouldRollback = true;
					vlogError("Inside Exception: Exception occured while updating order" + e);
				} finally {
					try {
						td2.end(shouldRollback);
					} catch (TransactionDemarcationException e) {
						this.logError("Transaction roll back error", e);
					}
				}
				
				
			
			}
			if (!BBBUtility.isEmpty(countrycode) && !countrycode.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY)) {
				getIntlCheckoutManager().populateThirdPartyVO(order, orderSubmitVO);
		 }
		 }
			request.setParameter("orderSubmitVO", orderSubmitVO);
		} catch (CommerceException e) {
			
			logError(LogMessageFormatter.formatMessage(request, "Exception while creating the new order" ), e);
			
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(request, "Exception while creating the new order" ), e);
			
		}
		}else {
			logError("Inside class: InternationalOrderEnvoyDroplet : Parameters are not valid");
		}
		request.serviceParameter(OUTPUT, request, response);
		logDebug("Exiting class: InternationalOrderEnvoyDroplet,  "
				+ "method : service");
	
	} 
	
	/**
	 * Rest call for persisting International Order
	 */
	public BBBInternationalOrderSubmitVO restPersistOrderFromXml(final String orderId, final String merchantOrderId, final String fraudState, final String countrycode2, final String currencyCode2)throws BBBSystemException
	{
		logDebug("Entering class: InternationalOrderEnvoyDroplet,  "
				+ "method : restPersistOrderFromXml");
		 DynamoHttpServletRequest 	 request=null;
		 DynamoHttpServletResponse   response=null;
		 BBBInternationalOrderSubmitVO internationalOrderSubmitVO = new BBBInternationalOrderSubmitVO();
		try {
			
			request    = ServletUtil.getCurrentRequest();
			response   = ServletUtil.getCurrentResponse();
			request.setParameter(ORDERID,orderId);
			request.setParameter(MERCHANTORDERID,merchantOrderId);
			request.setParameter(FRAUDSTATE,fraudState);
			request.setParameter(COUNTRYCODE,countrycode2);
			request.setParameter(CURRENCYCODE,currencyCode2);
			this.service(request,response);
			internationalOrderSubmitVO.setOmnitureProductString(request.getParameter(OMNITUREPRODUCTSTRING));
			internationalOrderSubmitVO = (BBBInternationalOrderSubmitVO) request.getObjectParameter("orderSubmitVO");
		} catch (ServletException e) {
			logError(LogMessageFormatter.formatMessage(request, "Exception while creating the new order" ), e);
			throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error while creating order from xml", e);
		} catch (IOException e) {
			logError(LogMessageFormatter.formatMessage(request, "Exception while creating the new order" ), e);
			throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error while creating order from xml", e);
		}
		return internationalOrderSubmitVO;
	}
	
}
