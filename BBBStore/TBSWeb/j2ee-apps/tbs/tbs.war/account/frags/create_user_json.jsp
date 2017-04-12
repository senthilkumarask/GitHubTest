<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>	
	
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="ProfileFormHandler.errorMap"/>
		<dsp:oparam name="false">
			<json:object>
				<json:property name="error"><dsp:valueof bean="ProfileFormHandler.errorMap.registerError"/></json:property>
				<json:property name="exists" value="${true}"/>
			</json:object>	
		</dsp:oparam>
		<dsp:oparam name="true">
			<dsp:droplet name="IsEmpty">
				<dsp:param name="value" param="email"/>				
				<dsp:oparam name="true">
					<dsp:getvalueof id="email" param="uemail" scope="session"/>
					<json:object>
						<json:property name="exists" value="${false}"/>
						<json:property name="url"><dsp:valueof value="${contextPath}/account/create_account.jsp"/></json:property>
					</json:object>									
				</dsp:oparam>				
				<dsp:oparam name="false">
					<json:object>
						<dsp:getvalueof id="email" param="email" scope="session"/>		
						<json:property name="exists" value="${true}"/>
						<json:property name="url"><dsp:valueof value="${contextPath}/account/frags/login_extension.jsp?email=${email}"/></json:property>
					</json:object>	
				</dsp:oparam>
			</dsp:droplet>					
		</dsp:oparam>					
	</dsp:droplet>	
</dsp:page>