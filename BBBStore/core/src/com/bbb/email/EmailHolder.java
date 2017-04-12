package com.bbb.email;

import java.util.HashMap;
import java.util.Map;

import com.bbb.common.BBBGenericService;
/**
 * 
 * This class object stores email specific parameters.
 * 
 * This class has been used to make  email_friend.jsp
 * a generic email popup for similar emails.
 * 
 * @author ikhan2
 *
 */
public class EmailHolder extends BBBGenericService {
	
	/** form component name */
	private String formComponentName;
	private String handlerName;
	private boolean ccFlag;
	private Map<String,Object> values = new HashMap<String, Object>();
	
	/**
	 * @return the formComponentName
	 */	
	public String getFormComponentName() {
		return this.formComponentName;
	}
	
	/**
	 * @param formComponentName the formComponentName to set
	 */
	public void setFormComponentName(final String formComponentName) {
		this.formComponentName = formComponentName;
	}
	
	/**
	 * @return the handlerName
	 */	
	public String getHandlerName() {

		return this.handlerName;
	}

	/**
	 * @param handlerName the handlerName to set
	 */	
	public void setHandlerName( final String handlerName) {
		
		this.handlerName = handlerName;
	}
	
	/**
	 * @return the values
	 */		
	public Map<String, Object> getValues() {

		if(this.values == null){
			this.values = new HashMap<String, Object>();
		}
		/*Commented as customer email is getting printed in Logs along with large no of Info Statements for “EmailHolder.getValues”  
		 * almost 350k lines per hour and repeatedly for same request as well
		 * else{
			logInfo((String) ("EmailHolder.getValues | printing senderEmail : " + this.values.get("senderEmail")));
		}*/
		return this.values;
	}
	
	/**
	 * @param values the values to set
	 */	
	public void setValues(final Map<String,Object> values) {

		this.values = values;
		
	}
	
	/**
	 * @param the successURL
	 */		
	public void setSuccessURL(final String pSuccessURL) {
		
		getValues().put("successURL",pSuccessURL);
	}
	
	/**
	 * @param successURL 
	 */	
	public void setErrorURL(final String  pErrorURL) {
		
		getValues().put("errorURL", pErrorURL);
	}

	/**
	 * @return the successURL
	 */		
	public String getSuccessURL() {
		
		return (String)getValues().get("successURL");
	}
	
	/**
	 * @return the errorURL
	 */		
	public String getErrorURL() {
		
		return (String)getValues().get("errorURL");
	}

	/**
	 * @return the ccFlag
	 */
	public boolean getCcFlag() {
		return this.ccFlag;
	}

	/**
	 * @param ccFlag the ccFlag to set
	 */
	public void setCcFlag(boolean ccFlag) {
		this.ccFlag = ccFlag;
	}	

}
