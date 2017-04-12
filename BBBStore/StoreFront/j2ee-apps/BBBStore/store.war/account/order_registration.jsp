<dsp:page>
	<bbb:pageContainer index="false" follow="false">
	
	    <jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
	    <jsp:attribute name="section">accounts</jsp:attribute>
	    <jsp:attribute name="pageWrapper">createAccount</jsp:attribute>
<%-- 	    <jsp:body> --%>
	
		<dsp:importbean bean="atg/userprofiling/ProfileFormHandler"/>
		<dsp:importbean bean="atg/userprofiling/Profile"/>
		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		
		<dsp:form method="post" id="createAccount" action="myaccount.jsp">
			<dsp:getvalueof var="createProfilePasswordError" bean="ProfileFormHandler.errorMap.createProfilePasswordError"></dsp:getvalueof>
			<dsp:getvalueof var="createProfileConfirmPasswordError" bean="ProfileFormHandler.errorMap.createProfileConfirmPasswordError"></dsp:getvalueof>
			<dsp:droplet name="/atg/dynamo/droplet/Switch">
				<dsp:param name="value" bean="Profile.transient"/>
				<dsp:oparam name="true">
					<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
						<dsp:oparam name="output">
							<dsp:valueof param="message"/>
						</dsp:oparam>
					</dsp:droplet>
						<div id="content" class="createAccount container_12 clearfix" role="main">
							<div class="grid_9">
								<h1><bbbl:label key="lbl_createaccount_main_header" language="${pageContext.request.locale.language}"/></h1>
								<div class="grid_6 alpha">
										<dsp:input bean="ProfileFormHandler.value.email" type="hidden" iclass="input_large211 block" name="email"/>
										<dsp:input bean="ProfileFormHandler.value.firstName" type="hidden" iclass="input_large211 block" name="fname"/>
										<dsp:input bean="ProfileFormHandler.value.lastName" type="hidden" iclass="input_large211 block" name="lname"/>
                                    <div class="formRow marBottom_5">
                                        <input name="showPassword" type="checkbox" value="" data-toggle-class="showpassOrderRegis" class="showPassword" id="showPassword" aria-labelledby="lblshowPassword" />
                                        <label id="lblshowPassword" for="showPassword" class="textDgray11 bold"><bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/></label>
                                    </div>
									<div class="formRow">
										<label id="lblpassword" for="password" class="textLgray12"><bbbl:label key="lbl_profile_password" language="${pageContext.request.locale.language}"/><span class="starMandatory"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}"/></span></label>
										<dsp:input bean="ProfileFormHandler.value.password" type="password" autocomplete="off" iclass="input_large211 block" id="password" name="password">
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
                                        </dsp:input>
										<c:if test="${not empty createProfilePasswordError}">
												<label id="errorpassword" class="error" for="password"><dsp:valueof bean="ProfileFormHandler.errorMap.createProfilePasswordError"/></label>
										</c:if>
									</div>
									<div class="formRow">
										<label id="lblcPassword" for="cPassword" class="textLgray12"><bbbl:label key="lbl_profile_confirmpassword" language="${pageContext.request.locale.language}"/><span class="starMandatory"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}"/></span></label>
										<dsp:input bean="ProfileFormHandler.value.confirmPassword" type="password" autocomplete="off" iclass="input_large211 block" id="cPassword" name="cPassword">
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblcPassword errorcPassword"/>
                                        </dsp:input>
										<c:if test="${not empty createProfileConfirmPasswordError}">
											<c:choose>
											<c:when test="${createProfilePasswordError == 'KEY_MAX_LENGTH_INCORRECT'}">
											<jsp:useBean id="placeHolderPwd" class="java.util.HashMap" scope="request"/>
											<c:set target="${placeHolderPwd}" property="fieldName"><bbbl:label key='lbl_profile_password' language='${pageContext.request.locale.language}'/></c:set>
											<bbbe:error key="err_profile_invalid" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolderPwd}"/>
												
											</c:when>
											<c:otherwise>
												<dsp:valueof bean="ProfileFormHandler.errorMap.createProfilePasswordError"/>
											</c:otherwise>
											</c:choose>
										</c:if>
									</div>
									<div class="formRow">
										<div class="optIn">
											<dsp:getvalueof bean="ProfileFormHandler.value.receiveEmail" id="flag">
											<c:if test="${flag == 'yes'}">
											<dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" name="emailOptIn" checked="true" value="true" iclass="fl">
                                                <dsp:tagAttribute name="aria-checked" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblemailOptIn erroremailOptIn"/>
                                            </dsp:input>
											</c:if>
											<c:if test="${flag == 'no'}">
											<dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" name="emailOptIn" checked="false" iclass="fl">
                                                <dsp:tagAttribute name="aria-checked" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblemailOptIn erroremailOptIn"/>
                                            </dsp:input>
											</c:if>
											</dsp:getvalueof> 
											<label id="lblemailOptIn" for="optIn" class="textDgray11"><bbbl:label key="lbl_opt_in" language="${pageContext.request.locale.language}" /> </label>
											<p class="textLgray11"><bbbl:label key="lbl_profile_OptIn" language="${pageContext.request.locale.language}"/></p>
										</div>
									</div>
									<div class="formRow shareAccount">
										<input name="shareAccount" type="checkbox" value="false" class="fl" id="shareAccount" disabled="disabled" aria-labelledby="lblshareAccount" />
										<label id="lblshareAccount" for="shareAccount" class="textDgray11"><bbbl:label key="lbl_profile_shareaccount" language="${pageContext.request.locale.language}"/></label>
										<p class="textLgray11"><bbbt:textArea key="txt_profile_shareSite_text" language="${pageContext.request.locale.language}"/></p>
									</div>
									<div class="formRow">  
										<dsp:input  bean="ProfileFormHandler.createAccOrderReg" type="submit" value="CREATE ACCOUNT" iclass="submitBtn submitBtnMarginRight" id="createAccontBtn">
                                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="createAccontBtn"/>
                                        </dsp:input>
										<a href="#" class="generalBold" title="Cancel"><bbbl:label key="lbl_profile_Cancel" language="${pageContext.request.locale.language}"/></a>
									</div>
									<div class="formRow">
										<ul class="viewPrivacyPolicy">
											<li><a href="#" class="general" title="View Privacy Policy"><bbbl:label key="lbl_profile_private_policy" language="${pageContext.request.locale.language}"/></a></li>
										</ul>
									</div>
									<div class="formRow">
										<ul class="viewPrivacyPolicy">
											<li><a href="login.jsp" class="general" title="View Privacy Policy">Back to Home Page</a></li>
										</ul>
									</div>
								</div>
							</div>
							<div class="grid_3">
								<div class="teaser_229 benefitsAccountTeaser">
									<bbbt:textArea key="txt_login_benefits_account" language="${pageContext.request.locale.language}"/>
								</div>
							</div>
						</div>
				</dsp:oparam>
				<dsp:oparam name="false">
				<bbbl:label key="lbl_profile_loggedIn" language="${pageContext.request.locale.language}" />
				 <dsp:a page="/login.jsp">
				<dsp:property bean="ProfileFormHandler.logoutSuccessURL" value="account/login.jsp"/>
				<dsp:property bean="ProfileFormHandler.logout" value="true"/>
				<bbbl:label key="lbl_profile_logout" language="${pageContext.request.locale.language}" />
				</dsp:a>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:form>
	</bbb:pageContainer>
</dsp:page>