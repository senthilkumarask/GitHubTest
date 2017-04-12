<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

	<c:set var="reclaim_incorrect_password_title" scope="page">
		<bbbt:textArea key="txtarea_reclaim_incorrect_password_title"
			language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="reclaim_incorrect_password_request" scope="page">
		<bbbt:textArea key="txtarea_reclaim_incorrect_password_request"
			language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="reclaim_incorrect_password_txt" scope="page">
		<bbbl:label key="lbl_legacy_incorrect_password"
			language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="profile_password" scope="page">
		<bbbl:label key="lbl_profile_password"
			language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="forgot_pwd_submit" scope="page">
		<bbbl:label key="lbl_forgot_pwd_submit"
			language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="profile_Cancel" scope="page">
		<bbbl:label key="lbl_profile_Cancel"
			language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="reg_forgot_password" scope="page">
		<bbbl:label key="lbl_reg_forgot_password"
			language="${pageContext.request.locale.language}" />
	</c:set>
	<div id="forgotPasswordDialog" title="Incorrect Password">
	    <div class="width_6 clearfix">
	    	<p>${reclaim_incorrect_password_txt}</p>
	        <p>${reclaim_incorrect_password_title}</p>
	        <p>${reclaim_incorrect_password_request}</p>
			<dsp:form  method="post" id="frmLegacyIncorrectPwd" action="${contextPath}/account/incorrect_password_json.jsp">
				<div class="formRow fl noMarBot clearfix width_6">
					<div class="input fl marRight_20">
						<div class="fl label marRight_10 posRel">
							<label id="lblpassword" for="password">${profile_password}<span class="required">*</span></label>
                            <div class="showPassDiv clearfix">
                                <input name="showPassword" type="checkbox" value="" data-toggle-class="showpassLoginExtension" class="showPassword" id="showPasswordIncorrectPass" aria-labelledby="lblshowPasswordIncorrectPass" />
                                <label id="lblshowPasswordIncorrectPass" for="showPasswordIncorrectPass" class="lblShowPassword"><bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/></label>
                                <div class="clear"></div> 
                            </div>
						</div>
						<div class="text fl width_3">
							<dsp:getvalueof id="email" param="email"/>
							<dsp:input name="email" id="email" bean="ProfileFormHandler.value.login" type="hidden" value="${email}"/>
							<dsp:input bean="ProfileFormHandler.value.password" type="password" id="password" autocomplete="off" iclass="showpassIncorrectPass" >
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
                            </dsp:input>
							<dsp:input bean="ProfileFormHandler.incorrectPasswordLogin" type="submit" name="submit" value="${forgot_pwd_submit}" iclass="hidden"></dsp:input>
						</div>
					</div>
					<div class="fl">
						<dsp:input bean="ProfileFormHandler.legacyForgetPassword" type="submit" value="${reg_forgot_password}" iclass="hidden"></dsp:input>
						<a href="#" id="legacyForgotPwd" title="Forgot Password"><bbbl:label key="lbl_forgot_password" language="${pageContext.request.locale.language}" /></a>
					</div>
				</div>
				<input name="submit" value="submit" type="hidden"/> 
				<%-- Client DOM XSRF 
				<dsp:input bean="ProfileFormHandler.reclaimLegacyAccountIncorrectPasswordURL" type="hidden" value="${contextPath}/account/incorrect_password_json.jsp"/>
				<dsp:input bean="ProfileFormHandler.reclaimLegacyAccountSuccessURL" type="hidden" value="${contextPath}/account/incorrect_password_json.jsp"/>
				<dsp:input bean="ProfileFormHandler.reclaimLegacyIncorrectPasswordSuccessURL" type="hidden" value="${contextPath}/account/create_account.jsp"/> --%>
				
				<div class="formRow fl clearfix width_6">
					<div class="fl marRight_10 button">
						<dsp:input bean="ProfileFormHandler.incorrectPasswordLogin" type="submit" name="submit" value="${forgot_pwd_submit}">
                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                            <dsp:tagAttribute name="role" value="button"/>
                        </dsp:input>
					</div>
					<div class="button fl">
						<input type="button" value="${profile_Cancel}" class="close-any-dialog" role="button" />
					</div>
				</div>
			</dsp:form>
	    </div>
	</div>
</dsp:page>