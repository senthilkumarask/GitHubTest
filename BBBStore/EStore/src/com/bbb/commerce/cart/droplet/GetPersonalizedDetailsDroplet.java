package com.bbb.commerce.cart.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * This class is used to call the Exim Webservice to get the personalized 
 * details and set in commerce items
 * 
 * @author rjai39
 */
public class GetPersonalizedDetailsDroplet extends BBBDynamoServlet {
	
	private BBBEximManager eximManager;
	private ClientLockManager localLockManager;
	private TransactionManager transactionManager;
	private BBBOrderManager orderManager;
	/**
	 * @return the eximManager
	 */
	public BBBEximManager getEximManager() {
		return eximManager;
	}

	/**
	 * @param eximManager the eximManager to set
	 */
	public void setEximManager(BBBEximManager eximManager) {
		this.eximManager = eximManager;
	}
	
	/**
	 * @return the orderManager
	 */
	public BBBOrderManager getOrderManager() {
		return this.orderManager;
	}

	/**
	 * @param orderManager the orderManager to set
	 */
	public void setOrderManager(BBBOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	
	/**
	 * @return the transactionManager
	 */
	public TransactionManager getTransactionManager() {
		return this.transactionManager;
	}

	/**
	 * @param transactionManager the transactionManager to set
	 */
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * @return the localLockManager
	 */
	public ClientLockManager getLocalLockManager() {
		return this.localLockManager;
	}

	/**
	 * @param localLockManager the localLockManager to set
	 */
	public void setLocalLockManager(ClientLockManager localLockManager) {
		this.localLockManager = localLockManager;
	}
	
	/**
	 * This method calls the Exim Webservice to get the personalized details 
	 * and set details in commerce items
	 * 
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @throws ServletException
	 *             , IOException
	 */
	
    @Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
    	
    	logDebug("Start service of GetPersonalizedDetails");
    	BBBOrder order = (BBBOrder)pRequest.getObjectParameter("pOrder");
    	Profile profile = (Profile)pRequest.getObjectParameter("pProfile");
    	boolean isEximWebserviceErr = false;
    	
    	if(null != order && null != profile){
	    	ClientLockManager lockManager = getLocalLockManager();
			boolean acquireLock = false;
			boolean shouldRollback = false;
			TransactionDemarcation td = new TransactionDemarcation();
			try {
				acquireLock = !lockManager.hasWriteLock(profile.getRepositoryId(), Thread.currentThread());
				if (acquireLock) {
					lockManager.acquireWriteLock(profile.getRepositoryId(), Thread.currentThread());
				}
				td.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

				synchronized (order) {
					if("true".equalsIgnoreCase(getEximManager().getKatoriAvailability())) {
					List<CommerceItem> commItems = order.getCommerceItems();
					isEximWebserviceErr = getEximManager().setEximDetailsbyMultiRefNumAPI(commItems, order);
					if(!isEximWebserviceErr){
						logDebug("Updating the order as webservice failure flag is false");
						getOrderManager().updateOrder(order);
					}
					}
				}
			} catch (DeadlockException e) {
				shouldRollback = true;
				this.logError("DeadlockException while service of GetPersonalizedDetails", e);
			} catch (TransactionDemarcationException e) {
				shouldRollback = true;
				this.logError("TransactionDemarcationException while service of GetPersonalizedDetails", e);
			}catch (CommerceException e) {
				shouldRollback = true;
				this.logError("CommerceException while service of GetPersonalizedDetails", e);
			}
			finally {
				logDebug("Ends service of GetPersonalizedDetails");
				try {
					td.end(shouldRollback);
				} catch (TransactionDemarcationException e) {
					this.logError("TransactionDemarcationException while creating Rollback transaction", e);
	
				} finally {
					if (acquireLock){
						try {
							lockManager.releaseWriteLock(profile.getRepositoryId(), Thread.currentThread(), true);
						} catch (LockManagerException e) {
							this.logError("TransactionDemarcationException releasing lock on profile", e);
	
						}
					}
				}
		    }
	    	pRequest.setParameter("isEximWebServiceErr", isEximWebserviceErr);
	    	pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
    	}
	}
}
