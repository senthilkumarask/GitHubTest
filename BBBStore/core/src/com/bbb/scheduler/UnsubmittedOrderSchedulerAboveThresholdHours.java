package com.bbb.scheduler;

import java.sql.Timestamp;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.transaction.TransactionManager;

import com.bbb.commerce.catalog.BBBCatalogTools;

import atg.commerce.order.OrderManager;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

public class UnsubmittedOrderSchedulerAboveThresholdHours extends
		SingletonSchedulableService {

	private Schedule mSchedule;
	private Scheduler mScheduler;
	private OrderManager mOrderManager;
	private static final String ORDER_DESCRIPTOR = "order";
	private static final long MILLISECONDS_IN_HOUR = 3600000;

	private String mJobName;
	private String mJobDescription;
	private String recipientFrom;
	private String recipientTo;
	private String rqlQuery;

	private TransactionManager mTransactionManager;
	private BBBCatalogTools mCatalogTools;

	private boolean mSchedulerEnabled = false;

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
	 * The method calls the processUncompleteOrdersAboveThreshold to start the purging process
	 */
	public void doScheduledTask(Scheduler pScheduler, ScheduledJob pScheduledjob) {
		this.doScheduledTask();
	}

	public void doScheduledTask() {

		long startTime = System.currentTimeMillis();
		if (isLoggingInfo()) {
			logInfo("Entry doScheduledTask");
		}
		processUncompleteOrdersAboveThreshold();
		long endTime = System.currentTimeMillis();
		if (isLoggingInfo()) {
			logInfo("Total time took for the job::" + (endTime - startTime)
					+ " milliseconds");
			logInfo("Exit doScheduledTask");
		}
	}

	/**
	 * The method loops through the orders which are eligible to be and calls
	 * the OrderManager component to remove these orders
	 */
	public void processUncompleteOrdersAboveThreshold() {
		if (isLoggingDebug()) {
			logDebug("processUncompleteOrdersAboveThreshold::");
		}
		if (isSchedulerEnabled()) {
			TransactionDemarcation td = new TransactionDemarcation();
			boolean rollback = true;
			try {
				String to = getCatalogTools().getAllValuesForKey(
						"unsubmittedOrdersSchedulerConfigValues", "toRecipient").get(0);
				String from = getCatalogTools().getAllValuesForKey(
						"unsubmittedOrdersSchedulerConfigValues", "fromRecipient").get(0);

				int thresholdTime = Integer.parseInt(getCatalogTools()
						.getAllValuesForKey("unsubmittedOrdersSchedulerConfigValues",
								"thresholdTime").get(0));

				RepositoryItem[] expiredOrders = findAllUncompleteOrders(thresholdTime);
				StringBuffer msg = new StringBuffer();

				if (expiredOrders != null && expiredOrders.length > 0) {
					if (isLoggingInfo()) {
						logInfo("Order count : " + expiredOrders.length);
					}
					msg.append("Hello Team, \n\n");
					msg.append("Please find the unsubmitted order retained for more than "
							+ thresholdTime + " hours\n\n");
					msg.append(expiredOrders.length
							+ " orders that are unsubmitted are:\n\n");
					for (int i = 0; i < expiredOrders.length; i++) {

						/*
						 * if(expiredOrders[i].getPropertyValue("tbsAssociateID")
						 * !=null &&
						 * expiredOrders[i].getPropertyValue("tbsStoreNo"
						 * )!=null) { msg.append("TBS Order - "+
						 * expiredOrders[i]
						 * .getPropertyValue("onlineOrderNumber")+ "\n"); } else
						 * {
						 */
						if (expiredOrders[i]
								.getPropertyValue("onlineOrderNumber") != null) {
							msg.append(expiredOrders[i]
									.getPropertyValue("onlineOrderNumber")
									+ "\n");
						}
						// }
						if (expiredOrders[i]
								.getPropertyValue("bopusOrderNumber") != null) {
							msg.append(expiredOrders[i]
									.getPropertyValue("bopusOrderNumber")
									+ "\n");
						}

					}

					// SEND EMAIL the list of Orders with count

					/*
					 * String to = getRecipientTo(); String from =
					 * getRecipientFrom();
					 */
					String host = "localhost";
					// Get system properties
					Properties properties = System.getProperties();
					// Setup mail server
					properties.setProperty("mail.smtp.host", host);
					// Get the default Session object.
					Session session = Session.getDefaultInstance(properties);

					try {
						// Create a default MimeMessage object.
						MimeMessage message = new MimeMessage(session);
						// Set From: header field of the header.
						message.setFrom(new InternetAddress(from));
						// Set To: header field of the header.
						message.addRecipient(Message.RecipientType.TO,
								new InternetAddress(to));
						message.setSubject("Un-submitted orders above "
								+ thresholdTime + " hours");
						msg.append("\n\nRegards,\nPSI8 Team");
						message.setText(msg.toString());
						Transport.send(message);

						if (isLoggingInfo()) {
							logInfo("Sent message successfully...");
						}
					} catch (MessagingException mex) {
						mex.printStackTrace();
					}
					// iterate expiredOrders repository get the Order numbers

				}
			} catch (Exception e) {
				if (isLoggingError()) {
					logError(
							"CommerceException from processUncompleteOrdersAboveThreshold:",
							e);
				}
			} finally {
				if (td != null) {
					try {
						td.end(rollback);
					} catch (TransactionDemarcationException tde) {
						logError("Error ending transaction::" + tde);
					}
				}
			}

			if (isLoggingDebug()) {
				logDebug("Exit processUncompleteOrdersAboveThreshold");
			}
		}
	}

	/**
	 * The method finds the uncomplete order beyond threshold time
	 * 
	 * @return RepositoryItem[]
	 */
	private RepositoryItem[] findAllUncompleteOrders(int thresholdTime) {
		if (isLoggingInfo()) {
			logInfo("findAllUncompleteOrders::");
			logInfo("query provided::" + this.getRqlQuery());
		}
		long startTime = System.currentTimeMillis();
		Repository orderRepository = getOrderManager().getOrderTools()
				.getOrderRepository();
		RepositoryItem[] expiredOrders = null;
		try {
			RepositoryItemDescriptor orderDescriptor = orderRepository
					.getItemDescriptor(ORDER_DESCRIPTOR);
			RepositoryView orderView = orderDescriptor.getRepositoryView();
			RqlStatement statement = null;
			if (orderView != null) {
				Object[] params = new Object[1];
				Timestamp timestamp = new Timestamp(System.currentTimeMillis()
						- (thresholdTime * MILLISECONDS_IN_HOUR));
				params[0] = timestamp;

				statement = RqlStatement.parseRqlStatement(getRqlQuery());
				if (isLoggingDebug()) {
					logDebug("query::" + statement.getQuery().toString());

				}
				expiredOrders = statement.executeQuery(orderView, params);
				if (expiredOrders != null) {
					if (isLoggingDebug()) {
						logDebug("total orders ::" + expiredOrders.length);
					}
				} else {
					if (isLoggingInfo()) {
						logInfo("no orders found");
					}
				}
			} else {
				if (isLoggingDebug()) {
					logDebug("order view is null::");
				}
			}
		} catch (RepositoryException e) {
			if (isLoggingError()) {
				logError("Repository Exception in findAllUncompleteOrders:", e);
			}
		}
		long endTime = System.currentTimeMillis();
		if (isLoggingInfo()) {
			logInfo("Total time took to find orders::" + (endTime - startTime)
					+ " milliseconds");
			logInfo("Exit findAllUncompleteOrders::");
		}
		return expiredOrders;
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