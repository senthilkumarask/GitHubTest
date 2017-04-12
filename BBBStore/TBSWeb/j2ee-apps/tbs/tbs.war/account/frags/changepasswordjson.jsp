<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<c:if test="${sessionScope.userCheckingOut eq 'true'}">
		<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="SHIPPING_SINGLE"/>
	</c:if>
	
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="ProfileFormHandler.errorMap"/>
		<dsp:oparam name="false">
			<json:object>
				<json:property name="error">
					<dsp:valueof bean="ProfileFormHandler.errorMap.passwordError"/>
				</json:property>
			</json:object>
		</dsp:oparam>
		<dsp:oparam name="true">
			<json:object>
				<json:property name="success">Password successfully changed</json:property>
			</json:object>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>