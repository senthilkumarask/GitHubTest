<%--
  Default welcome page
 --%>
<dsp:page>
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="atg/userprofiling/ProfileFormHandler"/>
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:importbean bean="/com/bbb/cms/droplet/ChallengeQuestionDroplet" />
    <c:set var="pageWrapper" value="createAccount myAccount useFB" scope="request" />
    <c:set var="pageNameFB" value="registrationEdit" scope="request" />
    <c:set var="urlParam" value="${param.writeReviewSuccessURL}"/>
    <dsp:getvalueof id="currentSiteId" bean="Site.id" />
    <c:set var="email"><dsp:valueof value="${email}"/></c:set>
    <c:set var="challengeQuestionON"><bbbc:config key="challenge_question_flag" configName="FlagDrivenFunctions" /></c:set>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
        <dsp:param name="value" bean="SessionBean.legacyEmailId" />
        <dsp:oparam name="false">
            <dsp:getvalueof var="legacyEmail" bean="SessionBean.legacyEmailId"/>
            <c:set var="emailToRegister" scope="session" value="${legacyEmail}" />
            <dsp:setvalue bean="SessionBean.legacyEmailId" value="" />
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
            <dsp:droplet name="/atg/dynamo/droplet/Redirect">
                <dsp:param name="url" value="${contextPath}/account/login.jsp"/>
            </dsp:droplet>
        </c:when>
    </c:choose>

  <bbb:pageContainer section="accounts" bodyClass="" index="false" follow="false">
    <jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
    <jsp:body>

<dsp:importbean bean="atg/userprofiling/Profile"/>
<c:set var="profile_private_policy_title"><bbbl:label key="lbl_private_policy_account" language="${pageContext.request.locale.language}"/></c:set>

<div id="content" class="createAccount container_12 clearfix" role="main">

    <div class="grid_9">
    <h1 class="createAccount"><bbbl:label key="lbl_createaccount_header" language="${pageContext.request.locale.language}"/></h1>
        <div class="grid_9 alpha fbConnectWrap" role="application">
<dsp:form method="post" id="createAccount" action="myaccount.jsp">
                <dsp:droplet name="/atg/dynamo/droplet/Switch">
                    <dsp:param name="value" bean="Profile.transient"/>
                    <dsp:oparam name="true">

    <dsp:getvalueof var="createProfileEmailError" bean="ProfileFormHandler.errorMap.createProfileEmailError"></dsp:getvalueof>
    <dsp:getvalueof var="createProfileFirstNameError" bean="ProfileFormHandler.errorMap.createProfileFirstNameError"></dsp:getvalueof>
    <dsp:getvalueof var="createProfileLastNameError" bean="ProfileFormHandler.errorMap.createProfileLastNameError"></dsp:getvalueof>
    <dsp:getvalueof var="createProfilePasswordError" bean="ProfileFormHandler.errorMap.createProfilePasswordError"></dsp:getvalueof>
    <dsp:getvalueof var="createProfileConfirmPasswordError" bean="ProfileFormHandler.errorMap.createProfileConfirmPasswordError"></dsp:getvalueof>
    <dsp:getvalueof var="createProfileContainsName" bean="ProfileFormHandler.errorMap.createProfileContainsName"></dsp:getvalueof>
    <dsp:getvalueof var="createProfilePhoneNumberError" bean="ProfileFormHandler.errorMap.createProfilePhoneNumberError"></dsp:getvalueof>
    <dsp:getvalueof var="createProfileMobileNumberError" bean="ProfileFormHandler.errorMap.createProfileMobileNumberError"></dsp:getvalueof>
    <dsp:getvalueof var="createProfileSystemError" bean="ProfileFormHandler.errorMap.createProfileSystemError"></dsp:getvalueof>

 <%--   ErrorMessageForEach  starts --%>
    <dsp:include page="/global/gadgets/errorMessage.jsp">
      <dsp:param name="formhandler" bean="ProfileFormHandler"/>
    </dsp:include>
