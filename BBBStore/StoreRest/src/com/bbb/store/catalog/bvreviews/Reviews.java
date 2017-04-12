
package com.bbb.store.catalog.bvreviews;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Reviews {

	@JsonProperty("Includes")
	private Includes includes;
	@JsonProperty("HasErrors")
    private Boolean hasErrors;
    @JsonProperty("Offset")
    private Long offset;
    @JsonProperty("TotalResults")
    private Long totalResults;
    /*@JsonProperty("Locale")
    private String locale;*/
    @JsonProperty("Errors")
    private List<Object> errors = new ArrayList<Object>();
    @JsonProperty("Results")
    private List<Result> results = new ArrayList<Result>();
    @JsonProperty("Limit")
    private Long limit;
    /**
	 * @return the includes
	 */
    @JsonProperty("Includes")
	public Includes getIncludes() {
		return includes;
	}
	/**
	 * @param includes the includes to set
	 */
    @JsonProperty("Includes")
	public void setIncludes(Includes includes) {
		this.includes = includes;
	}
	/**
	 * @return the hasErrors
	 */
    @JsonProperty("HasErrors")
	public Boolean getHasErrors() {
		return hasErrors;
	}
	/**
	 * @param hasErrors the hasErrors to set
	 */
    @JsonProperty("HasErrors")
	public void setHasErrors(Boolean hasErrors) {
		this.hasErrors = hasErrors;
	}
	/**
	 * @return the offset
	 */
    @JsonProperty("Offset")
	public Long getOffset() {
		return offset;
	}
	/**
	 * @param offset the offset to set
	 */
    @JsonProperty("Offset")
	public void setOffset(Long offset) {
		this.offset = offset;
	}
	/**
	 * @return the totalResults
	 */
    @JsonProperty("TotalResults")
	public Long getTotalResults() {
		return totalResults;
	}
	/**
	 * @param totalResults the totalResults to set
	 */
    @JsonProperty("TotalResults")
	public void setTotalResults(Long totalResults) {
		this.totalResults = totalResults;
	}
	/**
	 * @return the locale
	 */
    /*@JsonProperty("Locale")
	public String getLocale() {
		return locale;
	}
	*//**
	 * @param locale the locale to set
	 *//*
    @JsonProperty("Locale")
	public void setLocale(String locale) {
		this.locale = locale;
	}*/
	/**
	 * @return the errors
	 */
    @JsonProperty("Errors")
	public List<Object> getErrors() {
		return errors;
	}
	/**
	 * @param errors the errors to set
	 */
    @JsonProperty("Errors")
	public void setErrors(List<Object> errors) {
		this.errors = errors;
	}
	/**
	 * @return the results
	 */
    @JsonProperty("Results")
	public List<Result> getResults() {
		return results;
	}
	/**
	 * @param results the results to set
	 */
    @JsonProperty("Results")
	public void setResults(List<Result> results) {
		this.results = results;
	}
	/**
	 * @return the limit
	 */
    @JsonProperty("Limit")
	public Long getLimit() {
		return limit;
	}
	/**
	 * @param limit the limit to set
	 */
    @JsonProperty("Limit")
	public void setLimit(Long limit) {
		this.limit = limit;
	}

}
