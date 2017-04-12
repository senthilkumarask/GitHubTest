package com.bbb.search.bean;



import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import com.bbb.commerce.vo.OrderVO;


import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;

/**
 * This bean acts as cache to store List<OrderVO>. The cache size is also defined here.
 *
 */
public class TBSAdvancedOrderSearchResultsCacheBean extends GenericService {

	private LinkedHashMap <String, HashMap <List<RepositoryItem>, List<OrderVO>>> outputOrders;
	private LinkedHashMap <String, List<OrderVO>> minimalOutputOrders;
	
	private static final int MAX_ENTRIES = 200;

	public LinkedHashMap<String, HashMap <List<RepositoryItem>, List<OrderVO>>> getOutputOrders() {
		if(outputOrders == null){
			outputOrders = new LinkedHashMap<String, HashMap <List<RepositoryItem>, List<OrderVO>>>(){
				protected boolean removeEldestEntry(Map.Entry eldest) {
					return size() > MAX_ENTRIES;
				}
			};
		}
		return outputOrders;
	}
	
	public LinkedHashMap<String, List<OrderVO>> getOutputMinimalOrders() {
		if(minimalOutputOrders == null){
			minimalOutputOrders = new LinkedHashMap<String,List<OrderVO>>(){
				protected boolean removeEldestEntry(Map.Entry eldest) {
					return size() > MAX_ENTRIES;
				}
			};
		}
		return minimalOutputOrders;
	}

	public void setOutputOrders(LinkedHashMap<String, HashMap <List<RepositoryItem>, List<OrderVO>>> outputOrders) {
		this.outputOrders = outputOrders;
	}
	
}