<%--    ErrorMessageForEach Ends --%>
<dsp:getvalueof var="load_fb_info" param="load_fb_info" />
<dsp:getvalueof id="memberId" param="memberId"/>



    <dsp:getvalueof var="memberVar" param="memberId"/>

      <c:if test="${not empty memberVar}">
            <p class="textLgray12"><bbbt:textArea key="txtarea_reclaim_registration_message" language="${pageContext.request.locale.language}"/></p>
      </c:if>


        <label class="textLgray12 block padBottom_5"><bbbl:label key="lbl_profilemain_email" language="${pageContext.request.locale.language}"/></label>
        <span class="textDgray14 emailId">${emailToRegister}

        <dsp:input bean="ProfileFormHandler.value.email" value="${emailToRegister}" type="hidden"/>
        </span>

        <c:if test="${empty memberVar}">
                <a href="login.jsp" class="general" title="Change"><bbbl:label key="lbl_profile_change_email" language="${pageContext.request.locale.language}"/></a>
        </c:if>
<%--
        <c:if test="${not empty createProfileEmailError}">
                <label class="error" for="email"><bbbe:error key="${createProfileEmailError}" language="${pageContext.request.locale.language}"/></label>
        </c:if>
--%>
            <c:if test="${FBOn}">
        <div class="renderFBConnect marTop_20" data-fb-connect-section="registrationEdit">
            <img src="${imagePath}/_assets/global/images/fb-loader.gif" alt="Facebook User" class="fbConnectLoader" />
            <p class="marBottom_5 noMarTop hideWithFBInfo hidden showWithFBCBtn"><bbbl:label key="lbl_facebook_connect" language="${pageContext.request.locale.language}" /></p>
        </div>
        </c:if>
          <div class="formRow">
            <div class="optIn">
              <dsp:getvalueof bean="ProfileFormHandler.value.receiveEmail" id="flag">
              <c:choose>
                <c:when test="${currentSiteId eq BedBathCanadaSite}">
                    <dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" id="optIn" name="emailOptIn" checked="false" iclass="fl">
                    <dsp:tagAttribute name="aria-checked" value="false"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
                    </dsp:input>
                    <label id="lbloptIn" for="optIn" class="textDgray11 wrapTextAfterCheckBox"><bbbl:label key="lbl_email_optin_bedbath_canada" language="${pageContext.request.locale.language}"/></label>
                    <dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn_BabyCA" id="emailOptIn_BabyCA" name="emailOptIn_BabyCA" checked="false" iclass="fl">
		            <dsp:tagAttribute name="aria-checked" value="false"/>
		            <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
		            </dsp:input>
		          	<label id="lbloptIn" for="emailOptIn_BabyCA" class="textDgray11 wrapTextAfterCheckBox"><bbbl:label key="lbl_email_optin_baby_canada" language="${pageContext.request.locale.language}"/></label>
                </c:when>
                <c:otherwise>
                    <c:if test="${flag == 'yes'}">
                        <dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" id="optIn" name="emailOptIn" checked="true" value="true" iclass="fl">
                        <dsp:tagAttribute name="aria-checked" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
                        </dsp:input>
                    </c:if>
                    <c:if test="${flag == 'no'}">
                      <dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" id="optIn" name="emailOptIn" checked="false" iclass="fl">
                      <dsp:tagAttribute name="aria-checked" value="false"/>
                      <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
                      </dsp:input>
                    </c:if>
                    <label id="lbloptIn" for="optIn" class="textDgray11 wrapTextAfterCheckBox"><bbbl:label key="lbl_email_optin_bedbath_canada" language="${pageContext.request.locale.language}"/></label>
                </c:otherwise>
              </c:choose>
              </dsp:getvalueof>
            </div>
            <dsp:getvalueof var="babyCA" bean="SessionBean.babyCA"/>
            <div class="clear"></div>
          </div>
          <div class="input" aria-live="assertive">
            <div class="label">
                <label id="lblfirstName" for="firstName" class="textLgray12"><bbbl:label key="lbl_profile_firstname" language="${pageContext.request.locale.language}"/>&nbsp;<span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}"/></span></label>
            </div>
            <div class="text width_3">
                <dsp:input bean="ProfileFormHandler.value.firstName" type="text" iclass="input_large211 block" name="firstName" id="firstName">
                <dsp:tagAttribute name="data-load-fb" value="first_name"/>
                <dsp:tagAttribute name="aria-required" value="true"/>
                <dsp:tagAttribute name="aria-labelledby" value="lblfirstName errorfirstName"/>
                </dsp:input>
            </div>
            <%-- <input type="text" id="fname" name="firstName" value="" class="input_large211 block"/>--%>
