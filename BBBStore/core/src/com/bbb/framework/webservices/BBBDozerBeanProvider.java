/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBDozerBeanProvider.java
 *
 *  DESCRIPTION: BBBDozerBeanProvider 
 *  1. global component in core which loads all the module/service specific dozer-mapping files once when starting.
 *  2. It allows use of multiple dozer mapping files.
 *  3. Works as singleton, provides single instance of DozerBeanMapper.
 *  4. It is injected in RequestMarshaller and RequestUnMarshaller base classes. So, in our module specific Marshaller/UnMarshaller, just need to call the method getDozerBean().getDozerMapper(); 
 *  
 *  HISTORY:
 *  12/07/11 Initial version
 *
 */

package com.bbb.framework.webservices;

import java.util.Arrays;
import java.util.List;

import org.dozer.DozerBeanMapper;

public class BBBDozerBeanProvider {

	private String[] mDozerMappingFiles;
	private DozerBeanMapper mDozerMapper;

	public void setDozerMappingFiles( final String[] pDozerMappingFiles)
	{
		mDozerMappingFiles = pDozerMappingFiles;
	}
	public final String[]  getDozerMappingFiles()
	{
		return mDozerMappingFiles;
	}
	/**
	 * @return the dozerMapper
	 * 
	 * Description: Works like singleton
	 * If the DozerBeanMapper instance is null then only new instance is created 
	 * else returns global instance created.
	 */
	public DozerBeanMapper getDozerMapper() {
		if(mDozerMapper == null || mDozerMapper.getMappingFiles()==null || mDozerMapper.getMappingFiles().isEmpty())
		{
			
			List<String> mappingFiles = Arrays.asList(mDozerMappingFiles);
			mDozerMapper = new DozerBeanMapper(mappingFiles);
		}

		return mDozerMapper;
	}
	/**
	 * @param dozerMapper the dozerMapper to set
	 */
	public  void setDozerMapper(DozerBeanMapper dozerMapper) {
		this.mDozerMapper = dozerMapper;
	}
}
