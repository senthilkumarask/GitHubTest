<dsp:page>
<dsp:importbean bean="/atg/userprofiling/ForgotPasswordHandler"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<c:set var="lbl_forgot_password_heading"><bbbl:label key="lbl_forgot_password_heading" language ="${pageContext.request.locale.language}"/></c:set>
<c:set var="challengeQuestionON"><bbbc:config key="challenge_question_flag" configName="FlagDrivenFunctions" /></c:set>
<dsp:getvalueof var="isFromRegistry" param="isFromRegistry" />
<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
<dsp:getvalueof var="pageName" param="pageName" />
<dsp:setvalue bean="SessionBean.pwdReqFrmPageName" value="${pageName}" />
<div id="forgotPasswordDialog">
    <div class="width_3 <c:if test='${true eq isFromRegistry}'>forgotPasswordLink</c:if> clearfix">
        <div class="formRow">
		<c:choose>
		<c:when test="${not empty isFromRegistry && isFromRegistry eq true}">
			<bbbl:label key="lbl_forgotPasswordModelMessage" language="${pageContext.request.locale.language}"/>
		</c:when>
		<c:otherwise>
			<bbbl:textArea key="txtarea_forgot_password_desc" language="${pageContext.request.locale.language}"/>
		</c:otherwise>
		</c:choose>
		<%---Please enter the email address associated with your Bed Bath & Beyond account to receive a resset password link.--%>
		</div>
    
		<dsp:form iclass="clearfix width_3" id="frmForgotEmail" method="post" action="${contextPath}/account/frags/forgot_password.jsp">
			<div class="formRow fl clearfix width_3">
				<div class="input clearfix">
					<c:choose>
					<c:when test="${not empty isFromRegistry && isFromRegistry eq true}">
					</c:when>
					<c:otherwise>
					<div class="width_3 fl label noMar">
						<label id="lblemail" for="email"><bbbl:label key="lbl_forgot_password__email" language="${pageContext.request.locale.language}" /><span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label> 
						<%-- <label id="lblemail" for="email">email</label>--%>
					</div>
					</c:otherwise>
					</c:choose>
					<div class="width_3 fl text noMar">
					<c:set var="lbl_forgot_password__email">
                       <bbbl:label key="lbl_forgot_password__email" language="<c:out param='${pageContext.request.locale.language}'/>" /></c:set>
						<dsp:input id="email" name="email" type="email" bean="ForgotPasswordHandler.value.email" priority="1000">
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                            <dsp:tagAttribute name="placeholder" value="${lbl_forgot_password__email}"/>
                        </dsp:input>
						<dsp:input bean="ForgotPasswordHandler.forgotPasswordErrorURL" type="hidden" value="${contextPath}/account/frags/forgot_password_json.jsp"/>
				 	<dsp:input bean="ForgotPasswordHandler.forgotPasswordSuccessURL" type="hidden" value="${contextPath}/account/frags/forgot_password_json.jsp"/>
					</div>
				</div>
			</div>
			

			<div class="formRow fl noMar clearfix width_3">
				<div class="fl">
				<c:set var="submitKey">
					<bbbl:label key='lbl_forgot_password__submit' language='${pageContext.request.locale.language}' />
				</c:set>
				<c:choose>
				<c:when test="${challengeQuestionON}">
				<input type="hidden" name="pageName" value="${pageName}"/>
				<dsp:input bean="ForgotPasswordHandler.resetPasswordWithChallengeQuestion" iclass="blueButton button-Med" type="submit" name="pwdRestButton" value="${submitKey}" priority="-1000">
                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                    <dsp:tagAttribute name="role" value="button"/>
                </dsp:input>
                </c:when>
                <c:otherwise>
                <dsp:input bean="ForgotPasswordHandler.forgotPassword" iclass="blueButton button-Med" type="submit" name="pwdRestButton" value="${submitKey}" priority="-1000">
                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                    <dsp:tagAttribute name="role" value="button"/>
                </dsp:input>
                </c:otherwise>
                </c:choose>
				</div>
				<a href="#" title="Cancel" class="close-any-dialog buttonTextLink <c:if test='${true ne isFromRegistry}'>hidden</c:if> <c:if test='${true eq isFromRegistry}'>fromRegistry</c:if>" role="link"><bbbl:label key="lbl_change_pass_model_cancel" language ="${pageContext.request.locale.language}"/></a>
			</div>
		</dsp:form>
    </div>
</div>
<dsp:getvalueof var="omnitureForCheckout" param="omnitureForCheckout" />
<c:set var="spcCheck" value="false"/>
<c:if test="${pageName == 'spcPage' || pageName=='multiShip'}">
	<c:set var="spcCheck" value="true"/>
</c:if>
<c:if test="${omnitureForCheckout == 'true'}">
	<script type="text/javascript">
		if (typeof s !== 'undefined') {
			s.pageName ='Checkout>Reset your password';
			s.channel = 'Checkout';
			s.prop1 = 'Checkout';
			s.prop2 = 'Checkout';
			s.prop3 = 'Checkout';

			var s_code = s.t();
			if (s_code)
				document.write(s_code);
		}
	</script>	
</c:if>

<c:set var="omniPageName" value="My Account"/>
<c:set var="omniSeparator" value=">"/>
<c:set var="redirectionPageName" value="Reset your password"/>
<c:if test="${pageName == 'simpleRegPrimaryRegistrant'}">
	<c:set var="redirectionPageName" value="Registry Create Page"/>
	<c:set var="omniPageName" value=""/>
	<c:set var="omniSeparator" value=""/>
</c:if>


<c:if test="${spcCheck == 'false'}">
<script type="text/javascript">
		
	function resetPasswordLoadOmniture() {
	if (typeof s !== "undefined") {
			s.prop1 = s.prop2 = s.prop3 = s.prop4 = s.prop5 = s.prop6 =s.prop7 = s.prop8 = s.prop25 ='';
			s.eVar1 = s.eVar2 = s.eVar3 = s.eVar4 = s.eVar5 = s.eVar6 =s.eVar7 = s.eVar8 = s.eVar47 ='';
			s.pageName  = '${omniPageName}${omniSeparator}${redirectionPageName}';
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
</c:if>
</dsp:page>