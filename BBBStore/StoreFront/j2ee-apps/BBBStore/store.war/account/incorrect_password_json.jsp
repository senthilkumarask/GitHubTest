<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:getvalueof var="error" bean="ProfileFormHandler.formErrorVal"></dsp:getvalueof>
	<dsp:getvalueof var="success_url" bean="ProfileFormHandler.legacyPasswordSuccessURL"></dsp:getvalueof>
	<dsp:getvalueof var="success" bean="ProfileFormHandler.legacyForgetPasswordStatus"></dsp:getvalueof>
	<dsp:getvalueof var="dialogMessage" bean="ProfileFormHandler.strReminderSent"></dsp:getvalueof>
	<dsp:getvalueof var="errorMap" bean="ProfileFormHandler.errorMap"></dsp:getvalueof>
	
	<dsp:getvalueof var="reclaim_account_ws_err" bean="ProfileFormHandler.errorMap.reclaim_account_ws_err"></dsp:getvalueof>
	<dsp:getvalueof var="reclaim_account_form_err" bean="ProfileFormHandler.errorMap.reclaim_account_form_err"></dsp:getvalueof>
	<dsp:getvalueof var="reclaim_account_password_err" bean="ProfileFormHandler.errorMap.reclaim_account_password_err"></dsp:getvalueof>
	<dsp:getvalueof var="reclaim_account_ws_err" bean="ProfileFormHandler.errorMap.reclaim_account_ws_err"></dsp:getvalueof>
	
	<dsp:getvalueof var="json_reclaim_account_password_err" bean="ProfileFormHandler.errorMap.json_reclaim_account_password_err"></dsp:getvalueof>
	<dsp:getvalueof var="json_reclaim_account_disp_error_get_account" bean="ProfileFormHandler.errorMap.json_reclaim_account_disp_error_get_account"></dsp:getvalueof>
	
	<dsp:getvalueof var="reclaim_account_invalid_email" bean="ProfileFormHandler.errorMap.reclaim_account_invalid_email"></dsp:getvalueof>
	<dsp:getvalueof var="reclaim_account_already_reclaimed" bean="ProfileFormHandler.errorMap.reclaim_account_already_reclaimed"></dsp:getvalueof>
	<dsp:getvalueof var="reclaim_account_not_found" bean="ProfileFormHandler.errorMap.reclaim_account_not_found"></dsp:getvalueof>
	<dsp:getvalueof var="reclaim_account_invalid_username_password" bean="ProfileFormHandler.errorMap.reclaim_account_invalid_username_password"></dsp:getvalueof>
		
	<dsp:getvalueof var="reclaim_account_tech_error_verify_account" bean="ProfileFormHandler.errorMap.reclaim_account_tech_error_verify_account"></dsp:getvalueof>
	<dsp:getvalueof var="reclaim_account_tech_error_get_account" bean="ProfileFormHandler.errorMap.reclaim_account_tech_error_get_account"></dsp:getvalueof>
	<dsp:getvalueof var="reclaim_account_tech_error_forget_password" bean="ProfileFormHandler.errorMap.reclaim_account_tech_error_forget_password"></dsp:getvalueof>
	
	<dsp:getvalueof var="reclaim_account_disp_error_forget_password" bean="ProfileFormHandler.errorMap.reclaim_account_disp_error_forget_password"></dsp:getvalueof>
	<dsp:getvalueof var="reclaim_account_disp_error_reclaim_account" bean="ProfileFormHandler.errorMap.reclaim_account_disp_error_reclaim_account"></dsp:getvalueof>
	<dsp:getvalueof var="reclaim_account_disp_error_verify_account" bean="ProfileFormHandler.errorMap.reclaim_account_disp_error_verify_account"></dsp:getvalueof>
	<dsp:getvalueof var="reclaim_account_disp_error_get_account" bean="ProfileFormHandler.errorMap.reclaim_account_disp_error_get_account"></dsp:getvalueof>
		
	<json:object>
		<c:choose>
		<c:when test="${success == true}">
			<json:property name="success">${dialogMessage}</json:property>
		</c:when>
		<c:otherwise>
			<c:choose>
			<c:when test="${error == true}">
			<json:property name="error">
				
				<c:if test="${not empty reclaim_account_ws_err}">
					<bbbe:error key="err_reclaim_account_ws_err" language="${pageContext.request.locale.language}"/>
				</c:if>
				<c:if test="${not empty reclaim_account_form_err}">
					 <bbbe:error key="err_reclaimuser_exception_verify_account" language="${pageContext.request.locale.language}"/>
				</c:if>
				<c:if test="${not empty reclaim_account_password_err}">
					<bbbe:error key="err_reclaimaccount_invalid_password" language="${pageContext.request.locale.language}"/>
				</c:if>
				<c:if test="${not empty reclaim_account_email_err}">
					 <bbbe:error key="err_reclaimaccount_invalid_email" language="${pageContext.request.locale.language}"/></p>
				</c:if>
								
				<c:if test="${not empty reclaim_account_invalid_email}">
					<bbbe:error key="err_reclaim_account_invalid_email" language="${pageContext.request.locale.language}"/>
				</c:if>
				<c:if test="${not empty reclaim_account_already_reclaimed}">
					<bbbe:error key="err_reclaim_account_already_reclaimed" language="${pageContext.request.locale.language}"/>
				</c:if>
				<c:if test="${not empty reclaim_account_not_found}">
					<bbbe:error key="err_reclaim_account_not_found" language="${pageContext.request.locale.language}"/>
				</c:if>
				<c:if test="${not empty reclaim_account_invalid_username_password}">
					<bbbe:error key="err_reclaim_account_invalid_username_password" language="${pageContext.request.locale.language}"/>
				</c:if>
				<c:if test="${not empty reclaim_account_unableto_reclaim}">
					<bbbe:error key="err_reclaim_account_unableto_reclaim" language="${pageContext.request.locale.language}"/>
				</c:if>
				
				<c:if test="${not empty reclaim_account_tech_error_verify_account}">
					<bbbe:error key="err_reclaim_account_tech_error_verify_account" language="${pageContext.request.locale.language}"/>
				</c:if>
				<c:if test="${not empty reclaim_account_tech_error_get_account}">
					<bbbe:error key="err_reclaim_account_tech_error_get_account" language="${pageContext.request.locale.language}"/>
				</c:if>
				<c:if test="${not empty reclaim_account_tech_error_reclaim_account}">
					<bbbe:error key="err_reclaim_account_tech_error_reclaim_account" language="${pageContext.request.locale.language}"/>
				</c:if>
				<c:if test="${not empty reclaim_account_tech_error_forget_password}">
					<bbbe:error key="err_reclaim_account_tech_error_forget_password" language="${pageContext.request.locale.language}"/>
				</c:if>
				
				<c:if test="${not empty json_reclaim_account_password_err}">
					<bbbe:error key="err_reclaimaccount_invalid_password" language="${pageContext.request.locale.language}"/>
				</c:if>
				
				${reclaim_account_disp_error_forget_password} ${reclaim_account_disp_error_reclaim_account} ${reclaim_account_disp_error_verify_account} ${reclaim_account_disp_error_get_account}
				${json_reclaim_account_disp_error_get_account}
			</json:property>
			</c:when>
			<c:otherwise>
				<json:property name="url" escapeXml="false"><c:out escapeXml="false" value="${success_url}"></c:out></json:property>
			</c:otherwise>
			</c:choose>
		</c:otherwise>
		</c:choose>
	</json:object>	 
	<c:set var="errorMap" scope="page"></c:set>
</dsp:page>
