<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	<json:object>
		<dsp:getvalueof var="index" bean="BBBShippingGroupFormhandler.cisiIndex" />
		<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
			<dsp:param name="value" bean="BBBShippingGroupFormhandler.cisiIndex" />
			<dsp:oparam name="false">
				<dsp:getvalueof var="addressKey" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingGroupName"/>
			</dsp:oparam>
		</dsp:droplet>
		
		<dsp:getvalueof var="formError" bean="BBBShippingGroupFormhandler.formError"/>
		<dsp:getvalueof var="storeId" bean="BBBShippingGroupFormhandler.storeId"/>

		<json:property name="addressKey" value="${addressKey}"></json:property>
		<json:property name="error" value="${formError}"></json:property>
		<json:property name="storeId" value="${storeId}"></json:property>
		
		<dsp:getvalueof bean="BBBShippingGroupFormhandler.formError" var="formError"/>
		<c:if test="${formError == true}">
			<json:array name="errorMessages">
				<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
					<dsp:param param="BBBShippingGroupFormhandler.formExceptions" name="exceptions"/>
					<dsp:oparam name="output">
							<json:property><dsp:valueof param="message"/></json:property>
					</dsp:oparam>
				</dsp:droplet>
			</json:array>
		</c:if>
	</json:object> 
</dsp:page>