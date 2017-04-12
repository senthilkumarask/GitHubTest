/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBRestDozerBeanProvider.java
 *
 *  DESCRIPTION: BBBRestDozerBeanProvider 
 *  1. global component in core which loads all the module/service specific dozer-mapping files once when starting.
 *  2. It allows use of multiple dozer mapping files.
 *  3. Works as singleton, provides single instance of DozerBeanMapper.
 *
 */

package com.bbb.rest.output;

import java.util.Arrays;
import java.util.List;

import org.dozer.DozerBeanMapper;

public class BBBRestDozerBeanProvider {

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
	
	/**
	 * method to apply dozer mapping
	 *  
	 * @param destinationObjectType absolute class name of output object 
	 * @param fromObject input Object which needs to be converted
	 * @return output object with dozer mapping applied
	 * @throws ClassNotFoundException in case output object name provides not found
	 * @throws InstantiationException in case error occurred while applying dozer
	 * @throws IllegalAccessException in case error occurred while applying dozer
	 */
	public Object map(String destinationObjectType, Object fromObject) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		
		Class<?> name=Class.forName(destinationObjectType);
		Object outputObject = name.newInstance();
		DozerBeanMapper mapper = getDozerMapper();				 
		mapper.map(fromObject, outputObject);
		return outputObject;
		
	}
}
