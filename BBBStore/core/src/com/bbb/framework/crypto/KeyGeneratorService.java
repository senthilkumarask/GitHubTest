package com.bbb.framework.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import atg.core.util.Base64;
import atg.nucleus.ServiceException;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;

public abstract class KeyGeneratorService {
	
	private static final ApplicationLogging logger = ClassLoggingFactory.getFactory().getLoggerForClass(KeyGeneratorService.class);

	/**
	 * Constant to store the encryption algorithm
	 */
	public static final String ENCRYPTION_ALGORITHEM = "AES";
	/**
	 * Constant to store the select query to be executed
	 */
	public static final String SELECT_QUERY = "SELECT * FROM BBB_UTL where is_head='Y'";
	/**
	 * Constant to store the updare query to be executed
	 */
	public static final String UPDATE_QUERY = "UPDATE BBB_UTL set is_head='N' where ENC_ID=?";
	/**
	 * Constant to store the insert query to be executed
	 */
	public static final String INSERT_QUERY = "INSERT into BBB_UTL values(?, ?, ?)";
	/**
	 * Constant to store the key property name
	 */
	public static final String KEY_PROPERTY_NAME = "key";
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
	 * Constant to store the version number column name
	 */
	public static final String VERSION_NO_COLUMN_NAME = "VERSION_NO";

	/**
	 *
	 * @param args
	 * @throws ServiceException
	 */
	public static void main (final String[] args) throws ServiceException {

		//load a properties file from class path, inside static method
		final String componentLocation = args[0];
		final String propertiesFileLocation = args[1];
		final Properties properties = new Properties();
	    InputStream inputStream = null;
	    FileOutputStream outputStream = null;
	    String key = null;
	    AESEncryptor encryptor = null;
	    File file = null;
	    String encryptedKey = null;
	    String decryptedKey = null;
	    String oldEncryptionKey = null;
	    int version = 0;
	    ResultSet resultSet = null;
	    final String query = SELECT_QUERY;
	    Connection connection = null;
	    Statement statement = null;

	    final String newEncryptionKey = generateKey();
	    final Logger logger = Logger.getLogger( KeyGeneratorService.class.getName() );
	    logger.info("New encryprion key generated successfully....");
	    try {
	    	file = new File(componentLocation);
			inputStream = new FileInputStream( file );
			properties.load( inputStream );
			key = properties.getProperty(KEY_PROPERTY_NAME);
			encryptor = new AESEncryptor();
			connection = getConnection(propertiesFileLocation);
			statement = connection.createStatement();
	        resultSet = statement.executeQuery(query);

		    if (!resultSet.next()) {
		    	// Update the DB with the newly generated encryption key
			    updateEncryprionKey(oldEncryptionKey, newEncryptionKey, version, propertiesFileLocation, connection);
			    logger.info("New encryprion key updated into DB table successfully....");
				encryptor.setKey(Base64.decodeToByteArray(newEncryptionKey));
				encryptedKey = encryptor.encrypt(key);
				logger.info("Original encryption key encrypted successfully....");
	        } else {
	        	oldEncryptionKey = resultSet.getString(COLUMN_NAME);
		        version = resultSet.getInt(VERSION_NO_COLUMN_NAME);
		        // Update the DB with the newly generated encryption key
	    	    updateEncryprionKey(oldEncryptionKey, newEncryptionKey, version, propertiesFileLocation, connection);
	    	    logger.info("New encryprion key updated into DB table successfully....");
	        	encryptor.setKey(Base64.decodeToByteArray(oldEncryptionKey));
	        	decryptedKey = encryptor.decrypt(key);

	        	encryptor.setKey(Base64.decodeToByteArray(newEncryptionKey));
	        	encryptedKey = encryptor.encrypt(decryptedKey);
	        	logger.info("Original encryption key encrypted successfully....");
	        }
		    properties.setProperty(KEY_PROPERTY_NAME, encryptedKey);
		    outputStream = new FileOutputStream(file);
			properties.store(outputStream, "Encrypted");
			outputStream.close();
			
	    } catch (final FileNotFoundException e) {
	    	throw new ServiceException("schemaDetails.properties file is not present at the provided location. "
	    			+ " Please check your environment configuration and try restarting.");
		} catch (final IOException e) {
			throw new ServiceException("An exception occurred while reading properties from the file.", e);
		} catch (final EncryptorException e) {
			throw new ServiceException("AbstractEncryptorComponent could not create an AESEncryptor object using the given"
					    + "key property value. Please check your environment configuration and try restarting.", e);
		} catch (final SQLException e) {
			throw new ServiceException("An exception occurred while executing the sql statement.", e);
		} 
	    finally {
				try {
					if (null != inputStream) {inputStream.close();}
					if (null != connection) {connection.close();}
					if( statement!=null && !statement.isClosed())
					{
						statement.close();
					}
					if(resultSet!=null && !resultSet.isClosed())
					{
						resultSet.close();
					}
				} catch (final SQLException e) {
					throw new ServiceException("An exception occurred while executing the sql statement.", e);
				} catch (IOException e) {
					throw new ServiceException("An exception occurred while reading inputstream ", e);
				}
		}
	}

