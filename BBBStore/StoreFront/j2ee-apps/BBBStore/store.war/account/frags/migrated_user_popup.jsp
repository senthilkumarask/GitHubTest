<dsp:importbean bean="/atg/userprofiling/ForgotPasswordHandler"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<c:set var="title"><bbbl:label key="lbl_account_creation_message" language="${pageContext.request.locale.language}"/></c:set>
<c:set var="button_yes_title"><bbbl:label key="lbl_please_extend_account" language="${pageContext.request.locale.language}"/></c:set>	
<c:set var="button_no_title"><bbbl:label key="lbl_enter_new_emailaddress" language="${pageContext.request.locale.language}"/></c:set>		
<div id="extendAccountDialog" title="${title}">
    <div class="clearfix">
	
	<div class="error"></div>
       <bbbt:textArea key="txt_extend_account_description" language ="${pageContext.request.locale.language}"/>
		<dsp:form iclass="clearfix" id="frmNewPassword" method="post" action="${contextPath}/account/login.jsp">
			<div class="formRow noMarBot clearfix">
				<div class="button">
					<dsp:input bean="ForgotPasswordHandler.forgotPasswordWithProfileExtenstion" value="true" type="hidden"/>
					<dsp:input bean="ForgotPasswordHandler.value.email" type="hidden" value="${sessionScope.emailAddr}"/>
					<dsp:input bean="ForgotPasswordHandler.forgotPasswordSuccessURL" type="hidden" value="${contextPath}/account/frags/migrated_user_json.jsp"/>
					<dsp:input bean="ForgotPasswordHandler.forgotPasswordErrorURL" type="hidden" value="${contextPath}/account/frags/migrated_user_json.jsp"/>
					<dsp:input bean="ForgotPasswordHandler.forgotPassword" id="extendAccount" type="Submit" value="${button_yes_title}" />
				</div>
				<div class="button marLeft_10">
                	<a href="#" class="close-any-dialog">${button_no_title}</a>
                </div>
                <div class="clear"></div>
			</div>
		</dsp:form>
    </div>
</div>