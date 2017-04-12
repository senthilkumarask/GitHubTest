<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<c:set var="passwordTilte"><bbbl:label key="lbl_change_pass_model_title" language ="${pageContext.request.locale.language}"/></c:set>
<div id="newPasswordDialog" title="${passwordTilte}">
    <div class="width_6 clearfix">
	
	<div class="error"></div>
    
		<dsp:form iclass="clearfix width_6" id="frmNewPassword" method="post" action="${contextPath}/account/login.jsp" autocomplete="off">
			<div class="formRow fl noMarBot marTop_10 clearfix width_6">
				<div class="input clearfix">
					<div class="width_3 label marRight_10 posRel">
						<label id="lblnewPassword" for="newPassword"><bbbl:label key="lbl_personalinfo_newpassword" language ="${pageContext.request.locale.language}"/>&nbsp;<span class="required">*</span></label>
					</div>
					<div class="text fl width_3">
						<dsp:input bean="ProfileFormHandler.value.password" id="newPassword" name="password" value="" type="password" autocomplete="off" iclass="showpassChangePassModal">
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblnewPassword errornewPassword"/>
                        </dsp:input>
					</div>
				</div>
				
				<div class="input clearfix">
					<div class="width_3 label marRight_10">
						<label id="lblconfirmNewPassword" for="confirmNewPassword"><bbbl:label key="lbl_change_pass_model_confirm" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
					</div>
					<div class="text fl width_3">
						<dsp:input bean="ProfileFormHandler.value.confirmpassword" type="password" autocomplete="off" name="confirmNewPassword" id="confirmNewPassword" value="" iclass="showpassChangePassModal" >
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblconfirmNewPassword errorconfirmNewPassword"/>
                        </dsp:input>
					</div>
				</div>
			</div>
			
			<div class="formRow fl noMarBot clearfix small-12 columns no-padding">
				<div class="fl button_active button_active_orange small-12 medium-3 columns no-padding-left">
					<dsp:input bean="ProfileFormHandler.value.firstName" name="fname" id="firstName" type="hidden" value="${sessionScope.fstName}"/>
					<dsp:input bean="ProfileFormHandler.value.lastName" name="lname" id="lastName" type="hidden" value="${sessionScope.lstName}"/>
					<dsp:input bean="ProfileFormHandler.autoLoginAfterChangePwd" type="hidden" value="true"/>
					<dsp:input bean="ProfileFormHandler.fromPage" type="hidden" value="changePassword" />
					<!--<dsp:input bean="ProfileFormHandler.changePasswordSuccessURL" type="hidden" value="${contextPath}/account/frags/changepasswordjson.jsp"/>
					<dsp:input bean="ProfileFormHandler.changePasswordErrorURL" type="hidden" value="${contextPath}/account/frags/changepasswordjson.jsp"/>-->
					<c:if test="${sessionScope.userCheckingOut eq 'true'}">
						<dsp:input bean="ProfileFormHandler.userCheckingOut" type="hidden" value="userCheckingOut"/>
					</c:if>
					<dsp:input bean="ProfileFormHandler.changePassword" id="changePassword" type="hidden" value="Submit" />
					<input type="submit" value="Submit" name="newPasswordButton" id="newPasswordButton" class="button small primary column">
				</div>
                <a href="#" title="Cancel" role="link" class="close-modal buttonTextLink button small secondary"><bbbl:label key="lbl_change_pass_model_cancel" language ="${pageContext.request.locale.language}"/></a>
			</div>
		</dsp:form>
    </div>
</div>
<a class="close-reveal-modal">x</a>