<%--
        <c:if test="${not empty createProfileFirstNameError}">
                <label class="error" for="email"><bbbe:error key="${createProfileFirstNameError}" language="${pageContext.request.locale.language}"/></label>
        </c:if>
--%>
          </div>
          <div class="input" aria-live="assertive">
            <div class="label">
                <label id="lbllastName" for="lastName" class="textLgray12"><bbbl:label key="lbl_profile_lastname" language="${pageContext.request.locale.language}"/>&nbsp;<span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}"/></span></label>
            </div>
            <div class="text width_3">
                <dsp:input bean="ProfileFormHandler.value.lastName" type="text" iclass="input_large211 block" name="lastName" id="lastName">
                    <dsp:tagAttribute name="data-load-fb" value="last_name"/>
                    <dsp:tagAttribute name="aria-required" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lbllastName errorlastName"/>
                </dsp:input>
            </div>
            <c:if test="${not empty load_fb_info}">
                <dsp:input bean="ProfileFormHandler.fbLinking" type="hidden" name="fbLinking" id="fbLinking" value="true" />
            </c:if>
            <%-- <input type="text" name="lastName" id="lname" class="input_large211 block"/>--%>
<%--
        <c:if test="${not empty createProfileLastNameError}">
                <label class="error" for="email"><bbbe:error key="${createProfileLastNameError}" language="${pageContext.request.locale.language}"/></label>
        </c:if>
--%>
          </div>
          <div class="input clearfix" aria-live="assertive">
                <div class="label">
                    <label id="lblbasePhoneFull" for="basePhoneFull"><bbbl:label key="lbl_profile_primaryphone" language="${pageContext.request.locale.language}"/></label>
                </div>
                <div class="grid_3 alpha phone phoneFieldWrap">
                    <fieldset class="phoneFields noMarBot"><legend class="phoneFieldLegend offScreen"><bbbl:label key='lbl_phonefield_phnumber' language='${pageContext.request.locale.language}'/></legend>
                    <div class="text fl">
                        <input id="basePhoneFull" type="text" name="basePhoneFull" class="phone phoneField" maxlength="10" aria-required="false" aria-labelledby="lblbasePhoneFull errorbasePhoneFull" />
                    </div>
                    <div class="cb">
                        <label id="errorbasePhoneFull" for="basePhoneFull" class="error" generated="true"></label>
                    </div>
                    <dsp:input bean="ProfileFormHandler.value.phoneNumber" type="hidden" iclass="fullPhoneNum" name="phone"/>
                    </fieldset>
                </div>
            <input id="memberId" name="memberId" type="hidden" value='<dsp:valueof param="memberId"/>'/>
            <%-- input name="phone" type="hidden" value="" />--%>
<%--
        <c:if test="${not empty createProfilePhoneNumberError}">
                <label class="error" for="email">
                    <jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
                    <c:set target="${placeHolderMap}" property="fieldName"><bbbl:label key='lbl_profile_primaryphone' language='${pageContext.request.locale.language}'/></c:set>
                    <bbbe:error key="${createProfilePhoneNumberError}" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolderMap}"/>

                </label>
        </c:if>
