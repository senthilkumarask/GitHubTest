<dsp:page>
<dsp:importbean bean="atg/userprofiling/Profile"/>
<dsp:importbean bean="/com/bbb/commerce/cart/EmailCartFormHandler"/>
	<json:object>
		<json:property name="error"><dsp:valueof bean="EmailCartFormHandler.formError"/></json:property>
		<json:property name="email"><dsp:valueof bean="EmailCartFormHandler.recipientEmail"/></json:property>
	</json:object>
	<%-- Fix for defect# 30 --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:droplet name="/atg/dynamo/droplet/Redirect">
  		<dsp:param name="url" value="${contextPath}/cart/cart.jsp"/>
	</dsp:droplet>
</dsp:page>