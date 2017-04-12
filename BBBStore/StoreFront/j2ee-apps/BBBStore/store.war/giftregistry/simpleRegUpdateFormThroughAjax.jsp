<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/com/bbb/simplifyRegistry/droplet/SimpleRegFieldsDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>		

	<dsp:getvalueof var="formExceptions" bean="GiftRegistryFormHandler.formExceptions"/>	
	

	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="GiftRegistryFormHandler.formExceptions"/>
		<dsp:oparam name="false">
			<json:object>
				<json:property name="error" value="${true}"/>
				    <json:array name="errorMessages">
						<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
							<dsp:param bean="GiftRegistryFormHandler.formExceptions" name="exceptions" />
							<dsp:oparam name="output">
								<json:property>
									<dsp:getvalueof param="message" var="err_msg_key" />
									<c:set var="under_score" value="_"/>
									<c:choose>
									<c:when test="${fn:contains(err_msg_key, under_score)}">
										<c:set var="err_msg" scope="page">
											<bbbe:error key="${err_msg_key}" language="${pageContext.request.locale.language}"/>
										</c:set>
									</c:when>
									</c:choose>
									<c:choose>
									<c:when test="${empty err_msg}"><dsp:valueof param="message" valueishtml="true" /></c:when>
									<c:otherwise>${err_msg}</c:otherwise>
									</c:choose>
								</json:property>						
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