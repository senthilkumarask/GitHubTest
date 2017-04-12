package com.bbb.framework.cache;

import java.io.Serializable;
import java.util.List;

import atg.nucleus.dms.DASMessage;

public class CacheInvalidationMessage extends DASMessage implements Serializable {
	private static final long serialVersionUID = -4791562578930066086L;
	private String mEntryKey;
	private List<String> keys;
	private boolean mPaternMatch = true;

	public String getEntryKey() {
		return mEntryKey;
	}

	public void setEntryKey(String entryKey) {
		this.mEntryKey = entryKey;
	}

	public boolean isPaternMatch() {
		return mPaternMatch;
	}

	public void setPaternMatch(boolean paternMatch) {
		this.mPaternMatch = paternMatch;
	}

	public CacheInvalidationMessage(String pEntryKey, boolean pPaternMatch) {
		mEntryKey = pEntryKey;
		mPaternMatch = pPaternMatch;
	}

	public CacheInvalidationMessage(String pEntryKey) {
		mEntryKey = pEntryKey;
	}

	public CacheInvalidationMessage(String pEntryKey, List<String> keys) {
		mEntryKey = pEntryKey;
		this.keys = keys;
	}

	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

}
