<dsp:page>
<dsp:importbean bean="atg/userprofiling/Profile"/>
<dsp:importbean bean="/com/bbb/commerce/cart/EmailSavedItemsFormHandler"/>

	<json:object>
		<json:property name="error"><dsp:valueof bean="EmailSavedItemsFormHandler.formError"/></json:property>
		<json:property name="email"><dsp:valueof bean="EmailSavedItemsFormHandler.recipientEmail"/></json:property>			    
	</json:object> 
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
   <dsp:droplet name="/atg/dynamo/droplet/Redirect">
  		<dsp:param name="url" value="${contextPath}/wishlist/wish_list.jsp"/>
	</dsp:droplet>

</dsp:page>