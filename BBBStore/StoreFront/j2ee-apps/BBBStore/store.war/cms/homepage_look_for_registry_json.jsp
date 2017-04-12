<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />

	<dsp:setvalue bean="GiftRegistryFormHandler.registrySearchVO.firstName" value="${param.firstNameReg}" />
	<dsp:setvalue bean="GiftRegistryFormHandler.registrySearchVO.lastName" value="${param.lastNameReg}" />
	<dsp:setvalue bean="GiftRegistryFormHandler.registrySearchVO.state" value="${param.stateName}" />
	<dsp:setvalue bean="GiftRegistryFormHandler.hidden" value="1" />

	<dsp:setvalue bean="GiftRegistryFormHandler.registrySearchFromHomePage"  />

	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="GiftRegistryFormHandler.formExceptions"/>
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
				<json:property name="success" value="true" />
			</json:object>
		</dsp:oparam>
	</dsp:droplet>                        
</dsp:page>