
package com.bbb.profile;

import java.util.HashMap;
import java.util.Map;

import com.bbb.common.BBBGenericService;

/**
 * This class is used as a global component that will hold
 * a Map of profile ids to list of commerce items. Entries are
 * stored in this Map when the user enters the checkout
 * process as a recognized user, but chooses to proceed
 * through checkout as an anonymous user. In this case,
 * the user is logged out, and the cart is lost. As a result
 * the items need to be stored temporarily in this global
 * component so they can be retrieved and added to the new
 * cart after the session has been expired and the new
 * cart is created.
 *
 * @author 
 * @version $ $
 */
public class UserItems extends BBBGenericService {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: /com/bbb/profile/UserItems.java#2 $$Change:  $";

  /**
   * Item key constant.
   */
  public static final String ITEM_KEY = "itemKey";

  /**
   * Items map.
   */
  Map mItems;
  
  /**
   * @param pItems - map of items.
   */
  public void setItems(Map pItems) {
    mItems = pItems;
  }

  /**
   * @return Map of profile id's to List of AddCommerceItemInfos.
   */
  public Map getItems() {
    if (mItems == null) {
      mItems = new HashMap();
    }
    return mItems;
  }
  
}
