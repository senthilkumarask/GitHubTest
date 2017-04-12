package atg.commerce.order.processor;

import java.util.HashMap;

import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PipelineConstants;
import atg.core.util.ResourceUtils;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.service.pipeline.PipelineResult;

import com.bbb.ecommerce.order.BBBOrder;

/**
 * This component add tax information of anonymous user to database then calls
 * super method to continue OOTB functionality.
 * 
 * @author Sapient Consulting Pvt. Ltd.
 * 
 */
public class BBBProcAddOrderToRepository extends ProcAddOrderToRepository {
	private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(
			MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

	/**
	 * This method calls addOrder of OrderManager, if the order is transient, as
	 * addOrder in turn calls updateOrder method, otherwise updateOrder is
	 * called for persistent order.
	 * 
	 * @param pParam
	 * @param pResult
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
		HashMap paramMap = (HashMap) pParam;
		OrderManager orderManager = (OrderManager) paramMap.get(PipelineConstants.ORDERMANAGER);
		BBBOrder order = (BBBOrder) paramMap.get(PipelineConstants.ORDER);

		if (order == null) {
			vlogError("BBBProcAddOrderToRepository.runProcess: Order is null.");
			throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter", MY_RESOURCE_NAME,
					sResourceBundle));
		}
		if (orderManager == null) {
			vlogError("BBBProcAddOrderToRepository.runProcess: OrderManager is null.");
			throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderManagerParameter",
					MY_RESOURCE_NAME, sResourceBundle));
		}

		if (order.isTransient() && !order.getAnonymousOrderTaxItem().isEmpty()) {
			/*
			 * In case of transient order we are saving order tax
			 * info(dsLineItemTaxInfo) to database while committing order, means
			 * in process order pipeline.
			 */
			for (MutableRepositoryItem orderTaxItem : order.getAnonymousOrderTaxItem().values()) {
				((MutableRepository) orderManager.getOrderTools().getOrderRepository()).addItem(orderTaxItem);
			}
			vlogDebug("BBBProcAddOrderToRepository.runProcess: dsLineItemTaxInfo for order {0} is {1}", order,
					order.getAnonymousOrderTaxItem());
		}
		return super.runProcess(pParam, pResult);
	}
}
