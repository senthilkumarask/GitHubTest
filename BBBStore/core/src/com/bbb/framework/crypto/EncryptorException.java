/*
 *
 * File  : EncryptorException.java
 * Project:     BBB
 */
package com.bbb.framework.crypto;

/**
 * This exception indicates that a severe error occurred while performing a cryptograpy operation.
 * 
 * 
 */
public class EncryptorException extends Exception {
    /**
     * Generated serial version UID.
     */
    private static final long serialVersionUID = -1327072933000456078L;

    /**
     * Constructs a new EncryptorException.
     */
    public EncryptorException() {
	super();
    }

    /**
     * Constructs a new EncryptorException with the given explanation.
     * 
     * @param pMessage
     *                the message for this exception.
     */
    public EncryptorException(final String pMessage) {
	super(pMessage);
    }

    /**
     * Constructs a new EncryptorException.
     * 
     * @param pSourceException
     *                the initial exception which was the root cause of the problem
     */
    public EncryptorException(final Throwable pSourceException) {
	super(pSourceException);
    }

    /**
     * Constructs a new EncryptorException with the given explanation.
     * 
     * @param pMessage
     *                the message for this exception.
     * @param pSourceException
     *                the initial exception which was the root cause of the problem
     */
    public EncryptorException(final String pMessage, final Throwable pSourceException) {
	super(pMessage, pSourceException);
    }
}
