package com.bbb.commerce.browse.marshaller


import com.bbb.commerce.browse.vo.Main;
import com.bbb.commerce.browse.vo.OOSEmailRequestVO
import com.bbb.exception.BBBSystemException
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.TextMessage
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller
import javax.xml.datatype.XMLGregorianCalendar
import org.junit.Test;
import spock.lang.specification.BBBExtendedSpec

class OOSMessageMarshallerSpecification extends BBBExtendedSpec {
	

		def OOSMessageMarshaller testObj
		def OOSEmailRequestVO oosRequestMock = new OOSEmailRequestVO()
		def XMLGregorianCalendar DTMock = Mock()
		def TextMessage message = Mock()
		def JAXBContext context = Mock()
		def Marshaller marshallerMock = Mock()
		 
		def setup(){
			testObj = new OOSMessageMarshaller()
		}
		
		def"marshall .TC to check the marshal "(){
			given:
			testObj = Spy()
			setDataRecord()
		    
			testObj.callCreateMarshaller(_) >> marshallerMock
			marshallerMock.marshal(_, _) >> {}
			when:
			testObj.marshall(oosRequestMock, message)
			
			then:
			1*message.setText(_)
		}
		
		def"marshall .when throw JMSException  "(){
			given:
			testObj = Spy()
			setDataRecord()
			StringWriter stringWriter = new StringWriter();
			testObj.callCreateMarshaller(_) >> {throw new JMSException("jms Exception")}
			when:
			testObj.marshall(oosRequestMock, message)
			then:
			BBBSystemException exception = thrown()
		}
		
		def"marshall .when throw Exception  "(){
			given:
			testObj = Spy()
			setDataRecord()
			StringWriter stringWriter = new StringWriter();
			testObj.callCreateMarshaller(_) >> {throw new Exception("jms Exception")}
			when:
			testObj.marshall(oosRequestMock, message)
			then:
			BBBSystemException exception = thrown()
		}

	
		
		public setDataRecord(){
			oosRequestMock.getSkuId() >> "skuID"
			oosRequestMock.getProductId() >> "p24582"
			oosRequestMock.getProductName() >> "Toy"
			oosRequestMock.getEmailAddr() >> "test@gmail.com"
			oosRequestMock.getCustName() >> "john"
			oosRequestMock.getUserIp() >> "Ip"
			oosRequestMock.getRequestedDT() >> DTMock
			oosRequestMock.getInStockNotifyDT() >> DTMock
			oosRequestMock.getNotice1DT() >> DTMock
			oosRequestMock.getNotice2DT() >> DTMock
			oosRequestMock.getUnsubscribeDT() >> DTMock
			oosRequestMock.getFinalNoticeDT() >> DTMock
			oosRequestMock.getSiteFlag() >> true
	
		}
		
}