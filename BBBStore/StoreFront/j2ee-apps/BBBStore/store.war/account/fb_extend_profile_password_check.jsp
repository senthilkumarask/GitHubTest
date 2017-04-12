<dsp:importbean var="ProfileFormHandler"	bean="/atg/userprofiling/ProfileFormHandler" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<json:object>
	<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
		<dsp:param name="value" bean="ProfileFormHandler.errorMap"/>
		<dsp:oparam name="false">
			<json:property name="error"><dsp:valueof bean="ProfileFormHandler.errorMap.loginPasswordError"/></json:property>
		</dsp:oparam>
		<dsp:oparam name="true">
			<dsp:getvalueof id="url" bean="ProfileFormHandler.assoSite"/>
			<c:choose>
				<c:when test="${url ne 'default'}">
					<json:property name="url" escapeXml="false"><c:out value="${url}" escapeXml="false"></c:out></json:property>
				</c:when>
				<c:otherwise>
					<json:property name="url" escapeXml="false"><c:out value="${contextPath}/account/login.jsp" escapeXml="false"></c:out></json:property>
				</c:otherwise>
			</c:choose>
			
		</dsp:oparam>			
	</dsp:droplet>
</json:object>