--%>
          </div>
          <div class="input clearfix" aria-live="assertive">
            <div class="label">
                <label id="lblcellPhoneFull" for="cellPhoneFull"><bbbl:label key="lbl_profile_mobilephone" language="${pageContext.request.locale.language}"/></label>
            </div>
            <div class="grid_3 alpha phone phoneFieldWrap">
                <fieldset class="phoneFields noMarBot"><legend class="phoneFieldLegend offScreen"><bbbl:label key='lbl_profile_mobilephone' language='${pageContext.request.locale.language}'/></legend>
                <div class="text fl">
                    <input id="cellPhoneFull" type="text" name="cellPhoneFull" class="phone phoneField" maxlength="10" aria-required="false" aria-labelledby="lblcellPhoneFull errorcellPhoneFull" />
                </div>
                <div class="cb">
                    <label id="errorcellPhoneFull" for="cellPhoneFull" class="error" generated="true"></label>
                </div>
            <dsp:input bean="ProfileFormHandler.value.mobileNumber" type="hidden" iclass="fullPhoneNum" name="cell"/>
                </fieldset>
            </div>
            <script type="text/javascript">
                var BBB = BBB || {};
                BBB.accountManagement = true;
            </script>

            <%-- <input name="cell" type="hidden" value="" />--%>
        <c:if test="${not empty createProfileMobileNumberError}">
                <label class="error" for="email">
                    <%--<bbbe:error key="${createProfileMobileNumberError}" language="${pageContext.request.locale.language}"/> --%>
                    <jsp:useBean id="placeHolderMobile" class="java.util.HashMap" scope="request"/>
                    <c:set target="${placeHolderMobile}" property="fieldName"><bbbl:label key='lbl_profile_mobilephone' language='${pageContext.request.locale.language}'/></c:set>
                    <bbbe:error key="${createProfilePhoneNumberError}" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolderMobile}"/>
                </label>
        </c:if>
          </div>
          <c:if test="${challengeQuestionON}">
          <c:set var="your_answer_here"><bbbl:label key="lbl_type_your_answer" language="${pageContext.request.locale.language}"/></c:set>
	<dsp:droplet name="ChallengeQuestionDroplet">
		        <dsp:param name="fromLoginPage" value="fromLoginPage"/>
			<dsp:oparam name="output">
			<dsp:getvalueof param="PreferredQuestion1" var="PreferredQuestion1" />
			<dsp:getvalueof param="PreferredQuestion2" var="PreferredQuestion2" />
			<dsp:getvalueof param="ChallengeQuestionsMap1" var="ChallengeQuestionsMap1" />
			<dsp:getvalueof param="ChallengeQuestionsMap2" var="ChallengeQuestionsMap2" />
			<dsp:getvalueof param="PreferredAnswer1" var="PreferredAnswer1" />
			<dsp:getvalueof param="PreferredAnswer2" var="PreferredAnswer2" />
	<div class="input">
	<h2>Challenge Questions</h2>
		<label class="label" for="challengeQuestion1">	<bbbl:label key="lbl_Question" language="${pageContext.request.locale.language}" /> 1	</label>
		<div class="select width_3">
			<dsp:select bean="ProfileFormHandler.editValue.challengeQuestion1" name="challengeQuestion1" iclass="selector" id="challengeQuestion1" >
			<dsp:option value="">Please select First Question</dsp:option>
					<c:forEach items="${ChallengeQuestionsMap1}" var="entry">
					<c:if test="${entry.key ne PreferredQuestion1}">
					<dsp:option value="${entry.key}" >${entry.value}</dsp:option>
					</c:if>
					<c:if test="${entry.key eq PreferredQuestion1}">
					<dsp:option value="${entry.key}" selected="true">${entry.value}</dsp:option>
					</c:if>
		            </c:forEach>
		            </dsp:select>

		</div>
		<label class="label" id="lblanswer1" for="challengeAnswer1">	<bbbl:label key="lbl_Answer" language="${pageContext.request.locale.language}" /> 1 </label>
		<div class="text width_3 noMarBot">
		<dsp:input type="text" name="challengeAnswer1" id="challengeAnswer1"  bean="ProfileFormHandler.editValue.challengeAnswer1" value="${PreferredAnswer1}" autocomplete="off">
		<dsp:tagAttribute name="aria-required" value="true"/>
        <dsp:tagAttribute name="aria-labelledby" value="lblanswer1 errorcPassword"/>
        <dsp:tagAttribute name="placeholder" value="${your_answer_here}"/>
		</dsp:input>
		</div>
		 <div class="clear"></div> 
	</div>
	<div class="input">
		<label class="label" for="challengeQuestion2">	<bbbl:label key="lbl_Question" language="${pageContext.request.locale.language}" /> 2	</label>
		<div class="select  width_3">	
			<dsp:select bean="ProfileFormHandler.editValue.challengeQuestion2" name="challengeQuestion2" iclass="selector" id="challengeQuestion2">
					<dsp:option value="">Please select Second Question</dsp:option>
					<c:forEach items="${ChallengeQuestionsMap2}" var="entry">
					<c:if test="${entry.key ne PreferredQuestion2}">
					<dsp:option value="${entry.key}" >${entry.value}</dsp:option>
					</c:if>
					<c:if test="${entry.key eq PreferredQuestion2}">
					<dsp:option value="${entry.key}" selected="true">${entry.value}</dsp:option>
					</c:if>
		            </c:forEach>
		            </dsp:select>
		</div>
		<label class="label" id="lblanswer2" for="challengeAnswer2">	<bbbl:label key="lbl_Answer" language="${pageContext.request.locale.language}" /> 2 </label>
		<div class="text width_3">
			<dsp:input type="text" name="challengeAnswer2" id="challengeAnswer2"  bean="ProfileFormHandler.editValue.challengeAnswer2"  value="${PreferredAnswer2}" autocomplete="off">
			<dsp:tagAttribute name="aria-required" value="true"/>
			<dsp:tagAttribute name="aria-labelledby" value="lblanswer2 errorcPassword"/>
			<dsp:tagAttribute name="placeholder" value="${your_answer_here}"/>
                    </dsp:input>
		</div>
		 <div class="clear"></div> 
                </div>
            </dsp:oparam>
          </dsp:droplet>
          </c:if>
		  
		  
		  
		
		  <h2>Create Password</h2>
          <input type="text" class="hidden" value="${emailToRegister}" name="pwdSaveFixEmail" />
          <div class="input" aria-live="assertive">
            	<div class="label posRel">	
				<label id="lblpassword" for="password" class="textLgray12"><bbbl:label key="lbl_profile_password" language="${pageContext.request.locale.language}"/>&nbsp;<span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}"/></span></label>
                <div class="showPassDiv clearfix">
                    <input name="showPassword" type="checkbox" value="" data-toggle-class="showpassCreateAccount" class="showPassword" id="showPassword" />
                    <label for="showPassword" class="lblShowPassword"><bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/></label>
                    <div class="clear"></div> 
                </div>
			</div>
            <div class="text width_3">
                <dsp:input bean="ProfileFormHandler.value.password" type="password" autocomplete="off" iclass="input_large211 block" id="password" name="password" value="">
                <dsp:tagAttribute name="aria-required" value="true"/>
                <dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
                </dsp:input>
            </div>
            <%-- <input id="password" name="password" type="password" autocomplete="off" class="input_large211 block"/> --%>
