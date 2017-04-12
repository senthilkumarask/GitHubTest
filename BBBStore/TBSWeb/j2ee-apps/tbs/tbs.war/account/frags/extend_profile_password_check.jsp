<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/multisite/Site"/>

	<%-- Variables --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:param name="currentSiteId" bean="Site.id"/>

	<json:object escapeXml="false">
		<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
			<dsp:param name="value" bean="ProfileFormHandler.errorMap"/>
			<dsp:oparam name="false">
				<json:property name="error">
					<dsp:valueof bean="ProfileFormHandler.errorMap.loginPasswordError"/>
				</json:property>
			</dsp:oparam>
			<dsp:oparam name="true">
				<dsp:getvalueof id="url" bean="ProfileFormHandler.assoSite"/>
				<c:choose>
					<%-- Added extra check for BBBSL-52 --%>
					<c:when test="${not empty url && currentSiteId ne url}">
						<json:property name="url" escapeXml="false">
							<c:out value="${url}" escapeXml="false" />
						</json:property>
					</c:when>
					<c:otherwise>
						<json:property name="url" escapeXml="false">
							<c:out value="${contextPath}/account/login.jsp" escapeXml="false" />
						</json:property>
					</c:otherwise>
				</c:choose>
			</dsp:oparam>
		</dsp:droplet>
	</json:object>

</dsp:page>
