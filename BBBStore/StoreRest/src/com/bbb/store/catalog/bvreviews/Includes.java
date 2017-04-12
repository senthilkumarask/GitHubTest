
package com.bbb.store.catalog.bvreviews;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Includes {

    @JsonProperty("Products")
    private Map<String, Products> products = new HashMap<String, Products>();
    
	/**
	 * @return the products
	 */
    @JsonProperty("Products")
	public Map<String, Products> getProducts() {
		return products;
	}

	/**
	 * @param products the products to set
	 */
    @JsonProperty("Products")
	public void setProducts(Map<String, Products> products) {
    	this.products = products;
	}
}