<%--
           <c:if test="${not empty createProfileContainsName}">
                <label class="error" for="email"><bbbe:error key="${createProfileContainsName}" language="${pageContext.request.locale.language}"/></label>
            </c:if>
        <c:if test="${not empty createProfilePasswordError}">
                <label class="error" for="email">
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

                </label>
        </c:if>
--%>
          </div>
          <div class="input" aria-live="assertive">
            <div class="label">
                <label id="lblcPassword" for="cPassword" class="textLgray12"><bbbl:label key="lbl_profile_confirmpassword" language="${pageContext.request.locale.language}"/>&nbsp;<span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}"/></span></label>
            </div>
            <div class="text width_3">
                <dsp:input bean="ProfileFormHandler.value.confirmPassword" type="password" autocomplete="off" iclass="input_large211 block" id="cPassword" name="confirmPassword" value="">
                <dsp:tagAttribute name="aria-required" value="true"/>
                <dsp:tagAttribute name="aria-labelledby" value="lblcPassword errorcPassword"/>
                </dsp:input>
            </div>
            <%-- <input id="cPassword" name="confirmPassword" type="password" autocomplete="off" class="input_large211 block"/>--%>
<%--
            <c:if test="${not empty createProfileConfirmPasswordError}">
                <label class="error" for="email"><bbbe:error key="${createProfileConfirmPasswordError}" language="${pageContext.request.locale.language}"/></label>
        </c:if>
