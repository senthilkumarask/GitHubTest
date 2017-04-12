<dsp:page>
	<%@ page import="com.bbb.social.facebook.FBConstants"%>
	<dsp:importbean bean="/com/bbb/social/facebook/FBUserSiteAssocDroplet"/>
	<dsp:importbean var="ProfileFormHandler"	bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:droplet name="FBUserSiteAssocDroplet">
	<dsp:param name="profileLookup" value="<%=FBConstants.PROFILE_LOOKUP_BASED_ON_FBEMAILID%>"/>
	<dsp:param name="siteId" bean="Site.id"/>
	<dsp:oparam name="output">
	<div title='<bbbl:label key="lbl_fb_extend_profile_title" language ="${pageContext.request.locale.language}"/>'>
		<jsp:useBean id="customKeys" class="java.util.HashMap" scope="request"/>
		<c:set target="${customKeys}" property="emailAddress">${sessionScope.FB_BASIC_INFO.email}</c:set>

		<bbbl:textArea key="txt_fb_extend_profile_with_pwd" language ="${pageContext.request.locale.language}" placeHolderMap="${customKeys}"/>
		
		<dsp:form id="frmExtendAccount" action="${contextPath}/account/fb_extend_profile_password_check.jsp" method="post">
			<dsp:input bean="ProfileFormHandler.value.login" type="hidden" value="${sessionScope.FB_BASIC_INFO.email}"/>
			<dsp:input bean="ProfileFormHandler.emailAddress" type="hidden" value="${sessionScope.FB_BASIC_INFO.email}"/>
			<dsp:input bean="ProfileFormHandler.assoSite" type="hidden" value="default"/>
			<div class="input clearfix">
				<div class="label posRel">
					<label id="lblpassword" for="password"><bbbl:label key="lbl_spc_login_frag__password" language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
                    <div class="showPassDiv clearfix">
                        <input name="showPassword" type="checkbox" value="" data-toggle-class="showpassExtendAccount" class="showPassword" id="showPasswordExtend" aria-labelledby="lblshowPasswordExtend" />
                        <label id="lblshowPasswordExtend" for="showPasswordExtend" class="lblShowPassword"><bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/></label>
                        <div class="clear"></div> 
                    </div>
				</div>
				<div class="text width_3">
					<dsp:input id="password" bean="ProfileFormHandler.value.password" name="password" value="" type="password" autocomplete="off" iclass="showpassExtendAccount">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
                    </dsp:input>
					<label id="errorpassword" class="error"><dsp:valueof bean="ProfileFormHandler.errorMap.loginPasswordError"/></label>
				</div>
				<div>
					<dsp:input type="checkbox" bean="ProfileFormHandler.extendEmailOptn" id="optIn_extend" name="emailOptIn_extend" checked="false" iclass="fl">
                    <dsp:tagAttribute name="aria-checked" value="false"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
                    </dsp:input>
                    <label id="lbl_optIn_extend" for="optIn_extend" class="textDgray11 wrapTextAfterCheckBox"><bbbl:label key="lbl_email_optin_for_extend_account" language="${pageContext.request.locale.language}"/></label>
				</div>			
			</div>
	
			<div class="formRow clearfix">
				<div class="button fl marRight_10">					
					<dsp:input bean="ProfileFormHandler.validateAndExtendAccount" name="btnExtend" type="submit" value="EXTEND ACCOUNT">
                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="btnExtend"/>
                    </dsp:input>
				</div>
				<div class="button">				
					<input type="button" id="btnCancel" value="Cancel" role="button" aria-pressed="false" aria-labelledby="btnCancel"/>				
				</div>
			</div>
		</dsp:form>
	</div>
	</dsp:oparam>
	</dsp:droplet>
</dsp:page>