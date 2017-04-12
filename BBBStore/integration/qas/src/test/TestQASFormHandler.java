package test;

import atg.core.util.Address;
import atg.droplet.GenericFormHandler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class TestQASFormHandler extends GenericFormHandler{
	
	private Address address;
	
	public Address getAddress() {
		if(null == address) {
			address = new Address();
		}
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void handleTestValidateAddress(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse){
		logDebug("inside handleTestValidateAddress");
		logDebug("Address -" + address);
		logDebug("PostalCode -" + address.getPostalCode());		
	}
}
