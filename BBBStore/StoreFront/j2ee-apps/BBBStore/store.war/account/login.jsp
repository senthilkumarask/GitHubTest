<dsp:page>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof var="appid" bean="Site.id" />

<c:set var="pageWrapper" value="login myAccount useFB" scope="request" />
<c:set var="pageNameFB" value="login" scope="request" />

<bbb:pageContainer section="accounts">
    <jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
    	<jsp:attribute name="PageType">Login</jsp:attribute>
	<c:set var="redirectPage" value="${param.redirectPage}" scope="session"/>
	
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	 <dsp:importbean bean="/atg/multisite/Site"/>
	 <dsp:importbean bean="/atg/targeting/TargetingRandom"/>

    <dsp:getvalueof var="bvChk" param="BVDoLogin" />
  <c:set var="userCheckingOut" value="false" scope="session"/>
  <c:if test="${(not empty bvChk)}">
    <dsp:setvalue value="txt_login_bazaar_voice" bean="Profile.loginFrom" />
  </c:if>
  <c:if test="${param.kirsch == 'yes'}">
  	<dsp:setvalue value="txt_levolor_login_message" bean="Profile.loginFrom" />
  </c:if>
   <div id="content" class="container_12 clearfix" role="main">
		
			<dsp:getvalueof id="emailError" bean="/atg/userprofiling/ForgotPasswordHandler.errorMap.emailError" scope="session"/>
			<div class="grid_12">
				<h1><bbbl:label key="lbl_login_welcome" language="${pageContext.request.locale.language}" /></h1>
			 </div>
			<div class="grid_9">
				<c:if test="${empty param.checkout || param.checkout ne true}">
						<p class="noMarTop">
							<dsp:droplet name="TargetingRandom">
							  <dsp:param bean="/atg/registry/RepositoryTargeters/TargeterLoginPage" name="targeter"/>
								<dsp:param name="howMany" value="1"/>
								   <dsp:oparam name="output">
							       <dsp:getvalueof var="loginInfo" param="element.key"/>
							       
								  <bbbl:textArea key="${loginInfo}" language="${pageContext.request.locale.language}" />
								</dsp:oparam>
						    </dsp:droplet>
					 	</p>
					 	
					 	<c:if test="${appid ne 'BedBathCanada'}">
					 	<div id="accountCheckContent">
							<h3><bbbl:textArea key="txt_do_have_an_account_header" language="${pageContext.request.locale.language}" /></h3>
							<p><bbbl:textArea key="txt_do_have_an_login_account_para" language="${pageContext.request.locale.language}" /></p>
						</div>
					</c:if>
			 		<div id="resetPasswordContent" class="successMsgColoredBox">
							<p><bbbl:label key="lbl_reset_password_message_head" language="${pageContext.request.locale.language}" /></p>
					</div>
				</c:if>
			 	<p class="error cb fcConnectErrorMsg hidden"></p>
				
				<dsp:setvalue   bean="Profile.loginFrom" beanvalue="/Constants.null" />
				<dsp:getvalueof var ="securityStatus" bean="Profile.securityStatus"/>
				<c:choose>
				<c:when test="${securityStatus eq '2' && not empty param.checkout && param.checkout eq true}">
					<dsp:include page="frags/guest_checkout_frag.jsp"/>
					<dsp:include page="frags/login_frag.jsp">
						<dsp:param name="disableUid" value="false"/>
					</dsp:include>
				</c:when>
				<c:when test="${securityStatus eq '2' }">
					<dsp:include page="frags/create_user_frag.jsp"/>
					<dsp:include page="frags/login_frag.jsp">
						<dsp:param name="disableUid" value="false"/>
					</dsp:include>
				</c:when>
				<c:otherwise>
					<dsp:droplet name="/atg/dynamo/droplet/Switch">
						<dsp:param name="value" bean="Profile.transient"/>
						<dsp:oparam name="true">
								<dsp:include page="frags/create_user_frag.jsp"/>
								<dsp:include page="frags/login_frag.jsp">
								 <dsp:param name="showLegacyPwdPopup" param="showLegacyPwdPopup"/>
								 <dsp:param name="showMigratedPopup" param="showMigratedPopup"/>
								</dsp:include>
							
				   		</dsp:oparam>			
				</dsp:droplet>
			</c:otherwise>
			</c:choose>
			</div>
			<div class="grid_3">
				<div class="teaser_229 benefitsAccountTeaser">
					<bbbt:textArea key="txt_login_benefits_account" language="${pageContext.request.locale.language}"/>
				</div>
			</div>
			<c:if test='${param.kirsch == "yes"}'>
                <div class="grid_12 clearfix">
                    <p class="marTop_20 marBottom_20"><a href="${sessionScope.kirschRedirectUrl}" title="Browse without signing in &gt;&gt;"><bbbl:label key="lbl_browse_guest" language="${pageContext.request.locale.language}" /> &gt;&gt;</a></p>
                </div>
            </c:if>
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
</dsp:page>