--%>
          </div>
		  
					
		            
          <c:choose>
          <c:when test="${shareProfileOn}">
           <c:set var="checkSharedAccountByDefault" scope="request">
                <bbbc:config key="checkSharedAccountByDefault" configName="FlagDrivenFunctions" />
            </c:set>
          <dsp:droplet name="/atg/dynamo/droplet/Switch">
            <dsp:param name="value" bean="Site.id"/>
            <dsp:oparam name="BedBathCanada">
            </dsp:oparam>
            <dsp:oparam name="default">
                <div class="formRow shareAccount">
                    <dsp:input bean="ProfileFormHandler.sharedCheckBoxEnabled" type="checkbox" value="true" checked="${checkSharedAccountByDefault}" iclass="fl" id="shareAccount">
                    <dsp:tagAttribute name="aria-checked" value="true"/>                    
                    <dsp:tagAttribute name="aria-labelledby" value="lblshareAccount errorshareAccount"/>
					</dsp:input>
					<label id="lblshareAccount" for="shareAccount" class="textDgray11 wrapTextAfterCheckBox"><bbbl:label key="lbl_profile_shareaccount" language="${pageContext.request.locale.language}"/></label>
					<div id="accountCheckContent">
										
					<c:if test="${currentSiteId eq BuyBuyBabySite}">
						<img class="babyImg" align="left" itemprop="logo" src="/_assets/global/images/logo/logo_bbb_by.png" height="42" width="146" alt="buybuy BABY">
					</c:if>
					<c:if test="${currentSiteId eq BedBathUSSite}">
						<img class="bbImg" align="left" itemprop="logo" src="/_assets/global/images/logo/logo_baby_nav.png" height="32" width="62" alt="Bed Bath &amp; Beyond">
					</c:if>
					<c:if test="${currentSiteId eq BedBathCanadaSite}">
		            
					</c:if>					

                    <p class="textLgray11"><bbbt:textArea key="txt_profile_shareSite_text" language="${pageContext.request.locale.language}"/></p></div>
					<div class="shareEmailSubscription hidden">
					<c:if test="${currentSiteId ne BedBathCanadaSite}">
					<dsp:input type="checkbox" bean="ProfileFormHandler.emailOptInSharedSite" id="optIn_shared" name="emailOptIn_shared" checked="false" iclass="fl">
                    <dsp:tagAttribute name="aria-checked" value="false"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
                    </dsp:input>
                    <label id="lbl_optIn_shared" for="optIn_shared" class="textDgray11 wrapTextAfterCheckBox"><bbbl:label key="lbl_email_optin_for_sis_site" language="${pageContext.request.locale.language}"/></label>
                    </c:if></div>
                    </div>
	</dsp:oparam>
