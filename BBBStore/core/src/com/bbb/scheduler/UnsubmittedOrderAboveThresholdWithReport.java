/**
 * The UnsubmittedOrderAboveThresholdWithReport is a ATG scheduler component reports the unsubmitted orders more than specific hours
 */

package com.bbb.scheduler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.transaction.TransactionManager;

import atg.commerce.order.OrderManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public class UnsubmittedOrderAboveThresholdWithReport extends
		SingletonSchedulableService {

	private Schedule mSchedule;
	private Scheduler mScheduler;
	private OrderManager mOrderManager;
	private static final String ORDER_DESCRIPTOR = "order";
	private static final String CREATED_BY_ANNAPOLIS = "DC1";
	private static final String CREATED_BY_SANDEIGO = "DC2";

	private String mJobName;
	private String mJobDescription;
	private String rqlQuery;
	private String rqlQueryReport;
	private String recipientFrom;
	private String recipientTo;

	private TransactionManager mTransactionManager;
	private int mMaxItemsPerTransaction;
	private boolean mSchedulerEnabled = false;
	private List<ReportDataVO> reportData;
	private BBBCatalogTools mCatalogTools;

	/**
	 * get the maximum transactions performed in batch for removing orders
	 * 
	 * @return
	 */
	public int getMaxItemsPerTransaction() {
		return mMaxItemsPerTransaction;
	}

	/**
	 * set the maximum transactions which can be performed in batch process
	 * 
	 * @param mMaxItemsPerTransaction
	 */
	public void setMaxItemsPerTransaction(int pMaxItemsPerTransaction) {
		mMaxItemsPerTransaction = pMaxItemsPerTransaction;
	}

	/**
	 * Get the transaction manager component for the scheduler
	 * 
	 * @return
	 */
	public TransactionManager getTransactionManager() {
		return mTransactionManager;
	}

	/**
	 * Sets the transaction manager component for the scheduler
	 * 
	 * @param pTransactionManager
	 */
	public void setTransactionManager(TransactionManager pTransactionManager) {
		mTransactionManager = pTransactionManager;
	}

	/**
	 * Get the job name for the scheduler
	 */
	public String getJobName() {
		return mJobName;
	}

	/**
	 * Set the job name for the scheduler
	 */
	public void setJobName(String pJobName) {
		this.mJobName = pJobName;
	}

	/**
	 * Get the job description for the scheduler
	 */
	public String getJobDescription() {
		return mJobDescription;
	}

	/**
	 * Set the job description for the scheduler
	 */
	public void setJobDescription(String pJobDescription) {
		this.mJobDescription = pJobDescription;
	}

	/**
	 * method returns the OrderManager component
	 * 
	 * @return
	 */
	public OrderManager getOrderManager() {
		return mOrderManager;
	}

	/**
	 * setter method sets the OrderManager from the properties file
	 * 
	 * @param pOrderManager
	 */
	public void setOrderManager(OrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}

	/**
	 * method returns the schedule for running the scheduler
	 * 
	 * @return
	 */
	public Schedule getSchedule() {
		return mSchedule;
	}

	/**
	 * setter method sets the schedule for running the scheduler from the
	 * properties file
	 * 
	 * @param pSchedule
	 */
	public void setSchedule(Schedule pSchedule) {
		mSchedule = pSchedule;
	}

	/**
	 * method returns the ATG scheduler object
	 * 
	 * @return
	 */
	public Scheduler getScheduler() {
		return mScheduler;
	}

	/**
	 * setter method sets the ATG scheduler from the properties file
	 * 
	 * @param pScheduler
	 */
	public void setScheduler(Scheduler pScheduler) {
		mScheduler = pScheduler;
	}

	/**
	 * @return the rqlQuery
	 */
	public String getRqlQuery() {
		return rqlQuery;
	}

	/**
	 * @param rqlQuery
	 *            the rqlQuery to set
	 */
	public void setRqlQuery(String rqlQuery) {
		this.rqlQuery = rqlQuery;
	}

	/**
	 * @return the rqlQueryReport
	 */
	public String getRqlQueryReport() {
		return rqlQueryReport;
	}

	/**
	 * @param rqlQuery
	 *            the rqlQuery to set
	 */
	public void setRqlQueryReport(String rqlQueryReport) {
		this.rqlQueryReport = rqlQueryReport;
	}

	/**
	 * @return the schedulerEnabled
	 */
	public final boolean isSchedulerEnabled() {
		return mSchedulerEnabled;
	}

	/**
	 * @param pSchedulerEnabled
	 *            the schedulerEnabled to set
	 */
	public final void setSchedulerEnabled(boolean pSchedulerEnabled) {
		mSchedulerEnabled = pSchedulerEnabled;
	}

	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.mCatalogTools = catalogTools;
	}

	@Override
	/**
	 * Overridden method which is invoked on call of the scheduler.
	 * The method calls the findUnsubmittedOrdersAboveThresholdHours to start the find the unsubmitted orders
	 */
	public void doScheduledTask(Scheduler pScheduler, ScheduledJob pScheduledjob) {
		this.doScheduledTask();
	}

	public void doScheduledTask() {

		long startTime = System.currentTimeMillis();
		if (isLoggingInfo()) {
			logInfo("Entry doScheduledTask");
		}
		if (isSchedulerEnabled()) {
			findUnsubmittedOrdersAboveThresholdHours();
		}
		long endTime = System.currentTimeMillis();
		if (isLoggingInfo()) {
			logInfo("Total time took for the job::" + (endTime - startTime)
					+ " milliseconds");
			logInfo("Exit doScheduledTask");
		}
	}

	/**
	 * The method runs the query to find the orders unsubmitted more than
	 * specified hours
	 * 
	 * @return RepositoryItem[]
	 */
	private Integer[] findAllUnsubmittedOrdersMoreThanHours() {
		if (isLoggingInfo()) {
			logInfo("findAllUnsubmittedOrdersMoreThanHours::");
			logInfo("range provided::" + getMaxItemsPerTransaction());
			logInfo("query provided::" + this.getRqlQuery());
		}
		long startTime = System.currentTimeMillis();
		Repository orderRepository = getOrderManager().getOrderTools()
				.getOrderRepository();
		int dcOne = 0;
		int dcTwo = 0;
		try {
			// StringBuffer msg1 = new StringBuffer();
			RepositoryItemDescriptor orderDescriptor = orderRepository
					.getItemDescriptor(ORDER_DESCRIPTOR);
			RepositoryView orderView = orderDescriptor.getRepositoryView();
			RqlStatement statement = null;
			if (orderView != null) {
				Object[] params = new Object[1];
				params[0] = CREATED_BY_ANNAPOLIS;

				statement = RqlStatement.parseRqlStatement(getRqlQuery());
				if (isLoggingDebug()) {
					logDebug("query::" + statement.getQuery().toString());

				}

				dcOne = statement.executeCountQuery(orderView, params);
				if (isLoggingDebug()) {
					logDebug("DC1::" + dcOne);
				}

				if (isLoggingDebug()) {
					logDebug("query::" + statement.getQuery().toString());

				}

				params[0] = CREATED_BY_SANDEIGO;
				statement = RqlStatement.parseRqlStatement(getRqlQuery());
				dcTwo = statement.executeCountQuery(orderView, params);
				if (isLoggingDebug()) {
					logDebug("DC2::" + dcTwo);
				}

			} else {
				if (isLoggingDebug()) {
					logDebug("order view is null::");
				}
			}
		} catch (RepositoryException e) {
			if (isLoggingError()) {
				logError(
						"Repository Exception in findAllUnsubmittedOrdersMoreThanHours:",
						e);
			}
		}
		long endTime = System.currentTimeMillis();
		if (isLoggingInfo()) {
			logInfo("Total time took to find  orders::" + (endTime - startTime)
					+ " milliseconds");
			logInfo("Exit findAllUnsubmittedOrdersMoreThanHours::");
		}
		Integer[] detailsOfDC = new Integer[2];

		detailsOfDC[0] = dcOne;
		detailsOfDC[1] = dcTwo;

		return detailsOfDC;
	}

	/**
	 * The method find the orders unsubmitted more than specified hours
	 * 
	 * @return RepositoryItem[]
	 */
	private void findUnsubmittedOrdersAboveThresholdHours() {
		if (isLoggingInfo()) {
			logInfo("findUnsubmittedOrdersAboveThresholdHours::");
			logInfo("range provided::" + getMaxItemsPerTransaction());
			logInfo("query provided::" + this.getRqlQueryReport());
		}
		long startTime = System.currentTimeMillis();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		DateFormat dateFormat = new SimpleDateFormat("HH");
		String dateInStringPastWeekStartTime;
		String dateInStringPastWeekEndTime;
		String dateInStringTodayStartTime;
		String dateInStringTodayEndTime;

		// TODAY
		Calendar now = Calendar.getInstance();
		// LAST WEEK
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);

		StringBuffer msg = new StringBuffer();
		String host = "localhost";
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		Session session = Session.getDefaultInstance(properties);

		try {
			String to = getCatalogTools().getAllValuesForKey(
					"unsubmittedOrdersSchedulerConfigValues", "toRecipient")
					.get(0);
			String from = getCatalogTools().getAllValuesForKey(
					"unsubmittedOrdersSchedulerConfigValues", "fromRecipient")
					.get(0);
			int threshouldLimit = Integer.parseInt(getCatalogTools()
					.getAllValuesForKey(
							"unsubmittedOrdersSchedulerConfigValues",
							"threshouldLimit").get(0));
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));
			message.setSubject("Un-submitted orders and submitted orders with ranges");
			msg.append("Hello Team, <br/>");
			msg.append("Please find the unsubmitted order for Annapolis and San Degio[if any above threshold limit] and the submitted order report for prest and past week:<br/><br/>");
			Integer[] detailsOfDC = findAllUnsubmittedOrdersMoreThanHours();
			int dcOne = (Integer) detailsOfDC[0];
			int dcTwo = (Integer) detailsOfDC[1];
			if (dcOne != 0 && dcOne > threshouldLimit) {
				msg.append(dcOne
						+ " no of unsubmitted orders in Annapolis<br/>");
			}
			if (dcTwo != 0 && dcTwo > threshouldLimit) {
				msg.append(dcTwo
						+ " no of unsubmitted orders in San Diego<br/><br/>");
			}
			msg.append("<br/>Order Comparison<br/><br/>");
			msg.append("<table style='width:70%' border='0.5'><tr><td colspan='2'>Last Week</td><td colspan='2'>Present Day</td><td colspan='2'>Last Week Vs Today</td></tr><tr><td>Date</td><td>Count</td><td>Date</td><td>Count</td><td>Order Difference</td><td>Percent Diff</td></tr>");
			if (isLoggingDebug()) {
				logDebug("reportData reportData reportData  for--------------------"
						+ reportData);
			}
			int maxLimit = Integer.parseInt(dateFormat.format(now.getTime())
					.toString());
			for (int ii = 0; ii < maxLimit; ii++) {
				dateInStringPastWeekStartTime = cal.get(Calendar.DATE) + "-"
						+ (cal.get(Calendar.MONTH) + 1) + "-"
						+ cal.get(Calendar.YEAR) + " " + ii + ":00:00";
				dateInStringPastWeekEndTime = cal.get(Calendar.DATE) + "-"
						+ (cal.get(Calendar.MONTH) + 1) + "-"
						+ cal.get(Calendar.YEAR) + " " + ii + ":59:00";
				dateInStringTodayStartTime = now.get(Calendar.DATE) + "-"
						+ (now.get(Calendar.MONTH) + 1) + "-"
						+ now.get(Calendar.YEAR) + " " + ii + ":00:00";
				dateInStringTodayEndTime = now.get(Calendar.DATE) + "-"
						+ (now.get(Calendar.MONTH) + 1) + "-"
						+ now.get(Calendar.YEAR) + " " + ii + ":59:00";
				ReportDataVO reportDataVO = new ReportDataVO();

				Date dateTodayStartTime = null;
				Date dateTodayEndTime = null;
				Date pastWeekStartTime = null;
				Date pastWeekEndTime = null;

				try {
					dateTodayStartTime = sdf.parse(dateInStringTodayStartTime);
					dateTodayEndTime = sdf.parse(dateInStringTodayEndTime);
					pastWeekStartTime = sdf
							.parse(dateInStringPastWeekStartTime);
					pastWeekEndTime = sdf.parse(dateInStringPastWeekEndTime);

				} catch (ParseException e) {
					if (isLoggingError()) {
						logError("Parse Exception:", e);
					}

				}

				Repository orderRepository = getOrderManager().getOrderTools()
						.getOrderRepository();
				try {
					RepositoryItemDescriptor orderDescriptor = orderRepository
							.getItemDescriptor(ORDER_DESCRIPTOR);
					RepositoryView orderView = orderDescriptor
							.getRepositoryView();
					RqlStatement statement = null;
					if (orderView != null) {
						Object[] params = new Object[2];

						java.sql.Timestamp ts1 = new java.sql.Timestamp(
								dateTodayStartTime.getTime());
						java.sql.Timestamp ts2 = new java.sql.Timestamp(
								dateTodayEndTime.getTime());

						params[0] = ts1;
						params[1] = ts2;
						statement = RqlStatement
								.parseRqlStatement(getRqlQueryReport());
						if (isLoggingDebug()) {
							logDebug("query::"
									+ statement.getQuery().toString());
							logDebug("param 0::" + ts1);
							logDebug("param 1::" + ts2);

						}
						int orderCountToday = 0;
						orderCountToday = statement.executeCountQuery(
								orderView, params);

						if (isLoggingDebug()) {
							logDebug("ii::" + ii + "---expiredOrderLength:"
									+ orderCountToday);

						}

						reportDataVO.setTodayDate(dateInStringTodayStartTime);
						reportDataVO
								.setLastWeekDate(dateInStringPastWeekStartTime);
						reportDataVO.setTotalCountToday("" + orderCountToday);

						// FOR PAST WEEk
						params = new Object[2];

						ts1 = new java.sql.Timestamp(
								pastWeekStartTime.getTime());
						ts2 = new java.sql.Timestamp(pastWeekEndTime.getTime());

						params[0] = ts1;
						params[1] = ts2;
						statement = RqlStatement
								.parseRqlStatement(getRqlQueryReport());
						if (isLoggingDebug()) {
							logDebug("query::"
									+ statement.getQuery().toString());
							logDebug("param 0::" + ts1);
							logDebug("param 1::" + ts2);

						}
						int orderCountPastWeek = statement.executeCountQuery(
								orderView, params);

						if (isLoggingDebug()) {
							logDebug("ii::" + ii + "---expiredOrderLength:"
									+ orderCountPastWeek);

						}

						reportDataVO.setTotalCountLastWeek(""
								+ orderCountPastWeek);
						msg.append("<tr>");
						msg.append("<td>" + reportDataVO.getLastWeekDate()
								+ "</td>");
						msg.append("<td>"
								+ reportDataVO.getTotalCountLastWeek()
								+ "</td>");
						msg.append("<td>" + reportDataVO.getTodayDate()
								+ "</td>");
						msg.append("<td>" + reportDataVO.getTotalCountToday()
								+ "</td>");
						msg.append("<td>" + reportDataVO.getDifference()
								+ "</td>");
						msg.append("<td>" + reportDataVO.getPercentDifference()
								+ "</td>");
						msg.append("</tr>");

					} else {
						if (isLoggingDebug()) {
							logDebug("order view is null::");
						}
					}
				} catch (Exception e) {
					if (isLoggingError()) {
						logError(
								"Repository Exception in findAllUnsubmittedOrdersMoreThanHours:",
								e);
					}
				}

			}

			msg.append("</table>");
			msg.append("<br/><br/>Regards,<br/>PSI8 Team");
			message.setText(msg.toString());
			message.setContent(msg.toString(), "text/html; charset=utf-8");
			Transport.send(message);

			if (isLoggingInfo()) {
				logInfo("Sent message successfully...");
			}
		} catch (MessagingException mex) {
			mex.printStackTrace();
		} catch (BBBBusinessException ex) {
			ex.printStackTrace();
		} catch (BBBSystemException ex) {
			ex.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		if (isLoggingInfo()) {
			logInfo("Total time took to find  orders::" + (endTime - startTime)
					+ " milliseconds");
			logInfo("Exit findAllUnsubmittedOrdersMoreThanHours::");
		}

	}

	/**
	 * @return the reportData
	 */
	public List<ReportDataVO> getReportData() {
		return reportData;
	}

	/**
	 * @param reportData
	 *            the reportData to set
	 */
	public void setReportData(List<ReportDataVO> reportData) {
		this.reportData = reportData;
	}

	/**
	 * @return the recipientFrom
	 */
	public String getRecipientFrom() {
		return recipientFrom;
	}

	/**
	 * @param recipientFrom
	 *            the recipientFrom to set
	 */
	public void setRecipientFrom(String recipientFrom) {
		this.recipientFrom = recipientFrom;
	}

	/**
	 * @return the recipientTo
	 */
	public String getRecipientTo() {
		return recipientTo;
	}

	/**
	 * @param recipientTo
	 *            the recipientTo to set
	 */
	public void setRecipientTo(String recipientTo) {
		this.recipientTo = recipientTo;
	}
}