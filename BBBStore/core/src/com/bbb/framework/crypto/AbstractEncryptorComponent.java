/*
 *
 * File  : AbstractEncryptorComponent.java
 * Project:     BBB
 */
package com.bbb.framework.crypto;

import com.bbb.common.BBBGenericService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import atg.nucleus.ServiceException;



/**
 * This is an abstract class of an encryptor component.
 * 
 * 
 */
public abstract class AbstractEncryptorComponent extends BBBGenericService {

	/**
	 * Constant to store the connectionString property name
	 */
	public static final String CONNECTION_STRING_PROPERTY_NAME = "connectionString";
	/**
	 * Constant to store the driverClassName property name
	 */
	public static final String DRIVER_CLASS_NAME = "driverClassName";
	/**
	 * Constant to store the usetName property name
	 */
	public static final String USERNAME_PROPERTY_NAME = "userName";
	/**
	 * Constant to store the password property name
	 */
	public static final String PASSWORD_PROPERTY_NAME = "password";	
	/**
	 * Constant to store the column name
	 */
	public static final String COLUMN_NAME = "ENC_ID";
	/**
     * This is the local AbstractEncryptor instance which this component will use to perform the encryption and
     * decryption operations.
     */
    private AbstractEncryptor mEncryptor;
      
    /**
     * This is location of the properties file from where we have to fetch the schema details.
     */
    private DataSource mDatasource;
    /**
     * This is location of the properties file from where we have to fetch the key.
     */
    private String mKeyComponentLocation;
    
    /**
     * This is the query to be run to get KEY2.
     */
    private String mQuery;
    
    public AbstractEncryptorComponent() {
		// TODO Auto-generated constructor stub
	}
    
    
    /**
     * This method computes the MAC message signature for the clear text. This is basically a checksum so you know you
     * decrypted the data successfully and that it wasn't tampered with.
     * 
     * @param pClearText
     *                the plain text you wish to compute the MAC for. This must match the plain text you encrypt.
     * @return a String, which is a Base64 encoded version of the checksum signature of the cleartext, based on the
     *         current algorithm and key.
     * @throws EncryptorException
     *                 if the key is invalid, if the algorithm is improperly defined, or if the encoding fails.
     */
    public String computeMAC(final String pClearText) throws EncryptorException {

	logDebug("AbstractEncryptorComponent.computeMAC() - Entering...");
	
	final String hmacString = this.mEncryptor.computeMAC(pClearText);
	
	logDebug("AbstractEncryptorComponent.computeMAC() - Returning HMAC: " + hmacString);
	
	return hmacString;
    }

    /**
     * This method takes in a byte array and decrypts it using the AES algorithm and the key used to setup this class.
     * 
     * @param pBytesToDecrypt
     *                the byte array to be decrypted using AES and the key in mKeyBytes.
     * @return a byte array of decrypted data.
     * @throws EncryptorException
     *                 if decryption fails.
     */
    public byte[] decryptBytes(final byte[] pBytesToDecrypt) throws EncryptorException {
	return this.mEncryptor.decrypt(pBytesToDecrypt);
    }

    /**
     * This method takes in a String of a Base64 encoded byte array of encrypted data and decrypts it using the AES
     * algorithm and the key used to setup this class.
     * 
     * @param pEncryptedContent
     *                the String of encoded and encrypted content which should be decrypted.
     * @return a byte array of decrypted data.
     * @throws EncryptorException
     *                 if decryption fails.
     */
    public String decryptString(final String pEncryptedContent) throws EncryptorException {
	return this.mEncryptor.decrypt(pEncryptedContent);
    }

    /**
     * This method sets up our component, and creates the Encryptor object.
     * 
     * @see atg.nucleus.GenericService#doStartService()
     * @throws ServiceException
     *                 if key is null
     */
    public void doStartService() throws ServiceException {
	super.doStartService();
	String decryptedKey = null;
	String encryptionKey2 = null;
	// Check our properties.
	if (getKey() == null) {
	    throw new ServiceException("AbstractEncryptorComponent.key has not been set properly. Please check your "
		    + "environment configuration and try restarting.");
	}
	
	// Set up our Encryptor.
	try {
		// Get KEY2 from DB using JBDC
		encryptionKey2 = getEncryptionKey();
		if (encryptionKey2 != null) {
			setupEncryptor(encryptionKey2);
			// Decrypt KEY1 using KEY2
			decryptedKey = decryptString(getKey());
			// Set decrypted value of KEY1 to be used for decrypting the actual credit card numbers
		    setupEncryptor(decryptedKey);
		} else {
			setupEncryptor();
		}
		
	} catch (EncryptorException ee) {
	    throw new ServiceException(
		    "AbstractEncryptorComponent could not create an AESEncryptor object using the given "
			    + "\"key\" property. Please check your environment configuration and try restarting.", ee);
	}
	if (this.mEncryptor == null) {
	    throw new ServiceException("AbstractEncryptorComponent created a NULL AESEncryptor object using the given "
		    + "\"key\" property. Please check your environment configuration and try restarting.");
	}
    }
    
