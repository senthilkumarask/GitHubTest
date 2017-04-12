<dsp:page>
<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />

		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		<dsp:getvalueof id="registryId" idtype="java.lang.String"	param="registryId" />
	<div id="forgotPasswordDialog" title="Forgot Password">
    <div class="width_6 clearfix">
		<p class="noMarBot"><bbbl:label key='lbl_forgot_pwd_txt' language="${pageContext.request.locale.language}"></bbbl:label></p>

		<dsp:form iclass="clearfix width_6" id="frmForgotEmail" method="post" action="${contextPath}/giftregistry/modals/forgot_password_registry.jsp">
			<%-- <div class="formRow fl noMarBot clearfix width_6">
				<div class="input clearfix">
					<div class="width_4 fl label marRight_10">
						<label for="email"><bbbl:label key='lbl_forgot_pwd_email' language="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
					</div>
					<div class="text fl width_4">
					
					<!--<dsp:input bean="GiftRegistryFormHandler.forgetPasswordEmailId" value="" id="email" name="email"/>-->
					</div>
				</div>
			</div> --%>
			 <dsp:input	bean="GiftRegistryFormHandler.forgetPasswordRegistryId" type="hidden" value="${registryId}"/>
			<div class="formRow fl noMarBot clearfix width_6">
				<div class="fl button button_active button_active_orange">
					<input type="submit" name="regPwdRestButton" value="<bbbl:label key='lbl_forgot_pwd_retrieve' language='${pageContext.request.locale.language}'/>"
					  title="<bbbl:label key='lbl_forgot_pwd_retrieve' language='${pageContext.request.locale.language}'/>" role="button" aria-pressed="false" />
				</div>
                <a href="#" title="<bbbl:label key='lbl_forgot_pwd_cancel' language='${pageContext.request.locale.language}'/>" class="close-any-dialog buttonTextLink"><bbbl:label key='lbl_forgot_pwd_cancel' language='${pageContext.request.locale.language}'/></a>
			</div>
		
		</dsp:form>
    </div>
</div>
</dsp:page>