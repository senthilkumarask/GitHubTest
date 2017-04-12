<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/targeting/TargetingRandom"/>
	<dsp:importbean bean="/atg/userprofiling/ForgotPasswordHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

	<%-- Variables --%>
	<c:set var="pageWrapper" value="login myAccount useFB" scope="request" />
	<c:set var="pageNameFB" value="login" scope="request" />
	<c:set var="redirectPage" value="${param.redirectPage}" scope="session"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="bvChk" param="BVDoLogin" />
	<c:set var="userCheckingOut" value="false" scope="session"/>
	<c:if test="${(not empty bvChk)}">
		<dsp:setvalue value="txt_login_bazaar_voice" bean="Profile.loginFrom" />
	</c:if>
	<c:if test="${param.kirsch == 'yes'}">
		<dsp:setvalue value="txt_levolor_login_message" bean="Profile.loginFrom" />
	</c:if>
	<dsp:getvalueof id="emailError" bean="ForgotPasswordHandler.errorMap.emailError" scope="session"/>
	<dsp:getvalueof var="associateName" value="${sessionScope.associate1}"/>
	<bbb:pageContainer section="accounts">

		<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
		
		<div class="row login-forms">
			<div class="small-12 columns">
				<%-- <h1 class="<c:if test="${not empty associateName}">marTop_35</c:if>"> --%>
				<h1>
					<bbbl:label key="lbl_login_welcome" language="${pageContext.request.locale.language}" /></h1>
				<dsp:droplet name="TargetingRandom">
					<dsp:param bean="/atg/registry/RepositoryTargeters/TargeterLoginPage" name="targeter"/>
					<dsp:param name="howMany" value="1"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="loginInfo" param="element.key"/>
						<p><bbbl:textArea key="${loginInfo}" language="${pageContext.request.locale.language}" /></p>
					</dsp:oparam>
				</dsp:droplet>
				<%-- KP COMMENT START: Not sure if this is needed, it was present in the legacy code --%>
				<%-- <p class="error cb fcConnectErrorMsg hidden"></p> --%>
				<%-- KP COMMENT END --%>
			</div>

			<dsp:setvalue bean="Profile.loginFrom" beanvalue="/Constants.null" />

			<dsp:droplet name="Switch">
				<dsp:param name="value" bean="Profile.transient"/>
				<dsp:oparam name="true">
					<dsp:include page="frags/login_frag.jsp">
						<dsp:param name="showLegacyPwdPopup" param="showLegacyPwdPopup"/>
						<dsp:param name="showMigratedPopup" param="showMigratedPopup"/>
					</dsp:include>
					<dsp:include page="frags/create_user_frag.jsp"/>
				</dsp:oparam>
			</dsp:droplet>

			<%-- KP COMMENT START: Present in legacy code but not in design, so removed --%>
			<%--
				<bbbt:textArea key="txt_login_benefits_account" language="${pageContext.request.locale.language}"/>
				<c:if test='${param.kirsch == "yes"}'>
							<a href="${sessionScope.kirschRedirectUrl}" title="Browse without signing in &gt;&gt;">Browse without signing in &gt;&gt;</a>
				</c:if>
			<%-- KP COMMENT END --%>
		</div>

		<jsp:attribute name="footerContent">
			<script type="text/javascript">
				if (typeof s !== 'undefined') {
					s.channel = 'My Account';
					s.pageName='My Account>Log In';
					s.prop1='My Account';
					s.prop2='My Account';
					s.prop3='My Account';
					s.prop6='${pageContext.request.serverName}';
					s.eVar9='${pageContext.request.serverName}';
				var s_code = s.t();
				if (s_code)
					document.write(s_code);
				}
			</script>
		</jsp:attribute>

	</bbb:pageContainer>

	<dsp:include page="/account/idm/idm_login.jsp" />

</dsp:page>
