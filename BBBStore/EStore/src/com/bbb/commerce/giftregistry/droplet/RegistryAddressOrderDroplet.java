package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.common.BBBDynamoServlet;

/**
 * The Class RegistryAddressOrderDroplet.
 *
 *
 */
public class RegistryAddressOrderDroplet extends BBBDynamoServlet {
	
	
	private String territories;

	private Entry element;
	public String getTerritories() {
		return territories;
	}
	public void setTerritories(String territories) {
		this.territories = territories;
	}

	/**
	 * This method sorts user address (Map) with either 1. Default shipping
	 * address on top or 2. If the input web service address is found in user's
	 * address-book (Map) then place it on top
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */
	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		this.logDebug("RegistryAddressOrderDroplet Service starts");

		final String defaultId = pRequest.getParameter(DEFAULT_ID);
		final String defaultKey = pRequest.getParameter(DEFAULT_KEY);
		final String sortByKeysParameter = pRequest.getParameter(SORT_BY_KEYS);
		final Map map = (Map) pRequest.getObjectParameter(MAP);
		String shippingFirstName = pRequest.getParameter(SHIP_FIRST_NAME);
		String shippingLastName = pRequest.getParameter(SHIP_LAST_NAME);
		final AddressVO wsContactAddress = (AddressVO) pRequest
				.getObjectParameter(WS_ADDRESS_VO);

		boolean sortByKeys = false;
		if (!StringUtils.isEmpty(sortByKeysParameter)) {
			sortByKeys = Boolean.parseBoolean(sortByKeysParameter);
		}

		this.logDebug((new StringBuilder()).append(" defaultId = ")
				.append(defaultId).toString());
		this.logDebug((new StringBuilder()).append(" defaultKey = ")
				.append(defaultKey).toString());
		this.logDebug((new StringBuilder()).append(" sortByKeys = ")
				.append(sortByKeys).toString());
		this.logDebug((new StringBuilder()).append(" map = ").append(map).toString());

		if (map == null) {

			this.logDebug("map param is null");
			pRequest.setParameter(SORTED_ARRAY, null);
			pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
			return;
		}
		String defaultMapKey = this.getDefaultMapKey(map, defaultId);
		if ((defaultMapKey == null) && (defaultKey != null)
				&& map.containsKey(defaultKey)) {
			defaultMapKey = defaultKey;
		}

		this.logDebug((new StringBuilder()).append("Map size: ").append(map.size())
				.toString());
		if (defaultMapKey != null) {
			this.logDebug("Default entry was found in the map");
		} else {
			this.logDebug("Defauld entry was not found in the map");
		}

		final Object sortedArray[] = this.getSortedArray(map, defaultMapKey, sortByKeys,
				pRequest);

		Object newSortedArray[] = null;

		final StringBuilder addressWS = new StringBuilder("");
		boolean isWSAddFoundInAddressBook = false;
		java.util.Map.Entry addressEntry = null;

		if (wsContactAddress != null) {

			String addLine2 = "";
			if (wsContactAddress.getAddressLine2() != null) {
				addLine2 = wsContactAddress.getAddressLine2();
			}
			addressWS.append(wsContactAddress.getAddressLine1())
					.append(addLine2).append(wsContactAddress.getCity())
					.append(wsContactAddress.getState())
					.append(wsContactAddress.getZip());
			
			if(!StringUtils.isBlank(shippingFirstName))
				addressWS.append(shippingFirstName);
			
			if(!StringUtils.isBlank(shippingLastName))
				addressWS.append(shippingLastName);
			
			this.logDebug("RegistryAddressOrderDroplet/MSG =[WSAddress ="
					+ addressWS.toString());

			if (sortedArray != null) {
				for (final Object element : sortedArray) {
					addressEntry = (java.util.Map.Entry) element;

					final RepositoryItem profileAddress = (RepositoryItem) addressEntry
							.getValue();
					if (profileAddress != null) {
						final StringBuilder addressFromProfile = new StringBuilder("");

						String pAddLine2 = "";
						if (profileAddress.getPropertyValue("address2") != null) {
							pAddLine2 = (String) profileAddress
									.getPropertyValue("address2");
						}

						addressFromProfile
								.append(profileAddress
										.getPropertyValue("address1"))
								.append(pAddLine2)
								.append(profileAddress.getPropertyValue("city"))
								.append(profileAddress
										.getPropertyValue("state"))
								.append(profileAddress
										.getPropertyValue("postalCode"));

						if(!StringUtils.isBlank(shippingFirstName))
							addressFromProfile.append(profileAddress
										.getPropertyValue("firstName"));
						if(!StringUtils.isBlank(shippingLastName))
							addressFromProfile.append(profileAddress
									.getPropertyValue("lastName"));
						// if the web service address match this profile address
						if (addressWS.toString().equalsIgnoreCase(
								addressFromProfile.toString())) {
							isWSAddFoundInAddressBook = true;

							this.logDebug("RegistryAddressOrderDroplet/MSG =[Web service address is found in AddressBook at id ="
									+ profileAddress.getPropertyValue("id"));

							break;
						}
					}
				}
			}
		} else {
			this.logDebug("RegistryAddressOrderDroplet/MSG =[WebService Contact Address is null]");
		}

