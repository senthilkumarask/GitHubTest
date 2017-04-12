package com.bbb.exception;


public class InternationalServiceException extends Exception{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InternationalServiceException(String pErrMsg){
		
		super(pErrMsg);
		
	}
	
	public InternationalServiceException(Throwable pThrown){
		
		super(pThrown);
	}
	
	public InternationalServiceException(String pErrMsg,Throwable pThrown){
		
		super(pErrMsg, pThrown);
	}
}
