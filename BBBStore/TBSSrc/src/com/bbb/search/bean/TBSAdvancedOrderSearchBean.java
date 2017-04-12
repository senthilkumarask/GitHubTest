package com.bbb.search.bean;



import atg.nucleus.GenericService;

/**
 * This bean is session scoped and is used to create the search key using  and retain search key which is used for getting results from cache and display the same.
 *
 */
public class TBSAdvancedOrderSearchBean extends GenericService {

	
	
	private String firstName;
	private String lastName;
	private String registryNum;
	private String storeNum;
	private String startDate;
	private String endDate;
	private String searchTerm;
	private int totalOrders;
	private boolean isOrderThresholdExceeded;
	
	public boolean isOrderThresholdExceeded() {
		return isOrderThresholdExceeded;
	}
	public void setOrderThresholdExceeded(boolean isOrderThresholdExceeded) {
		this.isOrderThresholdExceeded = isOrderThresholdExceeded;
	}
	
	public int getTotalOrders() {
		return totalOrders;
	}
	public void setTotalOrders(int totalOrder) {
		this.totalOrders = totalOrder;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @return the registryNum
	 */
	public String getRegistryNum() {
		return registryNum;
	}
	/**
	 * @return the storeNum
	 */
	public String getStoreNum() {
		return storeNum;
	}
	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}
	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}
	/**
	 * @param pFirstName the firstName to set
	 */
	public void setFirstName(String pFirstName) {
		firstName = pFirstName;
	}
	/**
	 * @param pLastName the lastName to set
	 */
	public void setLastName(String pLastName) {
		lastName = pLastName;
	}
	/**
	 * @param pRegistryNum the registryNum to set
	 */
	public void setRegistryNum(String pRegistryNum) {
		registryNum = pRegistryNum;
	}
	/**
	 * @param pStoreNum the storeNum to set
	 */
	public void setStoreNum(String pStoreNum) {
		storeNum = pStoreNum;
	}
	/**
	 * @param pStartDate the startDate to set
	 */
	public void setStartDate(String pStartDate) {
		startDate = pStartDate;
	}
	/**
	 * @param pEndDate the endDate to set
	 */
	public void setEndDate(String pEndDate) {
		endDate = pEndDate;
	}
	
	public void clear(){
		setFirstName(null);
		setLastName(null);
		setRegistryNum(null);
		setStoreNum(null);
		setStartDate(null);
		setEndDate(null);
		setSearchTerm(null);
		setTotalOrders(0);
		setOrderThresholdExceeded(false);
	}
	
	public String getSearchTerm() {
		return searchTerm;
	}
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
}

