package com.bbb.search.bean.result;

import java.io.Serializable;

public class AutoSuggestVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String searchKey;
	private String searchMode;
	private String searchTerms;
	private String spellCorrection;
	private String dymSuggestion;
	private boolean isAutoPhrase;
	
	public String getDymSuggestion() {
		return dymSuggestion;
	}
	public void setDymSuggestion(String dymSuggestions) {
		this.dymSuggestion = dymSuggestions;
	}
	public String getSpellCorrection() {
		return spellCorrection;
	}
	public void setSpellCorrection(String spellCorrection) {
		this.spellCorrection = spellCorrection;
	}
	public String getSearchKey() {
		return searchKey;
	}
	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}
	public String getSearchMode() {
		return searchMode;
	}
	public void setSearchMode(String searchMode) {
		this.searchMode = searchMode;
	}
	public String getSearchTerms() {
		return searchTerms;
	}
	public void setSearchTerms(String searchTerms) {
		this.searchTerms = searchTerms;
	}
	public boolean isAutoPhrase() {
		return isAutoPhrase;
	}
	public void setAutoPhrase(boolean isAutoPhrase) {
		this.isAutoPhrase = isAutoPhrase;
	}
	
}
