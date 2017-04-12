/**
 * 
 */
package com.bbb.framework.cache;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.transaction.TransactionManager;

import atg.service.scheduler.SchedulableService;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * @author bniral
 * 
 */
public class CoherenceStatsScheduler extends SchedulableService {
	public static final String WEBLOGIC_NAME_PROPERTY = "weblogic.Name";
	private static final String WEBLOGIC_SERVER = System.getProperty(WEBLOGIC_NAME_PROPERTY);
	private TransactionManager transactionManager;
	private Scheduler scheduler;
	private Schedule schedule;
	private String coherenceStatsFilePath;
	private String coherenceStatsFileName;
	private String fieldDelimiter;
	private List<String> coherenceStatsHeaders;
	private List<String> coherenceMBeanlist;
	private String isReachableTimeoutMiliSec;
	private List<String> iPsToPingList;
	private static final String JMXQUERYFRONTPART1 = "Coherence:type=Cache,service=*,name=";
	private static final String JMXQUERYFRONTPART2 = ",nodeId=*,tier=front,loader=*";
	private static final String JMXQUERYBACKPART1 = "Coherence:type=Cache,service=*,name=";
	private static final String JMXQUERYBACKPART2 = ",nodeId=*,tier=back";
	private boolean isEnable;
	private boolean isThresholdReachableEnable;
	private BBBCatalogTools catalogTools;
	private List<String> headerConfigList;
	private List<String> mBeanConfigList;
	int jobId;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atg.service.scheduler.SingletonSchedulableService#doScheduledTask(atg
	 * .service.scheduler.Scheduler, atg.service.scheduler.ScheduledJob)
	 */
	@Override
	public void performScheduledTask(Scheduler paramScheduler,
			ScheduledJob paramScheduledJob) {
		if (isLoggingDebug()) {
			logDebug("CoherenceStatsScheduler.doScheduledTask() method has started");
		}
		this.doScheduledTask();

	}

	/**
	 * doScheduledTask() method override & called by Scheduler method
	 * doScheduledTask() This method is invoked by dyna admin as well to test
	 * Scheduler operation any time.
	 * 
	 */
	public void doScheduledTask() {

		if (isLoggingDebug()) {
			logDebug("CoherenceStatsScheduler.doScheduledTask() method has started");
		}

		if (!isEnable()) {
			//insert debug
			return;
		}
		
		initializeConfiguration();		

		// (use relative path for Unix systems)
		File file = new File(getCoherenceStatsFilePath()
				+ getCoherenceStatsFileName());
		if (!file.exists()) {
			// (works for both Windows and Linux)
			file.getParentFile().mkdirs();
			try {
				if (file.createNewFile()) {
					if (isLoggingDebug()) {
						logDebug(BBBCoreConstants.FILE_PATH
								+ getCoherenceStatsFilePath()
								+ getCoherenceStatsFileName());
					}
					writeStaticsHeaderInFile();
				}
			} catch (IOException exception) {

				logError(getCoherenceStatsFilePath()
						+ getCoherenceStatsFileName()
						+ BBBCoreConstants.FILEPATH_EXCEPTION + exception);
			}
		}

		MBeanServerConnection mBeanServer = getMBeanServerConnection();

		if (isLoggingDebug()) {
			if (mBeanServer != null) {
				logDebug(BBBCoreConstants.MBEAN_SERVER);
			} else {
				logDebug(BBBCoreConstants.MBEAN_SERVER_FAILED);
			}
		}

		if (isLoggingDebug()) {
			logDebug("JMX operation has been started ");
		}

		for (String regionName : getCoherenceMBeanlist()) {
			try {

				String jmxQueryFront = JMXQUERYFRONTPART1 + regionName
						+ JMXQUERYFRONTPART2;

				if (isLoggingDebug()) {
					logDebug(BBBCoreConstants.FRONT_QUERY + jmxQueryFront);
				}

				Set<ObjectInstance> queryResultsFront = mBeanServer
						.queryMBeans(new ObjectName(jmxQueryFront), null);
				parserQueryResult(queryResultsFront, regionName, mBeanServer);

				String jmxQuery = JMXQUERYBACKPART1 + regionName
						+ JMXQUERYBACKPART2;

				if (isLoggingDebug()) {
					logDebug(BBBCoreConstants.BACK_QUERY + jmxQuery);
				}

				Set<ObjectInstance> queryResultsBack = mBeanServer.queryMBeans(
						new ObjectName(jmxQuery), null);
				parserQueryResult(queryResultsBack, regionName, mBeanServer);
				
			
			} catch (MalformedObjectNameException exception) {
				logError(BBBCoreConstants.JMXQUERY_EXCEPTION, exception);
			} catch (IOException exception) {
				logError(BBBCoreConstants.JMXQUERYIO_EXCEPTION, exception);
			}
		}

		if (isLoggingDebug()) {
			logDebug("JMX operation has been ended ");
		}

		if (isThresholdReachableEnable()) {
			isThresholdReachable();
		}
		if (isLoggingDebug()) {
			logDebug("CoherenceStatsScheduler.doScheduledTask() method has ended");
		}
	}

	/**
	 * initializeConfiguration() method, basically override the component
	 * configuration at runtime with BCC configuration if exist.
	 */
	private void initializeConfiguration() {
		if (isLoggingDebug()) {
			logDebug("CoherenceStatsScheduler.initializeConfiguration() method has been started");
		}
		try {
			//BPSI-6385 changed COHERENCE_CACHE_MONITOR_KEY to COHERENCE_CACHE_KEY
			
			List<String> isEnable = this.getCatalogTools().getAllValuesForKey(
					BBBCoreConstants.COHERENCE_CACHE_MONITOR_KEY, BBBCoreConstants.IS_ENABLE);
			List<String> isThresholdReachableEnable = this.getCatalogTools()
					.getAllValuesForKey(BBBCoreConstants.COHERENCE_CACHE_MONITOR_KEY,
							BBBCoreConstants.IS_THRESHOLDRECHABLE_ENABLE);
			List<String> filePath = this.getCatalogTools().getAllValuesForKey(
					BBBCoreConstants.COHERENCE_CACHE_MONITOR_KEY,
					BBBCoreConstants.COHERENCE_STATICS_FILEPATH);
			List<String> fileName = this.getCatalogTools().getAllValuesForKey(
					BBBCoreConstants.COHERENCE_CACHE_MONITOR_KEY,
					BBBCoreConstants.COHERENCE_STATICS_FILENAME);
			List<String> iPsToPingList = this.getCatalogTools()
					.getAllValuesForKey(BBBCoreConstants.COHERENCE_CACHE_MONITOR_KEY,
							BBBCoreConstants.IP_PING_LIST);
			List<String> isReachableTimeoutMiliSec = this.getCatalogTools()
					.getAllValuesForKey(BBBCoreConstants.COHERENCE_CACHE_MONITOR_KEY,
							BBBCoreConstants.IS_RECHABLE_TIMEOUT);
			List<String> concatenateHeaderList = new ArrayList<String>();
			List<String> concatenateMbeanList = new ArrayList<String>();

			for (String headerConfigKey : getHeaderConfigList()) {
				concatenateHeaderList.addAll(this.getCatalogTools()
						.getAllValuesForKey(BBBCoreConstants.COHERENCE_CACHE_MONITOR_KEY,
								headerConfigKey));
			}

			if (concatenateHeaderList.size() != 0
					&& concatenateHeaderList != null) {
				getCoherenceStatsHeaders().clear();
				getCoherenceStatsHeaders().addAll(concatenateHeaderList);
			}

			for (String mbeanConfigKey : getmBeanConfigList()) {
				concatenateMbeanList.addAll(this.getCatalogTools()
						.getAllValuesForKey(BBBCoreConstants.COHERENCE_CACHE_MONITOR_KEY,
								mbeanConfigKey));
			}

			if (concatenateMbeanList.size() != 0
					&& concatenateMbeanList != null) {
				getCoherenceMBeanlist().clear();
				getCoherenceMBeanlist().addAll(concatenateMbeanList);
			}

			if (isEnable != null) {
				this.setEnable(Boolean.valueOf(isEnable.get(0)));
			}

			if (isThresholdReachableEnable != null) {
				this.setThresholdReachableEnable((Boolean
						.valueOf(isThresholdReachableEnable.get(0))));
			}

			if (filePath != null) {
				this.setCoherenceStatsFilePath(String.valueOf(filePath.get(0))+WEBLOGIC_SERVER+BBBCoreConstants.SLASH);
			}else{
				this.setCoherenceStatsFilePath(this.getCoherenceStatsFilePath()+WEBLOGIC_SERVER+BBBCoreConstants.SLASH);
			}

			if (fileName != null) {
				this.setCoherenceStatsFileName(String.valueOf(fileName.get(0)));
			}
			

			if (isReachableTimeoutMiliSec != null) {
				this.setIsReachableTimeoutMiliSec((String
						.valueOf(isReachableTimeoutMiliSec.get(0))));
			}

			if (iPsToPingList != null) {
				this.setiPsToPingList(iPsToPingList);
			}
			
			if (isLoggingDebug()) {
				logDebug("CoherenceStatsScheduler.initializeConfiguration() method has been ended");
			}
		} catch (BBBSystemException exception) {
			logError(BBBCoreConstants.BBBSYSTEM_EXCEPTION, exception);
		} catch (BBBBusinessException exception) {
			logError(BBBCoreConstants.BBBBUSINESS_EXCEPTION, exception);
		}

	}

	/**
	 * @param queryResults
	 * @param regionName
	 * @param mBeanServer
	 *            parserQueryResult() method parse the JMX Query Result and
	 *            perform File Write operation.
	 */
	private void parserQueryResult(Set<ObjectInstance> queryResults,
			String regionName, MBeanServerConnection mBeanServer) {
		if (isLoggingDebug()) {
			logDebug("CoherenceStatsScheduler.parserQueryResult() method has been started");
			if(null != queryResults) {
				logDebug("CoherenceStatsScheduler.parserQueryResult() queryResults.size : "+queryResults.size());
			}
		}
		for (ObjectInstance objectInstance : queryResults) {
			ObjectName objectName = objectInstance.getObjectName();
			writeStaticsDataInFile(objectName, regionName, mBeanServer);
		}
		
		if (isLoggingDebug()) {
			logDebug("CoherenceStatsScheduler.parserQueryResult() method has been ended");
		}

	}

	/**
	 * isThresholdReachable() method to test Coherence server is reachable or
	 * not.
	 */
	private void isThresholdReachable() {
		if (isLoggingDebug()) {
			logDebug("CoherenceStatsScheduler.isThresholdReachable() method has been started");
		}
		InetAddress[] addresses = null;
		try {
			for (String siteName : getiPsToPingList()) {
				addresses = InetAddress.getAllByName(siteName);
				for (InetAddress address : addresses) {
					try {
						if (address.isReachable(Integer
								.valueOf(getIsReachableTimeoutMiliSec()))) {
							if (isLoggingDebug()) {
								logDebug(address
										+ BBBCoreConstants.THRESHOLDRECHABLE
										+ Integer
												.valueOf(getIsReachableTimeoutMiliSec())
										+ BBBCoreConstants.MILLISECONDS);
							}
						} else {
							logWarning(address
									+ BBBCoreConstants.THRESHOLDNOTRECHABLE
									+ Integer
											.valueOf(getIsReachableTimeoutMiliSec())
									+ BBBCoreConstants.MILLISECONDS);
						}

					} catch (IOException exception) {
						logError(siteName
								+ BBBCoreConstants.THRESHOLDIO_EXCEPTION
								+ exception);
					}
				}
			}
			
			if (isLoggingDebug()) {
				logDebug("CoherenceStatsScheduler.isThresholdReachable() method has been ended");
			}
		} catch (UnknownHostException exception) {
			logError(BBBCoreConstants.THRESHOLDUNKNOWNHOST_EXCEPTION, exception);
		}
	}

	/**
	 * @param objectName
	 * @param regionName
	 * @param mBeanServer
	 *            writeStaticsDataInFile() method writes coherence MBean
	 *            Attribute value writes in file .
	 */
	private void writeStaticsDataInFile(ObjectName objectName,
			String regionName, MBeanServerConnection mBeanServer) {
		if (isLoggingDebug()) {
			logDebug("CoherenceStatsScheduler.writeStaticsDataInFile() method has been started");
			logDebug("CoherenceStatsScheduler.writeStaticsDataInFile() objectName : "+objectName);
			logDebug("CoherenceStatsScheduler.writeStaticsDataInFile() regionName : "+regionName);
			logDebug("CoherenceStatsScheduler.writeStaticsDataInFile() mBeanServer : "+mBeanServer);
		}
		Writer fileWriter = null;
		try {
			fileWriter = new FileWriter(getCoherenceStatsFilePath()
					+ getCoherenceStatsFileName(), true);

			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			List<String> headers = getCoherenceStatsHeaders();
			String schemeId = objectName.getKeyProperty(BBBCoreConstants.TIER);
			String loader = objectName.getKeyProperty(BBBCoreConstants.LOADER);
			if (loader != null) {
				schemeId = schemeId + BBBCoreConstants.UNDERSCORE + loader;
			}

			try {
				bufferedWriter
						.append(String.valueOf(new Timestamp(
								new java.util.Date().getTime())))
						.append(getFieldDelimiter())
						.append(regionName)
						.append(getFieldDelimiter())
						.append(schemeId)
						.append(getFieldDelimiter())
						.append(String.valueOf(mBeanServer.getAttribute(
								objectName, headers.get(3).toString())))
						.append(getFieldDelimiter())
						.append(String.valueOf(mBeanServer.getAttribute(
								objectName, headers.get(4).toString())))
						.append(getFieldDelimiter())
						.append(String.valueOf(mBeanServer.getAttribute(
								objectName, headers.get(5).toString())))
						.append(getFieldDelimiter())
						.append(String.valueOf(mBeanServer.getAttribute(
								objectName, headers.get(6).toString())))
						.append(getFieldDelimiter())
						.append(String.valueOf(mBeanServer.getAttribute(
								objectName, headers.get(7).toString())))
						.append(getFieldDelimiter())
						.append(String.valueOf(mBeanServer.getAttribute(
								objectName, headers.get(8).toString())))
						.append(getFieldDelimiter())
						.append(String.valueOf(mBeanServer.getAttribute(
								objectName, headers.get(9).toString())))
						.append(getFieldDelimiter())
						.append(String.valueOf(mBeanServer.getAttribute(
								objectName, headers.get(10).toString())))
						.append(getFieldDelimiter())
						.append(String.valueOf(mBeanServer.getAttribute(
								objectName, headers.get(11).toString())))
						.append(getFieldDelimiter())
						.append(String.valueOf(mBeanServer.getAttribute(
								objectName, headers.get(12).toString())))
						.append(getFieldDelimiter())
						.append(String.valueOf(mBeanServer.getAttribute(
								objectName, headers.get(13).toString())))
						.append(getFieldDelimiter())
						.append(String.valueOf(mBeanServer.getAttribute(
								objectName, headers.get(14).toString())))
						.append(getFieldDelimiter())
						.append(String.valueOf(mBeanServer.getAttribute(
								objectName, headers.get(15).toString())))
						.append(getFieldDelimiter())
						.append(String.valueOf(mBeanServer.getAttribute(
								objectName, headers.get(16).toString())))
						.append(getFieldDelimiter())
						.append(String.valueOf(mBeanServer.getAttribute(
								objectName, headers.get(17).toString())));

				bufferedWriter.newLine();
				bufferedWriter.flush();
				
			} catch (AttributeNotFoundException exception) {
				logError(BBBCoreConstants.JMXATTRIBUTE_EXCEPTION, exception);
			} catch (InstanceNotFoundException exception) {
				logError(BBBCoreConstants.JMXINSTANCE_EXCEPTION, exception);
			} catch (MBeanException exception) {
				logError(BBBCoreConstants.JMXMBEAN_EXCEPTION, exception);
			} catch (ReflectionException exception) {
				logError(BBBCoreConstants.JMXREFELCTION_EXCEPTION, exception);
			} finally {
				bufferedWriter.close();
			}
		} catch (IOException exception) {
			logError(BBBCoreConstants.WRITELOGFILE_EXCEPTION, exception);
		}
		finally{
			if(fileWriter!=null){
				try {
					fileWriter.close();
				} catch (IOException e) {
					logError("IO exception occured while closing file writer", e);
				}
			}
		}
		if (isLoggingDebug()) {
			logDebug(BBBCoreConstants.MBEAN_VALUE + regionName);
			logDebug("CoherenceStatsScheduler.writeStaticsDataInFile() method has been ended");
		}
	}

	/**
	 * writeStaticsHeaderInFile() method writes coherence MBean Attribute name
	 * in file as Header.
	 */
	private void writeStaticsHeaderInFile() {
		if (isLoggingDebug()) {
			logDebug("CoherenceStatsScheduler.writeStaticsHeaderInFile() method has been started");
		}
		Writer fileWriter = null;
		try {
			fileWriter = new FileWriter(getCoherenceStatsFilePath()
					+ getCoherenceStatsFileName());
			// Always wrap FileWriter in BufferedWriter.
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			// Write Header in File System
			Iterator<String> header = getCoherenceStatsHeaders().iterator();
			while (header.hasNext()) {
				bufferedWriter.write(header.next());

				if (header.hasNext()) {
					bufferedWriter.write(getFieldDelimiter());
				}
			}
			bufferedWriter.newLine();
			bufferedWriter.close();

			if (isLoggingDebug()) {
				logDebug(BBBCoreConstants.HEADER + getCoherenceStatsFilePath()
						+ getCoherenceStatsFileName());
				logDebug("CoherenceStatsScheduler.writeStaticsHeaderInFile() method has been ended");
			
			}
		} catch (IOException exception) {
			logError(BBBCoreConstants.HEADERFILEPATH_EXCEPTION, exception);
		}
		finally{
			if(fileWriter!=null){
				try {
					fileWriter.close();
				} catch (IOException e) {
					logError("IO exception occured while closing file writer", e);
				}
			}
		}

	}

	/**
	 * @return MBeanServerConnection , JMX Connection from Localhost
	 */
	public MBeanServerConnection getMBeanServerConnection() {
		return ManagementFactory.getPlatformMBeanServer();
	}

	public String getCoherenceStatsFilePath() {
		return coherenceStatsFilePath;
	}

	/**
	 * @param coherenceStatsFilePath
	 */
	public void setCoherenceStatsFilePath(String coherenceStatsFilePath) {
		this.coherenceStatsFilePath = coherenceStatsFilePath;
	}

	public String getCoherenceStatsFileName() {
		return coherenceStatsFileName;
	}

	/**
	 * @param coherenceStatsFileName
	 */
	public void setCoherenceStatsFileName(String coherenceStatsFileName) {
		this.coherenceStatsFileName = coherenceStatsFileName;
	}

	public String getFieldDelimiter() {
		return fieldDelimiter;
	}

	/**
	 * @param fieldDelimiter
	 */
	public void setFieldDelimiter(String fieldDelimiter) {
		this.fieldDelimiter = fieldDelimiter;
	}

	public List<String> getCoherenceStatsHeaders() {
		return coherenceStatsHeaders;
	}

	/**
	 * @param coherenceStatsHeaders
	 */
	public void setCoherenceStatsHeaders(List<String> coherenceStatsHeaders) {
		this.coherenceStatsHeaders = coherenceStatsHeaders;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atg.service.scheduler.SchedulableService#setScheduler(atg.service.scheduler
	 * .Scheduler)
	 */
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	/**
	 * @return the getSchedule
	 */
	public Schedule getSchedule() {
		return schedule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atg.service.scheduler.SchedulableService#setSchedule(atg.service.scheduler
	 * .Schedule)
	 */
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	/**
	 * @return the getTransactionManager
	 */
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	/**
	 * @param transactionManager
	 */
	public void setTransactionManager(
			final TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * @return the getCoherenceMBeanlist
	 */
	public List<String> getCoherenceMBeanlist() {
		return coherenceMBeanlist;
	}

	/**
	 * @param coherenceMBeanlist
	 */
	public void setCoherenceMBeanlist(List<String> coherenceMBeanlist) {
		this.coherenceMBeanlist = coherenceMBeanlist;
	}

	/**
	 * @return the getIsReachableTimeoutMiliSec
	 */

	public String getIsReachableTimeoutMiliSec() {
		return isReachableTimeoutMiliSec;
	}

	/**
	 * @param isReachableTimeoutMiliSec
	 */
	public void setIsReachableTimeoutMiliSec(String isReachableTimeoutMiliSec) {
		this.isReachableTimeoutMiliSec = isReachableTimeoutMiliSec;
	}

	/**
	 * @return the getiPsToPingList
	 */
	public List<String> getiPsToPingList() {
		return iPsToPingList;
	}

	/**
	 * @param iPsToPingList
	 */
	public void setiPsToPingList(List<String> iPsToPingList) {
		this.iPsToPingList = iPsToPingList;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return the headerConfigList
	 */
	public List<String> getHeaderConfigList() {
		return headerConfigList;
	}

	/**
	 * @param headerConfigList
	 *            the headerConfigList to set
	 */
	public void setHeaderConfigList(List<String> headerConfigList) {
		this.headerConfigList = headerConfigList;
	}

	/**
	 * @return the mBeanConfigList
	 */
	public List<String> getmBeanConfigList() {
		return mBeanConfigList;
	}

	/**
	 * @param mBeanConfigList
	 *            the mBeanConfigList to set
	 */
	public void setmBeanConfigList(List<String> mBeanConfigList) {
		this.mBeanConfigList = mBeanConfigList;
	}

	/**
	 * @return the isEnable
	 */

	public boolean isEnable() {
		return isEnable;
	}

	/**
	 * @param isEnable
	 */
	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}

	/**
	 * @return the isThresholdReachableEnable
	 */
	public boolean isThresholdReachableEnable() {
		return isThresholdReachableEnable;
	}

	/**
	 * @param isThresholdReachableEnable
	 *            the isThresholdReachableEnable to set
	 */
	public void setThresholdReachableEnable(boolean isThresholdReachableEnable) {
		this.isThresholdReachableEnable = isThresholdReachableEnable;
	}

}
