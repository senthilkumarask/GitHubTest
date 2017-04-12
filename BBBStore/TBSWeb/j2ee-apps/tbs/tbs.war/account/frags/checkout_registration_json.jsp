<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>

	<%-- Variables --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="email" param="email" />

	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="ProfileFormHandler.errorMap"/>
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
			<dsp:droplet name="IsEmpty">
				<dsp:param name="value" param="email"/>
				<dsp:oparam name="true">
					<dsp:getvalueof id="email" param="uemail" scope="session"/>
					<json:object>
						<json:property name="exists" value="${false}"/>
						<json:property name="url"><dsp:valueof value="${contextPath}/account/myaccount.jsp"/></json:property>
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
