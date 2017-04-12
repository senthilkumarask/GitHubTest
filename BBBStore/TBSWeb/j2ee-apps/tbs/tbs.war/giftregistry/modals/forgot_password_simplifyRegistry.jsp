<dsp:page>
<dsp:importbean bean="/atg/userprofiling/ForgotPasswordHandler"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<c:set var="lbl_forgot_password_heading">forgot your password</c:set>
<div id="forgotPasswordDialog" title="${lbl_forgot_password_heading}">
    <div class="width_6 clearfix">
        <p class="description noMarBot"><bbbl:textArea key="txtarea_forgot_password_desc" language="${pageContext.request.locale.language}" /></p>
    	<div id="pageErrors">
    	
    	</div>
		<dsp:form iclass="clearfix width_6" id="frmForgotPwd" method="post" action="${contextPath}/giftregistry/modals/forgot_password_simplifyRegistry.jsp">
			<div class="formRow fl noMarBot clearfix width_6 small-7 columns no-padding">
				<div class="input clearfix">
					<div class="width_4 fl text noMar">
						<dsp:input id="email" type="text" bean="ForgotPasswordHandler.value.email" name="email" priority="1000" iclass="required escapeHTMLTag">
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                            <dsp:tagAttribute name="placeholder" value="Email"/>
                        </dsp:input>
                        <%-- Client DOM XSRF | Part -2 || not making changes in these urls as it is using OOB method, so not extending the method just for urls --%>
						<dsp:input bean="ForgotPasswordHandler.forgotPasswordErrorURL" type="hidden" value="${contextPath}/account/frags/forgot_password_json.jsp"/>
				 		<dsp:input bean="ForgotPasswordHandler.forgotPasswordSuccessURL" type="hidden" value="${contextPath}/account/frags/forgot_password_json.jsp"/>
					</div>
				</div>
			</div>
			
			<div class="formRow fl noMarBot clearfix small-12 columns no-padding formButtons">
				<div class="fl button_active button_active_orange small-12 medium-5 columns no-padding-left ">
					<c:set var="submitKey">
						<bbbl:label key='lbl_forgot_password__submit' language='${pageContext.request.locale.language}' />
					</c:set>
					<dsp:input type="hidden" value="submit" bean="ForgotPasswordHandler.forgotPassword" name="forgotPassword" id="forgotPassword"/>
					<input type="submit" value="${submitKey}" name="pwdRestButton" id="pwdRestButton" class="button small primary column">
				</div>
				<div class="cancel columns medium-2"><a href="#" title="Cancel" class="close-modal cancelForm" role="link">Cancel</a></div>
			</div>
		</dsp:form>
    </div>
</div>
<dsp:getvalueof var="omnitureForCheckout" param="omnitureForCheckout" />

<c:if test="${omnitureForCheckout == 'true'}">
	<script type="text/javascript">
		if (typeof s !== 'undefined') {
			s.pageName ='Checkout';
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
</dsp:page>