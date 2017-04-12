<dsp:page>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
<c:set var="passwordTilte"><bbbl:label key="lbl_change_pass_model_title" language ="${pageContext.request.locale.language}"/></c:set>
 <bbb:pageContainer section="accounts">
<div id="newPasswordDialog" title="${passwordTilte}">

 <%-- Validate external parameters --%>

 
 
  <div id="content" class="container_12 clearfix" role="main">
	<dsp:include page="/global/gadgets/errorMessage.jsp">
		<dsp:param name="formhandler" bean="ProfileFormHandler"/>
	 </dsp:include>	
   <div class="grid_6 clearfix">
  <h4><bbbl:label key="lbl_reset_password_header" language="${pageContext.request.locale.language}" /></h4>
 	<dsp:form iclass="clearfix width_6 changePswdAfterRestModal" id="createAccount" method="post" action="${contextPath}/account/login.jsp" autocomplete="off">
          <div class="input" aria-live="assertive">
            <div class="label">
				<label id="lblEmail" for="Email"><bbbl:label key="lbl_reset_email" language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
            </div>
            <div class="text width_3">
                
 						<dsp:input bean="ProfileFormHandler.value.loginemailid"  autocomplete="off" name="email" id="loginemailid" value="" iclass="input_large211 block escapeHTMLTag changePassValidation" >
	                            <dsp:tagAttribute name="aria-required" value="true"/>
	                            <dsp:tagAttribute name="aria-labelledby" value="lblEmail errorEmail"/>
	                        </dsp:input>
 			</div> 

          </div>
          <input id="firstName" name="fname" value="" type="hidden">
		  <input id="lastName" name="lname" value="" type="hidden">
          <div class="input" aria-live="assertive">
            	<div class="label posRel">	
				<label id="lblpassword" for="password" class="textLgray12 lblError"><bbbl:label key="lbl_password" language="${pageContext.request.locale.language}"/>&nbsp;<span class="required">*</span></label>
                <div class="showPassDiv clearfix">
                    <div class="checker" id="uniform-showPassword"><span><input name="showPassword" type="checkbox" value="" data-toggle-class="showpassCreateAccount" class="showPassword" id="showPassword" style="opacity: 0;"></span></div>
                    <label for="showPassword" class="lblShowPassword"><bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/></label>
                    <div class="clear"></div> 
                </div>
			</div>
            <div class="text width_3">
                	<dsp:input bean="ProfileFormHandler.value.password" id="password" name="password" value="" type="password" autocomplete="off" iclass="input_large211 block escapeHTMLTag">
	                 <dsp:tagAttribute name="aria-required" value="true"/>
	                 <dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
	             	</dsp:input>
 			</div> 

          </div>
          <div class="input" aria-live="assertive">
            <div class="label">
				<label id="lblcPassword" for="cPassword"><bbbl:label key="lbl_change_pass_model_confirm" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
            </div>
            <div class="text width_3">
                
 						<dsp:input bean="ProfileFormHandler.value.confirmpassword" type="password" autocomplete="off" name="confirmPassword" id="confirmNewPassword" value="" iclass="input_large211 block escapeHTMLTag" >
	                            <dsp:tagAttribute name="aria-required" value="true"/>
	                            <dsp:tagAttribute name="aria-labelledby" value="lblcPassword errorcPassword"/>
	                        </dsp:input>
 			</div> 

          </div>
          <div class="formRow clearfix">
           <div class="button button_active  button_active_orange">
           		<%-- 				<dsp:getvalueof var="accountVO" bean="SessionBean.accountVo" />
	  					<dsp:getvalueof var="firstName" value="${accountVO.firstName}" />
	 					<dsp:getvalueof var="lastName" value="${accountVO.lastName}" />
						<dsp:input bean="ProfileFormHandler.value.firstName" name="fname" id="firstName" type="hidden" value="${firstName}"/>
						<dsp:input bean="ProfileFormHandler.value.lastName" name="lname" id="lastName" type="hidden" value="${lastName}"/>
			--%>			
						<dsp:getvalueof var="token" param="token"/>
					<dsp:input bean="ProfileFormHandler.value.urlToken" type="hidden" value="${token}"/>
						<dsp:input bean="ProfileFormHandler.value.resetFlag" type="hidden" value="true"/>
						<dsp:input bean="ProfileFormHandler.autoLoginAfterChangePwd" type="hidden" value="true"/>
						
						<%-- <dsp:input bean="ProfileFormHandler.changePasswordSuccessURL" type="hidden" value="${contextPath}/account/login.jsp?fromResetPage=true"/>
						<dsp:input bean="ProfileFormHandler.changePasswordErrorURL" type="hidden" value="${contextPath}/account/frags/changePwdAfterResetmodel.jsp"/>
						--%>
						<c:if test="${sessionScope.userCheckingOut eq 'true'}">
							<dsp:input bean="ProfileFormHandler.userCheckingOut" type="hidden" value="userCheckingOut"/>
						</c:if>
						<dsp:input bean="ProfileFormHandler.resetPasswordToken" id="newPasswordButton" type="Submit" value="Submit" />
				 </div>
	               <!-- <a href="#" title="Cancel" role="link" class="close-any-dialog buttonTextLink"><bbbl:label key="lbl_change_pass_model_cancel" language ="${pageContext.request.locale.language}"/></a> -->
				</div>
			</dsp:form>
			  
	    </div>
    </div>
</div>


</bbb:pageContainer>
<c:set var="omniPageName" value="My Account"/>
<script type="text/javascript">
		
	function resetPasswordLoadOmniture() {
	if (typeof s !== "undefined") {
			s.prop1 = s.prop2 = s.prop3 = s.prop4 = s.prop5 = s.prop6 =s.prop7 = s.prop8 = s.prop25 ='';
			s.eVar1 = s.eVar2 = s.eVar3 = s.eVar4 = s.eVar5 = s.eVar6 =s.eVar7 = s.eVar8 = s.eVar47 ='';
			s.pageName  = '${omniPageName}>Create new password';
			s.channel = s.prop1 = s.prop2 = s.prop3 = "${omniPageName}";
			s.prop4 = s.prop5 = "";
			s.events ='';
			s.products ='';

			s.t();
			s.linkTrackVars="None";
			s.linkTrackEvents="None";

		}	
	}
	
	resetPasswordLoadOmniture();
</script>
</dsp:page>



