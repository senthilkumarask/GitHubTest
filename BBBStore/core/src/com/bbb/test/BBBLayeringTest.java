package com.bbb.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;

/**
 * This class is responsible for logging weblogic server name, dynamo server
 * name, servertype, envtype and ip address. Also it will set serverDetails
 * property with the above information which can be captured from dyn/admin.
 * This class will be invoked on server startup as it is configured in
 * Initial.properties in envtype. Having this information will help us
 * quickly cross check whether right envtype/servertype values are loaded.
 * 
 */
public class BBBLayeringTest extends GenericService {
	private static final String JVM_SERVER_NAME = "weblogic.Name";
	private static final String DYNAMO_SERVER_NAME = "atg.dynamo.server.name";
	private static final String SERVERTYPE = "servertype";
	private static final String ENVTYPE = "envtype";
	private static final String TRACK = "track";
	private static final String SERVERTYPE_ERROR_MESSAGE = "There are zero or multiple servertype entries in startup, this will cause layering issues";
	private static final String ENVTYPE_ERROR_MESSAGE = "There are zero or multiple envtype entries in startup, this will cause layering issues";
	private static final String TRACK_ERROR_MESSAGE = "There are zero or multiple track entries in startup, this will cause layering issues";

	private String mServerDetails;

	public String getServerDetails() {
		return mServerDetails;
	}

	public void setServerDetails(String pServerDetails) {
		this.mServerDetails = pServerDetails;
	}

	public void doStartService() {
		String serverType = "";
		String envType = "";
		String track = "";
		String localAddress = "";
		int servertypeCount = 0;
		int envtypeCount = 0;
		int trackCount = 0;
		String weblogicServerName = System.getProperty(JVM_SERVER_NAME);
		String dynamoServerName = System.getProperty(DYNAMO_SERVER_NAME);
		String configPaths[] = getNucleus().getConfigPathNames();

		for (String configPath : configPaths) {
			if (configPath.contains(SERVERTYPE)) {
				serverType = serverType + configPath + " ";
				servertypeCount++;
			}
			if (configPath.contains(ENVTYPE)) {
				envType = envType + configPath + " ";
				envtypeCount++;
			}
			if (configPath.contains(TRACK)) {
				track = track + configPath + " ";
				trackCount++;
			}
		}

		try {
			super.doStartService();
			if (envtypeCount > 1 || envtypeCount == 0) {
				logError(ENVTYPE_ERROR_MESSAGE);

			}
			if (servertypeCount > 1 || servertypeCount == 0) {
				logError(SERVERTYPE_ERROR_MESSAGE);
			}
			if (trackCount > 1 || trackCount == 0) {
				logError(TRACK_ERROR_MESSAGE);
			}
			localAddress = InetAddress.getLocalHost().getHostAddress();
			setServerDetails("wls server name: " + weblogicServerName
					+ " :: dyn server name:" + dynamoServerName
					+ " :: servertype: " + serverType +" :: track: "
							+ track + " :: envtype: "
					+ envType + " ::: localAddress: " + localAddress);
			if (isLoggingDebug()) {
				logDebug(getServerDetails());
			}

		} catch (UnknownHostException e) {
			setServerDetails("wls server name: " + weblogicServerName
					+ " :: dyn server name:" + dynamoServerName
					+ " :: servertype: " + serverType +" :: track: "
							+ track + " :: envtype: "
					+ envType);
			logError(e + "\n" + getServerDetails());
		} catch (ServiceException e) {
			logError(e + "\n" + getServerDetails());
		}

	}
}
