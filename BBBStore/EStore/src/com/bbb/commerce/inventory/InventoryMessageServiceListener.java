package com.bbb.commerce.inventory;

import java.io.StringReader;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.jaxb.inventory.InventoryDecrement;
import com.bbb.framework.messaging.MessageServiceListener;
import atg.nucleus.ServiceException;
import com.bbb.framework.jaxb.inventory.HeaderElement;
import com.bbb.framework.jaxb.inventory.InventoryDataCenterDecrement;
/**
 * @author vchan5
 * 
 */
public class InventoryMessageServiceListener extends MessageServiceListener {

	/**
	 * This variable is used to get the JMS provider Details
	 */
	/*
	 * private MessageServiceProvider provider;
	 *//**
	 * The Variable will hold the Queue Name for which listener should listen
	 */
	/*
	 * private String queueName;
	 *//**
	 * This Variable will used to identify what type connection to create
	 * eg.Queue or Topic
	 */
	/*
	 * private String connectionType;
	 *//**
	 * The variable is used to store the Connection Instance
	 */
	/*
	 * Connection connection;
	 *//**
	 * 
	 */
	/*
	 * Session session;
	 */
    private boolean enableService;

	/**
	 * JAXB context instance
	 */
	private JAXBContext context;

	/**
	 * @return the context
	 */
	public JAXBContext getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(JAXBContext context) {
		this.context = context;
	}

	public static OnlineInventoryManager onlineInventoryManager;
	private OnlineInventoryManager mInventoryManager;

	/**
	 * @return the inventoryManager
	 */
	public OnlineInventoryManager getInventoryManager() {
		return mInventoryManager;
	}

	/**
	 * @param inventoryManager
	 *            the inventoryManager to set
	 */
	public void setInventoryManager(OnlineInventoryManager mInventoryManager) {
		this.mInventoryManager = mInventoryManager;
	}

	/*
	 * This Method is called when the new JMS message is received to the Queue
	 * 
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	
	@Override
	public void onMessage(Message message) {
	    if(isEnableService()) {
    		Unmarshaller unMarshaller = null;
    
    		try {
    
    			if (message.getJMSType() != null && getDestinationFactory().getDestination(getService()).getJmsType() != null
    					&& message.getJMSType().equals(getDestinationFactory().getDestination(getService()).getJmsType())) {
    
    				TextMessage txtMessage = (TextMessage) message;
    				logDebug("Message Type" + message.getJMSType());
    				
    				if (getContext() == null) {
    					setContext(JAXBContext.newInstance(InventoryDataCenterDecrement.class));
    				}
    
    				unMarshaller = getContext().createUnmarshaller();
    				Source source = new StreamSource(new StringReader(txtMessage.getText()));
    				InventoryDataCenterDecrement item = (InventoryDataCenterDecrement) unMarshaller.unmarshal(source);
    				List<InventoryDecrement.Item> sku = item.getItemList().getItem();
    				HeaderElement header =  item.getHeader();
    				logDebug("*********Message Recieved***********");
    				logDebug(txtMessage.getText());
    				
    				InventoryVO[] inventoryVOs = new InventoryVO[sku.size()];
    				for (int i = 0; i < sku.size(); i++) {
    					inventoryVOs[i] = new InventoryVO();
      					inventoryVOs[i].setSkuID(sku.get(i).getSku());
    					inventoryVOs[i].setSiteID(header.getSite().value());
    					inventoryVOs[i].setOrderedQuantity(Long.parseLong(sku.get(i).getQty()));
    					
						logDebug("ordered Items:");
						logDebug("sku id:" + sku.get(i).getSku());
						logDebug("Site Id" + header.getSite().value());
						logDebug("Ordered Quantity" + Long.parseLong(sku.get(i).getQty()));
    					logDebug("No of items ordered :-->" + inventoryVOs.length);
    					
    				}
    				if (getInventoryManager() != null) {
    					getInventoryManager().decrementInventoryStock(inventoryVOs);
    				}
    
    			}
    		} catch (JMSException JMSe) {
    			if (isLoggingError()) {
    				logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1027 + ": Messaging Exception :::" + JMSe);
    			}
    		} catch (JAXBException jaxbe) {
    			if (isLoggingError()) {
    				logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1028 + ": Parssing Exception :::" + jaxbe);
    			}
    		} catch (BBBSystemException bse) {
    			if (isLoggingError()) {
    				logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1029 + ": System Exception :::" + bse);
    			}
    		}catch (Exception e) {
    			if (isLoggingError()) {
    				logError("Exception :::" + e);
    			}
    		}
	    }

	}

    public boolean isEnableService() {
        return enableService;
    }
    
    public boolean getEnableService() {
        return enableService;
    }

    public void setEnableService(boolean enableService) {
        this.enableService = enableService;
    }

    @Override
    public void doStartService() throws ServiceException {
    	if(!isEnableService()){
    		logInfo("InventoryMessageServiceListener is not enabled");
    		return;
    	}
    	super.doStartService();
    }
}
