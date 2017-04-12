<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>		
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupFormHandler"/>

	<dsp:getvalueof var="formExceptions" bean="PaymentGroupFormHandler.formExceptions"/>	
	

	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="PaymentGroupFormHandler.formExceptions"/>
		<dsp:oparam name="false">
			<json:object>
				<json:property name="error" value="${true}"/>
				    <json:array name="errorMessages">
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" param="value"/>
							<dsp:oparam name="output">						
								<json:property><dsp:valueof param="element"/></json:property>						
							</dsp:oparam>				
						</dsp:droplet>
					</json:array>	
			</json:object>	
		</dsp:oparam>
		<dsp:oparam name="true">
			<json:object>
				<json:property name="success" value="true"/>
			</json:object>	
		</dsp:oparam>					
	</dsp:droplet>	
</dsp:page>