</dsp:droplet>
          </c:when>
          <c:otherwise>
            <dsp:input  bean="ProfileFormHandler.sharedCheckBoxEnabled" type="hidden" value="false" checked="false" iclass="fl" id="shareAccount">
            <dsp:tagAttribute name="aria-checked" value="false"/>
            </dsp:input>
          </c:otherwise>
          </c:choose>
		  
          <div class="formRowAcc clearfix">
            <c:set var="lbl_createaccount_button">
                <bbbl:label key='lbl_createaccount_main_header' language='${pageContext.request.locale.language}' />
            </c:set>

		   <dsp:setvalue param="memberId" value="${memberVar}"/>
           <dsp:setvalue param="load_fb_info" value="${load_fb_info}" />

		<div class="button button_active  button_active_orange">
			<c:choose>
    			<c:when test="${not empty sessionScope.redirectPage && sessionScope.redirectPage=='MyRegistries'}">
        			<c:remove var="redirectPage" scope="session" />
        			<dsp:input  bean="ProfileFormHandler.createAcctMyReg" type="submit" value="${lbl_createaccount_button}" iclass="submitBtn submitBtnMarginRight" id="createAccontBtn">
                		<dsp:tagAttribute name="aria-pressed" value="false"/>
               			<dsp:tagAttribute name="aria-describedby" value="createAccontBtn"/>
                		<dsp:tagAttribute name="role" value="button"/>
                	</dsp:input>
                	<dsp:input  bean="ProfileFormHandler.createAcctMyReg" type="hidden" value="${lbl_createaccount_button}" iclass="submitBtn submitBtnMarginRight">
                	</dsp:input>        			
    			</c:when>
    			<c:otherwise>
    			
    			<c:if test="${not empty urlParam}">				  					  
					  <dsp:getvalueof var="sUrl" bean="ProfileFormHandler.writeReviewSuccessURL" />
					  <c:choose>
						    <c:when test="${empty sUrl}">						  	 
						  	  <dsp:input bean="ProfileFormHandler.writeReviewSuccessURL" type="hidden" value="${urlParam}"/>
					    </c:when>					    
					    <c:otherwise>
					    	<dsp:input bean="ProfileFormHandler.writeReviewSuccessURL" type="hidden" beanvalue="ProfileFormHandler.writeReviewSuccessURL"/>
					    </c:otherwise>
					  </c:choose>		    
		       </c:if>	
    			
    				<dsp:input  bean="ProfileFormHandler.createAcct" type="submit" value="${lbl_createaccount_button}" iclass="submitBtn submitBtnMarginRight" id="createAccontBtn">
                		<dsp:tagAttribute name="aria-pressed" value="false"/>
               			<dsp:tagAttribute name="aria-describedby" value="createAccontBtn"/>
                		<dsp:tagAttribute name="role" value="button"/>
                	</dsp:input>
                	
                	
                	
                	<dsp:input  bean="ProfileFormHandler.createAcct" type="hidden" value="${lbl_createaccount_button}" iclass="submitBtn submitBtnMarginRight"/>
                	
        			
    			</c:otherwise>
			</c:choose>
			
		</div>
        
			
           
            
            <%--<a href="#" class="submitBtn submitBtnMarginRight" title="CREATE ACCOUNT" id="createAccontBtn"><span>CREATE ACCOUNT</span></a>--%>


            <c:set var="lbl_cancel_button"><bbbl:label key='lbl_profile_Cancel' language='${pageContext.request.locale.language}'/></c:set>
            <a href="login.jsp" value="${lbl_cancel_button}" type="button" class="btnCancel" role="link">${lbl_cancel_button}</a>

            <%--
            <a href="#" class="generalBold" title="Cancel"><bbbl:label key="lbl_profile_Cancel" language="${pageContext.request.locale.language}"/></a>
            --%>
            </div>

          <div class="formRow">
            <ul class="viewPrivacyPolicy">
              <li><a href="/store/static/PrivacyPolicy?showAsPopup=true" class="privacyPolicyModal" title="${profile_private_policy_title}"><bbbl:label key="lbl_private_policy_account" language="${pageContext.request.locale.language}"/></a></li>
              <c:if test="${currentSiteId eq BedBathCanadaSite}">
             	 <li><bbbt:textArea key="txt_ca_address" language="${pageContext.request.locale.language}"/></li>
             	</c:if> 
            </ul>
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
        </div>
         <%--Grid 6 end from here--%>
    </div>
    <%--Grid 9 end from here--%>
    <%--Grid 3 start from here--%>
    <div class="grid_3">
        <div class="teaser_229 benefitsAccountTeaser">
          <bbbt:textArea key="txt_login_benefits_account" language="${pageContext.request.locale.language}"/>
        </div>
    </div>
    <%--Grid 3 end from here--%>
</div>
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