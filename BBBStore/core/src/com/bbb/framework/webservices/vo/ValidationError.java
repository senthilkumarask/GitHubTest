package com.bbb.framework.webservices.vo;

import java.io.Serializable;

public class  ValidationError implements Serializable {
		 /**
	 * 
	 */
	private static final long serialVersionUID = 8787513946935963573L;
		private String mKey;
		 private String mValue;
		/**
		 * @return the mKey
		 */
		public String getKey() {
			return mKey;
		}
		/**
		 * @param pKey the mKey to set
		 */
		public void setKey(String pKey) {
			this.mKey = pKey;
		}
		/**
		 * @return the mValue
		 */
		public String getValue() {
			return mValue;
		}
		/**
		 * @param pValue the mValue to set
		 */
		public void setValue(String pValue) {
			this.mValue = pValue;
		}
		@Override
		public String toString() {
			return "ValidationError [mKey=" + mKey + ", mValue=" + mValue + "]";
		}
	 }
