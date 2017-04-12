package atg.commerce.order.processor


import java.text.MessageFormat;

import com.bbb.commerce.order.Paypal

import atg.commerce.order.CreditCard
import atg.service.pipeline.PipelineResult;

import org.junit.Test

import spock.lang.specification.BBBExtendedSpec;;

class BBBProcAuthorizePaymentSpecification extends BBBExtendedSpec  {
    def BBBProcAuthorizePayment testObj 
	def Paypal paypalMock = Mock()
	def PipelineResult resultMock = Mock()
	def ResourceBundle rBundleMock = Mock()
	def CreditCard cCard = Mock()
	def setup() {
		testObj = new BBBProcAuthorizePayment()
	}

	def "addPaymentGroupError .when payment group type is paypal " () {
		given:
		paypalMock.getId() >> "pay12035"
		
		when:
		testObj.addPaymentGroupError(paypalMock, "Authorised", resultMock, rBundleMock)
		
		then:
		1*resultMock.addError("PayPay_Service_Errorpay12035", "Authorised")
	}

	def "addPaymentGroupError .when payment group type is not paypal " () {
		given:
		testObj = Spy()
		cCard.getId() >> "cCard12035"
		1 * testObj.callSuperAddPaymentGroupError(cCard, "Authorised", resultMock, rBundleMock) >> {}
		expect:
		testObj.addPaymentGroupError(cCard, "Authorised", resultMock, rBundleMock)
		
	}

}