	/**
     * This method generates an encryption key using AES and returns.
     *
     * @return String
     */
	public static String generateKey () {
		String base64Key = null;
		byte[] raw = null;
    	try {
    	    // Get the KeyGenerator
    	    final KeyGenerator kgen = KeyGenerator.getInstance(ENCRYPTION_ALGORITHEM);
    	    kgen.init(128); // 192 and 128 bits may be available
    	    // Generate the secret key specs.
    	    final SecretKey skey = kgen.generateKey();
    	    raw = skey.getEncoded();
    	    base64Key = Base64.encodeToString(raw);
    	} catch (final NoSuchAlgorithmException e) {
  		   logger.logError("Error while encryption "+ e.getMessage());
    	}
    	return base64Key;
    }

    /**
     * This method creates a connection to the Db and returns the value of KEY2..
     *
     * @return String key2
     * @throws ServiceException
     *                 if key is null
     */
    public static int updateEncryprionKey (final String pOldEncryptionKey, final String pNewEncryptionKey, final int pCurrentVersion, final String pPropertiesFileLocation, final Connection pConnection) throws ServiceException {
	    final String updateQuery = UPDATE_QUERY;
	    final String insertQuery = INSERT_QUERY;
	    PreparedStatement updateStatement = null;
	    PreparedStatement insertStatement = null;
	    int result = 0;

	    try {
	        updateStatement = pConnection.prepareStatement(updateQuery);
	        updateStatement.setString(1, pOldEncryptionKey);
	        updateStatement.executeUpdate();

	        insertStatement = pConnection.prepareStatement(insertQuery);
	        insertStatement.setString(1, pNewEncryptionKey);
	        insertStatement.setInt(2, pCurrentVersion + 1);
	        insertStatement.setString(3, "Y");
	        result = insertStatement.executeUpdate();

	        pConnection.commit();
	    } catch (final SQLException e) {
			throw new ServiceException("An exception occurred while executing the sql statement.", e);
		}
	    finally{
	    	try {
	    		if(null!=insertStatement && !insertStatement.isClosed())
	    		{
	    			insertStatement.close();
	    		}
	    		if(updateStatement!=null && !updateStatement.isClosed())
	    		{
	    			updateStatement.close();
	    		}
			} catch (SQLException e) {
				throw new ServiceException("An exception occurred while executing the sql statement.", e);
			}
	    }
	    	
	    return result;
    }
    /**
     * This method creates a connection to the DB and returns.
     *
     * @return Connection connection
     * @throws ServiceException
     *
     */
    public static Connection getConnection (final String pPropertiesFileLocation) throws ServiceException {
		final Properties properties = new Properties();
	    InputStream inputStream = null;
	    Connection connection = null;
    	final File file = new File(pPropertiesFileLocation);
    	try {
			inputStream = new FileInputStream( file );
	        properties.load( inputStream );
	        final String connectionString = properties.getProperty(CONNECTION_STRING_PROPERTY_NAME);
	        final String driver = properties.getProperty(DRIVER_CLASS_NAME);
	        final String userName = properties.getProperty(USERNAME_PROPERTY_NAME);
	        final String password = properties.getProperty(PASSWORD_PROPERTY_NAME);
	        Class.forName(driver).newInstance();
	        connection = DriverManager.getConnection(connectionString, userName, password);
    	} catch (final FileNotFoundException e1) {
	    	throw new ServiceException("schemaDetails.properties file is not present at the provided location. "
	    			+ " Please check your environment configuration and try restarting.");
		} catch (final IOException e) {
			throw new ServiceException("An exception occurred while reading properties from the file.", e);
		} catch (final InstantiationException e) {
			throw new ServiceException("An exception occurred while getting the instance for the driver class.", e);
		} catch (final IllegalAccessException e) {
			throw new ServiceException("An exception occurred while getting the instance for the driver class.", e);
		} catch (final ClassNotFoundException e) {
			throw new ServiceException("An exception occurred while getting the instance for the driver class. "
					+ "The driver class provided could not be found.", e);
		} catch (final SQLException e) {
			throw new ServiceException("An exception occurred while executing the sql statement.", e);
		}
    	return connection;
    }
}
