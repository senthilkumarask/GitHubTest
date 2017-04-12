package com.bbb.integration.cybersource.processor;

import atg.integrations.cybersourcesoap.CyberSourceUtils;
import atg.integrations.cybersourcesoap.MessageConstant;
import atg.integrations.cybersourcesoap.cc.CreditCardProcParams;
import atg.integrations.cybersourcesoap.cc.processor.AbstractCreditCardProcessor;
import atg.payment.creditcard.CreditCardInfo;
import atg.service.pipeline.PipelineResult;

import com.bbb.integration.cybersource.creditcard.CreditCardStatus;
import com.cybersource.stub.ReplyMessage;

/**
 * 
 * Processor creates CyberSourceStatus
 *
 */
public class ProcCreateCyberSourceStatus extends AbstractCreditCardProcessor {
  
  /**
   * Creates CyberSourceStatus from the CyberSource reply
   */
  protected int runCreditCardProcess(CreditCardProcParams pParams, PipelineResult pResult) throws Exception {
    CreditCardInfo ccinfo = pParams.getCreditCardInfo();
    ReplyMessage reply = pParams.getReply();
    
    pParams.setCyberSourceStatus(new CreditCardStatus(reply, CyberSourceUtils.convertDoubleToString(ccinfo.getAmount())));
    
    if(isLoggingDebug()) {
      logDebug(CyberSourceUtils.getString(MessageConstant.CYBER_SOURCE_STATUS_CREATE_SUCCESS));
    }
      
    return SUCCESS;
  }

}
