package com.bbb.commerce.inventory.vo;

import java.io.Serializable;

public class SBAuthTokenResponseVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9215271602583643529L;
	private String access_token;
	private String token_type;
	private String refresh_token;
	private long expires_in;
	private String scope;

	/**
	 * @return the access_token
	 */
	public String getAccess_token() {
		return access_token;
	}

	/**
	 * @param access_token
	 *            the access_token to set
	 */
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	/**
	 * @return the token_type
	 */
	public String getToken_type() {
		return token_type;
	}

	/**
	 * @param token_type
	 *            the token_type to set
	 */
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	/**
	 * @return the refresh_token
	 */
	public String getRefresh_token() {
		return refresh_token;
	}

	/**
	 * @param refresh_token
	 *            the refresh_token to set
	 */
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	/**
	 * @return the expires_in
	 */
	public long getExpires_in() {
		return expires_in;
	}

	/**
	 * @param expires_in
	 *            the expires_in to set
	 */
	public void setExpires_in(long expires_in) {
		this.expires_in = expires_in;
	}

	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @param scope
	 *            the scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public String toString() {
		StringBuilder token = new StringBuilder();
		token.append("{");
		token.append("\"access_token\" : ").append("\"" + this.access_token + "\"").append(",");
		token.append("\"token_type\" : ").append("\"" + this.token_type + "\"").append(",");
		token.append("\"refresh_token\" : ").append("\"" + this.refresh_token + "\"").append(",");
		token.append("\"expires_in\" : ").append(this.expires_in).append(",");
		token.append("\"scope\" : ").append("\"" + this.scope + "\"").append("}");
		return token.toString();
	}
}