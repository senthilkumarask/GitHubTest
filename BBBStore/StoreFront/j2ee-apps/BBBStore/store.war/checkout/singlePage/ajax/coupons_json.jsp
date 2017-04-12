<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>		
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	

	<dsp:droplet name="Switch">
		<dsp:param name="value" bean="CartModifierFormHandler.formError"/>
		<dsp:oparam name="true">
			<json:object>
				<json:property name="error" value="${true}"/>
				    <json:array name="errorMessages">
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" param="CartModifierFormHandler.couponErrorList"/>
							<dsp:oparam name="output">						
								<json:property><dsp:valueof param="element"/></json:property>						
							</dsp:oparam>				
						</dsp:droplet>
					</json:array>	
			</json:object>	
		</dsp:oparam>
		<dsp:oparam name="false">
			<json:object>
				<json:property name="success" value="true"/>
			</json:object>	
		</dsp:oparam>					
	</dsp:droplet>	
</dsp:page>

