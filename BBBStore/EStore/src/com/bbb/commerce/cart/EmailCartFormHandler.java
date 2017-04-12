package com.bbb.commerce.cart;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;




import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.browse.BBBEmailSenderFormHandler;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSavedItemsSessionBean;
import com.bbb.utils.BBBUtility;
import com.bbb.wishlist.GiftListVO;

/**
 * Form handler for sending email from the ATG Store website. <br/> The JSP form
 * that accepts the email can directly set the From, Subject and To fields or
 * use the defaults as named in the configuration of the DefaultEmailInfo. <br/>
 * When the form submits the parameters the template is used to format the email
 * and then the EmailSender sends the email. The names for the From, Subject, To
 * and Profile parameters that are sent to the Email Template are set in the
 * configuration and must match the email template. <br/>
 * 
 * @version $Id:
 *          //hosting-store/Store/main/estore/src/atg/projects/store/catalog/EmailAFriendFormHandler.java#15
 *          $$Change: 633752 $
 * @updated $DateTime: 2011/02/09 11:14:12 $$Author: rbarbier $
 */
public class EmailCartFormHandler extends BBBEmailSenderFormHandler {
 
  
  private static final String NO_ITEMS_IN_ORDER = "err_cart_no_items_in_cart";
  private static final String ERR_RECIPIENT_EMAIL_INVALID = "err_recipient_email_invalid";
  private static final String ERR_EMAIL_CART = "err_email_cart";
  private static final String NO_SAVED_ITEMS = "err_no_saved_items";
  protected static final String WISH_LIST = "wishlist";
  private BBBSavedItemsSessionBean mSavedItemsSessionBean;
  private BBBCatalogTools mCatTools;
  private Map<String,String> errorUrlMap;
	/**
 * @return the errorUrlMap
 */
public Map<String, String> getErrorUrlMap() {
	return errorUrlMap;
}

/**
 * @param errorUrlMap the errorUrlMap to set
 */
public void setErrorUrlMap(Map<String, String> errorUrlMap) {
	this.errorUrlMap = errorUrlMap;
}

/**
 * @return the fromPage
 */
public String getFromPage() {
	return fromPage;
}

/**
 * @param fromPage the fromPage to set
 */
public void setFromPage(String fromPage) {
	this.fromPage = fromPage;
}

/**
 * @return the successUrlMap
 */
public Map<String, String> getSuccessUrlMap() {
	return successUrlMap;
}

/**
 * @param successUrlMap the successUrlMap to set
 */
public void setSuccessUrlMap(Map<String, String> successUrlMap) {
	this.successUrlMap = successUrlMap;
}

	private String fromPage;// Page Name set from JSP
	private Map<String,String> successUrlMap;
  /**
	 * @return the savedItemsSessionBean
	 */
	public BBBSavedItemsSessionBean getSavedItemsSessionBean() {
		return mSavedItemsSessionBean;
	}

	/**
	 * @param pSavedItemsSessionBean the savedItemsSessionBean to set
	 */
	public void setSavedItemsSessionBean(BBBSavedItemsSessionBean pSavedItemsSessionBean) {
		mSavedItemsSessionBean = pSavedItemsSessionBean;
	}
  
  /**
	 * @return the catTools
	 */
	public BBBCatalogTools getCatTools() {
		return mCatTools;
	}

	/**
	 * @param pCatTools the catTools to set
	 */
	public void setCatTools(BBBCatalogTools pCatTools) {
		// changed for rest side implementation
		super.setCatalogTools(pCatTools);
		this.mCatTools = pCatTools;
		
	}

/**
   * Locale name.
   */
  private String mLocale = null;

  /**
   * shoppingcart.
   */
  private OrderHolder mShoppingCart = null;

  
  /**
   * Subject parameter name.
   */
  private String mSubjectParamName = null;

  /**
   * Locale parameter name - it represents the name of locale parameter to be
   * used in Email template.
   */
  private String mLocaleParamName = "locale";
  private String firstName;
  /**
   * 
   * @return
   */
  public String getFirstName() {
	return firstName;
  }
	/**
	 * 
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

/**
   * email Type
   */
  private String mEmailType;

  
  Profile mProfile;
  

  public Profile getProfile() {
	return mProfile;
  }

  public void setProfile(Profile profile) {
	mProfile = profile;
  }


/**
   * Gets the name of the parameter used for the Subject: field. This is
   * configured in the component property file.
   * 
   * @return the name of the parameter used for the Subject: field.
   */
  public String getSubjectParamName() {
    return mSubjectParamName;
  }

