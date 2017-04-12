<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<dsp:page>
<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
<dsp:importbean bean="/com/bbb/idm/TBSIDMFormHandler"/>
	<json:object> 
        <json:property name="error" value="${true}"/> 
        <dsp:getvalueof bean="TBSIDMFormHandler.formError" var="idmError"/>
        <json:property name="idmError" value="${idmError}"/>
        <json:array name="errors">
        	<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
        	<dsp:param name="value" bean="TBSIDMFormHandler.formExceptions"/>
	        	<dsp:oparam name="false">
	        		<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
				       <dsp:param name="exceptions" bean="CartModifierFormHandler.formExceptions"/>
				       <dsp:oparam name="output">
				          <json:property>
				          	<dsp:valueof param="message"/>
				          </json:property>
				      </dsp:oparam>
				    </dsp:droplet><%--End of ErrorMessageForEach --%>
	        	</dsp:oparam>
	        	<dsp:oparam name="true">
	        		<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
				       <dsp:param name="exceptions" bean="CartModifierFormHandler.formExceptions"/>
				       <dsp:oparam name="output">
				          <json:property>
				          	<dsp:valueof param="message"/>
				          </json:property>
				      </dsp:oparam>
				    </dsp:droplet><%--End of ErrorMessageForEach --%>
	        	</dsp:oparam>
        	</dsp:droplet><%--End of IsEmpty --%>
	     </json:array>
    </json:object>
</dsp:page>