    /**
     * This method creates a connection to the Db and returns the value of KEY2..
     * 
     * @return String key2
     * @throws ServiceException
     *                 if key is null
     */
    private String getEncryptionKey () throws ServiceException {
    	//load a properties file from class path, inside static method
		String query = getQuery();
	    String key2 = null;
	    Statement statement = null;
	    ResultSet resultSet = null;
	    Connection connection = null;
    	
	    try {	 
	    		connection = getConnection();
		    	if (connection == null) {
		    		throw new ServiceException("An exception occurred while creating connection to the PCI Schema. Please check your system configurations and restart.");    		
		    	}
		        statement = connection.createStatement();
		        if (statement != null) {
			        resultSet = statement.executeQuery(query);
			        if (resultSet != null) {
			        	if (resultSet.next()) {
				        	key2 = resultSet.getString(COLUMN_NAME);
				        }
					}
		        }
		    } catch (SQLException e) {
				throw new ServiceException("An exception occurred while executing the sql statement.", e);
			} finally {
				try {
					if(resultSet!=null){resultSet.close();}
					if(statement!=null){statement.close();}
					if(connection!=null){connection.close();}
				} catch (SQLException e) {
					throw new ServiceException("An exception occurred while executing the sql statement.", e); 
				}
			}
	    return key2;
    }
    
    /**
     * This method reads the value of encrypted key from AESEncryptorComponent.properties.
     * 
     * @return Connection connection
     * @throws ServiceException
     *                
     */
    
    protected String getKey () throws ServiceException {
    	String keyComponentLocation = getKeyComponentLocation();
    	String key = null;
    	Properties properties = new Properties();
	    InputStream inputStream = null;
    	File file = new File(keyComponentLocation);    	
		try {
			inputStream = new FileInputStream( file );
			properties.load( inputStream );
	        key = properties.getProperty("key");
		} catch (FileNotFoundException e) {
			throw new ServiceException("AESEncryptorComponent.properties file is not present at the required location. " 
	    			+ " Please check your environment configuration and try restarting.");
		} catch (IOException e) {
			throw new ServiceException("An exception occurred while reading key value from the file.", e);
		}			
	        
    	return key;
    }
    /**
     * This method creates a connection to the DB and returns.
     * 
     * @return Connection connection
     * @throws ServiceException
     *                
     */
    public Connection getConnection () throws ServiceException {
    	//load a properties file from class path, inside static method
    	
    	DataSource datasource = getDatasource();
    	if (datasource == null) {
    		throw new ServiceException("An exception occurred while creating datasource for PCI Schema. Please check your system configurations and restart");
    	}
	    Connection connection = null;    	
    	try {			
	        connection = datasource.getConnection();
    	} catch (SQLException e) {
			throw new ServiceException("An exception occurred while creating connection to the PCI Schema. Please check your system configurations and restart.", e);
		}    	
    	return connection;
    }
    
    /**
     * This method sets up the encryptor. It must be overriden by the actual type of encryptor component you wish to
     * use. An example body would look like this:
     * 
     * final byte[] keyBytes = Base64.decodeToByteArray(getKey()); this.mEncryptor = new AbstractEncryptor(keyBytes);
     * 
     * @throws EncryptorException
     *                 if the encryptor could not be created
     */
    public abstract void setupEncryptor() throws EncryptorException, ServiceException;
    
    /**
     * This method sets up the encryptor. It must be overriden by the actual type of encryptor component you wish to
     * use. An example body would look like this:
     * 
     * final byte[] keyBytes = Base64.decodeToByteArray(pKey); this.mEncryptor = new AbstractEncryptor(keyBytes);
     * @param pKey
     *                the key to be used to setup encrypter
     * @throws EncryptorException
     *                 if the encryptor could not be created
     */
    public abstract void setupEncryptor(String pKey) throws EncryptorException;

    /**
     * This method takes in a byte array and encrypts it using the AES algorithm and the key used to setup this class.
     * 
     * @param pBytesToEncrypt
     *                the byte array to be encrypted using AES and the key in mKeyBytes
     * @return a byte array of encrypted data
     * @throws EncryptorException
     *                 if encryption fails.
     */
    public byte[] encryptBytes(final byte[] pBytesToEncrypt) throws EncryptorException {
	return this.mEncryptor.encrypt(pBytesToEncrypt);
    }

    /**
     * This method takes in a String and encrypts is using the AES algorithm and the key used to setup this class.
     * 
     * @param pUnencryptedContent
     *                the String to be encrypted using AES and the key in mKey.
     * @return a String of Base64 encoded, encrypted bytes.
     * @throws EncryptorException
     *                 if encryption fails.
     */
    public String encryptString(final String pUnencryptedContent) throws EncryptorException {
	return this.mEncryptor.encrypt(pUnencryptedContent);
    }

    /**
     * @return the encryptor
     */
    public AbstractEncryptor getEncryptor() {
	return this.mEncryptor;
    }

    /**
     * @param pEncryptor
     *                the encryptor to set
     */
    public void setEncryptor(final AbstractEncryptor pEncryptor) {
	this.mEncryptor = pEncryptor;
    }

	/**
	 * @return the datasource
	 */
	public DataSource getDatasource() {
		return mDatasource;
	}


	/**
	 * @param pDatasource the datasource to set
	 */
	public void setDatasource(DataSource pDatasource) {
		this.mDatasource = pDatasource;
	}


	/**
	 * @return the keyComponentLocation
	 */
	public String getKeyComponentLocation() {
		return mKeyComponentLocation;
	}


	/**
	 * @param pKeyComponentLocation the keyComponentLocation to set
	 */
	public void setKeyComponentLocation(String pKeyComponentLocation) {
		this.mKeyComponentLocation = pKeyComponentLocation;
	}


	/**
	 * @return the query
	 */
	public String getQuery() {
		return mQuery;
	}


	/**
	 * @param pQuery the query to set
	 */
	public void setQuery(String pQuery) {
		this.mQuery = pQuery;
	}
    
    
}