  /**
   * Sets the name of the parameter used for the Subject: field. This is
   * configured in the component property file.
   * 
   * @param pSubjectParamName -
   *          the name of the parameter used for the Subject: field.
   */
  public void setSubjectParamName(String pSubjectParamName) {
    mSubjectParamName = pSubjectParamName;
  }

  /**
   * @param pLocaleParamName -
   *          locale parameter name.
   */
  public void setLocaleParamName(String pLocaleParamName) {
    mLocaleParamName = pLocaleParamName;
  }

  /**
   * @return the value of property getEmailParamName.
   */
  public String getLocaleParamName() {
    return mLocaleParamName;
  }

  /**
   * Gets the value of the Locale: field.
   * 
   * @return the value of the locale: field.
   */
  public String getLocale() {
    return mLocale;
  }

  /**
   * Sets the value of the locale: field.
   * 
   * @param pLocale -
   *          the value of the locale: field.
   */
  public void setLocale(String pLocale) {
    mLocale = pLocale;
  }

   
  /**
   * Gets the value of the shoppingcart.
   * 
   * @return the value of the shoppingcart.
   */
  

	public OrderHolder getShoppingCart() {
		return mShoppingCart;
	}
  	
  
  /**
   * Sets the value of the shoppingcart: field.
   * 
   * @param shoppingcart -
   *          the value of the shoppingcart.
   */
  	
	public void setShoppingCart(OrderHolder pShoppingCart) {
		mShoppingCart = pShoppingCart;
	}
	
	
	

	public String getEmailType() {
		return mEmailType;
	}
	
	public void setEmailType(String pEmailType) {
		this.mEmailType = pEmailType;
	}

		/* (non-Javadoc)
		 * @see com.bbb.browse.BBBEmailSenderFormHandler#getSiteId()
		 */
		@Override
		public String getSiteId() {			
			String siteId = super.getSiteId();
			if(siteId == null && SiteContextManager.getCurrentSiteId() != null)
			{
				return SiteContextManager.getCurrentSiteId();
			}else{
				return siteId;
			}
		}



/*// property: StoreIdParamName -----------
  private String mStoreIdParamName="storeId";

  *//**
  * The storeId parameter name - it represents the Store from which the email is dispatched
  * parameter to be used in Email template
  * @param pStoreIdParamName storeId parameter name
  *//*
  public void setStoreIdParamName(String pStoreIdParamName)
  {
    mStoreIdParamName = pStoreIdParamName;
  }

  *//**
  * @return the value of property StoreIdParamName
  *//*
  public String getStoreIdParamName()
  {
    return mStoreIdParamName;
  }

  //-------------------------------------

  //property: StoreId -----------
  private String mStoreId=null;

  *//**
   * Gets the value of the StoreId: field.
   * @return
   *  The value of the StoreId: field.
   *//*
  public String getStoreId() {
    return mStoreId;
  }

  *//**
   * Sets the value of the StoreId: field.
   * @param pStoreId
   *  The value of the StoreId: field.
   *//*
  public void setStoreId(String pStoreId) {
    mStoreId = pStoreId;
  }*/
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected Map collectParams(DynamoHttpServletRequest pRequest) {
    // collect params from form handler to map and pass them into tools class
    Map emailParams = super.collectParams(pRequest);
    SKUVO skuVO = null;
   
    if(getShoppingCart() != null)
    {
	    Order order = getShoppingCart().getCurrent();
		emailParams.put("order", order);
    }
    
    if(this.getEmailType().equalsIgnoreCase("EmailSavedItems"))	
	{
    	
		logDebug("EmailCartFormHandler - [collectParams] = Entered in method \n");
		
    	
		
		logDebug("EmailCartFormHandler - [collectParams] = Collecting parameters for transient user");
		
		BBBSavedItemsSessionBean savedItemsSessionBean = getSavedItemsSessionBean();
		if(savedItemsSessionBean != null)
		{
			List<GiftListVO> savedItems = savedItemsSessionBean.getItems();
			
			if(null != savedItems && savedItems.size()>0){
				for (GiftListVO savedItem:savedItems) {	
					try {
						skuVO = getCatTools().getSKUDetails(getSiteId(), savedItem.getSkuID());
					} catch (BBBSystemException e) {
						logError("Some BBBSystemException occuered while fetching product details for product Id : "+savedItem.getProdID());
					} catch (BBBBusinessException e) {
						logError("Some BBBBusinessException occuered while fetching product details for product Id : "+savedItem.getProdID());
					}
					savedItem.setSkuVO(skuVO);
				}
				emailParams.put("savedItems",savedItems);    		
			}
		}
	 }
	
	HashMap placeHolderValues = new HashMap();
	final Calendar currentDate = Calendar.getInstance();				
	long uniqueKeyDate = currentDate.getTimeInMillis();
	String emailPersistId = getProfile().getRepositoryId() + uniqueKeyDate;	
	if(!this.getProfile().isTransient()){
		this.setFirstName((String)this.getProfile().getPropertyValue(BBBCoreConstants.FIRST_NAME));
		placeHolderValues.put(BBBCoreConstants.FIRST_NAME, this.getFirstName());
	}
	placeHolderValues.put(BBBCoreConstants.EMAILTYPE, this.getEmailType());	
	placeHolderValues.put(BBBCoreConstants.FRMDATA_SITEID, getSiteId());
	placeHolderValues.put(BBBCoreConstants.EMAILFROM, this.getSenderEmail());
	placeHolderValues.put(BBBCoreConstants.EMAIL_CART_MESSAGE, this.getMessage());
	placeHolderValues.put(BBBCoreConstants.EMAIL_PERSIST_ID, emailPersistId);	
	 
	 
	emailParams.put("placeHolderValues", placeHolderValues);
	
	logDebug("EmailCartFormHandler - [collectParams] = emailParams :["+emailParams+"]");
	
	
    return emailParams;
  }

