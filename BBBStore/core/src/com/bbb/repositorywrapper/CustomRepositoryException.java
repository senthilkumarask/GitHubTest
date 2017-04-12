package com.bbb.repositorywrapper;
/**
* @author Prashanth K Bhoomula 
* @Version: 1.0
* @Modified Date: June 19, 2012
*/

public class CustomRepositoryException extends RuntimeException {
	private static final long	serialVersionUID	= -536714502387566490L;

	public CustomRepositoryException(Throwable cause) {
		super(cause);
	}

	public CustomRepositoryException(String message) {
		super(message);
	}



	public CustomRepositoryException(String message, Throwable cause) {
		super(message, cause);
	}
}