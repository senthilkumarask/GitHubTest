<dsp:page>
                <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>	
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
                <dsp:getvalueof bean="SessionBean.registryProfileStatus" var="profileStatus"/>
	
   <dsp:droplet name="Switch">
                        <dsp:param name="value" bean="GiftRegistryFormHandler.formError"/>
		<dsp:oparam name="true">
                        <dsp:getvalueof var="formError" bean="GiftRegistryFormHandler.formError"/>
		<json:object>
                        <json:property name="error" value="true">true</json:property>
		
 </json:object> 
		</dsp:oparam>
		<dsp:oparam name="false">
			<json:object>
                                        <json:property name="profileStatus" value="${profileStatus}"/>
										<json:property name="success" value="true"/>		
			</json:object>									
		</dsp:oparam>					
	</dsp:droplet>
</dsp:page>