  public boolean handleEmailCart(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws BBBSystemException
  {
	  boolean result = false;
	  if(BBBUtility.isValidEmail(getRecipientEmail())){
		  try {
			result = handleSend(pRequest, pResponse);
		} catch (ServletException e) {
			throw new BBBSystemException(ERR_EMAIL_CART,"System Exception while sending Email" + e);
		} catch (IOException e) {
			throw new BBBSystemException(ERR_EMAIL_CART,"System Exception while sending Email" + e);
		}
	  }
	  else{
		  addFormException(new DropletException("Please enter valid recipient email id",ERR_RECIPIENT_EMAIL_INVALID));}
		  
	  return result;
  }
    
  /**
   * @return the URL of the success page.
   */
 /* public String getSuccessURL() {
    return super.getSuccessURL() + "&recipientName="
                + getRecipientName().trim() + "&recipientEmail="
                + getRecipientEmail().trim();
  }*/
 
  /* (non-Javadoc)
	 * @see com.bbb.browse.BBBEmailSenderFormHandler#handleSend(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public boolean handleSend(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		logDebug("EmailCartFormHandler - [handleSend] = Entered in method \n");
		String siteId = SiteContextManager.getCurrentSiteId();
		if (StringUtils.isNotEmpty(getFromPage())) {
			setSuccessURL(pRequest.getContextPath()
					+ getSuccessUrlMap().get(getFromPage()));
			setErrorURL(pRequest.getContextPath()
					+ getErrorUrlMap().get(getFromPage()));

		}
		String email = super.getRecipientEmail();
		if(StringUtils.isEmpty(email)){
			email = pRequest.getParameter("email"); 
		}
		
		if(this.getEmailType().equalsIgnoreCase("EmailSavedItems"))	
	   {
			if(super.getMessage()!=null && !StringUtils.isEmpty(email))
			{
				return super.handleSend(pRequest, pResponse);
				
			}else{
				addFormException(new DropletException(
						getMsgHandler().getErrMsg(NO_SAVED_ITEMS, "EN", null),NO_SAVED_ITEMS));
				return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
			}
		   
		}else 
		{
			Order order = getShoppingCart().getCurrent();
			
			if(order.getCommerceItemCount() > 0)
			{
				return super.handleSend(pRequest, pResponse);
				
			}else{
			addFormException(new DropletException(
					getMsgHandler().getErrMsg(NO_ITEMS_IN_ORDER, "EN", null), NO_ITEMS_IN_ORDER));
				return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
			}
		}
	}
	
	
	private LblTxtTemplateManager mMsgHandler;
	/**
	   * returns the CMS message handler to add form exceptions
	   * @return
	   */
	  public LblTxtTemplateManager getMsgHandler() {
			return mMsgHandler;
	  }

	  /**
	   * Sets the CMS message handler to add form exceptions
	   * @param pMsgHandler
	   */
	  public void setMsgHandler(LblTxtTemplateManager pMsgHandler) {
			mMsgHandler = pMsgHandler;
	  }

}
