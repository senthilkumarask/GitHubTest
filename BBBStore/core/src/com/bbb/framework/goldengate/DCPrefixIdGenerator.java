package com.bbb.framework.goldengate;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import atg.service.idgen.IdGeneratorException;
import atg.service.idgen.SQLIdGenerator;

import com.bbb.utils.BBBUtility;

/**
 * @author ugoel
 * 
 */
public class DCPrefixIdGenerator extends SQLIdGenerator {
	
	private String dcPrefix = "DC";
	
	private static final String ZERO = "0";
	
	private Map<String,String> pIdSpacePrefixMap;
	
	private Map<String,String> pIdSpaceLengthMap;
	

	/**
	 * @return the dcPrefix
	 */
	public String getDcPrefix() {
		return this.dcPrefix;
	}

	/**
	 * @param dcPrefix the dcPrefix to set
	 */
	public void setDcPrefix(final String dcPrefix) {
		this.dcPrefix = dcPrefix;
	}

	/**
	 * @return the pIdSpacePrefixMap
	 */
	public Map<String, String> getIdSpacePrefixMap() {
		return this.pIdSpacePrefixMap;
	}

	/**
	 * @param pIdSpacePrefixMap the pIdSpacePrefixMap to set
	 */
	public void setIdSpacePrefixMap(final Map<String, String> idSpacePrefixMap) {
		this.pIdSpacePrefixMap = idSpacePrefixMap;
	}

	/**
	 * @return the pIdSpaceLengthMap
	 */
	public Map<String, String> getIdSpaceLengthMap() {
		return this.pIdSpaceLengthMap;
	}

	/**
	 * @param pIdSpaceLengthMap the pIdSpaceLengthMap to set
	 */
	public void setIdSpaceLengthMap(final Map<String, String> idSpaceLengthMap) {
		this.pIdSpaceLengthMap = idSpaceLengthMap;
	}	
	
	@Override
	protected String postGenerateStringId(final String pIdSpaceName, final String pCandidateId)
    throws IdGeneratorException {
		
		String finalCandidateId = pCandidateId;
		String prefix = getIdSpacePrefixMap().get(pIdSpaceName);
		final String length = getIdSpaceLengthMap().get(pIdSpaceName);
		
		if(prefix == null) {
			prefix = this.dcPrefix;
		}		
		
		//Start : R2.2 Added For New OrderId Generation for DS Online & Bopus Orders with new IDSPACE
		if(StringUtils.equals(pIdSpaceName, "OrderDS") && !BBBUtility.isEmpty(finalCandidateId)){
			if(isLoggingDebug()){
				logDebug("DCGenerator generated String ID" + finalCandidateId);
			}
			return finalCandidateId;
		}
		//End : R2.2 Added For New OrderId Generation for DS Online & Bopus Orders with new IDSPACE
			if(length != null){
	        	finalCandidateId = StringUtils.leftPad(pCandidateId, Integer.parseInt(length), ZERO);
	        }
			if(isLoggingDebug()){
				logDebug("DCGenerator generated String ID" + prefix + finalCandidateId);
			}
			return prefix + finalCandidateId;
		
	}	
}
