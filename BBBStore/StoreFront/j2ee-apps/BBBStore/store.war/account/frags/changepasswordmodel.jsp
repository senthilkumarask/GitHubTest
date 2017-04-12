<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<div id="newPasswordDialog" title="We need a new password!">
    <div class="width_6 clearfix">
	
	<div class="error"></div>
        <p class="noMarBot"><bbbl:label key="lbl_change_pass_model_info" language ="${pageContext.request.locale.language}"/> <br/>
			<span class="smallText"><bbbl:label key="lbl_change_pass_model_info2" language ="${pageContext.request.locale.language}"/></span>
		</p>
    
		<dsp:form iclass="clearfix width_6" id="frmNewPassword" method="post" action="${contextPath}/account/login.jsp">
			<div class="formRow fl noMarBot clearfix width_6">
				<div class="input clearfix">
					<div class="width_3 label marRight_10 posRel">
						<label id="lblnewPassword" for="newPassword"><bbbl:label key="lbl_change_pass_model_new" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
                        <div class="showPassDiv clearfix">
                            <input name="showPassword" type="checkbox" value="" data-toggle-class="showpassChangePassModal" class="showPassword" id="showPasswordMigrate" aria-labelledby="lblshowPasswordMigrate" />
                            <label id="lblshowPasswordMigrate" for="showPasswordMigrate" class="lblShowPassword"><bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/></label>
                            <div class="clear"></div> 
                        </div>
					</div>
					<div class="text fl width_3">
						<dsp:input bean="ProfileFormHandler.value.password" id="newPassword" name="password" value="" type="password" autocomplete="off">
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblnewPassword errornewPassword"/>
                        </dsp:input>
					</div>
					<div class="posRel fl">
						<a href="#" class="help info" alt="New Password" title="New Password" tabindex="-1" >						
						<span><bbbl:label key="lbl_change_pass_model_clever" language ="${pageContext.request.locale.language}"/></span>
						</a>
					</div>
				</div>
				
				<div class="input clearfix">
					<div class="width_3 label marRight_10">
						<label id="lblconfirmNewPassword" for="confirmNewPassword"><bbbl:label key="lbl_change_pass_model_confirm" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
					</div>
					<div class="text fl width_3">
						<dsp:input bean="ProfileFormHandler.value.confirmpassword" type="password" autocomplete="off" name="confirmNewPassword" id="confirmNewPassword" value="" >
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblconfirmNewPassword errorconfirmNewPassword"/>
                        </dsp:input>
					</div>
				</div>
			</div>
			
			<div class="formRow fl noMarBot clearfix width_6 padBottom_20">
				<div class="fl button button_active button_active_orange">
					<dsp:input bean="ProfileFormHandler.legacyUser" value="yes" type="hidden"/>
					<dsp:input bean="ProfileFormHandler.value.firstName" name="fname" id="firstName" type="hidden" value="${sessionScope.fName}"/>
					<dsp:input bean="ProfileFormHandler.value.lastName" name="lname" id="lastName" type="hidden" value="${sessionScope.lName}"/>
					<dsp:input bean="ProfileFormHandler.fromPage" type="hidden" value="changePassword" />
					<%-- Client DOM XSRF | Part -1
					<dsp:input bean="ProfileFormHandler.changePasswordSuccessURL" type="hidden" value="${contextPath}/account/frags/changepasswordjson.jsp"/>
					<dsp:input bean="ProfileFormHandler.changePasswordErrorURL" type="hidden" value="${contextPath}/account/frags/changepasswordjson.jsp"/> --%>
					<dsp:input bean="ProfileFormHandler.changePassword" id="newPasswordButton" type="Submit" value="Submit" >
                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="newPasswordButton"/>
                    </dsp:input>
				</div>
                <a href="#" title="Cancel" class="close-any-dialog buttonTextLink" role="link"><bbbl:label key="lbl_change_pass_model_cancel" language ="${pageContext.request.locale.language}"/></a>
			</div>
		</dsp:form>
    </div>
</div>