<%@page contentType="application/json"%>
<dsp:page>

	<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler" />
	<c:choose>
	<c:when test="${ProfileFormHandler.confirmPassword}">
	<json:object>
	<json:property name="success" value="${true}"/>
	<json:property name="modalToOpen">${contextPath}/account/login.jsp</json:property>
	</json:object>
	</c:when>
	<c:otherwise>
	<c:set var="lbl_password_reset_heading"><bbbl:label key="lbl_password_reset_heading" language ="${pageContext.request.locale.language}"/></c:set>
	<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
		<dsp:param name="value" bean="ProfileFormHandler.errorMap" />
		<dsp:oparam name="true">
		<json:object escapeXml="false">
				<json:property name="success" value="${true}"/>
		</json:object>
		</dsp:oparam>
		<dsp:oparam name="false">
			<json:object>
				<json:property name="error"><dsp:valueof bean="ProfileFormHandler.errorMap.loginPasswordError"/></json:property>
			</json:object>
		</dsp:oparam>
	</dsp:droplet>
	</c:otherwise>
	</c:choose>
</dsp:page>