<dsp:page>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof var="appid" bean="Site.id" />

<c:set var="pageWrapper" value="myAccount useFB" scope="request" />
<c:set var="pageNameFB" value="login" scope="request" />


   
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
   <div>
		
			<dsp:getvalueof id="emailError" bean="/atg/userprofiling/ForgotPasswordHandler.errorMap.emailError" scope="session"/>
			
			<div>
						
			 		<div id="resetPasswordContent" class="successMsgColoredBox">
							<p><bbbl:label key="lbl_reset_password_message_head" language="${pageContext.request.locale.language}" /></p>
					</div>
			 	<p class="error cb fcConnectErrorMsg hidden"></p>
				
				<dsp:setvalue   bean="Profile.loginFrom" beanvalue="/Constants.null" />
				<dsp:getvalueof var ="securityStatus" bean="Profile.securityStatus"/>
				<c:choose>
				<c:when test="${securityStatus eq '2' }">
					<dsp:include page="frags/login_frag_new.jsp">
						<dsp:param name="disableUid" value="true"/>
					</dsp:include>
				</c:when>
				<c:otherwise>
					<dsp:droplet name="/atg/dynamo/droplet/Switch">
						<dsp:param name="value" bean="Profile.transient"/>
						<dsp:oparam name="true">
								<dsp:include page="frags/login_frag_new.jsp">
								 <dsp:param name="showLegacyPwdPopup" param="showLegacyPwdPopup"/>
								 <dsp:param name="showMigratedPopup" param="showMigratedPopup"/>
								</dsp:include>
							
				   		</dsp:oparam>			
				</dsp:droplet>
			</c:otherwise>
			</c:choose>
			</div>
			<c:if test='${param.kirsch == "yes"}'>
                <div class="clearfix">
                    <p class="marTop_20 marBottom_20"><a href="${sessionScope.kirschRedirectUrl}" title="Browse without signing in &gt;&gt;">Browse without signing in &gt;&gt;</a></p>
                </div>
            </c:if>
		</div>


</dsp:page>