		if (isWSAddFoundInAddressBook) {

			// now make it topAddress
			newSortedArray = new Object[sortedArray.length];
			newSortedArray[0] = addressEntry;

			int fromIndex = 0;
			int toIndex = 1;
			while (fromIndex < sortedArray.length) {

				if (sortedArray[fromIndex] != addressEntry) {
					newSortedArray[toIndex] = sortedArray[fromIndex];
					toIndex++;

				} else {
					this.logDebug("RegistryAddressOrderDroplet/MSG =[While Sorting to new Array, address found at index ="
							+ fromIndex);
				}

				fromIndex++;
			}

		} else {
			List<String> territoriesList = new ArrayList<String>(Arrays.asList(getTerritories().split(",")));
			newSortedArray = new Object[sortedArray.length];
			int toCount=0;
			for (final Object element : sortedArray) {
				addressEntry = (java.util.Map.Entry) element;
				final RepositoryItem profileAddress = (RepositoryItem) addressEntry
						.getValue();
				if (profileAddress != null) {
					String state = (String) profileAddress.getPropertyValue("state");
					if(!territoriesList.contains(state)){
						newSortedArray[toCount]=addressEntry;
						toCount++;
					}
				}
			}
		}

		this.logDebug("RegistryAddressOrderDroplet Service ends");

		// If wsContactAddress is null then treat as if it is found in Address
		// book
		if (wsContactAddress == null) {
			isWSAddFoundInAddressBook = true;
		}

		pRequest.setParameter(IS_WEBSERVICE_ADDRESS_IN_ADDBOOK,	Boolean.valueOf(isWSAddFoundInAddressBook));
		pRequest.setParameter(SORTED_ARRAY, ((newSortedArray)));
		pRequest.setParameter(SORTED_ARRAY_SIZE, Integer.valueOf(newSortedArray.length));

		pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);

	}

	/**
	 * Gets the sorted array.
	 *
	 * @param pMap
	 *            the map
	 * @param pDefaultKey
	 *            the default key
	 * @param pSortByKeys
	 *            the sort by keys
	 * @param pRequest
	 *            the request
	 * @return the sorted array
	 */
	protected Object[] getSortedArray(final Map pMap, final String pDefaultKey,
			final boolean pSortByKeys, final DynamoHttpServletRequest pRequest) {
		Map resultMap = pMap;
		if (pSortByKeys) {
			resultMap = new TreeMap(pMap);
		}
		if (pDefaultKey == null) {
			return resultMap.entrySet().toArray();
		}
		final Object sortedArrayDefaultFirst[] = new Object[resultMap.size()];
		final Iterator iter = resultMap.entrySet().iterator();
		int index = 1;
		boolean defaultfound = false;
		while (iter.hasNext()) {
			final java.util.Map.Entry entry = (java.util.Map.Entry) iter.next();
			final String key = (String) entry.getKey();
			if (!defaultfound && key.equals(pDefaultKey)) {
				sortedArrayDefaultFirst[0] = entry;
				defaultfound = true;
			} else {
				sortedArrayDefaultFirst[index++] = entry;
			}
		}
		return sortedArrayDefaultFirst;
	}

	/**
	 * Gets the default map key.
	 *
	 * @param pMap
	 *            the map
	 * @param pDefaultId
	 *            the default id
	 * @return the default map key
	 */
	protected String getDefaultMapKey(final Map pMap, final String pDefaultId) {
		String defaultMapKey = null;
		if ((pMap == null) || (pDefaultId == null)) {
			return null;
		}
		final Set keys = pMap.keySet();
		Object key = null;
		Object value = null;
		RepositoryItem item = null;
		final Iterator keyterator = keys.iterator();
		do {
			if (!keyterator.hasNext()) {
				break;
			}
			key = keyterator.next();
			value = pMap.get(key);
			if ((value == null) || !(value instanceof RepositoryItem)) {
				continue;
			}
			item = (RepositoryItem) value;
			if (!item.getRepositoryId().equals(pDefaultId)) {
				continue;
			}
			defaultMapKey = (String) key;
			break;
		} while (true);
		return defaultMapKey;
	}

	/** The CLAS s_ version. */
	public static String CLASS_VERSION = "";

	/** The Constant MAP. */
	public static final ParameterName MAP = ParameterName
			.getParameterName("map");

	/** The Constant WS_ADDRESS_VO. */
	public static final ParameterName WS_ADDRESS_VO = ParameterName
			.getParameterName("wsAddressVO");

	/** The Constant DEFAULT_ID. */
	public static final ParameterName DEFAULT_ID = ParameterName
			.getParameterName("defaultId");

	/** The Constant DEFAULT_KEY. */
	public static final ParameterName DEFAULT_KEY = ParameterName
			.getParameterName("defaultKey");

	/** The Constant SORT_BY_KEYS. */
	public static final ParameterName SORT_BY_KEYS = ParameterName
			.getParameterName("sortByKeys");

	/** The Constant OUTPUT. */
	public static final ParameterName OUTPUT = ParameterName
			.getParameterName("output");

	/** The SORTE d_ array. */
	public static final String SORTED_ARRAY = "sortedArray";

	/** The SORTE d_ arra y_ size. */
	public static final String SORTED_ARRAY_SIZE = "sortedArraySize";

	/** The I s_ webservic e_ addres s_ i n_ addbook. */
	public static final String IS_WEBSERVICE_ADDRESS_IN_ADDBOOK = "isWSAddressInAddressBook";

	public static final ParameterName SHIP_FIRST_NAME = ParameterName.getParameterName("shippingFirstName");
	
	public static final ParameterName SHIP_LAST_NAME = ParameterName.getParameterName("shippingLastName");
}
