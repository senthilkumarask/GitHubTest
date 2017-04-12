<dsp:page>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Redirect"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

	<c:set var="pageWrapper" value="createAccount myAccount useFB" scope="request" />
	<c:set var="pageNameFB" value="registrationEdit" scope="request" />

	<dsp:getvalueof id="currentSiteId" bean="Site.id" />

	<c:set var="email"><dsp:valueof value="${email}"/></c:set>

	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="SessionBean.userEmailId" />
		<dsp:oparam name="false">
			<dsp:getvalueof var="legacyEmail" bean="SessionBean.userEmailId"/>
			<c:set var="emailToRegister" scope="session" value="${legacyEmail}" />
			<dsp:setvalue bean="SessionBean.userEmailId" value="" />
		</dsp:oparam>
	</dsp:droplet>

	<c:choose>
		<c:when test="${not empty email}">
			<c:set var="emailToRegister" scope="session" value="${email}" />
		</c:when>
		<c:when test="${empty email && empty emailToRegister}">
			<c:set var="emailToRegister" scope="session" value="${sessionScope.FB_BASIC_INFO.email}" />
		</c:when>
	</c:choose>
	<c:choose>
		<c:when test="${empty email && empty emailToRegister}">
			<dsp:droplet name="Redirect">
				<dsp:param name="url" value="/account/login.jsp"/>
			</dsp:droplet>
		</c:when>
	</c:choose>

		<bbb:pageContainer section="accounts" bodyClass="" index="false" follow="false">
			<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
			<jsp:body>
				<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
				<c:set var="profile_private_policy_title"><bbbl:label key="lbl_profile_private_policy" language="${pageContext.request.locale.language}"/></c:set>

				<dsp:form method="post" id="createAccount" action="myaccount.jsp">
					<dsp:droplet name="Switch">
						<dsp:param name="value" bean="Profile.transient"/>
						<dsp:oparam name="true">

							<div class="row">
								<div class="small-12 columns">
									<h1><bbbl:label key="lbl_createaccount_header" language="${pageContext.request.locale.language}"/></h1>
									<dsp:getvalueof var="createProfileEmailError" bean="ProfileFormHandler.errorMap.createProfileEmailError"></dsp:getvalueof>
									<dsp:getvalueof var="createProfileFirstNameError" bean="ProfileFormHandler.errorMap.createProfileFirstNameError"></dsp:getvalueof>
									<dsp:getvalueof var="createProfileLastNameError" bean="ProfileFormHandler.errorMap.createProfileLastNameError"></dsp:getvalueof>
									<dsp:getvalueof var="createProfilePasswordError" bean="ProfileFormHandler.errorMap.createProfilePasswordError"></dsp:getvalueof>
									<dsp:getvalueof var="createProfileConfirmPasswordError" bean="ProfileFormHandler.errorMap.createProfileConfirmPasswordError"></dsp:getvalueof>
									<dsp:getvalueof var="createProfileContainsName" bean="ProfileFormHandler.errorMap.createProfileContainsName"></dsp:getvalueof>
									<dsp:getvalueof var="createProfilePhoneNumberError" bean="ProfileFormHandler.errorMap.createProfilePhoneNumberError"></dsp:getvalueof>
									<dsp:getvalueof var="createProfileMobileNumberError" bean="ProfileFormHandler.errorMap.createProfileMobileNumberError"></dsp:getvalueof>
									<dsp:getvalueof var="createProfileSystemError" bean="ProfileFormHandler.errorMap.createProfileSystemError"></dsp:getvalueof>

									<%--	ErrorMessageForEach	 starts --%>
									<dsp:include page="/global/gadgets/errorMessage.jsp">
									<dsp:param name="formhandler" bean="ProfileFormHandler"/>
									</dsp:include>
									<%--	ErrorMessageForEach Ends --%>

									<dsp:getvalueof var="load_fb_info" param="load_fb_info" />
									<dsp:getvalueof id="memberId" param="memberId"/>
									<dsp:getvalueof var="memberVar" param="memberId"/>

									<c:if test="${not empty memberVar}">
										<p><bbbt:textArea key="txtarea_reclaim_registration_message" language="${pageContext.request.locale.language}"/><p>
									</c:if>
								</div>
							</div>
							<div class="row">
								<div class="small-12 large-6 columns">

									<c:set var="fnamePlaceholder">
										<bbbl:label key="lbl_profile_firstname" language="${pageContext.request.locale.language}"/>
									</c:set>

									<dsp:input bean="ProfileFormHandler.value.firstName" type="text" iclass="input_large211 block" name="firstName" id="firstName">
										<dsp:tagAttribute name="data-load-fb" value="first_name"/>
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lblfirstName errorfirstName"/>
										<dsp:tagAttribute name="placeholder" value="${fnamePlaceholder} *"/>
									</dsp:input>

									<c:set var="lnamePlaceholder">
										<bbbl:label key="lbl_profile_lastname" language="${pageContext.request.locale.language}"/>
									</c:set>

									<dsp:input bean="ProfileFormHandler.value.lastName" type="text" iclass="input_large211 block" name="lastName" id="lastName">
										<dsp:tagAttribute name="data-load-fb" value="last_name"/>
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lbllastName errorlastName"/>
										<dsp:tagAttribute name="placeholder" value="${lnamePlaceholder} *"/>
									</dsp:input>

									<c:set var="phonePlaceholder">
										<bbbl:label key="lbl_profile_primaryphone" language="${pageContext.request.locale.language}"/> (optional)
									</c:set>

									<dsp:input bean="ProfileFormHandler.value.phoneNumber" type="text" iclass="phone phoneField" name="basePhoneFull">
										<dsp:tagAttribute name="placeholder" value="${phonePlaceholder}"/>
										<dsp:tagAttribute name="maxlength" value="10"/>
										<dsp:tagAttribute name="aria-required" value="false"/>
										<dsp:tagAttribute name="aria-labelledby" value="lblbasePhoneFull errorbasePhoneFull"/>
									</dsp:input>
									
									<input id="memberId" name="memberId" type="hidden" value='<dsp:valueof param="memberId"/>'/>

									<c:set var="mobilePlaceholder">
										<bbbl:label key="lbl_profile_mobilephone" language="${pageContext.request.locale.language}"/> (optional)
									</c:set>

									<dsp:input bean="ProfileFormHandler.value.mobileNumber" type="text" iclass="phone phoneField" name="cellPhoneFull">
										 <dsp:tagAttribute name="placeholder" value="${mobilePlaceholder}"/>
										 <dsp:tagAttribute name="maxlength" value="10"/>
										 <dsp:tagAttribute name="aria-required" value="false"/>
                            			 <dsp:tagAttribute name="aria-labelledby" value="lblcellPhoneFull errorcellPhoneFull"/>
									</dsp:input>

									<script type="text/javascript">
										var BBB = BBB || {};
										BBB.accountManagement = true;
									</script>

									<%-- KP COMMENT START: Removing FB until confirmed in scope --%>
									<%--
										<c:if test="${FBOn}">
											<div class="renderFBConnect marTop_20" data-fb-connect-section="registrationEdit">
												<img src="${imagePath}/_assets/global/images/fb-loader.gif" alt="Facebook User" class="fbConnectLoader" />
												<p class="marBottom_5 noMarTop hideWithFBInfo hidden showWithFBCBtn">Connect your account to Facebook for faster registration</p>
											</div>
										</c:if>
									--%>
									<%-- KP COMMENT END --%>
									
									<%-- KP COMMENT START: Removing FB until confirmed in scope --%>
									<%--
										<c:if test="${not empty load_fb_info}">
											<dsp:input bean="ProfileFormHandler.fbLinking" type="hidden" name="fbLinking" id="fbLinking" value="true" />
										</c:if>
									--%>
									<%-- KP COMMENT END --%>
								</div>
								<div class="small-12 large-6 columns">

									<c:if test="${not empty createProfileMobileNumberError}">
										<label class="error" for="email">
											<%--<bbbe:error key="${createProfileMobileNumberError}" language="${pageContext.request.locale.language}"/> --%>
											<jsp:useBean id="placeHolderMobile" class="java.util.HashMap" scope="request"/>
											<c:set target="${placeHolderMobile}" property="fieldName"><bbbl:label key='lbl_profile_mobilephone' language='${pageContext.request.locale.language}'/></c:set>
											<bbbe:error key="${createProfilePhoneNumberError}" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolderMobile}"/>
										</label>
									</c:if>

									<c:set var="emailPlaceholder">
										<bbbl:label key="lbl_profilemain_email" language="${pageContext.request.locale.language}"/>
									</c:set>

									<c:choose>
										<c:when test="${empty emailToRegister}">
											<c:set var="emailToRegister"></c:set>
										</c:when>
										<c:otherwise>
										</c:otherwise>
									</c:choose>

									<c:set var="emailPlaceholder">
										<bbbl:label key="lbl_profilemain_email" language="${pageContext.request.locale.language}"/>
									</c:set>

									<dsp:input bean="ProfileFormHandler.value.email" value="${emailToRegister}" type="text">
										<dsp:tagAttribute name="placeholder" value="${emailPlaceholder} *"/>
									</dsp:input>

									<c:set var="passPlaceholder">
										<bbbl:label key="lbl_profile_password" language="${pageContext.request.locale.language}"/>
									</c:set>

									<dsp:input bean="ProfileFormHandler.value.password" type="password" autocomplete="off" iclass="" id="password" name="password" value="">
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
										<dsp:tagAttribute name="placeholder" value="${passPlaceholder} *"/>
									</dsp:input>

									<c:set var="pass2Placeholder">
										<bbbl:label key="lbl_profile_confirmpassword" language="${pageContext.request.locale.language}"/>
									</c:set>
									
									<dsp:input bean="ProfileFormHandler.value.confirmPassword" type="password" autocomplete="off" iclass="input_large211 block" id="cPassword" name="confirmPassword" value="">
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lblcPassword errorcPassword"/>
										<dsp:tagAttribute name="placeholder" value="${pass2Placeholder} *"/>
									</dsp:input>
								</div>
							</div>
							<div class="row">
								<div class="small-12 columns">
									<c:set var="lbl_createaccount_button">
										<bbbl:label key='lbl_createaccount_main_header' language='${pageContext.request.locale.language}' />
									</c:set>

									<dsp:setvalue param="memberId" value="${memberVar}"/>
									<dsp:setvalue param="load_fb_info" value="${load_fb_info}" />

									<c:choose>
										<c:when test="${not empty sessionScope.redirectPage && sessionScope.redirectPage=='MyRegistries'}">
											<c:remove var="redirectPage" scope="session" />
											<dsp:input  bean="ProfileFormHandler.createAcctMyReg" type="submit" value="${lbl_createaccount_button}" iclass="small button primary" id="createAccontBtn">
												<dsp:tagAttribute name="aria-pressed" value="false"/>
												<dsp:tagAttribute name="aria-describedby" value="createAccontBtn"/>
												<dsp:tagAttribute name="role" value="button"/>
											</dsp:input>
											<dsp:input	 bean="ProfileFormHandler.createAcctMyReg" type="hidden" value="${lbl_createaccount_button}" iclass="submitBtn submitBtnMarginRight">
											</dsp:input>

										</c:when>
										<c:otherwise>
											<dsp:input	bean="ProfileFormHandler.createAcct" type="submit" value="${lbl_createaccount_button}" iclass="small button primary" id="createAccontBtn">
												<dsp:tagAttribute name="aria-pressed" value="false"/>
												<dsp:tagAttribute name="aria-describedby" value="createAccontBtn"/>
												<dsp:tagAttribute name="role" value="button"/>
											</dsp:input>
											<dsp:input	 bean="ProfileFormHandler.createAcct" type="hidden" value="${lbl_createaccount_button}" iclass="submitBtn submitBtnMarginRight">
											</dsp:input>

										</c:otherwise>
									</c:choose>

									<c:set var="lbl_cancel_button"><bbbl:label key='lbl_profile_Cancel' language='${pageContext.request.locale.language}'/></c:set>
									<a href="login.jsp" value="${lbl_cancel_button}" type="button" class="small button secondary" role="link">${lbl_cancel_button}</a>

									<p><a href="/tbs/static/PrivacyPolicy?showAsPopup=true" class="privacyPolicyModalTrigger" title="${profile_private_policy_title}"><bbbl:label key="lbl_profile_private_policy" language="${pageContext.request.locale.language}"/></a></p>
								</div>
							</div>
							<div class="row">
								<div class="small-12 columns">
									<c:choose>
										<c:when test="${shareProfileOn}">
											<dsp:droplet name="Switch">
												<dsp:param name="value" bean="Site.id"/>
												<dsp:oparam name="TBS_BedBathCanada">
												</dsp:oparam>
												<dsp:oparam name="default">
													<label class="inline-rc checkbox" for="shareAccount">
														<dsp:input bean="ProfileFormHandler.sharedCheckBoxEnabled" type="checkbox" value="true" checked="true" iclass="fl" id="shareAccount">
															<dsp:tagAttribute name="aria-checked" value="true"/>
															<dsp:tagAttribute name="aria-labelledby" value="lblshareAccount errorshareAccount"/>
														</dsp:input>
														<span></span>
														<bbbl:label key="lbl_profile_shareaccount" language="${pageContext.request.locale.language}"/>
													</label>
													<p class="p-secondary">
														<bbbt:textArea key="txt_profile_shareSite_text" language="${pageContext.request.locale.language}"/>
													</p>
												</dsp:oparam>
											</dsp:droplet>
										</c:when>
										<c:otherwise>
											<dsp:input bean="ProfileFormHandler.sharedCheckBoxEnabled" type="hidden" value="false" checked="false" iclass="fl" id="shareAccount">
												<dsp:tagAttribute name="aria-checked" value="false"/>
											</dsp:input>
										</c:otherwise>
									</c:choose>
									<label class="inline-rc checkbox" for="optIn">
										<dsp:getvalueof bean="ProfileFormHandler.value.receiveEmail" id="flag">
											<c:choose>
												<c:when test="${(currentSiteId eq TBS_BedBathCanadaSite) || (flag == 'no')}">
													<dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" id="optIn" name="emailOptIn" checked="false" iclass="fl">
														<dsp:tagAttribute name="aria-checked" value="false"/>
														<dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
													</dsp:input>
												</c:when>
												<c:otherwise>
													<c:if test="${flag == 'yes'}">
														<dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" id="optIn" name="emailOptIn" checked="true" value="true" iclass="fl">
															<dsp:tagAttribute name="aria-checked" value="true"/>
															<dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
														</dsp:input>
													</c:if>
												</c:otherwise>
											</c:choose>
										</dsp:getvalueof>
										<span></span>
										<bbbl:label key="lbl_email_optin_bedbath_canada" language="${pageContext.request.locale.language}"/>
									</label>
									<dsp:getvalueof var="babyCA" bean="SessionBean.babyCA"/>

									<!-- Baby Canada -->
									<c:if test="${currentSiteId eq TBS_BedBathCanadaSite}">
										<label class="inline-rc checkbox" for="emailOptIn_BabyCA">
											<dsp:getvalueof bean="ProfileFormHandler.value.receiveEmail" id="flag">
												<c:choose>
													<c:when test="${(currentSiteId eq TBS_BedBathCanadaSite) || (flag == 'no')}">
														<dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn_BabyCA" id="emailOptIn_BabyCA" name="emailOptIn_BabyCA" checked="false" iclass="fl">
															<dsp:tagAttribute name="aria-checked" value="false"/>
															<dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
														</dsp:input>
													</c:when>
													<c:otherwise>
														<c:if test="${flag == 'yes'}">
															<dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn_BabyCA" id="emailOptIn_BabyCA" name="emailOptIn_BabyCA" checked="true" value="true" iclass="fl">
																<dsp:tagAttribute name="aria-checked" value="true"/>
																<dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
															</dsp:input>
														</c:if>
													</c:otherwise>
												</c:choose>
											</dsp:getvalueof>
											<span></span>
											<bbbl:label key="lbl_email_optin_baby_canada" language="${pageContext.request.locale.language}"/>
										</label>
									</c:if>
									<!-- Baby Canada -->
								</div>
							</div>

						</dsp:oparam>

						<dsp:oparam name="false">
							<bbbl:label key="lbl_profile_loggedIn" language="${pageContext.request.locale.language}"/>
							<dsp:a page="/login.jsp">
								<dsp:property bean="ProfileFormHandler.logoutSuccessURL" value="account/login.jsp"/>
								<dsp:property bean="ProfileFormHandler.logout" value="true"/>
								<bbbl:label key="lbl_profile_logout" language="${pageContext.request.locale.language}"/>
							</dsp:a>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:form>
			<%-- KP COMMENT START: Not including this in the new layout --%>
			<%-- <div class="row">
				<div class="small-12 columns">
					<bbbt:textArea key="txt_login_benefits_account" language="${pageContext.request.locale.language}"/>
				</div>
			</div> --%>
			<%-- KP COMMENT END --%>
		</jsp:body>
		<jsp:attribute name="footerContent">
			<script type="text/javascript">
				if (typeof s !== 'undefined') {
					s.channel = 'My Account';
					s.pageName='My Account > Create Account';
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
