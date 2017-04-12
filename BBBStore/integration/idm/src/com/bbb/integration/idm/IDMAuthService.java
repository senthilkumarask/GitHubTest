package com.bbb.integration.idm;

import javax.naming.*;
import javax.naming.directory.*;

import com.bbb.common.BBBGenericService;
import java.util.Hashtable;

public class IDMAuthService extends BBBGenericService {

	private String LDAPS_URL; 
	private String bindAccount;
	private String bindPassword;
	private String assocSubtree;
	private String vendorSubtree;
	private String userString;
	private boolean fakeIDM = false;
	private boolean useVendor = false;
	private int associateCookieTimeinHours;
	
	@SuppressWarnings({ "rawtypes", "unchecked", "finally" })
	public boolean authenticateAssociate( String pAssocID, String pPassword ) {
		
		// Check for fake IDM.  This is for testing when an actual IDM auth service is not available.
		if( isFakeIDM() ) {
			if( isLoggingDebug() ) logDebug("Using FAKE IDM..");
			boolean success = pPassword.equalsIgnoreCase("1234");
			return success;
		}
		
		boolean auth_success = false;
		
		// Set up environment for creating initial context
		Hashtable env = new Hashtable(11);
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

		// Specify LDAPS URL
		env.put(Context.PROVIDER_URL, getLDAPS_URL());
		env.put(Context.SECURITY_PROTOCOL, "ssl");
		
		// Authenticate as system for binding
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, getBindAccount());
		env.put(Context.SECURITY_CREDENTIALS, getBindPassword());

		try {
		    // Create initial context
			DirContext ctx = null;
			ctx = new InitialDirContext(env);
			if( isLoggingDebug() ) {
				logDebug("Got Initial Context : " + ctx.getNameInNamespace());
			}

			Hashtable environment = (Hashtable)ctx.getEnvironment().clone();
			if( isUseVendor() ) {
				environment.put(Context.SECURITY_PRINCIPAL, getUserString() + "=" + pAssocID + "," + getVendorSubtree());
			}
			else {
				environment.put(Context.SECURITY_PRINCIPAL, getUserString() + "=" + pAssocID + "," + getAssocSubtree());
			}
			environment.put(Context.SECURITY_CREDENTIALS, pPassword);

			DirContext dirContext = new InitialDirContext(environment);
			
			// Sucessful auth
			if( isLoggingInfo() ) {
				logInfo("Associate : " + pAssocID + " authenticated sucessfully");
			}
		    ctx.close();
		    dirContext.close();
		    auth_success = true;
		} 
		catch (Exception e) {
			if( e.getCause() instanceof java.net.ConnectException) {
				if( isLoggingInfo() ) {
					logInfo("IDM not accessible, returning success : " + pAssocID);
				}
				auth_success = true;
			}
			if( isLoggingInfo() ) {
				logInfo( "Invalid authentication : " + pAssocID);
			}
			e.printStackTrace();
		}		
		finally {
			env.clear();
			return auth_success;
		}
	}
	
	
	public String getLDAPS_URL() {
		return LDAPS_URL;
	}

	public void setLDAPS_URL(String lDAPS_URL) {
		LDAPS_URL = lDAPS_URL;
	}
	
	public boolean isFakeIDM() {
		return fakeIDM;
	}

	public void setFakeIDM(boolean fakeIDM) {
		this.fakeIDM = fakeIDM;
	}

	public boolean isUseVendor() {
		return useVendor;
	}

	public void setUseVendor(boolean useVendor) {
		this.useVendor = useVendor;
	}

	public String getBindAccount() {
		return bindAccount;
	}

	public void setBindAccount(String bindAccount) {
		this.bindAccount = bindAccount;
	}

	public String getBindPassword() {
		return bindPassword;
	}

	public void setBindPassword(String bindPassword) {
		this.bindPassword = bindPassword;
	}

	public String getAssocSubtree() {
		return assocSubtree;
	}

	public void setAssocSubtree(String assocSubtree) {
		this.assocSubtree = assocSubtree;
	}

	public String getVendorSubtree() {
		return vendorSubtree;
	}

	public void setVendorSubtree(String vendorSubtree) {
		this.vendorSubtree = vendorSubtree;
	}

	public String getUserString() {
		return userString;
	}

	public void setUserString(String userString) {
		this.userString = userString;
	}

	/**
	 * @return the associateCookieTimeinHours
	 */
	public int getAssociateCookieTimeinHours() {
		return associateCookieTimeinHours;
	}

	/**
	 * @param pAssociateCookieTimeinHours the associateCookieTimeinHours to set
	 */
	public void setAssociateCookieTimeinHours(int pAssociateCookieTimeinHours) {
		associateCookieTimeinHours = pAssociateCookieTimeinHours;
	}
	
}