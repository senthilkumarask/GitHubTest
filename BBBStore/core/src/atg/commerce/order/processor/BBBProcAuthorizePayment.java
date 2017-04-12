package atg.commerce.order.processor;

import java.util.ResourceBundle;

import atg.commerce.order.PaymentGroup;
import atg.service.pipeline.PipelineResult;

import com.bbb.commerce.order.Paypal;
import com.bbb.constants.BBBCoreErrorConstants;

public class BBBProcAuthorizePayment extends ProcAuthorizePayment{
	 /**
	   * This method handles adding authorization error messages to the pipeline result
	   * object.  It creates an errorMessage and errorKey and adds these to the ResultObject.
	   *
	   * @param pFailedPaymentGroup the payment group that failed to authorize
	   * @param pStatusMessage message indicating why the payment group failed to authorize
	   * @param pResult the pipeline result object.
	   * @param pBundle resource bundle specific to users locale
	   */
	  protected void addPaymentGroupError(PaymentGroup pFailedPaymentGroup, String pStatusMessage,
	                                      PipelineResult pResult, ResourceBundle pBundle)
	  {
		  if(pFailedPaymentGroup instanceof Paypal){
			  	String errorKey = BBBCoreErrorConstants.PIPELINE_ERR_PAYPAL_AUTH  + pFailedPaymentGroup.getId();
			    pResult.addError(errorKey, pStatusMessage);
		  } else {
			  callSuperAddPaymentGroupError(pFailedPaymentGroup, pStatusMessage, pResult, pBundle);
		  }
	  }

	protected void callSuperAddPaymentGroupError(PaymentGroup pFailedPaymentGroup, String pStatusMessage,
			PipelineResult pResult, ResourceBundle pBundle) {
		super.addPaymentGroupError(pFailedPaymentGroup, pStatusMessage, pResult, pBundle);
	}
}
