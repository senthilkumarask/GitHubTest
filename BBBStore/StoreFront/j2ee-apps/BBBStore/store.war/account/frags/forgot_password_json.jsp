<%@page contentType="application/json"%>
<dsp:page>

	<dsp:importbean var="ForgotPasswordHandler" bean="/atg/userprofiling/ForgotPasswordHandler" />
	<dsp:getvalueof var="pageName" param="pageName" />
	<c:choose>
	<c:when test="${ForgotPasswordHandler.challengeQuestionExist}">
	<json:object>
	<json:property name="success" value="${true}"/>
	<json:property name="pageName" value="${pageName}"/>
	<json:property name="modalToOpen">/store/account/challengeQuestionModal.jsp</json:property>
	</json:object>
	</c:when>
	<c:otherwise>
	<c:set var="lbl_password_reset_heading"><bbbl:label key="lbl_password_reset_heading" language ="${pageContext.request.locale.language}"/></c:set>
	<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
		<dsp:param name="value" bean="ForgotPasswordHandler.errorMap" />
		<dsp:oparam name="true">
		<json:object escapeXml="false">
				<json:property name="success" value="${true}"/>
				<jsp:useBean id="emailId" class="java.util.HashMap" scope="request"/>
					<c:set target="${emailId}" property="emailAddr"><dsp:valueof bean="ForgotPasswordHandler.emailAddr"/></c:set>
				<json:property name="dialog"><div title="${lbl_password_reset_heading}"><p><bbbl:textArea key="txtarea_forgot_password_success" language ="${pageContext.request.locale.language}" placeHolderMap="${emailId}" /></p></div></json:property>
		</json:object>
		</dsp:oparam>
		<dsp:oparam name="false">
			<%--  Added to fix  BBB-93 | DSK- Adding additional access points for the extend account modal --%>
			<json:object escapeXml="false">
			<jsp:useBean id="emailId" class="java.util.HashMap" scope="request"/>
			<c:set target="${emailId}" property="emailAddress"><dsp:valueof bean="ForgotPasswordHandler.value.email"/></c:set>
				<json:property name="error"><bbbe:error key="err_profile_email_associated_sister_site" language ="${pageContext.request.locale.language}" placeHolderMap="${emailId}" /></json:property>
			</json:object>
		</dsp:oparam>
	</dsp:droplet>
	</c:otherwise>
	</c:choose>
</dsp:page>